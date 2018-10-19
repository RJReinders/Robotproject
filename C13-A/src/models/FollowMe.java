package models;

import lejos.hardware.Sound;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import models.Lights;

public class FollowMe {

	private static EV3IRSensor irSensor;
	Lights lights = new Lights();

	public void run() {

		irSensor = new EV3IRSensor(SensorPort.S1);
		//follow();
		followbeacon();

	}
	
	
	private void follow() {
		
		SampleProvider afstand = irSensor.getDistanceMode();
		int distanceValue = 0;
		boolean stopped = false;
		float[] sample = new float[afstand.sampleSize()];

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
			} else if (distanceValue >= 45) {
				lights.brickLights(5, 50);

			} else {
				lights.brickLights(1, 50);
			}
		}
	}
	
	private void followbeacon() {
		
		int beaconInfo1H = 0;
		int beaconInfo1D = 0;
		
		SampleProvider afstand = irSensor.getSeekMode();
		
		final int iteration_threshold = 50;
		for(int i = 0; i <= iteration_threshold; i++) {
			
			float [] sample = new float[afstand.sampleSize()];

			System.out.println(afstand.sampleSize() + "Hierzo");
			
			// afstand.fetchSample(sample, 0);

			for (float meting : sample ) {
				System.out.println("Beacon Channel 1:" + meting);
			}
			
		    beaconInfo1H = (int)sample[0];
			beaconInfo1D = (int)sample[1];

			System.out.println("Iteration: {}" + i);
			System.out.println("Beacon Channel 1: Heading: {}, Distance: {}" + beaconInfo1H + beaconInfo1D);

			Delay.msDelay(1000);
		}
	}
}
