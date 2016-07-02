package pl.jalokim.propertiestojson.object;
import pl.jalokim.propertiestojson.util.JsonStringWrapper;

import java.util.HashMap;
import java.util.Map;

public class ObjectJson extends AbstractJsonType {
	Map<String, AbstractJsonType> fields = new HashMap<>();
	private static final String JSON_OBJECT_START = "{\n";
	private static final String JSON_OBJECT_END = "}\n";

	public void addField(final String field, final AbstractJsonType object) {
		fields.put(field, object);
	}

	public boolean containsField(String field){
		return fields.containsKey(field);
	}

	public AbstractJsonType getJsonTypeByFieldName(String field){
		return fields.get(field);
	}

	@Override
	public String toStringJson() {
		String result = JSON_OBJECT_START;
		int index = 0;
		int mapSize = fields.size()-1;
		for (String fieldName : fields.keySet()) {
			AbstractJsonType object = fields.get(fieldName);
			String lastSign = index == mapSize ? "":",\n";
			result = result + JsonStringWrapper.wrap(fieldName)+":"+object.toStringJson()+lastSign;
			index++;
		}
		result = result +JSON_OBJECT_END;
		return result;
	}
}
