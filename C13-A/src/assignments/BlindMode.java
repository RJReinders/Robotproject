package assignments;

import java.util.ArrayList;

import lejos.hardware.motor.Motor;
import lejos.utility.Delay;

public class BlindMode extends Assignment {

	private final int DEFAULT_SPEED = 50;
	ArrayList<Float> roadMapA = LineFollower.getRoadMapA();
	ArrayList<Float> roadMapB = LineFollower.getRoadMapB();
		
	public BlindMode() {
		
	}
	
	@Override
	public void run() {

		Motor.A.setSpeed(DEFAULT_SPEED);
		Motor.B.setSpeed(DEFAULT_SPEED);

		for (int i = 0; i < roadMapA.size(); i++) {
			Motor.A.forward();
			Motor.B.forward();

			float motorSpeedA = roadMapA.get(i);
			float motorSpeedB = roadMapB.get(i);

			System.out.println(roadMapA.get(i));
			System.out.println(roadMapB.get(i));
			
			if (motorSpeedA < 0) {
				Motor.A.backward();
				motorSpeedA = -motorSpeedA * 4;
			} else {
				Motor.A.forward();
			}

			if (motorSpeedB < 0) {
				Motor.B.backward();
				motorSpeedB = -motorSpeedB * 4;
			} else {
				Motor.B.forward();
			}
		
			Motor.A.setSpeed(motorSpeedA);
			Motor.B.setSpeed(motorSpeedB);
			
			Delay.msDelay(100);
			
		}
		
		Motor.A.stop();
		Motor.B.stop();
		
	}

}
