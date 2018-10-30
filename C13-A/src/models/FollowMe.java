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
		LCD.clear();
		followbeacon();

	}

	private void follow() {

		SampleProvider afstand = irSensor.getDistanceMode();
		int distanceValue = 0;
		boolean stopped = false;
		float[] sample = new float[afstand.sampleSize()];
		Motor.A.backward();
		Motor.B.backward();
		int motorSpeed = 50;

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
				Motor.A.stop();
				Motor.B.stop();

			} else if (distanceValue >= 45) {

				Motor.A.setSpeed(motorSpeed * 2);
				Motor.B.setSpeed(motorSpeed * 2);
				lights.brickLights(5, 50);

			} else {
				
				LCD.drawString("Kleine afstand:", 4, 2);
				Motor.A.setSpeed(motorSpeed);
				Motor.B.setSpeed(motorSpeed);
				lights.brickLights(1, 50);
			}
		}
	}

	
	private void followbeacon() {

		int hoekMeting = 0;
		int afstandMeting = 0;
//		Motor.A.backward();
//		Motor.B.backward();
//		int motorSpeed = 100;
//		Motor.A.setSpeed(motorSpeed);
//		Motor.B.setSpeed(motorSpeed);
//	
		SampleProvider meting = irSensor.getSeekMode();

		while(Button.ESCAPE.isUp()) {

			float[] sample = new float[meting.sampleSize()];

			meting.fetchSample(sample, 0);

			hoekMeting = (int) sample[0];
			afstandMeting = (int) sample[1];
			
			// zet Robot recht voor de beacon en verkleinen afstand
			
			LCD.drawInt(afstandMeting, 1,2);
			LCD.drawInt(hoekMeting, 1,4);
			Delay.msDelay(100);

			if (hoekMeting > 1) {
//				Motor.A.stop();
//				Motor.B.backward();
			} else if (hoekMeting < -1) {
//				Motor.B.stop();
//				Motor.A.backward();
			} else {
				
				if (afstandMeting < Integer.MAX_VALUE) {
//					Motor.A.backward();
//					Motor.B.backward();
				} else {
//					Motor.A.stop();
//					Motor.B.stop();
				}
			}
		}
		
//		Motor.A.close();
//		Motor.B.close();
		irSensor.close();
		
	}
}
