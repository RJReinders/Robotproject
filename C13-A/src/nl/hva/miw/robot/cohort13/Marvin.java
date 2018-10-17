package nl.hva.miw.robot.cohort13;

//imports
import lejos.hardware.Brick;
import lejos.hardware.Key;
import lejos.hardware.ev3.LocalEV3;
import lejos.utility.Delay;

public class Marvin {

	// variables
	Brick brick;

	public Marvin() {
		super();
		brick = LocalEV3.get();
	}

	public static void main(String[] args) {
		Marvin marvin = new Marvin();
		marvin.run();
	}

	private void run() {

	}

	public void waitForKey(Key key) {
		while (key.isUp()) {
			Delay.msDelay(100);
		}
		while (key.isDown()) {
			Delay.msDelay(100);
		}
	}
}
