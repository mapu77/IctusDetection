package org.hackprague.ictusdetection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.io.BaseEncoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CloudVision {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCuFfQKnWjqgHaThAhYeNNTDTlITbbZtfQ";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MAX_DIMENSION = 1200;

    private TaskDelegate delegate;

    @SuppressLint("StaticFieldLeak")
    public AsyncTask callCloudVision(final TaskDelegate delegate, final Uri uri, final Context context) {

        // Do the real work in an async task, because we need to use the network anyway
        return new AsyncTask<Object, Void, Boolean>() {

            public TaskDelegate myDelegate;

            @Override
            protected void onPostExecute(Boolean hasIctus) {
                this.myDelegate.TaskCompletionResult(hasIctus);
            }

            @Override
            protected Boolean doInBackground(Object... params) {

                this.myDelegate = delegate;
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = context.getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = getSignature(context.getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision vision = new Vision.Builder(httpTransport, jsonFactory, null).setVisionRequestInitializer(requestInitializer).build();

                    Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri));
                    List<AnnotateImageRequest> requests = new ArrayList<>();

                    Image base64EncodedImage = new Image();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();

                    // Base64 encode the JPEG
                    base64EncodedImage.encodeContent(imageBytes);

                    Feature feat = new Feature();
                    feat.setType("FACE_DETECTION");
                    feat.setMaxResults(10);

                    List<Feature> features = new ArrayList<>();
                    features.add(feat);

                    AnnotateImageRequest request = new AnnotateImageRequest().setFeatures(features).setImage(base64EncodedImage);
                    requests.add(request);

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest().setRequests(requests);

                    Vision.Images.Annotate annotate = vision.images().annotate(batchAnnotateImagesRequest);
                    annotate.setDisableGZipContent(true);

                    List<AnnotateImageResponse> responses = annotate.execute().getResponses();
                    float rollAngle = responses.get(0).getFaceAnnotations().get(0).getRollAngle();
                    return rollAngle > 7.5;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = MAX_DIMENSION;
        int resizedHeight = MAX_DIMENSION;

        if (originalHeight > originalWidth) {
            resizedHeight = MAX_DIMENSION;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = MAX_DIMENSION;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = MAX_DIMENSION;
            resizedWidth = MAX_DIMENSION;
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        return Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("I found these things:\n\n");

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("nothing");
        }

        System.out.println(message.toString());
        return message.toString();
    }


    private String getSignature(@NonNull PackageManager pm, @NonNull String packageName) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo == null
                    || packageInfo.signatures == null
                    || packageInfo.signatures.length == 0
                    || packageInfo.signatures[0] == null) {
                return null;
            }
            return signatureDigest(packageInfo.signatures[0]);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private String signatureDigest(Signature sig) {
        byte[] signature = sig.toByteArray();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(signature);
            return BaseEncoding.base16().lowerCase().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}
