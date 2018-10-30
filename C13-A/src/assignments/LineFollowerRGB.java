package assignments;

// imports
import java.util.ArrayList;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.utility.Stopwatch;
import models.*;

public class LineFollowerRGB extends Assignment {

	// attributes: engine
	private int finishLineColor = 0;
	private final int DEFAULT_SPEED = 75;
	private final int REVERSE_SPEEDFACTOR = 3;
	private int white = 1;
	private int black = 100;
	private int acceleration = 10;
	private int blackBorder;
	private int whiteBorder;
	private int currentLightIntensity;
	private double speedFactor = 3.0;
	private boolean start = false;
	private boolean finished = false;
	private int trackTime;

	// attributes: color sensor
	EV3ColorSensor colorSensor;
	
	// stopwatch
	Stopwatch stopwatch = new Stopwatch();
	CsvFile csvFile = new CsvFile();
	
	// attributes: roadmap
	private static ArrayList<Integer> roadMapA = new ArrayList<>();
	private static ArrayList<Integer> roadMapB = new ArrayList<>();

	Lights lights = new Lights();

	public LineFollowerRGB(EV3ColorSensor colorSensor) {
		this.colorSensor = colorSensor;
	}

	@Override
	public void run() {
		colorSensor.setCurrentMode("RGB");
		colorSensor.setFloodlight(Color.WHITE);
		
		calibrateColors();
		rotateBackToBlackLine();
		followLine();
		
		LCD.clear();
		String message = String.format("Tracktime = %d", trackTime);
		LCD.drawString(message, 0, 7);

		
		
		colorSensor.close();
		
		csvFile.createCsvFileMotor(roadMapA, "A");
		csvFile.createCsvFileMotor(roadMapB, "B");

		waitForEnter();
	}

	public void followLine() {
		// loop until we have finished
		while (!finished) {

			// take color sample
			float[] sample = new float[colorSensor.sampleSize()];
			colorSensor.fetchSample(sample, 0);
			
			float redMeasured = sample[0];
			float greenMeasured = sample[1];
			float blueMeasured = sample[2];
			
			// check if blue
			if (start == false && ((int) (sample[0] * 255) + 2 < (int) (sample[2] * 255))) {
				Sound.beep();
				stopwatch.reset();
				start = true;
			}
			else if (start == true && ((int) (sample[0] * 255) + 2 < (int) (sample[2] * 255)) && stopwatch.elapsed()/1000 > 4.0) {
				Sound.buzz();
				trackTime = (int) (stopwatch.elapsed() / 1000);
				finished = true;
				Motor.A.stop();
				Motor.B.stop();
			}
			
			if (!finished) {
				currentLightIntensity = (int) ((redMeasured + greenMeasured + blueMeasured)/3 * 100);
				LCD.clear();
				LCD.drawString("    ", 0, 5);
				LCD.drawInt(currentLightIntensity, 0, 5);
				
	
				// NOTE eerst formule, dan FW/BW, dan default speed erbij!
				// NOTE acceleratie voor of na het flippen van de motor?
				int motorSpeedA = (int) (speedFactor * (currentLightIntensity - blackBorder));
				int motorSpeedB = (int) (speedFactor * (whiteBorder - currentLightIntensity));
	
				// if (almost) straight line, accelerate
				if (motorSpeedA / (motorSpeedB + 1) > 0.55 && motorSpeedA / (motorSpeedB + 1) < 1.45) {
					acceleration += 15;
					if (acceleration > 450)
						acceleration = 450;
					lights.brickLights(1, 150);
				} else {
					acceleration -= 45;
					if (acceleration < 15)
						acceleration = 15;
					lights.brickLights(2, 150);
				}
	
				if (motorSpeedA < 0) {
					Motor.A.backward();
					motorSpeedA = -motorSpeedA * REVERSE_SPEEDFACTOR;
					if (start)
						roadMapA.add(-motorSpeedA);
				} else {
					Motor.A.forward();
					motorSpeedA += acceleration;
					if (start)
						roadMapA.add(motorSpeedA);
				}
	
				if (motorSpeedB < 0) {
					Motor.B.backward();
					motorSpeedB = -motorSpeedB * REVERSE_SPEEDFACTOR;
					if (start)
						roadMapB.add(-motorSpeedB);
				} else {
					Motor.B.forward();
					motorSpeedB += acceleration;
					if (start)
						roadMapB.add(motorSpeedB);
				}
	
				LCD.drawInt((int) motorSpeedA, 0, 7);
				LCD.drawInt((int) motorSpeedB, 12, 7);
				LCD.drawInt((int) acceleration, 7, 7);
	
				Motor.A.setSpeed(motorSpeedA + DEFAULT_SPEED);
				Motor.B.setSpeed(motorSpeedB + DEFAULT_SPEED);
				Delay.msDelay(100);
			}
		}
	}

