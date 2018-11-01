package assignments;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;
import lejos.utility.Stopwatch;
import models.*;

public class LineFollowerRGB extends Assignment {

	// variables
	private Sensors sensors;
	private Stopwatch stopwatch;
	private Lights lights;
	private int currentLightIntensity;
	private float redMeasured;
	private float greenMeasured;
	private float blueMeasured;
	private int finishLineColor; // 0 = geen meting, 1 = blauw, 2 = rood
	private int acceleration;
	private int blackBorder;
	private int whiteBorder;
	private boolean start;
	private boolean finished;
	private double trackTime;

	public LineFollowerRGB(Sensors sensors) {
		this.sensors = sensors;
		stopwatch = new Stopwatch();
		lights = new Lights();
	}

	@Override
	public void run() {
		sensors.setColorSensorRGBMode();
		calibrateColors();
		followLine();
		//sensors.closeColorSensor();
		displayTrackTime();
	}

	private void calibrateColors() {
		ColorCalibrator colorCalibrator = new ColorCalibrator(sensors);
		colorCalibrator.run();
		finishLineColor = colorCalibrator.getFinishLineColor();
		blackBorder = colorCalibrator.getBlackBorder();
		whiteBorder = colorCalibrator.getWhiteBorder();
	}

	private void followLine() {
		start = false;
		finished = false;
		acceleration = 10;

		// loop until we have finished
		while (!finished) {

			// take color sample
			float[] sample = sensors.getRGBSample();
			redMeasured = sample[0];
			greenMeasured = sample[1];
			blueMeasured = sample[2];

			if (finishLineColor == 1) {
				// check if finishline = blue
				if (start == false && ((int) (redMeasured * Finals.SAMPLE_TO_RGB)
						+ Finals.DIFFERENCE_BLUE_OVER_RED < (int) (blueMeasured * Finals.SAMPLE_TO_RGB))) {
					Sound.beep();
					stopwatch.reset();
					start = true;
				} else if (start == true
						&& ((int) (redMeasured * Finals.SAMPLE_TO_RGB)
								+ Finals.DIFFERENCE_BLUE_OVER_RED < (int) (blueMeasured * Finals.SAMPLE_TO_RGB))
						&& stopwatch.elapsed() / 1000 > 4.0) {
					Sound.buzz();
					trackTime = (stopwatch.elapsed() / 1000.0);
					finished = true;
					Motor.A.stop();
					Motor.B.stop();
				}

			} else {
				// check if finishline = red
				if (start == false && ((int) (blueMeasured * Finals.SAMPLE_TO_RGB)
						+ Finals.DIFFERENCE_RED_OVER_BLUE < (int) (redMeasured * Finals.SAMPLE_TO_RGB)
						&& (greenMeasured * Finals.SAMPLE_TO_RGB < Finals.MAXIMUM_GREEN_IN_RED))) {
					Sound.beep();
					stopwatch.reset();
					start = true;
				} else if (start == true
						&& ((int) (blueMeasured * Finals.SAMPLE_TO_RGB)
								+ Finals.DIFFERENCE_RED_OVER_BLUE < (int) (redMeasured * Finals.SAMPLE_TO_RGB)
								&& (greenMeasured * Finals.SAMPLE_TO_RGB < Finals.MAXIMUM_GREEN_IN_RED))
						&& stopwatch.elapsed() / 1000 > 4.0) {
					Sound.buzz();
					trackTime = (stopwatch.elapsed() / 1000.0);
					finished = true;
					Motor.A.stop();
					Motor.B.stop();
				}
			}

			if (!finished) {
				currentLightIntensity = (int) ((redMeasured + greenMeasured + blueMeasured) / 3 * 100);
				LCD.clear();
				LCD.drawString("    ", 0, 5);
				LCD.drawInt(currentLightIntensity, 0, 5);

				int motorSpeedA = (int) (Finals.SPEEDFACTOR * (currentLightIntensity - blackBorder));
				int motorSpeedB = (int) (Finals.SPEEDFACTOR * (whiteBorder - currentLightIntensity));

				// if (almost) straight line, accelerate
				if (motorSpeedA / (motorSpeedB + 1) > 0.55 && motorSpeedA / (motorSpeedB + 1) < 1.45) {
					acceleration += 15;
					if (acceleration > 450)
						acceleration = 450;
					lights.brickLights(1);
				} else {
					acceleration -= 45;
					if (acceleration < 15)
						acceleration = 15;
					lights.brickLights(2);
				}

				if (motorSpeedA < 0) {
					Motor.A.backward();
					motorSpeedA = -motorSpeedA * Finals.REVERSE_SPEEDFACTOR;
				} else {
					Motor.A.forward();
					motorSpeedA += acceleration;
				}

				if (motorSpeedB < 0) {
					Motor.B.backward();
					motorSpeedB = -motorSpeedB * Finals.REVERSE_SPEEDFACTOR;
				} else {
					Motor.B.forward();
					motorSpeedB += acceleration;
				}

				Motor.A.setSpeed(motorSpeedA + Finals.DEFAULT_SPEED);
				Motor.B.setSpeed(motorSpeedB + Finals.DEFAULT_SPEED);
				Delay.msDelay(50);
			}
		}
	}

	private void displayTrackTime() {
		LCD.clear();
		String stringTrackTime = String.format("Tracktime = %.2f", trackTime);
		LCD.drawString(stringTrackTime, 0, 0);
		Utilities.waitForEnter();
	}
}
