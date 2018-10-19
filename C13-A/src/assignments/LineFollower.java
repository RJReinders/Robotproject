package assignments;

// imports
import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import models.FindBlueLine;

public class LineFollower extends Assignment {

	// attributes: engine
	private final int DEFAULT_SPEED = 50;
	private final int DEVIATION = 7;
	private static int white = 1;
	private static int black = 100;
	private int blackBorder;
	private int whiteBorder;
	private int currentLightIntensity;
	private int speedFactor = 10;

	// attributes: color sensor
	private EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S3);
	private SampleProvider sp = colorSensor.getRedMode();
	private float[] lightIntensity = new float[sp.sampleSize()];

	// attributes: roadmap
	private static ArrayList<Float> roadMapA = new ArrayList<>();
	private static ArrayList<Float> roadMapB = new ArrayList<>();

	FindBlueLine findBlueLine = new FindBlueLine(colorSensor);

	public LineFollower() {

	}

	@Override
	public void run() {
		calibrateColors();
		rotateBackToBlackLine();
		followLine();
	}

	public void followLine() {
		Motor.A.setSpeed(DEFAULT_SPEED);
		Motor.B.setSpeed(DEFAULT_SPEED);

		// int i = 0;

		findBlueLine.start();

		Motor.A.forward();
		Motor.B.forward();

		while (!findBlueLine.getFinished()) {

			sp.fetchSample(lightIntensity, 0);
			currentLightIntensity = (int) (lightIntensity[0] * 100);

			float motorSpeedA = speedFactor * (currentLightIntensity - blackBorder);
			float motorSpeedB = speedFactor * (whiteBorder - currentLightIntensity);

			roadMapA.add(motorSpeedA);
			roadMapB.add(motorSpeedB);

			// System.out.println(roadMapA.get(i));
			// System.out.println(roadMapB.get(i));

			if (motorSpeedA < 0) {
				Motor.A.backward();
				motorSpeedA = -motorSpeedA * 4;
			} else {
				Motor.A.forward();
			}

			if (motorSpeedB < 0) {
				Motor.B.backward();
				motorSpeedB = -motorSpeedB * 4;
			} else {
				Motor.B.forward();
			}

			Motor.A.setSpeed(motorSpeedA);
			Motor.B.setSpeed(motorSpeedB);

			Delay.msDelay(100);

			// System.out.println(currentLightIntensity);
		}

		System.out.println("Tracktime = " + findBlueLine.getTrackTime());

		findBlueLine.endThread();

		Motor.A.stop();
		Motor.B.stop();
		
		Button.waitForAnyEvent();
	}

	private void rotateBackToBlackLine() {
		Motor.A.backward();
		Motor.B.forward();
		Motor.A.setSpeed(100);
		Motor.B.setSpeed(100);

		boolean greyLineFound = false;
		while (!greyLineFound) {
			sp.fetchSample(lightIntensity, 0);
			currentLightIntensity = (int) (lightIntensity[0] * 100);
			if (currentLightIntensity < blackBorder) {
				greyLineFound = true;
				Sound.buzz();
			}
		}
		Motor.A.stop();
		Motor.B.stop();

	}

	public void calibrateColors() {

		// local variables
		ArrayList<Float> calibrationValues = new ArrayList<>();
		boolean testingDone = false;
		final int TEST_SAMPLES = 25;

		// start rotating (clockwise)
		Motor.A.forward();
		Motor.B.backward();
		Motor.A.setSpeed(100);
		Motor.B.setSpeed(100);

		// making test readings
		while (!testingDone) {
			// add test sample then wait
			colorSensor.setCurrentMode("Red");
			sp.fetchSample(lightIntensity, 0);
			calibrationValues.add(lightIntensity[0] * 100);
			Delay.msDelay(250);

			// continue until number of samples is collected
			if (calibrationValues.size() >= TEST_SAMPLES) {
				testingDone = true;
				Sound.beep();
			}
		}

		// set the black(est) and white(st) values
		for (int i = 0; i < calibrationValues.size(); i++) {
			if (calibrationValues.get(i) < black)
				black = calibrationValues.get(i).intValue();
			if (calibrationValues.get(i) > white)
				white = calibrationValues.get(i).intValue();
		}
		// calibreren van de 'effectieve baan'
		//deviation = (white - black) / 5;
		
		blackBorder = black + DEVIATION;
		whiteBorder = white - (2 * DEVIATION);
		//percentueel scherpere correctie op wit toegevoegd

		// print the values (testcode: kan later weg)
		System.out.println("Zwartwaarde: " + black);
		System.out.println("Witwaarde: " + white);
		System.out.printf("Binnenbaan van %d tot %d", black + DEVIATION, white - (2 *DEVIATION));
		System.out.println();

		if (white / black > 0.90 && white / black < 1.10) {
			System.out.println("Geen twee kleuren gemeten!");
			Sound.beep();
			calibrateColors();
		}
		// reset motors
		Motor.A.stop();
		Motor.B.stop();
	}

	public static ArrayList<Float> getRoadMapA() {
		return roadMapA;
	}

	public static ArrayList<Float> getRoadMapB() {
		return roadMapB;
	}
}
