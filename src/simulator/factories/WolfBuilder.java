package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal> {
	private Factory<SelectionStrategy> factorySt;

	public WolfBuilder( Factory<SelectionStrategy> s) {
		super("wolf", "lobo");
		factorySt = s;
	}

	@Override
	protected Wolf create_instance(JSONObject data) {
		SelectionStrategy mateSt = new SelectFirst();
		SelectionStrategy huntSt = new SelectFirst();
		Vector2D pos = null;
		try {
			if (data.has("pos")) {
				double x = data.getJSONObject("pos").getJSONArray("x_range").getDouble(0);
				double x1 = data.getJSONObject("pos").getJSONArray("x_range").getDouble(1);
				double y = data.getJSONObject("pos").getJSONArray("y_range").getDouble(0);
				double y1 = data.getJSONObject("pos").getJSONArray("y_range").getDouble(1);
				double a = Utils._rand.nextDouble(x1 - x) + x;
				double b = Utils._rand.nextDouble(y1 - y) + y;
				pos = new Vector2D(a, b);
			}
			if (data.has("mate_strategy"))
				mateSt = factorySt.create_instance(data.getJSONObject("mate_strategy"));
			if (data.has("danger_strategy"))
				huntSt = factorySt.create_instance(data.getJSONObject("danger_strategy"));
			return new Wolf(mateSt, huntSt, pos);
		} catch (Exception e) {
			throw new IllegalArgumentException("datos incorrectos");
		}
	}
}
