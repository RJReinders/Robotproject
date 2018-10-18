package models;

import lejos.hardware.Button;
import lejos.utility.Delay;

public class Lights {
	
	public Lights() {
		super();
	}
	
	public void brickLights(int pattern, int delay) {

		Button.LEDPattern(pattern);
		Delay.msDelay(delay);
		
	}

}
