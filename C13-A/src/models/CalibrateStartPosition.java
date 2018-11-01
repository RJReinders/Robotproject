package models;

import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class CalibrateStartPosition extends Thread {

	// variables
	private EV3IRSensor iRSensor;
	private boolean stopThread;
	private int directionDeviation;

	public CalibrateStartPosition(Sensors sensors) {
		this.iRSensor = sensors.getIRSensor();
		stopThread = false;
		directionDeviation = 0;
	}

	public void run() {
		while (!stopThread) {
			checkBeacon();
		}
		iRSensor.close();
	}

	private void checkBeacon() {
		SampleProvider spIR = iRSensor.getSeekMode();
		float[] sample = new float[spIR.sampleSize()];
		spIR.fetchSample(sample, 0);

		directionDeviation = (int) sample[0];

		LCD.drawString("Hoekafwijking:", 0, 7);
		LCD.drawString("   ", 15, 7);
		LCD.drawInt(directionDeviation, 15, 7);
		Delay.msDelay(500);
	}

	public void endThread() {
		stopThread = true;
	}

	public int getDirectionDeviation() {
		return directionDeviation;
	}
	
}
