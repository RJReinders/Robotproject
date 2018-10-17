package models;

import lejos.hardware.motor.Motor;
import lejos.utility.Delay;

public class motor {

	public void bewegingVoertuig(int links, int rechts, int duur) {

		if (links > 0 || rechts > 0) {
			Delay.msDelay(duur);
			Motor.A.forward();
			Motor.B.forward();
		} else if (links < 0) {
			Delay.msDelay(duur);
			Motor.A.forward();
			Motor.B.forward();
		}
	}
}
