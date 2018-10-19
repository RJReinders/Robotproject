package models;

import lejos.hardware.sensor.EV3ColorSensor;

public class CheckColor {
	
	EV3ColorSensor colorSensor;
	
	public void FindBlueLine(EV3ColorSensor colorSensor) {
		
		this.colorSensor = colorSensor;
		
		System.out.println(colorSensor.getRGBMode());	
		
	}
	

}
