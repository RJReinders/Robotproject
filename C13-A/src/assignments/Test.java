package assignments;

import lejos.hardware.sensor.EV3ColorSensor;
import models.*;


public class Test extends Assignment {

	
	EV3ColorSensor colorSensor;

	// constructors
	public Test(Sensors sensors) {
		this.colorSensor = sensors.getColorSensor();
	}

	
	@Override
	public void run() {
		
		RobotWithWheeledChassis testDing = new RobotWithWheeledChassis();
		//testDing.run();		
}
}


