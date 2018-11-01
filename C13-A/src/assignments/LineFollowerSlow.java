package assignments;

import java.util.ArrayList;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;
import lejos.utility.Stopwatch;
import models.*;

public class LineFollowerSlow extends Assignment {

	// static variables
	private static ArrayList<Integer> roadMapA;
	private static ArrayList<Integer> roadMapB;

	// variables
	private Sensors sensors;
	private Stopwatch stopwatch;
	private CsvFile csvFile;
	private int currentLightIntensity;
	private float redMeasured;
	private float greenMeasured;
	private float blueMeasured;
	private int finishLineColor; // 0 = geen meting, 1 = blauw, 2 = rood
	private int blackBorder;
	private int whiteBorder;
	private boolean start;
	private boolean finished;
	private double trackTime;

	public LineFollowerSlow(Sensors sensors) {
		this.sensors = sensors;
		stopwatch = new Stopwatch();
		roadMapA = new ArrayList<>();
		roadMapB = new ArrayList<>();
		csvFile = new CsvFile();
	}

	@Override
	public void run() {
		sensors.setColorSensorRGBMode();
		calibrateColors();
		followLine();
		createCsvFiles();
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

				int motorSpeedA = (int) (Finals.SPEEDFACTOR * (currentLightIntensity - blackBorder) / Finals.SLOW_FACTOR);
				int motorSpeedB = (int) (Finals.SPEEDFACTOR * (whiteBorder - currentLightIntensity) / Finals.SLOW_FACTOR);

				if (motorSpeedA < 0) {
					Motor.A.backward();
					motorSpeedA = -motorSpeedA * Finals.REVERSE_SPEEDFACTOR;
					if (start)
						roadMapA.add(-motorSpeedA);
				} else {
					Motor.A.forward();
					if (start)
						roadMapA.add(motorSpeedA);
				}

				if (motorSpeedB < 0) {
					Motor.B.backward();
					motorSpeedB = -motorSpeedB * Finals.REVERSE_SPEEDFACTOR;
					if (start)
						roadMapB.add(-motorSpeedB);
				} else {
					Motor.B.forward();
					if (start)
						roadMapB.add(motorSpeedB);
				}

				Motor.A.setSpeed(motorSpeedA + Finals.DEFAULT_SPEED / Finals.SLOW_FACTOR);
				Motor.B.setSpeed(motorSpeedB + Finals.DEFAULT_SPEED / Finals.SLOW_FACTOR);
				Delay.msDelay(100);
			}
		}
	}

	private void createCsvFiles() {
		csvFile.createCsvFileMotor(roadMapA, "A");
		csvFile.createCsvFileMotor(roadMapB, "B");
	}

	private void displayTrackTime() {
		LCD.clear();
		String stringTrackTime = String.format("Tracktime = %.2f", trackTime);
		LCD.drawString(stringTrackTime, 0, 0);
		Utilities.waitForEnter();
	}
}
