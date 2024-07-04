package simulator.model;

import simulator.misc.Utils;

import java.util.List;
import java.util.function.Predicate;
import simulator.misc.Vector2D;

public class Sheep extends Animal {
	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;
	private final static double sight = 40;
	private final static double speed = 35;
	private final static double probability = 0.6;
	private final static double age = 8;
	private final static double aux1 = 100;
	private final static double aux2 = 0.007;
	private final static double aux3 = 20;
	private final static double aux4 = 40;
	private final static double dest = 8;
	private final static double desire = 65;
	private final static double aux5 = 2;
	private final static double aux6 = 1.2;

	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("Sheep", Diet.HERVIBORE, sight, speed, mate_strategy, pos);
		_danger_strategy = danger_strategy;
	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		_danger_strategy = p1._danger_strategy;
		_danger_source = null;
	}

	@Override
	public void update(double dt) {
		if (_state != State.DEAD) {
			actualiza(dt);
			if (!_pos.noSale()) {
				_pos.inMap();
				_state = State.NORMAL;
				_danger_source = null;
				_mate_target = null;
			}
			if (_energy == 0 || _age > age)
				_state = State.DEAD;
			if (_energy + _region_mngr.get_food(this, dt) < 100 && _state != State.DEAD)
				_energy += _region_mngr.get_food(this, dt);
		}
	}

	private void actualiza(double dt) {
		List<Animal> inRange = this._region_mngr.get_animals_in_range(this, new Predicate<Animal>() {
			@Override
			public boolean test(Animal a) {
				return a.get_diet() == Diet.CARNIVORE;
			}
		});
		if (_state == State.NORMAL) {
			avanza(dt);
			if (_danger_source == null)
				_danger_source = _danger_strategy.select(this, inRange);
			if (_danger_source != null) {
				_state = State.DANGER;
				_mate_target = null;
			} 
			else if (_danger_source == null && _desire > desire) {
				_state = State.MATE;
				_danger_source = null;
			}
		} else if (_state == State.DANGER) {
			if (_danger_source != null && _state == State.DEAD)
				_danger_source = null;
			else if (_danger_source == null)
				avanza(dt);
			else if (_danger_source != null) {
				_dest = _pos.plus(_pos.minus(_danger_source.get_position()).direction());
				move(aux5 * _speed * dt * Math.exp((_energy - aux1) * aux2));
				_age += dt;
				double aux = _energy - aux3 * aux6 * dt;
				if (0 < aux && aux < aux1)
					_energy = aux;
				aux = _desire + aux4 * dt;
				if (0 < aux && aux < aux1)
					_desire = aux;
			}

			if (_danger_source == null || !inRange.contains(_danger_source)) {
				_danger_source = this._danger_strategy.select(this, inRange);
				if (_danger_source == null) {
					if (_desire < 65) {
						_state = State.NORMAL;
						_danger_source = null;
						_mate_target = null;
					} else {
						_state = State.MATE;
						_danger_source = null;
					}
				}
			}
		}

		else if (_state == State.MATE) {
			List<Animal> inRange2 = this._region_mngr.get_animals_in_range(this, new Predicate<Animal>() {
				@Override
				public boolean test(Animal a) {
					return a.get_diet() == Diet.HERVIBORE;
				}
			});
			if (_mate_target != null && (_state == State.DEAD || !inRange2.contains(_mate_target)))
				_mate_target = null;
			if (_mate_target == null)
				_mate_target = _mate_strategy.select(this, inRange2);
			if (_mate_target == null)
				avanza(dt);
			else {
				_dest = _mate_target.get_position();
				move(aux5 * _speed * dt * Math.exp((_energy - aux1) * aux2));
				_age += dt;
				double aux = _energy - aux3 * aux6 * dt;
				if (0 < aux && aux < aux1)
					_energy = aux;
				_desire += aux4 * dt;
				if (aux > aux1)
					_desire = 100;
				if (_mate_target.get_position().distanceTo(_pos) < dest) {
					_desire = 0;
					_mate_target._desire = 0;
					if (_baby == null) {
						if (Utils._rand.nextDouble() < probability)
							_baby = new Sheep(this, _mate_target);
						_mate_target = null;
					}
				}

				if (_danger_source == null)
					_danger_source = _mate_strategy.select(this, inRange);
				if (_danger_source != null) {
					_state = State.DANGER;
					_mate_target = null;
				} else if (_desire < desire) {
					_state = State.NORMAL;
					_danger_source = null;
					_mate_target = null;
				}
			}
		}
	}

	private void avanza(double dt) {
		if (_pos.distanceTo(_dest) < dest)
			_dest = Vector2D.get_random_vector(_region_mngr.get_width() - 1, _region_mngr.get_height() - 1);
		this.move(_speed * dt * Math.exp((_energy - aux1) * aux2));
		_age += dt;
		double aux = _energy - aux3 * dt;
		if (0 < aux && aux < aux1)
			_energy = aux;
		aux = _desire + aux4 * dt;
		if (0 < aux && aux < aux1)
			_desire = aux;
	}
}
