package simulator.view;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.table.AbstractTableModel;


import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;

import java.util.HashMap;
import java.util.ArrayList;

class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	
	private String [] _columns;
	Object[][] _data;
	
	List<AnimalInfo> animales;
	Map<String,Map<State,Integer>> _map;
	
	
	SpeciesTableModel(Controller ctrl) {
		_columns = new String[State.values().length+1];
		for (int i = 0; i < State.values().length+1; i++) {
			if(i == 0) 
                _columns[i] = "Genetic Code";
           else 
        	   _columns[i] = State.values()[i-1].toString();
		}
		
		_map = new HashMap<String, Map<State,Integer>>();
		animales = new ArrayList<AnimalInfo>();
		ctrl.addObserver(this);
	}

	public void update() {
		fireTableStructureChanged();
	}
	
	private void setAnimals(List<AnimalInfo> a) {
		animales = a;
		int contador = 0;
		for (AnimalInfo animal : animales) {
			
			if (!_map.containsKey(animal.get_genetic_code())) {
				_map.put(animal.get_genetic_code(), new HashMap<State, Integer>());
				for (int j = 0; j < State.values().length; j++) {
					_map.get(animal.get_genetic_code()).put(State.values()[j], 0);
				}
			}
			if (!_map.get(animal.get_genetic_code()).containsKey(animal.get_state())) 
				_map.get(animal.get_genetic_code()).put(animal.get_state(), 1);
			else {
				contador = _map.get(animal.get_genetic_code()).get(animal.get_state());
				contador++;
				_map.get(animal.get_genetic_code()).put(animal.get_state(), contador);
			}
		}		
		update();
	}
	
	private int getAnimalsOnState(String tipo, State estado) {
		int contador = 0;
		AnimalInfo object;
		ListIterator<AnimalInfo> iterator = animales.listIterator();
		while (iterator.hasNext()) {
			object = iterator.next();
			if (object.get_state().equals(estado) && object.get_genetic_code().equals(tipo)) 
				contador++;
		}
		return contador;	
    }
	
	@Override
	public int getRowCount() {
		return _map.size();
	}

	@Override
	public int getColumnCount() {
		return State.values().length+1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
		case 0:
			return (new ArrayList<String>(_map.keySet())).get(rowIndex);
		default:
				return _map.get((new ArrayList<String>(_map.keySet())).get(rowIndex)).get(State.values()[columnIndex-1]);
		}
	}

	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		_map.clear();
		setAnimals(animals);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		_map.clear();
		setAnimals(animals);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		_map.clear();
		setAnimals(animals);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		_map.clear();
		setAnimals(animals);
	}
	
	@Override
	public String getColumnName(int column) {
		if (column == 0) 
            return "Genetic Code";
		return State.values()[column-1].toString();
	}
	
}