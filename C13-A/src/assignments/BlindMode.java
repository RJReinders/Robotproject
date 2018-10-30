package assignments;

import java.util.ArrayList;

import lejos.hardware.motor.Motor;
import lejos.utility.Delay;
import models.ArmRotation;

public class BlindMode extends Assignment {

	ArmRotation armRotation = new ArmRotation();
	
	private final int DEFAULT_SPEED = 75;
		
	public BlindMode() {
		
	}
	
	@Override
	public void run() {
		ArrayList<Integer> roadMapA = LineFollowerRGB.getRoadMapA();
		ArrayList<Integer> roadMapB = LineFollowerRGB.getRoadMapB();

		armRotation.rotateArm(-40);
		
		Motor.A.setSpeed(DEFAULT_SPEED);
		Motor.B.setSpeed(DEFAULT_SPEED);

		for (int i = 0; i < roadMapA.size(); i++) {
			Motor.A.forward();
			Motor.B.forward();

			int motorSpeedA = roadMapA.get(i);
			int motorSpeedB = roadMapB.get(i);

			System.out.println(roadMapA.get(i));
			System.out.println(roadMapB.get(i));
			
			if (motorSpeedA < 0) {
				Motor.A.backward();
				motorSpeedA = -motorSpeedA;
			} else {
				Motor.A.forward();
			}

			if (motorSpeedB < 0) {
				Motor.B.backward();
				motorSpeedB = -motorSpeedB;
			} else {
				Motor.B.forward();
			}
		
			Motor.A.setSpeed(motorSpeedA + DEFAULT_SPEED);
			Motor.B.setSpeed(motorSpeedB + DEFAULT_SPEED);
			
			Delay.msDelay(100);
			
		}
		
		Motor.A.stop();
		Motor.B.stop();
		
	}

}
