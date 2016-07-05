package pl.jalokim.propertiestojson.object;

import pl.jalokim.propertiestojson.util.StringToJsonStringWrapper;

public class StringJson extends AbstractJsonType{

	private String value;

	public StringJson(final String value) {
		this.value = value;
	}

	@Override
	public String toStringJson() {
		return StringToJsonStringWrapper.wrap(value);
	}
}
