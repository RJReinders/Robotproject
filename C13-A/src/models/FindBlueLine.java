package models;

import lejos.hardware.Sound;
import models.*;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.utility.Stopwatch;

public class FindBlueLine extends Thread {
	boolean blue;
	EV3ColorSensor colorSensor;
	int NONE = 0;
	int BLACK = 1;
	int BLUE = 2;
	int GREEN = 3;
	int YELLOW = 4;
	int RED = 5;
	int WHITE = 6;
	int BROWN = 7;
	int currentColor;
	boolean start = false;
	Stopwatch stopwatch = new Stopwatch();
	boolean finished = false;
	int trackTime;

	public FindBlueLine(EV3ColorSensor colorSensor) {
		this.colorSensor = colorSensor;
	}

	@Override
	public void run() {
/*
		while (!finished) {
			int blueGemeten;
			SampleProvider colorRGB = colorSensor.getRGBMode();
			float[] sample = new float[colorRGB.sampleSize()];

			colorRGB.fetchSample(sample, 0);
			blueGemeten = (int) (sample[2] * 10000); // gemeten

			if (blueGemeten < 1250 && blueGemeten > 750 && start == false) {
				stopwatch.reset();
				start = true;
				Sound.beep();
				Delay.msDelay(1000);

			} else if (blueGemeten < 1250 && blueGemeten > 750) {
				Sound.buzz();
				trackTime = (int) (stopwatch.elapsed() / 1000);
				System.out.println(trackTime);
				finished = true;

			}
			colorSensor.setCurrentMode("Red");
			
		}
*/
	}

	public boolean getFinished() {
		return finished;
	}

	public int getTrackTime() {
		return trackTime;
	}

	public void endThread() {
		finished = true;
	}

}
