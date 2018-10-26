package models;

import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class RobotWithWheeledChassis {
	
	private Wheel wheel1;
	private Wheel wheel2;
	private Chassis chassis;
	private MovePilot pilot;
	private double squareWidth = 8.5;
	private double linearSpeed = 2; // Speed in robot units/second
	private double angularSpeed = 90; // Speed in degrees/second
	private double linearAcceleration = 2; // Acceleration in robot units/second^2
	private double angularAcceleration = 90; // Acceleration in degrees/second^2
		

	public RobotWithWheeledChassis() {
		wheel1 = WheeledChassis.modelWheel(Motor.A, 43.2).offset(-64.8);
		wheel2 = WheeledChassis.modelWheel(Motor.B, 43.2).offset(64.8);
		Chassis chassis = new WheeledChassis(new Wheel[]{wheel1, wheel2}, WheeledChassis.TYPE_DIFFERENTIAL);
		MovePilot pilot = new MovePilot(chassis);
		chassis.setLinearSpeed(linearSpeed);
		chassis.setAngularSpeed(angularSpeed);
		chassis.setLinearAcceleration(linearAcceleration);
		chassis.setAngularAcceleration(angularAcceleration);
	}
	
	public void rotateRight() {
		chassis.rotate(90);
	}
	
	public void rotateLeft() {
		chassis.rotate(-90);
	}
	
	public void moveSquaresForward(int number) {
		chassis.travel(squareWidth * number);
	}
	
	public void moveSquaresBackward(int number) {
		chassis.travel(-squareWidth * number);
	}
	
	public void goToSquareNumber(int number) {
		switch (number) {
		case 1:
			this.moveSquaresForward(3);
			break;
		case 2:
			this.rotateLeft();
			this.moveSquaresForward(1);
			this.rotateRight();
			this.moveSquaresForward(3);
			break;
		case 3:
			this.rotateLeft();
			this.moveSquaresForward(2);
			this.rotateRight();
			this.moveSquaresForward(3);
			break;
		case 4:
			this.moveSquaresForward(2);
			break;
		case 5:
			this.rotateLeft();
			this.moveSquaresForward(1);
			this.rotateRight();
			this.moveSquaresForward(2);
			break;
		case 6:
			this.rotateLeft();
			this.moveSquaresForward(2);
			this.rotateRight();
			this.moveSquaresForward(2);
			break;
		case 7:
			this.moveSquaresForward(1);
			break;
		case 8:
			this.rotateLeft();
			this.moveSquaresForward(1);
			this.rotateRight();
			this.moveSquaresForward(1);
			break;
		case 9:
			this.rotateLeft();
			this.moveSquaresForward(2);
			this.rotateRight();
			this.moveSquaresForward(1);
			break;
		}
	}

}
