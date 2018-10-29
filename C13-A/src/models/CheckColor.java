package models;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

import models.Lights;

public class CheckColor {

	private static EV3ColorSensor colorSensor;
	private static Lights lights = new Lights();

	public void run() {
		LCD.clear();
		colorSensor = new EV3ColorSensor(SensorPort.S2);
		colorSensor.setFloodlight(false);
		checkRgb();
		// checkFloodLight();
	}

	public void checkRgb() {

		colorSensor.setCurrentMode("RGB");
		colorSensor.setFloodlight(Color.WHITE);
<<<<<<< HEAD
=======
		colorSensor.setFloodlight(Color.BLUE);
		colorSensor.setFloodlight(Color.GREEN);
		colorSensor.setFloodlight(Color.RED);
		

>>>>>>> beta
		int redGemeten;
		int greenGemeten;
		int blueGemeten;
		int teller = 0;

		float[] sample = new float[colorSensor.sampleSize()];
		boolean stop = false;

		while (!stop) {

			colorSensor.fetchSample(sample, 0);

			redGemeten = (int) (sample[0] * 255);
			greenGemeten = (int) (sample[1] * 255);
			blueGemeten = (int) (sample[2] * 255);

			// if ( blueGemeten < 1250 && blueGemeten > 750 ) {
			// lights.brickLights(2, 150);
			// Sound.beep();
			// System.out.println("Vanuit BLauw ? R: " + sample[0] + " G: " + sample[1] + "
			// B: " + blueGemeten);
			// stop = true;
			//
			// } else {
			//
			// System.out.println("Geen blauw");
			// }

			System.out.println("Rood: " + redGemeten + " / Groen: " + greenGemeten + " / Blauw: " + blueGemeten + "\n");

			if (teller == 5) {
				stop = true;
			}
			Delay.msDelay(2000);
			teller++;
		}

		colorSensor.close();
	}

	public void checkFloodLight() {

		for (int i = 0; i > 5; i++) {
			colorSensor.setFloodlight(Color.BLACK);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.BLUE);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.BROWN);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.CYAN);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.DARK_GRAY);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.GRAY);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.GREEN);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.LIGHT_GRAY);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.MAGENTA);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.ORANGE);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.PINK);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.RED);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.WHITE);
			Delay.msDelay(1000);
			colorSensor.setFloodlight(Color.YELLOW);
			Delay.msDelay(1000);
		}

	}
}

/*
 * http://stemrobotics.cs.pdx.edu/node/5200
 */
