package models;

// libraries
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.hardware.Sound;
import lejos.hardware.port.SensorPort;

public class TouchStop extends Thread {
	// attributes
	
	boolean stopThread;
	EV3TouchSensor touchButton = new EV3TouchSensor(SensorPort.S2);
	SampleProvider sp = touchButton.getTouchMode();
	float[] touchData = new float[sp.sampleSize()];
	
	// constructors
	public TouchStop() {		
		this.stopThread = false;
	}

	// thread methods
	@Override
	public void run() {
		while (!stopThread) {
			Delay.msDelay(100);
			sp.fetchSample(touchData, 0);
			if(touchData[0] == 1) {
				Sound.beep();
				
				System.exit(0);
			}
		}
	}

	public void endThread() {
		this.stopThread = true;
	}

	// getters and setters

		
}