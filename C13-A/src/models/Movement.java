package models;

import lejos.hardware.motor.Motor;

public class Movement {

	public void run() {
		
	}
		
	public boolean straight(int speed) {
		
		Motor.A.forward();
		Motor.B.forward();
		Motor.A.setSpeed(speed);
		Motor.B.setSpeed(speed);

		return true;
		
	}
	
public boolean curve(int speed) {
		
		Motor.A.forward();
		Motor.B.backward();
		Motor.A.setSpeed(speed);
		Motor.B.setSpeed(speed);

		return true;
		
	}
		
}
