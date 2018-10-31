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

public class FollowMe extends Thread {

	private static EV3IRSensor irSensor;
	Lights lights = new Lights();
	CsvFile csvFile = new CsvFile();
	boolean stopThread = false;
	int hoekMeting = 0;

	public void run() {

		irSensor = new EV3IRSensor(SensorPort.S1);
		// follow();
		while (!stopThread) {
			followbeacon();
		}
		irSensor.close();

	}

	private void follow() {

		SampleProvider afstand = irSensor.getDistanceMode();
		int distanceValue = 0;
		boolean stopped = false;
		float[] sample = new float[afstand.sampleSize()];

		int motorSpeed = 900;


		lights.brickLights(2, 50);

		System.out.println("Start meting");

		while (!stopped) {

			System.out.println("In while");

			afstand.fetchSample(sample, 0);
			distanceValue = (int) sample[0];

			lights.brickLights(0, 0);
			
			LCD.drawString("Kleine afstand:", 4, 3);
			LCD.drawInt(distanceValue, 4,4);

			if (distanceValue == 0) {
				stopped = true;
				// Motor.A.stop();
				// Motor.B.stop();

			} else if (distanceValue >= 45) {

//				Motor.A.setSpeed(motorSpeed * 2);
//				Motor.B.setSpeed(motorSpeed * 2);
				lights.brickLights(5, 50);

			} else {
				
				LCD.drawString("Kleine afstand:", 4, 2);
				// Motor.A.setSpeed(motorSpeed * 3);
				// Motor.B.setSpeed(motorSpeed * 3);
				lights.brickLights(5, 50);


			}
		}
	}

	private void followbeacon() {

		int afstandMeting = 0;

		SampleProvider meting = irSensor.getSeekMode();

		float[] sample = new float[meting.sampleSize()];

		meting.fetchSample(sample, 0);

		hoekMeting = (int) sample[0];
		afstandMeting = (int) sample[1];

		LCD.drawString("     ", 1, 6);
		LCD.drawString("     ", 1, 7);
		LCD.drawInt(hoekMeting, 1, 6);
		LCD.drawInt(afstandMeting, 1, 7);
		Delay.msDelay(500);

	}

	public void endThread() {
		stopThread = true;
	}

	public int getDeviation() {
		return hoekMeting;
	}
}
