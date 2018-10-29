package nl.hva.miw.robot.cohort13;

// import our packages
import assignments.*;
import models.*;

// import Lejos packages
import lejos.hardware.Brick;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Marvin {
	// get the brick
	Brick brick;

	// get our projects
	// TouchStop stopButton = new TouchStop();
	Assignment lineFollower = new LineFollower();
	//Assignment blindMode = new BlindMode();
	//CsvFile csvFile = new CsvFile();
	//Lights lights = new Lights();
	//Test testProgram = new Test();
	//ArmRotation armRotation = new ArmRotation();
	//FollowMe followMe = new FollowMe();
	TicTacToe ticTacToe = new TicTacToe();

	public Marvin() {
		super();
		brick = LocalEV3.get();
	}

	public static void main(String[] args) throws Exception {
		Marvin marvin = new Marvin();
		marvin.run();
	}

	private void run() {

		while (true) {
			waitForKeyPress();

		}

	}

	public void waitForKeyPress() {
		// draw Menu on screen
		Sound.twoBeeps();
		LCD.clear();
		LCD.drawString("Menu:", 0, 0);
		LCD.drawString("L = LineFollower", 0, 1);
		LCD.drawString("R = BlindMode", 0, 2);
		LCD.drawString("U = FollowMe", 0, 3);
		LCD.drawString("D = TicTacToe", 0, 4);
		LCD.drawString("ESC = EndProgram", 0, 5);

		// wait for user input
		int pressedButton = Button.waitForAnyEvent();

		// select user choice
		if (pressedButton == Button.ID_LEFT) {
			 lineFollower.run();
		} else if (pressedButton == Button.ID_RIGHT) {
			// blindMode.run();
		} else if (pressedButton == Button.ID_UP) {
			// followMe.run();
		} else if (pressedButton == Button.ID_DOWN) {
			ticTacToe.run();
		} else if (pressedButton == Button.ID_ENTER) {
			// enter restarts the menu			
			waitForKeyPress();
		} else if (pressedButton == Button.ID_ESCAPE) {
			System.exit(0);
		}

	}

}
