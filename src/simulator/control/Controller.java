package simulator.control;

import org.json.JSONObject;

import java.io.OutputStream;

import org.json.JSONArray;

import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

import java.io.PrintWriter;
import java.io.PrintStream;

import java.util.List;
import java.util.ArrayList;

public class Controller {
	private Simulator _sim;

	public Controller(Simulator sim) {
		_sim = sim;
	}

	public void load_data(JSONObject data) {
		set_regions(data);
		JSONArray a = data.getJSONArray("animals");
		for (int i = 0; i < a.length(); i++) {
			JSONObject animal = a.getJSONObject(i);
			animalSim(animal);
		}
	}

	private void regionSim(JSONObject r) {
		int rf = r.getJSONArray("row").getInt(0);
		int rt = r.getJSONArray("row").getInt(1);
		int cf = r.getJSONArray("col").getInt(0);
		int ct = r.getJSONArray("col").getInt(1);
		JSONObject spec = r.getJSONObject("spec");
		for (int i = rf; i <= rt; i++) {
			for (int j = cf; j <= ct; j++) {
				_sim.set_region(i, j, spec);
			}
		}
	}

	private void animalSim(JSONObject a) {
		int N = a.getInt("amount");
		JSONObject spec = a.getJSONObject("spec");
		for (int i = 0; i < N; i++) {
			_sim.add_animal(spec);
		}
	}

	public void run(double t, double dt, boolean sv, OutputStream out) {
		SimpleObjectViewer view = null;
		if (sv) {
			MapInfo m = _sim.get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.get_width(), m.get_height(), m.get_rows(), m.get_cols());
			view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		JSONObject initState = _sim.as_JSON();
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("in", initState);
		while (_sim.get_time() <= t) {
			_sim.advance(dt);
			if (sv)
				view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}
		JSONObject finalState = _sim.as_JSON();
		resultJSON.put("out", finalState);

		PrintStream o = new PrintStream(out);
		o.println(resultJSON.toString(2));
		o.flush();
		if (sv)
			view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		if (sv)
			view.close();
	}

	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.get_genetic_code(), (int) a.get_position().getX(), (int) a.get_position().getY(),
					(int) Math.round(a.get_age()) + 2));
		return ol;
	}
	
	public void reset(int cols, int rows, int width, int height){
		_sim.reset(cols, rows, width, height);
	}
	
	public void set_regions(JSONObject rs) {
		if (rs.has("regions")) {
			JSONArray r = rs.getJSONArray("regions");
			for (int i = 0; i < r.length(); i++) {
				JSONObject region = r.getJSONObject(i);
				regionSim(region);
			}
		}
	}
	
	public void advance(double dt) {
		_sim.advance(dt);
	}
	
	public void addObserver(EcoSysObserver o) {
		_sim.addObserver(o);
	}
	
	public void removeObserver(EcoSysObserver o) {
		_sim.removeObserver(o);
	}
}
