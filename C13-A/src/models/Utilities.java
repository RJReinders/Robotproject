package models;

import lejos.hardware.Button;
import lejos.utility.Delay;

public class Utilities {

	public static void waitForEnter() {
		boolean doorgaan = false;
		int pressedButton;
		while (!doorgaan) {
			Delay.msDelay(100);
			pressedButton = Button.waitForAnyEvent();
			if (pressedButton == Button.ID_ENTER)
				doorgaan = true;
		}
	}
	
}
