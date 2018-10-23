package assignments;

// imports
import java.util.ArrayList;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import models.*;

public class LineFollower extends Assignment {

	// attributes: engine
	private final int DEFAULT_SPEED = 50;
	private final int REVERSE_SPEEDFACTOR = 3;
	private int white = 1;
	private int black = 100;
	private int acceleration = 10;
	private int blackBorder;
	private int whiteBorder;
	private int currentLightIntensity;
	private double speedFactor = 3.0;

	// attributes: color sensor
	private EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S2);
	private SampleProvider sp = colorSensor.getRedMode();
	private float[] lightIntensity = new float[sp.sampleSize()];

	// attributes: roadmap
	private static ArrayList<Float> roadMapA = new ArrayList<>();
	private static ArrayList<Float> roadMapB = new ArrayList<>();

	Lights lights = new Lights();
	FindBlueLine findBlueLine = new FindBlueLine(colorSensor);

	public LineFollower() {

	}

	@Override
	public void run() {
		calibrateColors();
		rotateBackToBlackLine();

		while (!findBlueLine.getFinished()) {
			followLine();
			findBlueLine.run();

		}
		System.out.println("Tracktime = " + findBlueLine.getTrackTime());

		findBlueLine.endThread();

		Motor.A.stop();
		Motor.B.stop();

		Button.waitForAnyEvent();

	}

	public void followLine() {
		// start second thread to find Blue Line
		findBlueLine.start();
		
		// loop until we have finished
		while (!findBlueLine.getFinished()) {

			// take color sample
			sp.fetchSample(lightIntensity, 0);
			currentLightIntensity = (int) (lightIntensity[0] * 100);
			LCD.drawString("    ", 0, 5);
			LCD.drawInt(currentLightIntensity, 0, 5);

			// NOTE eerst formule, dan FW/BW, dan default speed erbij!
			// NOTE acceleratie voor of na het flippen van de motor?
			float motorSpeedA = (int) (speedFactor * (currentLightIntensity - blackBorder) + 70);
			float motorSpeedB = (int) (speedFactor * (whiteBorder - currentLightIntensity) + 70);

			// if (almost) straight line, accelerate
			if (motorSpeedA / motorSpeedB > 0.60 && motorSpeedA / motorSpeedB < 1.40) {
				acceleration += 10;
				if (acceleration > 300)
					acceleration = 300;
				lights.brickLights(1, 150);
			} else {
				acceleration -= 40;
				if (acceleration < 10)
					acceleration = 10;
				lights.brickLights(2, 150);
			}

			if (motorSpeedA < 70) {
				Motor.A.backward();
				motorSpeedA = -motorSpeedA * REVERSE_SPEEDFACTOR;
			} else {
				Motor.A.forward();
				motorSpeedA += acceleration;
			}

			if (motorSpeedB < 70) {
				Motor.B.backward();
				motorSpeedB = -motorSpeedB * REVERSE_SPEEDFACTOR;
			} else {
				Motor.B.forward();
				motorSpeedB += acceleration;
			}

			LCD.clear();
			LCD.drawInt((int) motorSpeedA, 0, 7);
			LCD.drawInt((int) motorSpeedB, 12, 7);
			LCD.drawInt((int) acceleration, 7, 7);

			// N.B. roadMap onthoudt niet forward of backward
			roadMapA.add(motorSpeedA);
			roadMapB.add(motorSpeedB);

			Motor.A.setSpeed(motorSpeedA);
			Motor.B.setSpeed(motorSpeedB);
			Delay.msDelay(50);
		}
	}

	private void rotateBackToBlackLine() {
		// backwards rotation, extra speed
		Motor.A.backward();
		Motor.B.forward();
		Motor.A.setSpeed(150);
		Motor.B.setSpeed(150);

		// find the grey line
		boolean greyLineFound = false;
		while (!greyLineFound) {
			sp.fetchSample(lightIntensity, 0);
			currentLightIntensity = (int) (lightIntensity[0] * 100);
			if (currentLightIntensity < blackBorder) {
				greyLineFound = true;
				Sound.buzz();
				LCD.drawString("Grijze lijn gevonden!", 0, 4);
			}
		}

		// start driving forward
		Delay.msDelay(100);
		Motor.A.setSpeed(DEFAULT_SPEED);
		Motor.B.setSpeed(DEFAULT_SPEED);
		Motor.A.forward();
		Motor.B.forward();

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

		// make test readings
		while (!testingDone) {
			// add test sample then wait
			colorSensor.setCurrentMode("Red");
			sp.fetchSample(lightIntensity, 0);
			calibrationValues.add(lightIntensity[0] * 100);
			Delay.msDelay(200);

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
		// calibrate 'effective course'
		final int DEVIATION = (white - black) / 5;
		blackBorder = black + DEVIATION;
		whiteBorder = white - DEVIATION;

		// draw results on screen (TEST - KAN LATER WEG)
		LCD.clear();
		LCD.drawString("Wit:", 0, 0);
		LCD.drawInt(white, 4, 0);
		LCD.drawString("Zwart:", 0, 1);
		LCD.drawInt(black, 6, 1);
		LCD.drawString("Baan van xx tot xx", 0, 2);
		LCD.drawInt((black + DEVIATION), 9, 2);
		LCD.drawInt(white, 16, 2);
		LCD.drawString("Deviation:", 0, 3);
		LCD.drawInt(DEVIATION, 10, 3);

		// check if two color have been detected, recalibrate if necessary
		if (white / black > 0.90 && white / black < 1.10) {
			LCD.drawString("Geen goede meting gedaan!", 0, 4);
			Sound.beep();
			Delay.msDelay(2000);
			LCD.clear();
			calibrateColors();
		}

	}

	public static ArrayList<Float> getRoadMapA() {
		return roadMapA;
	}

	public static ArrayList<Float> getRoadMapB() {
		return roadMapB;
	}
}
