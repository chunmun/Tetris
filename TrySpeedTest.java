package blueBlox;

public class TrySpeedTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		State s = new State();
		PlayerGreed p = new PlayerGreed();
		p.cloneFieldSpeedTest(s.getField());
		return;
	}

}
