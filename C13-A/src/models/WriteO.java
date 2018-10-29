package models;

import lejos.hardware.motor.Motor;
import lejos.utility.Delay;

public class WriteO {
	
	public void run() {
		schrijfO();
	}
	
	private void schrijfO() {
		
		int motorSpeed = 5;
		int factor = 10;
		Motor.A.forward();
		Motor.B.forward();
		
		Motor.A.setSpeed(motorSpeed);
		Motor.B.setSpeed(motorSpeed*factor);
		
		Delay.msDelay(4000);
		
		Motor.A.stop();
		Motor.B.stop();
		
	}

}
