package pl.jalokim.propertiestojson.object;


public class NumberJson extends AbstractJsonType{

	private Double number;
	public NumberJson(Double number) {
		this.number = number;
	}

	@Override
	public String toStringJson() {
		return number.toString();
	}
}
