package pl.jalokim.propertiestojson.object;


public class IntegerJson extends AbstractJsonType{

	private Integer integer;
	public IntegerJson(Integer integer) {
		this.integer = integer;
	}

	@Override
	public String toStringJson() {
		return integer.toString();
	}
}
