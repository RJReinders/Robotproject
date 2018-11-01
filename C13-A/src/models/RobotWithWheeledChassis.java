package models;

import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class RobotWithWheeledChassis {

	private final double SQUARE_WIDTH = 85;
	private final double LINEAR_SPEED = 100; // Speed in robot units/second
	private final double ANGULAR_SPEED = 90; // Speed in degrees/second
	private final double LINEAR_ACCELERATION = 100; // Acceleration in robot units/second^2
	private final double ANGULAR_ACCELERATION = 90; // Acceleration in degrees/second^2
	private final double OFFSET_X_AXIS_FOR_MOVE = - 0.6; // Offset in parts of a game square
	private final double OFFSET_Y_AXIS_FOR_MOVE = - 0.8; // Offset in parts of a game square
	
	private Wheel wheel1;
	private Wheel wheel2;
	private Chassis chassis;
	private MovePilot pilot;
	private int x;
	private int y;

	public RobotWithWheeledChassis() {
		wheel1 = WheeledChassis.modelWheel(Motor.A, 43.2).offset(-79);
		wheel2 = WheeledChassis.modelWheel(Motor.B, 43.2).offset(79);
		chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new MovePilot(chassis);
		chassis.setLinearSpeed(LINEAR_SPEED);
		chassis.setAngularSpeed(ANGULAR_SPEED);
		chassis.setLinearAcceleration(LINEAR_ACCELERATION);
		chassis.setAngularAcceleration(ANGULAR_ACCELERATION);
	}

	public void turnRight() {
		pilot.rotate(90.0);
	}

	public void turnLeft() {
		pilot.rotate(-90.0);
	}

	public void moveSquaresForward(double number) {
		pilot.travel(SQUARE_WIDTH * number);
	}

	public void moveSquaresBackward(double number) {
		pilot.travel(-SQUARE_WIDTH * number);
	}
	
	public void arcForward() {
		pilot.arc(-79, 25);
	}
	
	public void arcBackward() {
		pilot.arc(-79, -25);
	}
	
	public void goToSquareNumber(int number) {
		setXAndY(number);
		if (x + OFFSET_X_AXIS_FOR_MOVE != 0) {
			this.turnRight();
			if (x + OFFSET_X_AXIS_FOR_MOVE < 0)
				this.moveSquaresBackward(-(x + OFFSET_X_AXIS_FOR_MOVE));
			else
				this.moveSquaresForward(x + OFFSET_X_AXIS_FOR_MOVE);
			this.turnLeft();
		}
		this.moveSquaresForward(y + 1 + OFFSET_Y_AXIS_FOR_MOVE);
	}
		
	public void returnFromSquareNumber(int number) {
		setXAndY(number);
		this.moveSquaresBackward(y + 1 + OFFSET_Y_AXIS_FOR_MOVE);
		if (x - OFFSET_X_AXIS_FOR_MOVE != 0) {
			this.turnRight();
			if (x + OFFSET_X_AXIS_FOR_MOVE < 0)
				this.moveSquaresForward(-(x + OFFSET_X_AXIS_FOR_MOVE));
			else
				this.moveSquaresBackward(x + OFFSET_X_AXIS_FOR_MOVE);
			this.turnLeft();
		}
	}
	
	private void setXAndY(int number) {
		switch (number) {
		case 0:
			x = 0;
			y = 0;
			break;
		case 1:
			x = 0;
			y = 1;
			break;
		case 2:
			x = 0;
			y = 2;
			break;
		case 3:
			x = 1;
			y = 0;
			break;
		case 4:
			x = 1;
			y = 1;
			break;
		case 5:
			x = 1;
			y = 2;
			break;
		case 6:
			x = 2;
			y = 0;
			break;
		case 7:
			x = 2;
			y = 1;
			break;
		case 8:
			x = 2;
			y = 2;
			break;
		}
	}
		
	public void correctStartPosition(int deviation) {
		if (deviation != 0) {
			pilot.rotate(45);
			pilot.travel(14 * deviation);
			pilot.rotate(-45);
			pilot.travel(-10 * deviation);
		}
	}

}
