package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
	private double _food;
	private double _factor;
	private final static double probability = 0.5;
	private static double aux1 = 60;
	private static double aux2 = 5;
	private static double aux3 = 2;

	public DynamicSupplyRegion(double cantidad, double fc) {
		_food = cantidad;
		_factor = fc;
	}

	@Override
	public void update(double dt) {
		if (Utils._rand.nextDouble() < probability)
			_food += dt * _factor;
	}

	@Override
	public double get_food(Animal a, double dt) {
		double aux = 0.0;
		if (a._diet != Diet.CARNIVORE) {
			int n = 0;
			for (Animal animals : animalsInRegion) {
				if (a._diet == Diet.HERVIBORE)
					n++;
			}
			aux = Math.min(_food, aux1 * Math.exp(-Math.max(0, n - aux2) * aux3) * dt);
			_food -= aux;
		}
		return aux;
	}
	
	@Override
	public String toString() {
		return "Dynamic region";
	}
}
