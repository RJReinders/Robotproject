package assignments;

import java.io.File;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import models.*;

public class TicTacToe extends Assignment {

	// variables
	private Sensors sensors;
	private Lights lights;
	private CalibrateStartPosition calibrateStartPosition;
	private EV3ColorSensor colorSensor;
	private EV3TouchSensor touchSensor;
	private SampleProvider spTouch;
	private float[] touchData;
	private ArmRotation armRotation;
	private File wavFile;
	private boolean gameOver;
	private int winner; // -1 = no winner yet, 0 = draw, 1 = Marvin, 2 = player
	private int[] gameBoard;
	private int nextPlayerIs;
	private int lastMoveMarvin;

	private RobotWithWheeledChassis robot;

	public TicTacToe(Sensors sensors) {
		this.colorSensor = sensors.getColorSensor();
		this.touchSensor = sensors.getTouchSensor();
		this.sensors = sensors;
		calibrateStartPosition = new CalibrateStartPosition(sensors);
		armRotation = new ArmRotation();
		lights = new Lights();
		robot = new RobotWithWheeledChassis();
		gameBoard = new int[Finals.BOARD_SIZE]; // 0 = empty, 1 = Marvin, 2 = player
	}

	@Override
	public void run() {
		spTouch = touchSensor.getTouchMode();
		touchData = new float[spTouch.sampleSize()];
		sensors.setColorSensorRGBMode();
		calibrateStartPosition.start();

		startNewGame();
		playGame();
		displayOutcome();

		calibrateStartPosition.endThread();

	}

	private void startNewGame() {
		this.gameOver = false;
		this.winner = -1;
		for (int i = 0; i < this.gameBoard.length; i++) {
			this.gameBoard[i] = 0;
		}
		this.nextPlayerIs = 2; // speler mag altijd beginnen
	}

	private void playGame() {
		while (!gameOver) {
			if (nextPlayerIs == 1) {
				checkIfGameOver(gameBoard);
				if (!gameOver) {
					determineNextMove(gameBoard);
					drawNextMoveOnBoard();
					Delay.msDelay(500);
					while (calibrateStartPosition.getDirectionDeviation() < -1
							|| calibrateStartPosition.getDirectionDeviation() > 1) {
						robot.correctStartPosition(calibrateStartPosition.getDirectionDeviation());
					}
					checkIfGameOver(gameBoard);
				}
				nextPlayerIs = 2;
			} else {
				letPlayerMakeMove();
				waitForTouchButtonPress();
				scanBoard();
				Delay.msDelay(500);
				while (calibrateStartPosition.getDirectionDeviation() < -1
						|| calibrateStartPosition.getDirectionDeviation() > 1) {
					robot.correctStartPosition(calibrateStartPosition.getDirectionDeviation());
				}
				checkIfGameOver(gameBoard);
				nextPlayerIs = 1;
			}
		}
	}

	private void checkIfGameOver(int[] gameBoard) {
		// see if there is a winner
		// if there is, return the winner
		for (int i = 1; i <= Finals.NUMBER_OF_PLAYERS; i++) {
			if ((gameBoard[0] == i && gameBoard[1] == i && gameBoard[2] == i)
					|| (gameBoard[3] == i && gameBoard[4] == i && gameBoard[5] == i)
					|| (gameBoard[6] == i && gameBoard[7] == i && gameBoard[8] == i)
					|| (gameBoard[0] == i && gameBoard[3] == i && gameBoard[6] == i)
					|| (gameBoard[1] == i && gameBoard[4] == i && gameBoard[7] == i)
					|| (gameBoard[2] == i && gameBoard[5] == i && gameBoard[8] == i)
					|| (gameBoard[0] == i && gameBoard[4] == i && gameBoard[8] == i)
					|| (gameBoard[2] == i && gameBoard[4] == i && gameBoard[6] == i))
				winner = i;
		}

		// check if all fields are filled
		boolean noMoves = true;
		for (int i = 0; i < gameBoard.length; i++) {
			if (gameBoard[i] == 0)
				noMoves = false;
		}

		// determine winner
		if (noMoves && winner == -1)
			winner = 0;

		if (winner != -1) {
			gameOver = true;
		}
	}

