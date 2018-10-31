package models;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.Color;

public class Sensors {

	EV3ColorSensor colorSensor;
	EV3TouchSensor touchSensor;
	EV3IRSensor iRSensor;
	
	public Sensors() {
		colorSensor = new EV3ColorSensor(SensorPort.S2);
		touchSensor = new EV3TouchSensor(SensorPort.S3);
		iRSensor = new EV3IRSensor(SensorPort.S1);
	}
	
	public void setColorSensorRGBMode() {
		colorSensor.setCurrentMode("RGB");
		colorSensor.setFloodlight(Color.WHITE);
	}
	
	public void closeColorSensor() {
		colorSensor.close();
	}
	
	public EV3ColorSensor getColorSensor() {
		return colorSensor;
	}
	
	public EV3TouchSensor getTouchSensor() {
		return touchSensor;
	}
	
	public EV3IRSensor getIRSensor() {
		return iRSensor;
	}

	public float[] getRGBSample() {
		float[] sample = new float[colorSensor.sampleSize()];
		colorSensor.fetchSample(sample, 0);
		return sample;
	}
	
}
