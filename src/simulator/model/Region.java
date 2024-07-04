package simulator.model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Region implements Entity, FoodSupplier, RegionInfo {

	protected List<Animal> animalsInRegion;

	public Region() {
		animalsInRegion = new ArrayList<>();
	}

	final void add_animal(Animal a) {
		animalsInRegion.add(a);
	}

	final void remove_animal(Animal a) {
		animalsInRegion.remove(a);
	}

	final List<Animal> getAnimals() {
		return Collections.unmodifiableList(animalsInRegion);
	}

	public List<AnimalInfo> getAnimalsInfo() {
		return new ArrayList<>(animalsInRegion); 
	}

	public JSONObject as_JSON() {
		JSONObject json = new JSONObject();
		JSONArray animals = new JSONArray();
		for (Animal animal : animalsInRegion) {
			animals.put(animal.as_JSON());
		}
		json.put("animals", animals);
		return json;
	}
}
