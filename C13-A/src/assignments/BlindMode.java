package assignments;

import java.util.ArrayList;
import lejos.hardware.motor.Motor;
import lejos.utility.Delay;
import models.ArmRotation;
import models.CsvFile;
import models.Finals;

public class BlindMode extends Assignment {
	private ArmRotation armRotation = new ArmRotation();
	private CsvFile csvFile = new CsvFile();
	private ArrayList<Integer> roadMapA;
	private ArrayList<Integer> roadMapB;	

	public BlindMode() {

	}

	@Override
	public void run() {
		readCsvFile();
		armRotation.rotateArm(-55);
		runParcours();
	}

	public void readCsvFile() {
		roadMapA = csvFile.readCsvFileMotor("A");
		roadMapB = csvFile.readCsvFileMotor("B");

//		roadMapA = LineFollowerRGB.getRoadMapA();
//		roadMapB = LineFollowerRGB.getRoadMapB();
	}	

	public void runParcours() {
		Motor.A.setSpeed(Finals.DEFAULT_SPEED / Finals.SLOW_FACTOR);
		Motor.B.setSpeed(Finals.DEFAULT_SPEED / Finals.SLOW_FACTOR);

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

			Motor.A.setSpeed(motorSpeedA + Finals.DEFAULT_SPEED / Finals.SLOW_FACTOR);
			Motor.B.setSpeed(motorSpeedB + Finals.DEFAULT_SPEED / Finals.SLOW_FACTOR);

			Delay.msDelay(100);
		}

		armRotation.rotateArm(0);
		Motor.A.stop();
		Motor.B.stop();
	}



}

