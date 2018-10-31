package assignments;

import java.util.ArrayList;

import lejos.hardware.motor.Motor;
import lejos.utility.Stopwatch;
import models.ArmRotation;

public class BlindMode extends Assignment {

	ArmRotation armRotation = new ArmRotation();
	private Stopwatch stopwatch;
	
	private final int DEFAULT_SPEED = 150;
		
	public BlindMode() {
		
	}
	
	@Override
	public void run() {
		
		
		ArrayList<Integer> roadMapA = LineFollowerRGB.getRoadMapA();
		ArrayList<Integer> roadMapB = LineFollowerRGB.getRoadMapB();
		ArrayList<Integer> roadMapTime = LineFollowerRGB.getRoadMapTime();

		armRotation.rotateArm(-30);
		
		Motor.A.setSpeed(DEFAULT_SPEED);
		Motor.B.setSpeed(DEFAULT_SPEED);

		stopwatch = new Stopwatch();
		
		int i = 0;
		
		while (i < roadMapTime.size()) {
			int timeStamp = roadMapTime.get(i);
			if (stopwatch.elapsed() > timeStamp) {
				int motorSpeedA = roadMapA.get(i+1);
				int motorSpeedB = roadMapB.get(i+1);
	
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
				i++;
			}
			
		}
		
		armRotation.rotateArm(0);
		Motor.A.stop();
		Motor.B.stop();
		
	}

}
