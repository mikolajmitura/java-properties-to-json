package pl.jalokim.propertiestojson.object;

public abstract class PrimitiveJsonType<T> extends AbstractJsonType{

    T value;

    public PrimitiveJsonType(T value){
        this.value = value;
    }

    @Override
    public String toStringJson() {
        return value.toString();
    }
}
