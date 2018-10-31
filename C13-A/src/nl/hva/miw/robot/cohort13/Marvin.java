package nl.hva.miw.robot.cohort13;

import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import assignments.*;
import models.*;

public class Marvin {
	// variables
	boolean running;
	Brick brick;
	Sensors sensors;
	Assignment lineFollowerRGB;
	Assignment ticTacToe;
	Assignment blindMode;

	/* Testprogramma's
	CsvFile csvFile = new CsvFile();
	Lights lights = new Lights();
	Test testProgram = new Test(sensors);
	WriteO writeO = new WriteO();
	CheckColor checkColor = new CheckColor(sensor);
	FollowMe followMe = new FollowMe(sensors);
	ArmRotation armRotation = new ArmRotation();
	*/
	
	public Marvin() {
		super();
		running = true;
		brick = LocalEV3.get();
		sensors = new Sensors();
		lineFollowerRGB = new LineFollowerRGB(sensors);
		ticTacToe = new TicTacToe(sensors);
		blindMode = new BlindMode();
	}

	public static void main(String[] args) throws Exception {
		Marvin marvin = new Marvin();
		marvin.run();
		// System.exit?
	}

	private void run() {
		while (running) {
			waitForKeyPress();
		}
	}

	public void waitForKeyPress() {
		// draw Menu on screen
		Sound.beep();
		LCD.clear();
		LCD.drawString("Menus:", 0, 0);
		LCD.drawString("U = LineFollower", 0, 1);
		LCD.drawString("L = TicTacToe", 0, 2);
		LCD.drawString("R = BlindMode", 0, 3);
		LCD.drawString("D = Test", 0, 4);
		LCD.drawString("ESC = EndProgram", 0, 5);

		// wait for user input
		int pressedButton = Button.waitForAnyEvent();
		if (pressedButton == Button.ID_UP) {
			lineFollowerRGB.run();
		} else if (pressedButton == Button.ID_LEFT) {
			ticTacToe.run();
		} else if (pressedButton == Button.ID_RIGHT) {
			blindMode.run();
		} else if (pressedButton == Button.ID_DOWN) {
			// csvFile.check();
			// followMe.run();
			// writeO.run();
			// checkColor.run();
		} else if (pressedButton == Button.ID_ENTER) {
			// enter restarts the menu
			waitForKeyPress();
		} else if (pressedButton == Button.ID_ESCAPE) {
			running = false;
		}

	}

}
