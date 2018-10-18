package models;

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
	boolean start;
	Stopwatch stopwatch = new Stopwatch();
	boolean finished = false;
	int trackTime;
	
	public FindBlueLine(EV3ColorSensor colorSensor) {
		this.colorSensor = colorSensor;
	}
	
	@Override
	public void run() {

		while(!finished) {
		
			colorSensor.setCurrentMode("ColorID");
			currentColor = colorSensor.getColorID();
			
			if (currentColor == BLUE && start == false) {
				stopwatch.reset();
				start = true;
				Delay.msDelay(2000);
			} else if (currentColor == BLUE) {
				trackTime = stopwatch.elapsed();
				System.out.println(trackTime);
			}
			
			colorSensor.setCurrentMode("Red");
			
			Delay.msDelay(500);
		}
	}
	
	public boolean checkBlue() {
		return true;
	}
	
	public void endThread() {
		
	}
	
	public void startTimer() {
		
	}
	
	public void endTimer() {
		
	}
	
}
