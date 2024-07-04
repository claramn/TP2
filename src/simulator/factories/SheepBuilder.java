package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;
import simulator.model.Simulator;

public class SheepBuilder extends Builder<Animal> {
	private Factory<SelectionStrategy> factorySt;

	public SheepBuilder(Factory<SelectionStrategy> st) {
		super("sheep", "es una ovejita");
		factorySt = st;
	}

	@Override
	protected Sheep create_instance(JSONObject data) {
		Vector2D pos = null;
		SelectionStrategy mateSt = new SelectFirst();
		SelectionStrategy dangerSt = new SelectFirst();
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
				dangerSt = factorySt.create_instance(data.getJSONObject("danger_strategy"));
			return new Sheep(mateSt, dangerSt, pos);
		} 
		catch (Exception e) {
			throw new IllegalArgumentException("datos incorrectos");
		}
	}
}
