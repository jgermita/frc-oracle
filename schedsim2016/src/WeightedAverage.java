
public class WeightedAverage {

	double[] weights = { 2.0, 0.75, 0.50, 0.375, 0.25 };

	double sum = 0.0;
	int c = 0;
	double k = 0;

	public WeightedAverage(double[] weights) {
		this.weights = weights;

	}

	public void add(double in) {
		sum += in * weights[c];
		k += weights[c];
		c++;
		if (c > (weights.length - 1))
			c = (weights.length - 1);
	}

	public double get() {
		return sum / k;
	}

}
