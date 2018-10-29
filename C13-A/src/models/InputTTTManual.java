package models;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class InputTTTManual {
	// attributes
	int[] gameBoard = new int[9];

	// constructors
	public InputTTTManual() {

	}

	// behaviors
	public void drawCurrentBoard() {
		LCD.clear();
		LCD.drawInt(gameBoard[0], 2, 1);
		LCD.drawInt(gameBoard[1], 4, 1);
		LCD.drawInt(gameBoard[2], 6, 1);
		LCD.drawInt(gameBoard[3], 2, 2);
		LCD.drawInt(gameBoard[4], 4, 2);
		LCD.drawInt(gameBoard[5], 6, 2);
		LCD.drawInt(gameBoard[6], 2, 3);
		LCD.drawInt(gameBoard[7], 4, 3);
		LCD.drawInt(gameBoard[8], 6, 3);		
	}
	
	public void waitForEnter() {
		boolean doorgaan = false;		
		LCD.drawString("Druk op enter", 0, 7);
		int pressedButton;
		while(!doorgaan) {
			Delay.msDelay(100);
			pressedButton = Button.waitForAnyEvent();		
		if (pressedButton == Button.ID_ENTER)
			doorgaan = true;
		}
	}
	
	public void displayWinner(int winner) {
		LCD.clear();
		if(winner == 0)
			LCD.drawString("Gelijkspel", 5, 3);
		else if(winner == 1)
			LCD.drawString("Marvin wint!", 5, 3);
		else if(winner == 2)
			LCD.drawString("Speler wint", 5, 3);
	}
	
	public void inputUserMove() {
		boolean klaar = false;
		int keuzeSpeler = 5;
		
		while(!klaar) {
		LCD.drawString("             ", 0, 7);
		LCD.drawInt(keuzeSpeler, 9, 7);
		
		int pressedButton;
		pressedButton = Button.waitForAnyEvent();
		if (pressedButton == Button.ID_LEFT)
			keuzeSpeler -= 1;
		else if (pressedButton == Button.ID_RIGHT)
			keuzeSpeler += 1;
		else if (pressedButton == Button.ID_ENTER)
		klaar = true;
		}		
		this.gameBoard[keuzeSpeler-1] = 2;
	}

	// getters and setters
	public int[] getGameBoard() {
		return this.gameBoard;
	}
	
	public void setGameBoard(int[] gameBoard) {
		for(int i = 0; i < gameBoard.length; i++) {
			this.gameBoard[i] = gameBoard[i];
		}			
	}
}
