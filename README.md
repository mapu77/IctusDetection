# IctusDetection
## Inspiration
Ictus or brain stroke is the third cause of death in world and affects one in six people, becoming the leading cause of death in women. It occurs when a blood vessel that carries oxygen and nutrients to the brain is blocked or ruptured, so the brain cannot get all it needs and brain cells die. Acting fast is crucial to save the patients life and avoid future side effects such as restlessness, low blood pressure, dizziness...

To our best knowledge there's no application that actively detect the symptoms of an ictus attacks, but only ask questions to determine if the patient is suffering such an attack and it do not take any preventive actions.

Hopefully, ictus symptoms can be easily detect with a few test. Given the is lack of oxygen in the brain there are visible consequences that can be detected.

## What it does
_IctusDetection_ is a mobile app that takes the fast ictus detection to another level by applying the F.A.S.T test.
1. **F**ace Dropping detection: By taking a picture we detect face dropping produced by the brains dead cells that hold the right or left side of the face.
2. **A**rm wakeness: By holding the mobile phone with the arms stretched, gyroscope analyses the response to that act. If no oxygen is arriving to the brain, one of the sides of the brain wouldn't response as expected, therefore one of the arms wouldn't move.
3. **S**peech & Write difficulty: By making the user recognize easy pictures we measure its answer precision. Ictus' suffers have difficulties to write precisely and to speak clearly
4. **T**ime to call 911: Tests are passed by importance and when we found an evidence of a brain stroke, the app calls to a contact person or emergency number.

## How we built it
The app is built in _Kotlin_ for Android devices. It uses _Google Cloud Vision API_ to analyse faces and obtain face metrics. It also uses mobile gyroscope to detect the device movement. It integrates with the mobile camera and mobile phone services too.

## Challenges we ran into
It is always a challenge to imagine, think and built anything from scratch in a 24 hours project.  Android services are always a pain in the ass, and camera, api integration and gyroscope were tricky to set in.

## Accomplishments that we're proud of
We are proud of building full functional app in Kotlin in 24 hours, given that it was the first time using this language. In addition, it would be awesome if this idea could help to set up other ideas for diseases detection.

## What we learned
We've learned Kotlin which was our main focus this hackathon. We've found that is quite easy to adapt existing Java code to Kotlin and the more we practice the more new Kotlin lines we wrote without help.

## What's next for IctusDetection
Detecting symptoms automatically without the application launch. We know it could be an intrusive app, but hundreds of lifes could be saved.
