package simulator.factories;

import org.json.JSONObject;
import org.json.JSONArray;
import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	private String _type_tag;
	private String _desc;
	private double _factor;
	private double _food;
	
	public DynamicSupplyRegionBuilder(String type_tag, String desc) {
		super(type_tag, desc);
		_type_tag = type_tag;
		_desc = desc;
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		_factor = 2;
		_food = 1000;
		try {
			if (data.has("factor"))
				_factor = data.getDouble("factor");
			if (data.has("food"))
				_food = data.getDouble("food");
			return new DynamicSupplyRegion(_food, _factor);
		} 
		catch (Exception e) {
			throw new IllegalArgumentException("datos erroneos");
		}
	}
	
	@Override
	public JSONObject get_info() {
		JSONObject json = new JSONObject();
		json.put("type", _type_tag);
		json.put("desc", _desc);
		JSONObject data = new JSONObject();
		data.put("factor", _factor);
		data.put("food", _food);
		json.put("data", "");
		return super.get_info();
	}
	@Override
	protected void fill_in_data(JSONObject o) {
		o.put("factor", "food increase factor (optional, default 2.0)");
		o.put("food", "initial amount of food (optional, default 100.0)");
	}
}
