package assignments;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import models.*;

public class TicTacToe extends Assignment {

	// attributes
	boolean gameOver;
	int winner; // -1 = no winner yet, 0 = draw, 1 = Marvin, 2 = player
	int[] gameBoard = new int[9]; // 0 = empty, 1 = Marvin, 2 = player
	int nextPlayerIs;

	// sensors
	EV3TouchSensor touchButton = new EV3TouchSensor(SensorPort.S3);
	SampleProvider spTouch = touchButton.getTouchMode();
	float[] touchData = new float[spTouch.sampleSize()];

	// tijdelijke testsoftware
	InputTTTManual blokje = new InputTTTManual();

	// constructors
	public TicTacToe() {
	}

	// main
	@Override
	public void run() {
		startNewGame(); // resets all variables

		// TODO test code, kan later weg
		blokje.setGameBoard(gameBoard);
		blokje.drawCurrentBoard();
		blokje.waitForEnter();

		while (!gameOver) {
			if (nextPlayerIs == 1) {
				scanBoard(); // TODO bord scannen toevoegen, testcode verwijderen
				checkIfGameOver(gameBoard);
				if (!gameOver) {
					makeNextMove(gameBoard);
					drawNextMoveOnBoard(); // TODO Marvin laten tekenen toevoegen
					checkIfGameOver(gameBoard);
				}
			} else {
				letPlayerMakeMove(); // TODO tijdelijke testcode, later verwijderen
				// waitForTouchButtonPress(); // wait until user makes a move & presses the
				// button
				scanBoard(); // TODO bord scannen toevoegen, testcode verwijderen
				checkIfGameOver(gameBoard);
			}
		}
		displayOutcome(); // TODO passende uitkomst bedenken
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
		// TODO testcode, later verwijderen!
		blokje.drawCurrentBoard();
		blokje.inputUserMove();
		nextPlayerIs = 1;
	}

	private void scanBoard() {
		// TODO vervangen met algoritme bord scannen en array updaten
		this.gameBoard = blokje.getGameBoard();
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
		} else {
			// make the necessary move
			gameBoard[necessaryMove] = 1;
			nextPlayerIs = 2;
		}
	}

	public int findNecessaryMove(int[] inputBoard) {
		// copy the array
		int[] testBoard = new int[9];
		for (int i = 0; i < inputBoard.length; i++) {
			testBoard[i] = inputBoard[i];
		}
		// return winning move if there is one
		for (int i = 0; i < testBoard.length; i++) {
			if (testBoard[0] == 0 && (testBoard[1] == 1 && testBoard[2] == 1)
					|| (testBoard[3] == 1 && testBoard[6] == 1) || (testBoard[4] == 1 && testBoard[8] == 1))
				return 0;
			else if (testBoard[1] == 0 && (testBoard[0] == 1 && testBoard[2] == 1)
					|| (testBoard[4] == 1 && testBoard[7] == 1))
				return 1;
			else if (testBoard[2] == 0 && (testBoard[0] == 1 && testBoard[1] == 1)
					|| (testBoard[4] == 1 && testBoard[6] == 1) || (testBoard[5] == 1 && testBoard[8] == 1))
				return 2;
			else if (testBoard[3] == 0 && (testBoard[0] == 1 && testBoard[6] == 1)
					|| (testBoard[4] == 1 && testBoard[5] == 1))
				return 3;
			else if (testBoard[4] == 0 && (testBoard[3] == 1 && testBoard[5] == 1)
					|| (testBoard[1] == 1 && testBoard[7] == 1) || (testBoard[0] == 1 && testBoard[8] == 1)
					|| (testBoard[2] == 1 && testBoard[6] == 1))
				return 4;
			else if (testBoard[5] == 0 && (testBoard[3] == 1 && testBoard[4] == 1)
					|| (testBoard[2] == 1 && testBoard[8] == 1))
				return 5;
			else if (testBoard[6] == 0 && (testBoard[0] == 1 && testBoard[3] == 1)
					|| (testBoard[2] == 1 && testBoard[4] == 1) || (testBoard[7] == 1 && testBoard[8] == 1))
				return 6;
			else if (testBoard[7] == 0 && (testBoard[1] == 1 && testBoard[4] == 1)
					|| (testBoard[6] == 1 && testBoard[8] == 1))
				return 7;
			else if (testBoard[8] == 0 && (testBoard[0] == 1 && testBoard[4] == 1)
					|| (testBoard[6] == 1 && testBoard[7] == 1) || (testBoard[2] == 1 && testBoard[5] == 1))
				return 8;
		}

		// return non-losing move if there is one
		for (int i = 0; i < testBoard.length; i++) {
			if (testBoard[0] == 0 && (testBoard[1] == 2 && testBoard[2] == 2)
					|| (testBoard[3] == 2 && testBoard[6] == 2) || (testBoard[4] == 2 && testBoard[8] == 2))
				return 0;
			else if (testBoard[1] == 0 && (testBoard[0] == 2 && testBoard[2] == 2)
					|| (testBoard[4] == 2 && testBoard[7] == 2))
				return 1;
			else if (testBoard[2] == 0 && (testBoard[0] == 2 && testBoard[1] == 2)
					|| (testBoard[4] == 2 && testBoard[6] == 2) || (testBoard[5] == 2 && testBoard[8] == 2))
				return 2;
			else if (testBoard[3] == 0 && (testBoard[0] == 2 && testBoard[6] == 2)
					|| (testBoard[4] == 2 && testBoard[5] == 2))
				return 3;
			else if (testBoard[4] == 0 && (testBoard[3] == 2 && testBoard[5] == 2)
					|| (testBoard[1] == 2 && testBoard[7] == 2) || (testBoard[0] == 2 && testBoard[8] == 2)
					|| (testBoard[2] == 2 && testBoard[6] == 2))
				return 4;
			else if (testBoard[5] == 0 && (testBoard[3] == 2 && testBoard[4] == 2)
					|| (testBoard[2] == 2 && testBoard[8] == 2))
				return 5;
			else if (testBoard[6] == 0 && (testBoard[0] == 2 && testBoard[3] == 2)
					|| (testBoard[2] == 2 && testBoard[4] == 2) || (testBoard[7] == 1 && testBoard[8] == 1))
				return 6;
			else if (testBoard[7] == 0 && (testBoard[1] == 2 && testBoard[4] == 2)
					|| (testBoard[6] == 2 && testBoard[8] == 2))
				return 7;
			else if (testBoard[8] == 0 && (testBoard[0] == 2 && testBoard[4] == 2)
					|| (testBoard[6] == 2 && testBoard[7] == 2) || (testBoard[2] == 2 && testBoard[5] == 2))
				return 8;
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
		// tijdelijke testcode, later vervangen voor Algoritme waar Marvin op het bord
		// tekent
		blokje.setGameBoard(gameBoard);
		blokje.drawCurrentBoard();
		blokje.waitForEnter();
		nextPlayerIs = 2;
	}

	private void waitForTouchButtonPress() {
		// draw LCD prompt
		LCD.clear();
		LCD.drawString("Druk op de rode", 0, 4);
		LCD.drawString("knop als je een", 0, 5);
		LCD.drawString("zet gedaan hebt.", 0, 6);
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

		// tijdelijke code
		blokje.displayWinner(winner);
		blokje.waitForEnter();

	}

}