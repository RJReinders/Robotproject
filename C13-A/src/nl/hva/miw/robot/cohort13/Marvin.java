package nl.hva.miw.robot.cohort13;





public class Marvin {

	Brick brick;

	public Marvin() {
		super();
		brick = LocalEV3.get();
	}

	public static void main(String[] args) {
		Marvin marvin = new Marvin();
		marvin.run();
	}

	private void run() {

		TextLCD display = brick.getTextLCD();
		display.drawString("Welkom", 0, 3);
		display.drawString("Team Alpha!", 0, 4);
		waitForKey(Button.ENTER);
		

	}

	public void waitForKey(Key key) {
		while (key.isUp()) {
			Delay.msDelay(100);
		}
		while (key.isDown()) {
			Delay.msDelay(100);
		}
	}
}
