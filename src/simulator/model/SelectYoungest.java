package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty())
			return null;
		Animal aux = as.get(0);
		for (int i = 1; i < as.size(); i++) {
			if (as.get(i).get_age() < aux.get_age())
				aux = as.get(i);
		}
		return aux;
	}
}
