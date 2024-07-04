package simulator.factories;

import org.json.JSONObject;

import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region> {
	
	private String _type_tag;
	private String _desc;
	
	public DefaultRegionBuilder(String type_tag, String desc) {
		super(type_tag, desc);
		_type_tag = type_tag;
		_desc = desc;
	}

	@Override
	protected DefaultRegion create_instance(JSONObject data) {
		try {
			return new DefaultRegion();
		} 
		catch (Exception e) {
			throw new IllegalArgumentException("datos incorrectos");
		}
	}
	
	@Override
	public JSONObject get_info() {
		JSONObject json = new JSONObject();
		json.put("type", _type_tag);
		json.put("desc", _desc);
		json.put("data", "");
		return super.get_info();
	}
	@Override
	protected void fill_in_data(JSONObject o) {	}

}