	private void rotateBackToBlackLine() {
		// rotate back
		Motor.A.backward();
		Motor.B.backward();
		Motor.A.setSpeed(80);
		Motor.B.setSpeed(80);

		// find the grey line
		boolean greyLineFound = false;
		while (!greyLineFound) {

			float[] sample = new float[colorSensor.sampleSize()];
			colorSensor.fetchSample(sample, 0);
			float redMeasured = sample[0];
			float greenMeasured = sample[1];
			float blueMeasured = sample[2];						
			currentLightIntensity = (int) ((redMeasured + greenMeasured + blueMeasured)/3 * 100);

			if (currentLightIntensity < blackBorder) {
				greyLineFound = true;				
				LCD.drawString("Grijze lijn gevonden!", 0, 4);
			}
		}
		Motor.A.stop();
		Motor.B.stop();		
	}

	public void calibrateColors() {
		// local variables
		ArrayList<Float> calibrationValues = new ArrayList<>();
		final int TEST_SAMPLES = 15;
		colorSensor.setCurrentMode("RGB");

		// stand still
		Motor.A.stop();
		Motor.B.stop();

		// make test reading on finish line
		finishLineColor = 0;
		lights.brickLights(1, 150);
		while (finishLineColor == 0) {
			float[] sample = new float[colorSensor.sampleSize()];
			colorSensor.fetchSample(sample, 0);
			// determine color of finish line
			if ((int) (sample[0] * 255) + 2 < (int) (sample[2] * 255)) {
				finishLineColor = 1; // blauw
			} else if ((int) (sample[0] * 255) - 30 > (int) (sample[2] * 255) && (sample[1] * 255 < 50)) {
				finishLineColor = 2; // rood
			} else {
				finishLineColor = 0;
				lights.brickLights(2, 150);
			}
			Delay.msDelay(200);
			lights.brickLights(0, 150);
		}

		// drive backwards, make black readings
		Motor.A.backward();
		Motor.B.backward();
		Motor.A.setSpeed(150);
		Motor.B.setSpeed(150);

		boolean testingDone = false;
		// make test readings
		while (!testingDone) {
			// add test sample then wait
			float[] sample = new float[colorSensor.sampleSize()];
			colorSensor.fetchSample(sample, 0);
			float redMeasured = sample[0];
			float greenMeasured = sample[1];
			float blueMeasured = sample[2];
			currentLightIntensity = (int) ((redMeasured + greenMeasured + blueMeasured) / 3 * 100);
			calibrationValues.add((redMeasured + greenMeasured + blueMeasured) / 3 * 100);
			Delay.msDelay(200);
			// continue until number of samples is collected
			if (calibrationValues.size() >= TEST_SAMPLES) {
				testingDone = true;
				Motor.A.stop();
				Motor.B.stop();
			}
		}

		// rotate inward (leftward)
		Motor.A.setSpeed(150);
		Motor.B.setSpeed(150);
		Motor.A.backward();
		Motor.B.forward();
		Delay.msDelay(750);
		Motor.A.stop();
		Motor.B.stop();

		// drive forwards, make white readings
		Motor.A.setSpeed(125);
		Motor.B.setSpeed(125);
		Motor.A.forward();
		Motor.B.forward();

		testingDone = false;
		// make test readings
		while (!testingDone) {
			// add test sample then wait
			float[] sample = new float[colorSensor.sampleSize()];
			colorSensor.fetchSample(sample, 0);
			float redMeasured = sample[0];
			float greenMeasured = sample[1];
			float blueMeasured = sample[2];
			currentLightIntensity = (int) ((redMeasured + greenMeasured + blueMeasured) / 3 * 100);
			calibrationValues.add((redMeasured + greenMeasured + blueMeasured) / 3 * 100);
			Delay.msDelay(100);
			// continue until number of samples is collected
			if (calibrationValues.size() * 0.5 >= TEST_SAMPLES) {
				testingDone = true;
				Motor.A.stop();
				Motor.B.stop();
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
		final int DEVIATION = ((white - black) / 4);
		blackBorder = black + DEVIATION;
		whiteBorder = white - DEVIATION;
	}


	
	public void waitForEnter() {
		boolean doorgaan = false;		
		int pressedButton;
		while(!doorgaan) {
			Delay.msDelay(100);
			pressedButton = Button.waitForAnyEvent();		
		if (pressedButton == Button.ID_ENTER)
			doorgaan = true;
		}
	}

	public static ArrayList<Integer> getRoadMapA() {
		return roadMapA;
	}

	public static ArrayList<Integer> getRoadMapB() {
		return roadMapB;
	}
}
