package simulator.factories;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.LinkedList;

public class BuilderBasedFactory<T> implements Factory<T> {
	private Map<String, Builder<T>> _builders;
	private List<JSONObject> _builders_info;

	public BuilderBasedFactory() {
		_builders = new HashMap<>();
		_builders_info = new LinkedList<>();
	}

	public BuilderBasedFactory(List<Builder<T>> builders) {
		this();
		// call add_builder(b) for each builder b in builder
		// …
		for (Builder<T> b : builders) {
			add_builder(b);
		}
	}

	public void add_builder(Builder<T> b) {
		// add an entry “b.getTag() |−> b” to _builders.
		// ...
		_builders.put(b.get_type_tag(), b);
		// add b.get_info() to _buildersInfo
		// ...
		_builders_info.add(b.get_info());

	}

	@Override
	public T create_instance(JSONObject info) {
		if (info == null) 
			throw new IllegalArgumentException("’info’ cannot be null");

		String type = info.getString("type");
		Builder<T> builder = _builders.get(type);
		// Look for a builder with a tag equals to info.getString("type"), in the
		// map _builder, and call its create_instance method and return the result
		// if it is not null. The value you pass to create_instance is the following
		// because ‘data’ is optional:
		//
		// info.has("data") ? info.getJSONObject("data") : new getJSONObject()
		// …
		// If no builder is found or the result is null ...
		if (builder == null) 
			throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());
		else {
			if (info.has("data"))
				return builder.create_instance(info.getJSONObject("data"));
			else
				return builder.create_instance(new JSONObject());
		}
	}

	@Override
	public List<JSONObject> get_info() {
		return Collections.unmodifiableList(_builders_info);
	}
}
