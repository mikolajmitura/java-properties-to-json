package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.delegator.PrimitiveJsonTypeDelegatorResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.SuperObjectToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToObjectResolver;
import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter;


/**
 * When given text contains parsable json value, json object or json array then try build instance of ObjectJsonType or ArrayJsonType It will invoke {@link
 * #returnConcreteJsonType(PrimitiveJsonTypesResolver, Object, String)} after conversion from string (raw property value to some object) This Resolver will
 * convert number in json as number, text as text, boolean as boolean... It uses independent, own list of json type resolvers. The setup of resolvers in {@link
 * PropertiesToJsonConverter#PropertiesToJsonConverter(PrimitiveJsonTypeResolver... primitiveResolvers)} will not have impact of those list.
 */
@Deprecated
public class ObjectFromTextJsonTypeResolver extends PrimitiveJsonTypeDelegatorResolver<Object> {

    public ObjectFromTextJsonTypeResolver() {
        super(new TextToObjectResolver(), new SuperObjectToJsonTypeConverter());
    }

    /**
     * It convert to implementation of AbstractJsonType through use of json for conversion from java object to raw json, then raw json convert to
     * com.google.gson.JsonElement, and this JsonElement to instance of AbstractJsonType (json object, array json, or simple text json)
     *
     * @param propertyValue java bean to convert to instance of AbstractJsonType.
     * @param propertyKey currently processed propertyKey from properties.
     * @return instance of AbstractJsonType
     */
    public static AbstractJsonType convertFromObjectToJson(Object propertyValue, String propertyKey) {
        return SuperObjectToJsonTypeConverter.convertFromObjectToJson(propertyValue, propertyKey);
    }
}
