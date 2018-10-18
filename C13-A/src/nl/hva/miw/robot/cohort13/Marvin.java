package nl.hva.miw.robot.cohort13;

import assignments.Assignment;
import assignments.LineFollower;
import assignments.BlindMode;

import models.TouchStop;
import models.CsvFile;
import models.Lights;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.Keys;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.utility.Delay;



public class Marvin {

	Brick brick;
	TouchStop stopknop = new TouchStop();
	Assignment lineFollower = new LineFollower();
	Assignment blindMode = new BlindMode();
	CsvFile csvFile = new CsvFile();
	Lights lights = new Lights();

	public Marvin() {
		super();
		brick = LocalEV3.get();
	}

	public static void main(String[] args) throws Exception {
		Marvin marvin = new Marvin();
		marvin.run();
	}

	private void run() {
		
		stopknop.start();
		

		while (true) {
			waitForKeyPress();
			
		}

	}

	public void waitForKeyPress() {

		Sound.twoBeeps();

		System.out.println("Menu");
		System.out.println("L = LineFollower");
		System.out.println("R = Blindmode");
		System.out.println("U = CsvFile");
		System.out.println("D = Lights");

		int pressedButton = Button.waitForAnyEvent();

		if (pressedButton == Button.ID_LEFT) {
			System.out.println("Links");
			lineFollower.run();
		} else if (pressedButton == Button.ID_RIGHT) {
			System.out.println("Rechts");
			blindMode.run();
		} else if (pressedButton == Button.ID_UP) {
			System.out.println("Boven");
			csvFile.check();
		} else if (pressedButton == Button.ID_DOWN) {
			System.out.println("Onder");
			lights.brickLights(0, 150);
		} else if (pressedButton == Button.ID_ENTER) {
			System.out.println("Enter");
		} else if (pressedButton == Button.ID_ESCAPE) {
			stopknop.endThread();
			System.exit(0);
		}

		Delay.msDelay(2000);
	}

}
