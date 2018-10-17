package assignments;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class LineFollower extends Assignment {

	
	private final int DEFAULT_SPEED = 50;
	private int min = 12;
	private int max = 40;
	private float currentLightIntensity;
	// private int Mspeed = 10;
	private int Mspeed2 = 10;
	EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S3);
	SampleProvider sp = colorSensor.getRedMode();
	float[] lightIntensity = new float[sp.sampleSize()];

	public LineFollower() {

	}
	
	@Override
	public void run() {
	
		Motor.A.setSpeed(DEFAULT_SPEED);
		Motor.B.setSpeed(DEFAULT_SPEED);

		while (true) {
			Motor.A.forward();
			Motor.B.forward();
		
			sp.fetchSample(lightIntensity, 0);
			currentLightIntensity = lightIntensity[0] * 100;

			float motorSpeedA = Mspeed2 * (currentLightIntensity - min);
			float motorSpeedB = Mspeed2 * (max - currentLightIntensity);
		
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
		
			System.out.println(currentLightIntensity);
		
		}
	
		// Motor.A.stop();
		// Motor.B.stop();
	
	}
	
}
