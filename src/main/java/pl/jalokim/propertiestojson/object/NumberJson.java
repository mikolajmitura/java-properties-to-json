package pl.jalokim.propertiestojson.object;


public class NumberJson extends AbstractJsonType{

	private Double number;
	public NumberJson(Double integer) {
		this.number = integer;
	}

	@Override
	public String toStringJson() {
		return number.toString();
	}
}
