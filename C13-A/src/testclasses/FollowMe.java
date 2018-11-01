package testclasses;

import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import models.Lights;
import models.Sensors;

public class FollowMe {

	// variables
	private EV3IRSensor iRSensor;
	private Lights lights;
	private boolean stopThread;
	private int directionDeviation;

	public FollowMe(Sensors sensors) {
		this.iRSensor = sensors.getIRSensor();
		lights = new Lights();
		stopThread = false;
		directionDeviation = 0;
	}

	public void run() {

		follow();

	}

	private void follow() {

		SampleProvider afstand = iRSensor.getDistanceMode();
		int distanceValue = 0;
		boolean stopped = false;
		float[] sample = new float[afstand.sampleSize()];

		int motorSpeed = 900;

		lights.brickLights(2);

		System.out.println("Start meting");

		while (!stopped) {

			System.out.println("In while");

			afstand.fetchSample(sample, 0);
			distanceValue = (int) sample[0];

			lights.brickLights(0);

			LCD.drawString("Kleine afstand:", 4, 3);
			LCD.drawInt(distanceValue, 4, 4);

			if (distanceValue == 0) {
				stopped = true;
				// Motor.A.stop();
				// Motor.B.stop();

			} else if (distanceValue >= 45) {

				// Motor.A.setSpeed(motorSpeed * 2);
				// Motor.B.setSpeed(motorSpeed * 2);
				lights.brickLights(5);

			} else {

				LCD.drawString("Kleine afstand:", 4, 2);
				// Motor.A.setSpeed(motorSpeed * 3);
				// Motor.B.setSpeed(motorSpeed * 3);
				lights.brickLights(5);

			}
		}
	}

}
