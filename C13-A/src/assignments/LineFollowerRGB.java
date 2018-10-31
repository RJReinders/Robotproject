package assignments;

import java.util.ArrayList;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.utility.Delay;
import lejos.utility.Stopwatch;
import models.*;

public class LineFollowerRGB extends Assignment {

	// static variables
	private static ArrayList<Integer> roadMapA; 
	private static ArrayList<Integer> roadMapB;

	// final variables
	private final double SPEEDFACTOR = 4.0; // oud: 3.0
	private final int REVERSE_SPEEDFACTOR = 3;
	private final int DEFAULT_SPEED = 150; // oud: 75
	private final int DIFFERENCE_BLUE_OVER_RED = 2;
	private final int DIFFERENCE_RED_OVER_BLUE = 30;
	private final int MAXIMUM_GREEN_IN_RED = 50;
	private final int SAMPLE_TO_RGB = 255;

	// variables
	private EV3ColorSensor colorSensor;
	private Sensors sensors;
	private Stopwatch stopwatch;
	private Lights lights;
	private CsvFile csvFile;
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

	/* Test variables
	private int arrayACounter;
	private int arrayBCounter;
	*/

	public LineFollowerRGB(Sensors sensors) {
		this.colorSensor = sensors.getColorSensor();
		this.sensors = sensors;
		stopwatch = new Stopwatch();
		lights = new Lights();
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
		sensors.closeColorSensor();
		displayTrackTime();
	}

	private void calibrateColors() {
		ColorCalibrator colorCalibrator = new ColorCalibrator(sensors);
		colorCalibrator.run();
		finishLineColor = colorCalibrator.getFinishLineColor();
		blackBorder = colorCalibrator.getBlackBorder();
		whiteBorder = colorCalibrator.getWhiteBorder();
	}
	
	public void followLine() {
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
			// check if blue
				if (start == false && ((int) (redMeasured * SAMPLE_TO_RGB) + DIFFERENCE_BLUE_OVER_RED < (int) (blueMeasured * SAMPLE_TO_RGB))) {
					Sound.beep();
					stopwatch.reset();
					start = true;
				}
				else if (start == true && ((int) (redMeasured * SAMPLE_TO_RGB) + DIFFERENCE_BLUE_OVER_RED < (int) (blueMeasured * SAMPLE_TO_RGB)) && stopwatch.elapsed()/1000 > 4.0) {
					Sound.buzz();
					trackTime = (stopwatch.elapsed() / 1000.0);
					finished = true;
					Motor.A.stop();
					Motor.B.stop();
				}
				
			} else {
			// check if red	
				if (start == false && ((int) (blueMeasured * SAMPLE_TO_RGB) + DIFFERENCE_RED_OVER_BLUE < (int) (redMeasured * SAMPLE_TO_RGB) && (greenMeasured * SAMPLE_TO_RGB < MAXIMUM_GREEN_IN_RED))) {
					Sound.beep();
					stopwatch.reset();
					start = true;
				}
				else if (start == true && ((int) (blueMeasured * SAMPLE_TO_RGB) + DIFFERENCE_RED_OVER_BLUE < (int) (redMeasured * SAMPLE_TO_RGB) && (greenMeasured * SAMPLE_TO_RGB < MAXIMUM_GREEN_IN_RED)) && stopwatch.elapsed()/1000 > 4.0) {
					Sound.buzz();
					trackTime = (stopwatch.elapsed() / 1000.0);
					finished = true;
					Motor.A.stop();
					Motor.B.stop();
				}
			}
			
			if (!finished) {
				currentLightIntensity = (int) ((redMeasured + greenMeasured + blueMeasured)/3 * 100);
				LCD.clear();
				LCD.drawString("    ", 0, 5);
				LCD.drawInt(currentLightIntensity, 0, 5);
				
	
				// NOTE eerst formule, dan FW/BW, dan default speed erbij!
				// NOTE acceleratie voor of na het flippen van de motor?
				int motorSpeedA = (int) (SPEEDFACTOR * (currentLightIntensity - blackBorder));
				int motorSpeedB = (int) (SPEEDFACTOR * (whiteBorder - currentLightIntensity));
	
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
					if (start) {
						//arrayACounter++;
						roadMapA.add(-motorSpeedA);
					}
				} else {
					Motor.A.forward();
					motorSpeedA += acceleration;
					if (start) {
						//arrayACounter++;
						roadMapA.add(motorSpeedA);
					}
				}
	
				if (motorSpeedB < 0) {
					Motor.B.backward();
					motorSpeedB = -motorSpeedB * REVERSE_SPEEDFACTOR;
					if (start) {
						//arrayBCounter++;
						roadMapB.add(-motorSpeedB);
					}
				} else {
					Motor.B.forward();
					motorSpeedB += acceleration;
					if (start) {
						//arrayBCounter++;
						roadMapB.add(motorSpeedB);
					}
				}
	
				LCD.drawInt((int) motorSpeedA, 0, 7);
				LCD.drawInt((int) motorSpeedB, 12, 7);
				LCD.drawInt((int) acceleration, 7, 7);
	
				Motor.A.setSpeed(motorSpeedA + DEFAULT_SPEED);
				Motor.B.setSpeed(motorSpeedB + DEFAULT_SPEED);
				Delay.msDelay(50);
			}
		}
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
	
	public void createCsvFiles() {
		csvFile.createCsvFileMotor(roadMapA, "A");
		csvFile.createCsvFileMotor(roadMapB, "B");

	}
	
	public void displayTrackTime() {
		LCD.clear();
		String stringTrackTime = String.format("Tracktime = %.2f", trackTime);
		LCD.drawString(stringTrackTime, 0, 0);

		/* print Array information 
		String stringRoadMapASize = String.format("A size = %d", roadMapA.size());
		LCD.drawString(stringRoadMapASize, 0, 2);
		String stringArrayCounterA = String.format("A counter = %d", arrayACounter);
		LCD.drawString(stringArrayCounterA, 0, 3);
		String stringRoadMapBSize = String.format("B size = %d", roadMapB.size());
		LCD.drawString(stringRoadMapBSize, 0, 4);
		String stringArrayCounterB = String.format("B counter = %d", arrayBCounter);
		LCD.drawString(stringArrayCounterB, 0, 5);
		*/

		waitForEnter();
	}
}
