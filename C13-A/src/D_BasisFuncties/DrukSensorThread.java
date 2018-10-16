package D_BasisFuncties;

import lejos.hardware.sensor.EV3TouchSensor;
import lejos.utility.Delay;
import lejos.hardware.port.SensorPort;

public class DrukSensorThread extends Thread {
	// attributes
	int ingedrukt;
	boolean beeindigThread;
	EV3TouchSensor drukknop = new EV3TouchSensor(SensorPort.S2);

	// constructors
	public DrukSensorThread() {
		this.ingedrukt = 0;
		this.beeindigThread = false;
	}

	// thread methods
	@Override
	public void run() {
		while (!beeindigThread) {
			Delay.msDelay(500);
			ingedrukt = drukknop.getCurrentMode();
		}
	}

	public void endThread() {
		this.beeindigThread = true;
	}

	// getters and setters
	public int getIngedrukt() {
		return ingedrukt;
	}
}
