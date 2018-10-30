package models;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import models.Lights;
import models.CsvFile;

import java.util.ArrayList;

public class FollowMe {

	private static EV3IRSensor irSensor;
	Lights lights = new Lights();
	CsvFile csvFile = new CsvFile();

	public void run() {

		irSensor = new EV3IRSensor(SensorPort.S1);
		// follow();
		followbeacon();

	}

	private void follow() {

		SampleProvider afstand = irSensor.getDistanceMode();
		int distanceValue = 0;
		boolean stopped = false;
		float[] sample = new float[afstand.sampleSize()];
		// Motor.A.forward();
		// Motor.B.forward();
		int motorSpeed = 900;

		lights.brickLights(2, 50);

		System.out.println("Start meting");

		while (!stopped) {

			System.out.println("In while");

			afstand.fetchSample(sample, 0);
			distanceValue = (int) sample[0];

			System.out.println(distanceValue);

			lights.brickLights(0, 0);

			if (distanceValue == 0) {
				stopped = true;
				// Motor.A.stop();
				// Motor.B.stop();

			} else if (distanceValue >= 45) {

				// Motor.A.setSpeed(motorSpeed * 3);
				// Motor.B.setSpeed(motorSpeed * 3);
				lights.brickLights(5, 50);

			} else {
				// Motor.A.setSpeed(motorSpeed);
				// Motor.B.setSpeed(motorSpeed);
				lights.brickLights(1, 50);
			}
		}
	}

	
	private void followbeacon() {

		int hoekMeting = 0;
		int afstandMeting = 0;
		// Motor.A.forward();
		// Motor.B.forward();
		int motorSpeed = 400;
		// Motor.A.setSpeed(motorSpeed);
		// Motor.B.setSpeed(motorSpeed);
	
		SampleProvider meting = irSensor.getSeekMode();

		while(Button.ESCAPE.isUp()) {


			LCD.clear();
			float[] sample = new float[meting.sampleSize()];

			meting.fetchSample(sample, 0);

			hoekMeting = (int) sample[0];
			afstandMeting = (int) sample[1];
			LCD.drawInt(hoekMeting, 1, 4);
			LCD.drawInt(afstandMeting, 1, 6);
			Delay.msDelay(500);
			
			/*
			if (hoekMeting > 0) {
				Motor.A.forward();
				Motor.B.stop();
			} else if (hoekMeting < 0) {
				Motor.B.forward();
				Motor.A.stop(true);
			} else {
				if (afstandMeting < Integer.MAX_VALUE) {
					Motor.A.forward();
					Motor.B.forward();
				} else {
					Motor.A.stop();
					Motor.B.stop();
				}
			}
			*/
		}
		
		
		
		// Motor.A.close();
		// Motor.B.close();
		irSensor.close();
		
	}
}
