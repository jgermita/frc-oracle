
public class Util {

	/**
	 * Math.rand() scaled to -1.0 to 1.0;
	 * 
	 * @return
	 */
	public static double rand2() {
		return Math.min(0.5, (Math.random() * Math.random() * 2) - 1.0);
	}

}
