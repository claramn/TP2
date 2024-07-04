package simulator.model;

public class DefaultRegion extends Region {

	private static double aux1 = 60;
	private static double aux2 = 5;
	private static double aux3 = 2;
	
	public DefaultRegion() {}

	public double get_food(Animal a, double dt) {
		double aux = 0;
		if (a.get_diet() != Diet.CARNIVORE) {
			int n = 0;
			for (Animal animals : animalsInRegion) {
				if (a._diet != Diet.CARNIVORE) 
					n++;
				aux = aux1 * Math.exp(-Math.max(0, n - aux2) * aux3) * dt;
			}
		}
		return aux;
	}

	@Override
	public void update(double dt) {}
	
	@Override
	public String toString() {
		return "Default region";
	}
}
