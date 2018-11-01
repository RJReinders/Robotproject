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
	private boolean running;
	private Brick brick;
	private Sensors sensors;
	private Assignment lineFollowerSlow;
	private Assignment lineFollowerRGB;
	private Assignment ticTacToe;
	private Assignment blindMode;

	public Marvin() {
		super();
		running = true;
		brick = LocalEV3.get();
		sensors = new Sensors();
		lineFollowerRGB = new LineFollowerRGB(sensors);
		lineFollowerSlow = new LineFollowerSlow(sensors);
		ticTacToe = new TicTacToe(sensors);
		blindMode = new BlindMode();
	}

	public static void main(String[] args) throws Exception {
		Marvin marvin = new Marvin();
		marvin.run();
	}

	private void run() {
		while (running) {
			runMenu();
		}
	}

	public void runMenu() {
		// draw Menu on screen
		Sound.beep();
		LCD.clear();
		LCD.drawString("Menus:", 0, 0);
		LCD.drawString("LineFollower", 3, 1);
		LCD.drawString("TicTacToe", 0, 3);
		LCD.drawString("Blind", 12, 3);
		LCD.drawString("LineFollowerSlow", 1, 5);
		LCD.drawString("ESC = EndProgram", 0, 7);

		// wait for user input
		int pressedButton = Button.waitForAnyEvent();
		if (pressedButton == Button.ID_UP) {
			lineFollowerRGB.run();
		} else if (pressedButton == Button.ID_LEFT) {
			ticTacToe.run();
		} else if (pressedButton == Button.ID_RIGHT) {
			blindMode.run();
		} else if (pressedButton == Button.ID_DOWN) {
			lineFollowerSlow.run();
		} else if (pressedButton == Button.ID_ENTER) {
			runMenu();
		} else if (pressedButton == Button.ID_ESCAPE) {
			running = false;
		}

	}

}
