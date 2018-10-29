package nl.hva.miw.robot.cohort13;

import assignments.Assignment;
import assignments.LineFollower;
import assignments.BlindMode;
import assignments.Test;

import models.TouchStop;
import models.CsvFile;
import models.Lights;
import models.ArmRotation;
import models.FollowMe;
import models.WriteO;
import models.CheckColor;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.Keys;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.utility.Delay;

// NOTE Reinder: ik heb de stopknop en linefollower outgecomment

public class Marvin {

	Brick brick;
	// TouchStop stopknop = new TouchStop();

//	Assignment lineFollower = new LineFollower();
//	Assignment blindMode = new BlindMode();
//	CsvFile csvFile = new CsvFile();
//	Lights lights = new Lights();
//	Test testProgram = new Test();
//	ArmRotation armRotation = new ArmRotation();
//	FollowMe followme = new FollowMe();
//	WriteO writeO = new WriteO();
	CheckColor checkColor = new CheckColor();

	public Marvin() {
		super();
		brick = LocalEV3.get();
	}

	public static void main(String[] args) throws Exception {
		Marvin marvin = new Marvin();
		marvin.run();
	}

	private void run() {

		// stopknop.start();

		while (true) {
			waitForKeyPress();

		}

	}

	public void waitForKeyPress() {

		Sound.twoBeeps();

		System.out.println("Menu");
		System.out.println("L = LineFollower");
		System.out.println("R = Blindmode");
		System.out.println("U = FolowMe");
		System.out.println("D = Test");

		int pressedButton = Button.waitForAnyEvent();

		if (pressedButton == Button.ID_LEFT) {
			System.out.println("Links");
			//lineFollower.run();
		} else if (pressedButton == Button.ID_RIGHT) {
			System.out.println("Rechts");
			//blindMode.run();
		} else if (pressedButton == Button.ID_UP) {
			System.out.println("Boven");
// 			csvFile.check();
//			armRotation.rotateArm(-55);
//			Delay.msDelay(2000);
//			armRotation.rotateArm(0);
//			lights.brickLights(0, 150);
//			followme.run();
//			writeO.run();
			checkColor.run();
			} else if (pressedButton == Button.ID_DOWN) {
			System.out.println("Onder");
//			testProgram.run();
		} else if (pressedButton == Button.ID_ENTER) {
			System.out.println("Enter");
		} else if (pressedButton == Button.ID_ESCAPE) {
			//stopknop.endThread();
			System.exit(0);
		}

		Delay.msDelay(2000);
	}

}
