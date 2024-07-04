package simulator.model;

import java.util.List;

import simulator.factories.Factory;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Simulator implements JSONable,Observable<EcoSysObserver> {
	private RegionManager manager;
	private List<Animal> animales;
	private double tiempo;
	Factory<Animal> animal_f;
	Factory<Region> region_f;
	List<EcoSysObserver> _observable;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory,
			Factory<Region> regions_factory) {
		manager = new RegionManager(cols, rows, width, height);
		animales = new ArrayList<>();
		tiempo = 0;
		animal_f = animals_factory;
		region_f = regions_factory;
		_observable = new ArrayList<>();
	}

	public Simulator() {}

	private void set_region(int row, int col, Region r) {
		manager.set_region(row, col, r);
		for(EcoSysObserver o : _observable) {
			o.onRegionSet(row,col,manager,r);
		}
	}

	public void set_region(int row, int col, JSONObject r_json) {
		Region aux = region_f.create_instance(r_json);
		set_region(row, col, aux);
	}

	private void add_animal(Animal a) {
		animales.add(a);
		manager.register_animal(a);
	}

	public void add_animal(JSONObject a_json) {
		Animal aux = animal_f.create_instance(a_json);
		add_animal(aux);
		for(EcoSysObserver o : _observable) {
			o.onAnimalAdded(tiempo, manager, new ArrayList<>(animales) , aux);
		}
	}

	public MapInfo get_map_info() {
		return manager;
	}

	public List<? extends AnimalInfo> get_animals() {
		return Collections.unmodifiableList(animales);
	}

	public double get_time() {
		return tiempo;
	}

	public void advance(double dt) {
		tiempo += dt;
		List<Animal> aux = new ArrayList<>(animales);
		
		for(Animal a : aux) {
			if(a.get_state() == State.DEAD) {
				manager.unregister_animal(a);
				animales.remove(a);
			}
		}		
		for(Animal a: animales) {
			a.update(dt);
			manager.update_animal_region(a);
		}

		manager.update_all_regions(dt);
		
		aux = new ArrayList<>(animales);
		for(Animal a : aux) {
			if(a.is_pregnant())
				add_animal(a.deliver_baby());
		}
		for(EcoSysObserver o : _observable) {
			o.onAvanced(tiempo, manager, new ArrayList<>(animales), dt);
		}
	}
	
	public void reset(int cols, int rows, int width, int height) {
		animales.clear();
		manager = new RegionManager(cols, rows, width, height);
		tiempo = 0.0;
		for(EcoSysObserver o : _observable) {
			o.onReset(height, manager, new ArrayList<>(animales));
		}
	}
	
	public JSONObject as_JSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("time", tiempo);
		jsonObject.put("state", manager.as_JSON());
		return jsonObject;
	}

	@Override
	public void addObserver(EcoSysObserver o) {
		if(!_observable.contains(o)) {
			_observable.add(o);
			o.onRegister(tiempo, manager, new ArrayList<>(animales));
		}		
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
		_observable.remove(o);
	}
}
