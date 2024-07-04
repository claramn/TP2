package simulator.view;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.ListIterator;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.Region;
import simulator.model.RegionInfo;
import simulator.model.RegionManager;
import simulator.model.State;

@SuppressWarnings("serial")
class RegionsTableModel extends AbstractTableModel implements EcoSysObserver { // poner bonito lo d +3
	// TODO definir atributos necesarios
	String[] _columns;
	MapInfo _mng;
	Map<Diet, Integer> animals;
	Map<RegionData, Map<Diet, Integer>> regions;
	List<RegionData> rd;

	RegionsTableModel(Controller ctrl) {
		
		_columns = new String[Diet.values().length + 3];
		animals = new HashMap<>();
		regions = new HashMap<>();
		rd = new ArrayList<RegionData>(regions.keySet());

		for (int i = 0; i < Diet.values().length + 3; i++) {
			if (i == 0) 
				_columns[i] = "Row";
			else if (i == 1)
				_columns[i] = "Col";
			else if (i == 2) 
				_columns[i] = "Desc.";
			else 
				_columns[i] = Diet.values()[i - 3].toString();
		}
		ctrl.addObserver(this);
	}

	public void update() {
		fireTableStructureChanged();
	}

	private int getAnimalsOnState(RegionData r, Predicate<AnimalInfo> filter) {
		int contador = 0;
		for (int i = 0; i < r.r().getAnimalsInfo().size(); i++) {
			if (filter.test(r.r().getAnimalsInfo().get(i))) 
				contador++;
		}
		return contador;
	}

	private void idea(MapInfo mng) { 
		_mng = mng;
		Iterator<RegionData> it = mng.iterator();
		RegionData r;
		while (it.hasNext()) {

			r = it.next();
			for (int i = 0; i < Diet.values().length; i++) {
				Diet d = Diet.values()[i];
				int inRegion = getAnimalsOnState(r, new Predicate<AnimalInfo>() {
					@Override
					public boolean test(AnimalInfo a) {
						return a.get_diet() == d;
					}
				});
				animals.put(Diet.values()[i], inRegion);
			}
			regions.put(r, new HashMap<>(animals));

		}
		update();

	}

	@Override
	public int getRowCount() {
		return regions.size();
	}

	@Override
	public int getColumnCount() {
		return Diet.values().length + 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		rd = new ArrayList<RegionData>(regions.keySet());
		Collections.sort(rd, new Comparator<RegionData>() { 
			@Override
			public int compare(RegionData o1, RegionData o2) {
				if (o1.row() != o2.row()) 
					return Integer.compare(o1.row(), o2.row());
				return Integer.compare(o1.col(), o2.col());
			}
		});
		switch (columnIndex) {
		case 0:
			return rd.get(rowIndex).row();
		case 1:
			return rd.get(rowIndex).col();
		case 2:
			return rd.get(rowIndex).r().toString();

		default:
			return regions.get(rd.get(rowIndex)).get(Diet.values()[columnIndex - 3]);
		}

	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		idea(map);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		this.regions.clear();
		this.rd.clear();
		idea(map);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		idea(map);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		this.regions.clear();
		this.rd.clear();
		idea(map);
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this.regions.clear();
		this.rd.clear();
		idea(map);
	}

	@Override
	public String getColumnName(int column) {
		return _columns[column];
	}

}
