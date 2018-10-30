package assignments;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import models.*;

public class TicTacToe extends Assignment {

	// attributes
	boolean gameOver;
	int winner; // -1 = no winner yet, 0 = draw, 1 = Marvin, 2 = player
	int[] gameBoard = new int[9]; // 0 = empty, 1 = Marvin, 2 = player
	int nextPlayerIs;
	int lastMoveMarvin;
	Lights lights = new Lights();
	FollowMe followMe = new FollowMe();

	// sensors
	EV3TouchSensor touchButton = new EV3TouchSensor(SensorPort.S3);
	SampleProvider spTouch = touchButton.getTouchMode();
	float[] touchData = new float[spTouch.sampleSize()];
	
	EV3ColorSensor colorSensor;

	// vervangende software
	RobotWithWheeledChassis robot = new RobotWithWheeledChassis();

	// constructors
	public TicTacToe(EV3ColorSensor colorSensor) {
		this.colorSensor = colorSensor;
	}

	// main
	@Override
	public void run() {
		followMe.start();
		startNewGame(); // resets all variables

		while (!gameOver) {
			if (nextPlayerIs == 1) {
				checkIfGameOver(gameBoard);
				if (!gameOver) {
					makeNextMove(gameBoard);
					drawNextMoveOnBoard(); // TODO Marvin laten tekenen toevoegen
					Delay.msDelay(500);
					robot.correctStartPosition(followMe.getDeviation()); 
					checkIfGameOver(gameBoard);
				}
				nextPlayerIs = 2;
			} else {
				letPlayerMakeMove();
				waitForTouchButtonPress(); // wait until user makes a move & presses the
				// button
				scanBoard();
				Delay.msDelay(500);
				robot.correctStartPosition(followMe.getDeviation()); 
				checkIfGameOver(gameBoard);
				nextPlayerIs = 1;
			}
		}
		displayOutcome();
		followMe.endThread();
		// TODO passende uitkomst bedenken
	}

	// methods
	public void startNewGame() {
		this.gameOver = false;
		this.winner = -1;
		for (int i = 0; i < this.gameBoard.length; i++) {
			this.gameBoard[i] = 0;
		}
		this.nextPlayerIs = 2; // TODO de speler begint altijd, aanpassen?
	}

	public void letPlayerMakeMove() {
		nextPlayerIs = 1;
	}

	private void scanBoard() {
		float redMeasured;
		float greenMeasured;
		float blueMeasured;
		colorSensor.setCurrentMode("RGB");
		colorSensor.setFloodlight(Color.WHITE);
		int returnSquares = 0;
		boolean newMove = false;
		int columnReturnSquares = 0;
		
		if (gameBoard[0] == 0 || gameBoard[1] == 0 || gameBoard [2] == 0) {
			int i = 0;
			while (i < 3 && !newMove) {
				robot.moveSquaresForward(1);
				columnReturnSquares++;
				if (gameBoard[i] == 0) {
					float[] sample = new float[colorSensor.sampleSize()];
					colorSensor.fetchSample(sample, 0);
					redMeasured = sample[0];
					greenMeasured = sample[1];
					blueMeasured = sample[2];
					if ((int) (sample[0] * 255) + 2 < (int) (sample[2] * 255)) {
						gameBoard[i] = 2;
						newMove = true;
						lights.brickLights(1, 150);
					}
				}
				i++;
			}
			robot.moveSquaresBackward(columnReturnSquares);
			columnReturnSquares = 0;
		}
		
		if ((gameBoard[3] == 0 || gameBoard[4] == 0 || gameBoard [5] == 0) && !newMove) {
			robot.rotateRight();
			robot.moveSquaresForward(1);
			robot.rotateLeft();
			int i = 3;
			while (i < 6 && !newMove) {
				robot.moveSquaresForward(1);
				columnReturnSquares++;
				if (gameBoard[i] == 0) {
					float[] sample = new float[colorSensor.sampleSize()];
					colorSensor.fetchSample(sample, 0);
					redMeasured = sample[0];
					greenMeasured = sample[1];
					blueMeasured = sample[2];
					if ((int) (sample[0] * 255) + 2 < (int) (sample[2] * 255)) {
						gameBoard[i] = 2;
						newMove = true;
						lights.brickLights(1, 150);
					}
				}
				i++;
			}
			robot.moveSquaresBackward(columnReturnSquares);
			columnReturnSquares = 0;
			returnSquares = 1;
		}
		
		if ((gameBoard[6] == 0 || gameBoard[7] == 0 || gameBoard [8] == 0) && !newMove) {
			robot.rotateRight();
			robot.moveSquaresForward(2-returnSquares);
			robot.rotateLeft();
			int i = 6;
			while (i < 9 && !newMove) {
				robot.moveSquaresForward(1);
				columnReturnSquares++;
				if (gameBoard[i] == 0) {
					float[] sample = new float[colorSensor.sampleSize()];
					colorSensor.fetchSample(sample, 0);
					redMeasured = sample[0];
					greenMeasured = sample[1];
					blueMeasured = sample[2];
					if ((int) (sample[0] * 255) + 2 < (int) (sample[2] * 255)) {
						gameBoard[i] = 2;
						newMove = true;
						lights.brickLights(1, 150);
					}
				}
				i++;
			}
			robot.moveSquaresBackward(columnReturnSquares);
			columnReturnSquares = 0;
			returnSquares = 2;
		}
		
		if (returnSquares > 0) {
			robot.rotateRight();
			robot.moveSquaresBackward(returnSquares);
			robot.rotateLeft();
		}

		lights.brickLights(2, 150);
		
	}

