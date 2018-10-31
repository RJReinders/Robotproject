package models;

import lejos.hardware.motor.Motor;

public class ArmRotation {
	
	int motorSpeed = 100;
	
	public void rotateArm(int rotation) {
		
		int highSpeedRotation = (int) (rotation * 0.8);
		int lowSpeedRotation = rotation;
		// highspeed mode to 80%
		Motor.C.setSpeed(100);
		Motor.C.rotateTo(highSpeedRotation);
		// lowspeed mode to 100%
		Motor.C.setSpeed(10);
		Motor.C.rotateTo(lowSpeedRotation);
		Motor.C.stop();

		
	}
	

}
