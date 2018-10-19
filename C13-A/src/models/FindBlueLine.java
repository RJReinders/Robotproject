package models;

import lejos.hardware.Sound;
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

		//while(!finished) {
		
			colorSensor.setCurrentMode("ColorID");
			
			currentColor = colorSensor.getColorID();
			

			if (currentColor == BLUE && start == false) {
				stopwatch.reset();
				start = true;
				Sound.beep();
				Delay.msDelay(1000);
				System.out.println(colorSensor.getRGBMode());
				
			} else if (currentColor == BLUE) {
				Sound.beep();
				trackTime = (int) (stopwatch.elapsed()/1000);
				System.out.println(trackTime);
				finished = true;
				System.out.println(colorSensor.getRGBMode());
				
			}
			
			colorSensor.setCurrentMode("Red");
			
			//Delay.msDelay(200);
		//}
		
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
