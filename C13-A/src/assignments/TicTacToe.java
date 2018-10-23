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

	// behaviors
	@Override
	public void run() {
		startNewGame(); // resets all variables

		// test code
		blokje.setGameBoard(gameBoard);
		blokje.drawCurrentBoard();
		blokje.waitForEnter();

		while (!gameOver) {
			// TODO speler begint nu altijd, dat kunnen instellen of random bepalen
			if (!gameOver)
				scanBoard(); // TODO bord scannen toevoegen
			checkIfGameOver(gameBoard);
			if (gameOver) {
				displayOutcome(); // TODO passende uitkomst bedenken
			} else {
				makeNextMove(gameBoard);
				drawNextMoveOnBoard(); // TODO functie toevoegen
				// waitForTouchButtonPress(); // wait until user makes a move & presses the
				// button
				checkIfGameOver(gameBoard);
			}
		}
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

	private void drawNextMoveOnBoard() {
		// tijdelijke testcode, later vervangen voor Algoritme waar Marvin op het bord
		// tekent
		blokje.setGameBoard(gameBoard);
		blokje.drawCurrentBoard();
		blokje.waitForEnter();
		nextPlayerIs = 2;
	}

	private void displayOutcome() {
		// TODO een passend win geluidje/dansje toevoegen

		// tijdelijke code
		blokje.displayWinner(winner);
		blokje.waitForEnter();

	}

	private void checkIfGameOver(int[] testBoard) {
		// copy the array
		int[] inputBoard = new int[9];
		for (int i = 0; i < testBoard.length; i++) {
			inputBoard[i] = testBoard[i];
		}

		// check if all fields are filled
		// if so, set winner to 0 (it's a draw)
		boolean noMoves = true;
		for (int i = 0; i < inputBoard.length; i++) {
			if (inputBoard[i] == 0)
				noMoves = false;
		}
		if (noMoves)
			winner = 0;

		// check if anyone has three in a row
		// if so, winner is that player
		boolean aWinner = false;
		for (int i = 1; i < 3; i++) {
			if (inputBoard[0] == i && inputBoard[1] == i && inputBoard[2] == i) {
				aWinner = true;
				winner = i;
			} else if (inputBoard[3] == i && inputBoard[4] == i && inputBoard[5] == i) {
				aWinner = true;
				winner = i;
			} else if (inputBoard[6] == i && inputBoard[7] == i && inputBoard[8] == i) {
				aWinner = true;
				winner = i;
			} else if (inputBoard[0] == i && inputBoard[3] == i && inputBoard[6] == i) {
				aWinner = true;
				winner = i;
			} else if (inputBoard[1] == i && inputBoard[4] == i && inputBoard[7] == i) {
				aWinner = true;
				winner = i;
			} else if (inputBoard[2] == i && inputBoard[5] == i && inputBoard[8] == i) {
				aWinner = true;
				winner = i;
			} else if (inputBoard[0] == i && inputBoard[4] == i && inputBoard[8] == i) {
				aWinner = true;
				winner = i;
			} else if (inputBoard[2] == i && inputBoard[4] == i && inputBoard[6] == i) {
				aWinner = true;
				winner = i;
			}
		}
		if (noMoves || aWinner)
			gameOver = true;
	}

	private void makeNextMove(int[] testBoard) {
		// copy the array
		int[] inputBoard = new int[9];
		for (int i = 0; i < testBoard.length; i++) {
			inputBoard[i] = testBoard[i];
		}

		// rate each possible move
		int[] winChance = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		for (int i = 0; i < winChance.length; i++) {
			if (inputBoard[i] != 0) { // if Marvin can't make this move,
				winChance[i] = -10000; // Marvin will NEVER choose this move
			} else {
				inputBoard[i] = 1; // make this move 'virtually'
				nextPlayerIs = 2; // the player moves next 'virtually'
				winChance[i] = rateThisMove(inputBoard); // check how many games Marvin wins if this move is made
				inputBoard[i] = 0; // unmake this move
				nextPlayerIs = 1; // unmake this move
			}
		}
		// TESTCODE print alle winkansen
		for (int i = 0; i < 9; i += 3) {
			LCD.clear();
			LCD.drawInt(winChance[i], 5, 4);
			LCD.drawInt(winChance[i + 1], 5, 5);
			LCD.drawInt(winChance[i + 2], 5, 6);
			blokje.waitForEnter();
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
	}

	private int rateThisMove(int[] testBoard) {
		// copy the array
		int[] inputBoard = new int[9];
		for (int i = 0; i < testBoard.length; i++) {
			inputBoard[i] = testBoard[i];
		}

		// check if the board is full
		boolean noMoves = true;
		for (int i = 0; i < inputBoard.length; i++) {
			if (inputBoard[i] == 0)
				noMoves = false;
		}
		// if the board is full, return a score for this move:
		// -1 is player, 0 is draw, 1 is Marvin
		int returnScore = 0;
		if (noMoves) {
			checkIfGameOver(inputBoard);
			if (winner == 0)
				returnScore = 0;
			else if (winner == 1)
				returnScore = 1;
			else if (winner == 2)
				returnScore = -1;
			// reset static variables used in other method
			winner = -1;
			gameOver = false;
			// return score
			return returnScore;
		} else {
			// if the board isn't full, make each possible move in turn then
			// add the outcome to a winChance array
			int[] winChance = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			for (int i = 0; i < winChance.length; i++) {
				if (inputBoard[i] != 0)
					winChance[i] = -10000;
				else {
					// if it's Marvin's turn, make this move
					if (nextPlayerIs == 1) {
						inputBoard[i] = 1; // make move 'virtually'
						nextPlayerIs = 2; // let the player go 'virtually'
						winChance[i] = rateThisMove(inputBoard);
						inputBoard[i] = 0; // unmake this move
						nextPlayerIs = 1; // unmake this move
					} else {
						// if it's the player's turn, make this move
						inputBoard[i] = 2; // make move 'virtually'
						nextPlayerIs = 1; // let Marvin go 'virtually'
						winChance[i] = rateThisMove(inputBoard);
						inputBoard[i] = 0; // unmake this move
						nextPlayerIs = 2; // unmake this move
					}
				}
			}
			// sum the outcomes of all possible games with this move
			returnScore = 0;
			for (int j = 0; j < winChance.length; j++) {
				if (winChance[j] != -10000)
					returnScore += winChance[j];
			}
			// return score
			return returnScore;
		}
	}

	private void scanBoard() {
		// TODO vervangen met algoritme bord scannen en array updaten

		blokje.drawCurrentBoard();
		blokje.inputUserMove();
		nextPlayerIs = 1;
		this.gameBoard = blokje.getGameBoard();
	}

	public void startNewGame() {
		this.gameOver = false;
		this.winner = -1;
		for (int i = 0; i < this.gameBoard.length; i++) {
			this.gameBoard[i] = 0;
		}
		this.nextPlayerIs = 2; // de speler begint altijd, aanpassen?
	}
}