	private void determineNextMove(int[] inputBoard) {
		// make a copy of the array for testing
		int[] testBoard = new int[9];
		for (int i = 0; i < inputBoard.length; i++) {
			testBoard[i] = inputBoard[i];
		}

		// determine if there is a winning move or a non-losing move on this board
		int necessaryMove = 0;
		necessaryMove = findNecessaryMove(testBoard);

		if (necessaryMove == 0) {
			// rate each possible move
			int[] winChance = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			for (int i = 0; i < winChance.length; i++) {
				if (testBoard[i] != 0) { // if Marvin can't make this move,
					winChance[i] = -10000; // Marvin will NEVER choose this move
				} else {
					testBoard[i] = 1; // make this move 'virtually'
					nextPlayerIs = 2; // the player moves next 'virtually'
					winChance[i] = rateThisMove(testBoard); // check how many games Marvin wins if this move is made
					testBoard[i] = 0; // unmake this move
					nextPlayerIs = 1; // unmake this move
				}
			}
			// determine the move with the highest win chance
			int bestMove = 0;
			int bestChance = -9000;
			for (int i = 0; i < gameBoard.length; i++) {
				if (winChance[i] > bestChance) {
					bestMove = i;
					bestChance = winChance[i];
				}
			}
			// make that move (on the real board)
			gameBoard[bestMove] = 1;
			nextPlayerIs = 2;
			lastMoveMarvin = bestMove;
		} else {
			// make the necessary move (on the real board)
			gameBoard[necessaryMove] = 1;
			nextPlayerIs = 2;
			lastMoveMarvin = necessaryMove;
		}
	}

	private int findNecessaryMove(int[] inputBoard) {
		// make a copy of the array for testing
		int[] testBoard = new int[9];
		for (int i = 0; i < inputBoard.length; i++) {
			testBoard[i] = inputBoard[i];
		}

		// return winning move if there is one, then return non-losing move if there is
		// one
		for (int j = 1; j <= Finals.NUMBER_OF_PLAYERS; j++) {
			for (int i = 0; i < testBoard.length; i++) {
				if (testBoard[0] == 0 && ((testBoard[1] == j && testBoard[2] == j)
						|| (testBoard[3] == j && testBoard[6] == j) || (testBoard[4] == j && testBoard[8] == j)))
					return 0;
				else if (testBoard[1] == 0
						&& ((testBoard[0] == j && testBoard[2] == j) || (testBoard[4] == j && testBoard[7] == j)))
					return 1;
				else if (testBoard[2] == 0 && ((testBoard[0] == j && testBoard[1] == j)
						|| (testBoard[4] == j && testBoard[6] == j) || (testBoard[5] == j && testBoard[8] == j)))
					return 2;
				else if (testBoard[3] == 0
						&& ((testBoard[0] == j && testBoard[6] == j) || (testBoard[4] == j && testBoard[5] == j)))
					return 3;
				else if (testBoard[4] == 0 && ((testBoard[3] == j && testBoard[5] == j)
						|| (testBoard[1] == j && testBoard[7] == j) || (testBoard[0] == j && testBoard[8] == j)
						|| (testBoard[2] == j && testBoard[6] == j)))
					return 4;
				else if (testBoard[5] == 0
						&& ((testBoard[3] == j && testBoard[4] == j) || (testBoard[2] == j && testBoard[8] == j)))
					return 5;
				else if (testBoard[6] == 0 && ((testBoard[0] == j && testBoard[3] == j)
						|| (testBoard[2] == j && testBoard[4] == j) || (testBoard[7] == j && testBoard[8] == j)))
					return 6;
				else if (testBoard[7] == 0
						&& ((testBoard[1] == j && testBoard[4] == j) || (testBoard[6] == j && testBoard[8] == j)))
					return 7;
				else if (testBoard[8] == 0 && ((testBoard[0] == j && testBoard[4] == j)
						|| (testBoard[6] == j && testBoard[7] == j) || (testBoard[2] == j && testBoard[5] == j)))
					return 8;
			}
		}
		// return no move
		return 0;
	}

