package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty())
			return null;
		Animal aux = as.get(0);
		for (int i = 1; i < as.size(); i++) {
			if (as.get(i).get_position().distanceTo(a._pos) < aux.get_position().distanceTo(a.get_position()))
				aux = as.get(i);
		}
		return aux;
	}
}
