package simulator.model;

import java.util.List;
import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {
	private Animal _hunt_target;
	private SelectionStrategy _hunting_strategy;
	private final static double sight = 50;
	private final static double speed = 60;
	private final static double probability = 0.9;
	private final static double age = 14;
	private final static double aux1 = 100;
	private final static double dest = 8;
	private final static double aux2 = 0.007;
	private final static double aux3 = 18;
	private final static double aux4 = 30;
	private final static double aux5 = 50;
	private final static double desire = 65;
	private final static double aux6 = 1.2;
	private final static double aux7 = 3;

	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super("Wolf", Diet.CARNIVORE, sight, speed, mate_strategy, pos);
		_hunt_target = null;
		_hunting_strategy = hunting_strategy;
	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		_hunting_strategy = p1._hunting_strategy;
		_hunt_target = null;
	}

	@Override
	public void update(double dt) {
		if (_state != State.DEAD) {
			actualiza(dt);

			if (!_pos.noSale()) {
				_pos.inMap();
				_state = State.NORMAL;
				_hunt_target = null;
				_mate_target = null;
			}
			if (_energy == 0 || _age > age)
				_state = State.DEAD;
			if (_energy + _region_mngr.get_food(this, dt) < aux1 && _state != State.DEAD) 
				_energy += _region_mngr.get_food(this, dt);
		}
	}

	private void actualiza(double dt) {
		List<Animal> inRange1 = this._region_mngr.get_animals_in_range(this, new Predicate<Animal>() {
			@Override
			public boolean test(Animal a) {
				return a.get_diet() == Diet.HERVIBORE;
			}
		});
		List<Animal> inRange2 = this._region_mngr.get_animals_in_range(this, new Predicate<Animal>() {
			@Override
			public boolean test(Animal a) {
				return a.get_diet() == Diet.CARNIVORE;
			}
		});
		if (_state == State.NORMAL) {
			avanza(dt);
			if (_energy < aux5) {
				_state = State.HUNGER;
				_mate_target = null;
			} else if (_energy >= aux5 && _desire > desire) {
				_state = State.MATE;
				_hunt_target = null;
			}
		} else if (_state == State.HUNGER) {
			if (_hunt_target == null || _state == State.DEAD || !inRange1.contains(_hunt_target)) {
				_hunt_target = _hunting_strategy.select(this, inRange1);
			}
			if (_hunt_target == null)
				avanza(dt);
			else {
				_dest = _hunt_target.get_position();
				this.move(aux7 * _speed * dt * Math.exp((_energy - aux1) * aux2));
				_age += dt;
				double aux = _energy - aux3 * dt * aux6;
				if (0 < aux && aux < aux1)
					_energy = aux;
				_desire = +aux4 * dt;
				if (aux > aux1)
					_desire = 100;
				if (_hunt_target._pos.distanceTo(_pos) < dest) {
					_hunt_target._state = State.DEAD;
					_hunt_target = null;
					_energy += aux5;
					if (_energy > aux1)
						_energy = 100;
				}
				if (_energy > aux5) {
					if (_desire < desire) {
						_state = State.NORMAL;
						_hunt_target = null;
						_mate_target = null;
					} else {
						_state = State.MATE;
						_hunt_target = null;
					}
				}
			}
		} else if (_state == State.MATE) {
			if (_mate_target != null && (_mate_target._state == State.DEAD || !inRange2.contains(_mate_target)))
				_mate_target = null;
			if (_mate_target == null)
				_mate_target = this._mate_strategy.select(this, inRange2);
			if (_mate_target == null)
				avanza(dt);
			else {
				_dest = _mate_target.get_position();
				this.move(aux7 * _speed * dt * Math.exp((_energy - aux1) * aux2));
				_age += dt;
				double aux = _energy - aux3 * dt * aux6;
				if (0 < aux && aux < aux1)
					_energy = aux;
				_desire += aux4 * dt;
				if (_desire > aux1)
					_desire = 100;
				if (_mate_target._pos.distanceTo(_pos) < 8) {
					_desire = 0;
					_mate_target._desire = 0;
					if (_baby == null) {
						if (Utils._rand.nextDouble() < probability) {
							_baby = new Wolf(this, _mate_target);
						}
						aux = _energy - 10;
						if (aux < aux1 && aux > 0)
							_energy = aux;
						_mate_target = null;
					}
				}
			}
			if (_energy < aux5) {
				_state = State.HUNGER;
				_mate_target = null;
			}

			else if (_energy >= aux5 && _desire < desire) {
				_state = State.NORMAL;
				_hunt_target = null;
				_mate_target = null;
			}

		}
	}

	private void avanza(double dt) {
		if (_pos.distanceTo(_dest) < dest)
			_dest = Vector2D.get_random_vector(_region_mngr.get_width() - 1, _region_mngr.get_height() - 1);
		this.move(_speed * dt * Math.exp((_energy - aux1) * aux2));
		_age += dt;
		double aux = _energy - aux3 * dt;
		if (0 < aux && aux < 100)
			_energy = aux;
		aux = _desire + aux4 * dt;
		if (0 < aux && aux < aux1)
			_desire = aux;
	}
}