	private void checkIfGameOver(int[] inputBoard) {
		// copy the array
		int[] testBoard = new int[9];
		for (int i = 0; i < inputBoard.length; i++) {
			testBoard[i] = inputBoard[i];
		}

		// see if there is a winner
		// if there is, set winner to 1 (Marvin) or 2 (Player)
		for (int i = 1; i < 3; i++) {
			if (testBoard[0] == i && testBoard[1] == i && testBoard[2] == i) {
				winner = i;
			} else if (testBoard[3] == i && testBoard[4] == i && testBoard[5] == i) {
				winner = i;
			} else if (testBoard[6] == i && testBoard[7] == i && testBoard[8] == i) {
				winner = i;
			} else if (testBoard[0] == i && testBoard[3] == i && testBoard[6] == i) {
				winner = i;
			} else if (testBoard[1] == i && testBoard[4] == i && testBoard[7] == i) {
				winner = i;
			} else if (testBoard[2] == i && testBoard[5] == i && testBoard[8] == i) {
				winner = i;
			} else if (testBoard[0] == i && testBoard[4] == i && testBoard[8] == i) {
				winner = i;
			} else if (testBoard[2] == i && testBoard[4] == i && testBoard[6] == i) {
				winner = i;
			}
		}

		// check if all fields are filled
		// if so, set winner to 0 (it's a draw)
		boolean noMoves = true;
		for (int i = 0; i < testBoard.length; i++) {
			if (testBoard[i] == 0)
				noMoves = false;
		}
		if (noMoves)
			winner = 0;

		// if either, game is over
		if (winner != -1) {
			gameOver = true;
		}
	}

	private void makeNextMove(int[] inputBoard) {
		// copy the array
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
			// make that move
			gameBoard[bestMove] = 1;
			nextPlayerIs = 2;
			lastMoveMarvin = bestMove;
		} else {
			// make the necessary move
			gameBoard[necessaryMove] = 1;
			nextPlayerIs = 2;
			lastMoveMarvin = necessaryMove;
		}
	}

	public int findNecessaryMove(int[] inputBoard) {
		// copy the array
		int[] testBoard = new int[9];
		for (int i = 0; i < inputBoard.length; i++) {
			testBoard[i] = inputBoard[i];
		}
		// return winning move if there is one, then return non-losing move if there is one
		for (int j = 1; j < 3; j++) {
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

	public int rateThisMove(int[] inputBoard) {
		// copy the array
		int[] testBoard = new int[9];
		for (int i = 0; i < inputBoard.length; i++) {
			testBoard[i] = inputBoard[i];
		}

		int theWinner = -1;
		// check if there is a winner - if so, return outcome
		for (int i = 1; i < 3; i++) {
			if (testBoard[0] == i && testBoard[1] == i && testBoard[2] == i) {
				theWinner = i;
			} else if (testBoard[3] == i && testBoard[4] == i && testBoard[5] == i) {
				theWinner = i;
			} else if (testBoard[6] == i && testBoard[7] == i && testBoard[8] == i) {
				theWinner = i;
			} else if (testBoard[0] == i && testBoard[3] == i && testBoard[6] == i) {
				theWinner = i;
			} else if (testBoard[1] == i && testBoard[4] == i && testBoard[7] == i) {
				theWinner = i;
			} else if (testBoard[2] == i && testBoard[5] == i && testBoard[8] == i) {
				theWinner = i;
			} else if (testBoard[0] == i && testBoard[4] == i && testBoard[8] == i) {
				theWinner = i;
			} else if (testBoard[2] == i && testBoard[4] == i && testBoard[6] == i) {
				theWinner = i;
			}
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
		// draw action toevoegen
		Delay.msDelay(1000);
		robot.returnFromSquareNumber(lastMoveMarvin);
	}

	private void waitForTouchButtonPress() {
		// draw LCD prompt
		//LCD.clear();
		//LCD.drawString("Druk op de rode", 0, 4);
		//LCD.drawString("knop als je een", 0, 5);
		//LCD.drawString("zet gedaan hebt.", 0, 6);
		// wait for button press
		boolean buttonPressed = false;
		while (!buttonPressed) {
			Delay.msDelay(100);
			spTouch.fetchSample(touchData, 0);
			if (touchData[0] == 1) {
				Sound.buzz();
				buttonPressed = true;
				Delay.msDelay(2000);
			}
		}
	}

	private void displayOutcome() {
		// TODO een passend win geluidje/dansje toevoegen
		LCD.clear();
		if (winner == 0) {
			LCD.drawString("gelijkspel", 0, 4);
		} else if (winner == 1) {
			LCD.drawString("Marvin wint!", 0, 4);
		} else {
			LCD.drawString("Marvin verliest", 0, 4);
		}
	}
}