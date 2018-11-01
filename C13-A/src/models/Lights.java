package models;

import lejos.hardware.Button;

public class Lights {
	
	public Lights() {
		super();
	}
	
	public void brickLights(int pattern) {
		Button.LEDPattern(pattern);
	}

}
