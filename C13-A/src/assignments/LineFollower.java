package assignments;

import java.util.ArrayList;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import models.FindBlueLine;

public class LineFollower extends Assignment {

	
	private final int DEFAULT_SPEED = 50;
	private int white = 50;
	private int black = 6;
	private int min = black + 7;
	private int max = white - 7;
	private float currentLightIntensity;
	// private int Mspeed = 10;
	private int Mspeed2 = 10;
	EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S3);
	SampleProvider sp = colorSensor.getRedMode();
	float[] lightIntensity = new float[sp.sampleSize()];
	
	private static ArrayList<Float> roadMapA = new ArrayList<>();
	private static ArrayList<Float> roadMapB = new ArrayList<>();
	
	FindBlueLine findBlueLine = new FindBlueLine(colorSensor);
	
	public LineFollower() {

	}
	
	@Override
	public void run() {
		
		followLine();
	
	}

	public void followLine() {
		Motor.A.setSpeed(DEFAULT_SPEED);
		Motor.B.setSpeed(DEFAULT_SPEED);

		int i = 0;
		
		findBlueLine.start();
		
		while (!findBlueLine.getFinished()) {
			Motor.A.forward();
			Motor.B.forward();
		
			sp.fetchSample(lightIntensity, 0);
			currentLightIntensity = lightIntensity[0] * 100;

			float motorSpeedA = Mspeed2 * (currentLightIntensity - min);
			float motorSpeedB = Mspeed2 * (max - currentLightIntensity);
		
			roadMapA.add(motorSpeedA);
			roadMapB.add(motorSpeedB);
			
			//System.out.println(roadMapA.get(i));
			//System.out.println(roadMapB.get(i));
			
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
	}

	
	public static ArrayList<Float> getRoadMapA() {
		return roadMapA;
	}

	public static ArrayList<Float> getRoadMapB() {
		return roadMapB;
	}
	
	
}
