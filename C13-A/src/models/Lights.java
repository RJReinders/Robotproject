package models;

import java.io.File;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class Lights {
	
	public Lights() {
		super();
	}
	
	public void brickLights(int pattern, int delay) {

		Button.LEDPattern(pattern);
		Delay.msDelay(delay);
		File wavFile = new File("we.wav");
		Sound.playSample(wavFile, 60);
	}

}
