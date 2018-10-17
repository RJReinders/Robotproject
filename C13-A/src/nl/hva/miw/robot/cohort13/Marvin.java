package nl.hva.miw.robot.cohort13;

// imports
import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import models.*;

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
		// start of run

		TextLCD display = brick.getTextLCD();
		display.drawString("Drukknop test", 0, 3);
		display.drawString("Druk op enter.", 0, 4);
		waitForKey(Button.ENTER);

		TouchStop stopknop = new TouchStop();
		stopknop.start();
		for (int i = 0; i < 600; i++) {

			display.clear();

			display.drawString("False", 0, 0);

			Delay.msDelay(100);
		}

		stopknop.endThread();
		// end of run
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
