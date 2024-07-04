package simulator.model;

import org.json.JSONObject;

import simulator.misc.Utils;

import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {
	protected String _genetic_code;
	protected Diet _diet;
	protected State _state;
	protected Vector2D _pos;
	protected Vector2D _dest;
	protected double _energy;
	protected double _speed;
	protected double _age;
	protected double _desire;
	protected double _sight_range;
	protected Animal _mate_target;
	protected Animal _baby;
	protected AnimalMapView _region_mngr;
	SelectionStrategy _mate_strategy;

	static final double v1 = 0.1;
	static final double v2 = 0.2;
	static final double v3 = 60;
	static final double ener = 100;

	protected Animal(String genetic_code, Diet diet, double sight_range, double init_speed,
			SelectionStrategy mate_strategy, Vector2D pos) {
		_genetic_code = genetic_code;
		_diet = diet;
		_sight_range = sight_range;
		_pos = pos;
		_mate_strategy = mate_strategy;
		_speed = Utils.get_randomized_parameter(init_speed, v1);
		_state = State.NORMAL;
		_desire = 0.0;
		_energy = ener;
		_dest = new Vector2D(3, 19);
		_mate_target = null;
		_baby = null;
		_region_mngr = null;
	}

	protected Animal(Animal p1, Animal p2) {
		_dest = null;
		_mate_target = null;
		_baby = null;
		_region_mngr = null;
		_state = State.NORMAL;
		_desire = 0.0;
		_genetic_code = p1._genetic_code;
		_diet = p1._diet;
		_mate_strategy = p2._mate_strategy;
		_energy = (p1._energy + p2._energy) / 2;
		_pos = p1.get_position().plus(Vector2D.get_random_vector(-1, 1).scale(v3 * (Utils._rand.nextGaussian() + 1)));
		_sight_range = Utils.get_randomized_parameter((p1.get_sight_range() + p2.get_sight_range()) / 2, v2);
		_speed = Utils.get_randomized_parameter((p1.get_speed() + p2.get_speed()) / 2, v2);
	}

	public void checkParameter() throws IllegalArgumentException {
		if (_genetic_code == null)
			throw new IllegalArgumentException("Genetic code is null.");
		if (_sight_range < 0)
			throw new IllegalArgumentException("Sight range is negative.");
		if (_speed < 0)
			throw new IllegalArgumentException("Speed is negative.");
		if (_mate_strategy == null)
			throw new IllegalArgumentException("Mate strategy is null.");
	}

	public void init(AnimalMapView reg_mngr) {
		try {
			checkParameter();
		} catch (IllegalArgumentException e) {
			System.out.println("Se ha producido una excepción: " + e.getMessage());
		}
		
		_region_mngr = reg_mngr;
		
		if (_pos == null) {
			double col = Utils._rand.nextDouble() * _region_mngr.get_width();
			double row = Utils._rand.nextDouble() * _region_mngr.get_height();
			_pos = new Vector2D(col, row);
		} 
		else 
			_pos.inMap();
		
		double col = Utils._rand.nextDouble() * _region_mngr.get_width();
		double row = Utils._rand.nextDouble() * _region_mngr.get_height();
		_dest = new Vector2D(col, row);
	}

	public Animal deliver_baby() {
		Animal b = _baby;
		_baby = null;
		return b;
	}

	protected void move(double speed) {
		this._pos = this._pos.plus(_dest.minus(_pos).direction().scale(speed));
	}

	public JSONObject as_JSON() {
		JSONObject jsonObject = new JSONObject();
		Vector2D pos = _pos;
		jsonObject.put("gcode", _genetic_code);
		jsonObject.put("diet", _diet);
		jsonObject.put("state", _state);
		jsonObject.put("pos", pos.asJSONArray());
		return jsonObject;
	}

	public State get_state() {
		return _state;
	}

	public Vector2D get_position() {
		return _pos;
	}

	public String get_genetic_code() {
		return _genetic_code;
	}

	public Diet get_diet() {
		return _diet;
	}

	public double get_speed() {
		return _speed;
	}

	public double get_sight_range() {
		return _sight_range;
	}

	public double get_energy() {
		return _energy;
	}

	public double get_age() {
		return _age;
	}

	public Vector2D get_destination() {
		return _dest;
	}

	public boolean is_pregnant() {
		return !(_baby == null);
	}

}
