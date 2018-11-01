package models;

import java.util.ArrayList;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;

public class ColorCalibrator {
	// final variables
	private final int TEST_SAMPLES = 8;

	// variables
	private Lights lights;
	private Sensors sensors;
	private int black;
	private int white;
	private int blackBorder;
	private int whiteBorder;
	private int finishLineColor;
	private float redMeasured;
	private float greenMeasured;
	private float blueMeasured;
	private ArrayList<Integer> calibrationValues;
	private boolean testingDone;
	private int currentLightIntensity;

	public ColorCalibrator(Sensors sensors) {
		this.sensors = sensors;
		calibrationValues = new ArrayList<>();
		lights = new Lights();
	}

	public void run() {
		scanFinishLine();
		scanBlack();
		rotateToLeft();
		scanWhite();
		setBorders();
		rotateBackToBlackLine();
	}

	private void scanFinishLine() {
		finishLineColor = 0; // 0 = geen meting, 1 = blauw, 2 = rood
		white = 1;
		black = 100;

		// make test reading on finish line
		lights.brickLights(1);
		while (finishLineColor == 0) {
			Delay.msDelay(1000);

			float[] sample = sensors.getRGBSample();
			redMeasured = sample[0];
			greenMeasured = sample[1];
			blueMeasured = sample[2];

			// determine color of finish line
			if ((int) (redMeasured * Finals.SAMPLE_TO_RGB)
					+ Finals.DIFFERENCE_BLUE_OVER_RED < (int) (blueMeasured * Finals.SAMPLE_TO_RGB)) {
				finishLineColor = 1; // blauw
			} else if ((int) (blueMeasured * Finals.SAMPLE_TO_RGB)
					+ Finals.DIFFERENCE_RED_OVER_BLUE < (int) (redMeasured * Finals.SAMPLE_TO_RGB)
					&& (greenMeasured * Finals.SAMPLE_TO_RGB < Finals.MAXIMUM_GREEN_IN_RED)) {
				finishLineColor = 2; // rood
			} else {
				finishLineColor = 0;
				lights.brickLights(2);
				Delay.msDelay(2000);
			}
			lights.brickLights(0);
		}
	}

	private void scanBlack() {
		// drive backwards, make black readings
		Motor.A.setSpeed(125);
		Motor.B.setSpeed(125);
		Motor.A.backward();
		Motor.B.backward();

		testingDone = false;
		// make test readings
		while (!testingDone) {
			// add test sample then wait
			float[] sample = sensors.getRGBSample();
			redMeasured = sample[0];
			greenMeasured = sample[1];
			blueMeasured = sample[2];
			calibrationValues.add((int) ((redMeasured + greenMeasured + blueMeasured) / 3 * 100));
			Delay.msDelay(200);
			// continue until number of samples is collected
			if (calibrationValues.size() >= TEST_SAMPLES) {
				testingDone = true;
				Motor.A.stop();
				Motor.B.stop();
			}
		}
	}

	private void rotateToLeft() {
		// rotate inward (leftward)
		Motor.A.setSpeed(125);
		Motor.B.setSpeed(125);
		Motor.A.backward();
		Motor.B.forward();
		Delay.msDelay(750);
		Motor.A.stop();
		Motor.B.stop();
	}

	private void scanWhite() {
		// drive forwards, make white readings
		Motor.A.setSpeed(100);
		Motor.B.setSpeed(100);
		Motor.A.forward();
		Motor.B.forward();
		testingDone = false;

		// make test readings
		while (!testingDone) {
			// add test sample then wait
			float[] sample = sensors.getRGBSample();
			redMeasured = sample[0];
			greenMeasured = sample[1];
			blueMeasured = sample[2];
			calibrationValues.add((int) ((redMeasured + greenMeasured + blueMeasured) / 3 * 100));
			Delay.msDelay(100);
			// continue until number of samples is collected
			if (calibrationValues.size() >= TEST_SAMPLES * 2) {
				testingDone = true;
				Motor.A.stop();
				Motor.B.stop();
			}
		}
	}

	private void setBorders() {
		// set the black(est) and white(st) values
		for (int i = 0; i < calibrationValues.size(); i++) {
			if (calibrationValues.get(i) < black)
				black = calibrationValues.get(i).intValue();
			if (calibrationValues.get(i) > white)
				white = calibrationValues.get(i).intValue();
		}
		// calibrate 'effective course'
		final int DEVIATION = ((white - black) / 4);
		blackBorder = black + DEVIATION;
		whiteBorder = white - DEVIATION;
	}

	private void rotateBackToBlackLine() {
		// rotate back
		Motor.A.backward();
		Motor.B.backward();
		Motor.A.setSpeed(70);
		Motor.B.setSpeed(85);

		// find the grey line
		boolean lineFound = false;
		while (!lineFound) {
			float[] sample = sensors.getRGBSample();
			redMeasured = sample[0];
			greenMeasured = sample[1];
			blueMeasured = sample[2];
			currentLightIntensity = (int) ((redMeasured + greenMeasured + blueMeasured) / 3 * 100);
			if (currentLightIntensity < blackBorder) {
				lineFound = true;
			}
		}
		Motor.A.stop();
		Motor.B.stop();
	}

	public int getBlackBorder() {
		return blackBorder;
	}

	public int getWhiteBorder() {
		return whiteBorder;
	}

	public int getFinishLineColor() {
		return finishLineColor;
	}

}