	private int rateThisMove(int[] inputBoard) {
		// make a copy of the array for testing
		int[] testBoard = new int[9];
		for (int i = 0; i < inputBoard.length; i++) {
			testBoard[i] = inputBoard[i];
		}

		int theWinner = -1;
		// check if there is a winner - if so, return outcome
		for (int i = 1; i <= Finals.NUMBER_OF_PLAYERS; i++) {
			if ((testBoard[0] == i && testBoard[1] == i && testBoard[2] == i)
					|| (testBoard[3] == i && testBoard[4] == i && testBoard[5] == i)
					|| (testBoard[6] == i && testBoard[7] == i && testBoard[8] == i)
					|| (testBoard[0] == i && testBoard[3] == i && testBoard[6] == i)
					|| (testBoard[1] == i && testBoard[4] == i && testBoard[7] == i)
					|| (testBoard[2] == i && testBoard[5] == i && testBoard[8] == i)
					|| (testBoard[0] == i && testBoard[4] == i && testBoard[8] == i)
					|| (testBoard[2] == i && testBoard[4] == i && testBoard[6] == i))
				theWinner = i;

		}
		if (theWinner == 1)
			return 1; // Marvin wins
		else if (theWinner == 2)
			return -1; // Marvin loses

		// check if the board is full - if so, return 0
		boolean noMoves = true;
		for (int i = 0; i < testBoard.length; i++) {
			if (testBoard[i] == 0)
				noMoves = false;
		}
		if (noMoves)
			return 0; // It's a draw

		// if the board isn't full, make each possible move in turn then
		// add the outcome to a winChance array
		int[] winChance = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		for (int i = 0; i < winChance.length; i++) {
			if (testBoard[i] != 0)
				winChance[i] = -10000; // don't make this move
			else {
				// if it's Marvin's turn, make this move
				if (nextPlayerIs == 1) {
					testBoard[i] = 1; // make move 'virtually'
					nextPlayerIs = 2; // let the player go 'virtually'
					winChance[i] = rateThisMove(testBoard);
					testBoard[i] = 0; // unmake this move
					nextPlayerIs = 1; // unmake this move
				} else {
					// if it's the player's turn, make this move
					testBoard[i] = 2; // make move 'virtually'
					nextPlayerIs = 1; // let Marvin go 'virtually'
					winChance[i] = rateThisMove(testBoard);
					testBoard[i] = 0; // unmake this move
					nextPlayerIs = 2; // unmake this move
				}
			}
		}
		// sum the outcomes of all possible games with this move
		int returnScore = 0;
		for (int i = 0; i < winChance.length; i++) {
			if (winChance[i] != -10000)
				returnScore += winChance[i];
		}
		// return the total winscore for this move
		return returnScore;
	}

	private void drawNextMoveOnBoard() {
		robot.goToSquareNumber(lastMoveMarvin);
		Sound.beep();
		armRotation.rotateArm(-55);
		Delay.msDelay(1000);
		armRotation.rotateArm(0);
		robot.returnFromSquareNumber(lastMoveMarvin);
	}

	private void letPlayerMakeMove() {
		nextPlayerIs = 1;
	}

	private void waitForTouchButtonPress() {

		LCD.clear();
		LCD.drawString("Druk op de rode", 0, 1);
		LCD.drawString("knop als je een", 0, 2);
		LCD.drawString("zet gedaan hebt.", 0, 3);

		boolean buttonPressed = false;
		while (!buttonPressed) {
			Delay.msDelay(100);
			spTouch.fetchSample(touchData, 0);
			if (touchData[0] == 1) {
				Sound.beep();
				buttonPressed = true;
				Delay.msDelay(1000);
			}
		}
	}

