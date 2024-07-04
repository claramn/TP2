package simulator.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.Iterator;
import org.json.JSONObject;

import simulator.misc.Vector2D;

import org.json.JSONArray;


public class RegionManager implements AnimalMapView{

	private int widthRegion;
	private int heightRegion;
	private int cols;
	private int rows;
	private int widthMap;
	private int heightMap;
	private Region[][] _regions;
	private Map<Animal, Region> _animal_region;
	private final static double SightR = 50;
	
	public RegionManager(int cols, int rows, int width, int height) {
		this.cols = cols;
		this.rows = rows;
		widthMap = width;
		heightMap = height;
		widthRegion = width / cols + (width % cols != 0 ? 1 :0 );
		heightRegion = height / rows + (height % rows != 0 ? 1 :0 );
		_regions = new Region[rows][cols];
		
		for(int i=0; i< rows; i++) {
			for(int j=0; j<cols; j++) {
				_regions[i][j] = new DefaultRegion();
			}
		}
		_animal_region = new HashMap<>();
		
	}
	
	public boolean checkParameter(Region r){
		return r instanceof Region;
	}
	
	public void set_region(int row, int col, Region r) {	
		Region aux = _regions[row][col];
		_regions[row][col] = r;
		 
		try {
			checkParameter(r); 
	     } 
		 catch (IllegalArgumentException e) { 
	            System.out.println("r no es una instancia de Region");
	     }
	
 		for(Animal a : aux.getAnimals()) {
			_animal_region.remove(a);
			r.add_animal(a);
		}
		for(Animal a : r.getAnimals()) {
			_animal_region.put(a, r);
		}
	}
	
	private Region casillaPos(Vector2D a) {
		int col = (int) (a.getX() / (widthRegion));
        int row = (int) (a.getY() / (heightRegion));
        return _regions[row][col];					
	}
	
	public void register_animal(Animal a) {
		a.init(this);
  		Region aux = casillaPos(a.get_position());
		aux.add_animal(a);
		_animal_region.put(a,aux);
		
	}
	
	public void unregister_animal(Animal a) {
		_animal_region.get(a).remove_animal(a);
		_animal_region.remove(a);
		a = null;
	}
	
	public void update_animal_region(Animal a) {
		Region aux = casillaPos(a.get_position());
		if(_animal_region.get(a) != aux) {	
			_animal_region.get(a).remove_animal(a);
			aux.add_animal(a);
			_animal_region.put(a, aux);
		}
	}
	
	@Override
	public double get_food(Animal a, double dt) {	
		Region region = _animal_region.get(a);
        return region.get_food(a,dt);
	}
	
	void update_all_regions(double dt) {
		for(int i=0;i<rows;i++) {
			 for(int j=0; j<cols;j++) {
				 _regions[i][j].update(dt);
			 }
		 }
	}

	@Override
	public List<Animal> get_animals_in_range(Animal a, Predicate<Animal> filter) {	
		List<Region> regions = new ArrayList<>();
		double up = a.get_position().getY() - a.get_sight_range();
		double down =  a.get_position().getY() + a.get_sight_range();
		double left =  a.get_position().getX() - a.get_sight_range();
		double right = a.get_position().getX() + a.get_sight_range();
		if(up < 0) up = 0;
		if(down>heightMap) down = heightMap-1;
		if(left < 0) left = 0;
		if(right > widthMap) right = widthMap-1;
		 
		int colL = (int) (left / (widthRegion));
        int rowU = (int) (up / (heightRegion));
        int colR = (int) (right / (widthRegion));
        int rowD = (int) (down / (heightRegion));

		List<Animal> aux = new ArrayList<>();
        
		for(int i = rowU; i <= rowD; i++) {
			for(int j = colL; j <=  colR; j++) {
				for(Animal ani: _regions[i][j].getAnimals()) 
					if(filter.test(ani) && ani!=a && a.get_position().distanceTo(ani.get_position())<=a.get_sight_range())
						aux.add(ani);
			}
		}
		return aux;
	}
	
	 public JSONObject as_JSON() {			
		 JSONObject aux = new JSONObject();
		 JSONArray array = new JSONArray();
		 for(int i=0; i < rows; i++) {
			 for(int j=0; j< cols; j++) {
				 JSONObject json = new JSONObject();
				 json.put("data", _regions[i][j].as_JSON());	
				 array.put(json);
				 json.put("row", i);
				 json.put("col", j);
			 }
		 }
		 return aux.put("regiones", array);
	 }
	
	@Override
	public int get_cols() {
		return cols;
	}

	@Override
	public int get_rows() {
		return rows;
	}

	@Override
	public int get_width() {
		return widthMap;
	}

	@Override
	public int get_height() {
		return heightMap;
	}

	@Override
	public int get_region_width() {
		return widthRegion;
	}

	@Override
	public int get_region_height() {
		return heightRegion;
	}

	private class MyIterator<T> implements Iterator<RegionData>{
		private int col = 0;
		private int row = 0;
		@Override
		public boolean hasNext() {
			return ( row < rows && col < cols);
		}

		@Override
		public RegionData next() {
			RegionData region = new RegionData(row, col, _regions[row][col]);
			col++;
			if(col == cols) {
				row++;
				col = 0;
			}
			return region;
		}
	
	}

	@Override
	public Iterator<RegionData> iterator(){
		Iterator<RegionData> MyIterator = new MyIterator<RegionData>();
		return MyIterator;
	}
	
}