	private void scanBoard() {

		float redMeasured;
		float blueMeasured;
		int rowReturnSquares = 0;
		int columnReturnSquares = 0;
		boolean playerMoveFound = false;
		float[] sample;

		// scan first column
		if (gameBoard[0] == 0 || gameBoard[1] == 0 || gameBoard[2] == 0) {
			int i = 0;
			while (i < 3 && !playerMoveFound) {
				robot.moveSquaresForward(1);
				columnReturnSquares++;
				if (gameBoard[i] == 0) {
					sample = sensors.getRGBSample();
					redMeasured = sample[0];
					blueMeasured = sample[2];
					if ((int) (redMeasured * Finals.SAMPLE_TO_RGB)
							+ Finals.DIFFERENCE_BLUE_OVER_RED < (int) (blueMeasured * Finals.SAMPLE_TO_RGB)) {
						gameBoard[i] = 2;
						playerMoveFound = true;
						lights.brickLights(1, 150);
					}
				}
				i++;
			}
			robot.moveSquaresBackward(columnReturnSquares);
			columnReturnSquares = 0;
		}

		// scan second column
		if ((gameBoard[3] == 0 || gameBoard[4] == 0 || gameBoard[5] == 0) && !playerMoveFound) {
			robot.rotateRight();
			robot.moveSquaresForward(1);
			robot.rotateLeft();
			int i = 3;
			while (i < 6 && !playerMoveFound) {
				robot.moveSquaresForward(1);
				columnReturnSquares++;
				if (gameBoard[i] == 0) {
					sample = sensors.getRGBSample();
					redMeasured = sample[0];
					blueMeasured = sample[2];
					if ((int) (redMeasured * Finals.SAMPLE_TO_RGB)
							+ Finals.DIFFERENCE_BLUE_OVER_RED < (int) (blueMeasured * Finals.SAMPLE_TO_RGB)) {
						gameBoard[i] = 2;
						playerMoveFound = true;
						lights.brickLights(1, 150);
					}
				}
				i++;
			}
			robot.moveSquaresBackward(columnReturnSquares);
			columnReturnSquares = 0;
			rowReturnSquares = 1;
		}

		// scan third column
		if ((gameBoard[6] == 0 || gameBoard[7] == 0 || gameBoard[8] == 0) && !playerMoveFound) {
			robot.rotateRight();
			robot.moveSquaresForward(2 - rowReturnSquares);
			robot.rotateLeft();
			int i = 6;
			while (i < 9 && !playerMoveFound) {
				robot.moveSquaresForward(1);
				columnReturnSquares++;
				if (gameBoard[i] == 0) {
					sample = sensors.getRGBSample();
					redMeasured = sample[0];
					blueMeasured = sample[2];
					if ((int) (redMeasured * Finals.SAMPLE_TO_RGB)
							+ Finals.DIFFERENCE_BLUE_OVER_RED < (int) (blueMeasured * Finals.SAMPLE_TO_RGB)) {
						gameBoard[i] = 2;
						playerMoveFound = true;
						lights.brickLights(1, 150);
					}
				}
				i++;
			}
			robot.moveSquaresBackward(columnReturnSquares);
			columnReturnSquares = 0;
			rowReturnSquares = 2;
		}

		if (rowReturnSquares > 0) {
			robot.rotateRight();
			robot.moveSquaresBackward(rowReturnSquares);
			robot.rotateLeft();
		}

		lights.brickLights(2, 150);

	}

	private void displayOutcome() {
		LCD.clear();
		if (winner == 0) {
			LCD.drawString("Gelijkspel.", 0, 4);
			wavFile = new File("tryagain.wav");
			Sound.playSample(wavFile, 80);
		} else if (winner == 1) {
			LCD.drawString("Ik heb gewonnen!", 0, 4);
			wavFile = new File("we.wav");
			Sound.playSample(wavFile, 80);
		} else {
			LCD.drawString("Ik heb verloren...", 0, 4);
			wavFile = new File("always.wav");
			Sound.playSample(wavFile, 80);
		}
		waitForTouchButtonPress();
	}
}