package pl.jalokim.propertiestojson.util;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.IntegerJson;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.object.StringJson;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.Map;
import java.util.Set;

import static pl.jalokim.propertiestojson.util.NumberUtil.getInt;
import static pl.jalokim.propertiestojson.util.NumberUtil.isInteger;

public class PropertiesToJsonParser {

	public static final String DOT = "\\.";

	// napisac test i dopiero zrobic refactoring

	public static String parseToJson(Map<String,String> prop){
		ObjectJson objectJson = new ObjectJson();
		for (String key: getAllKeyFromMap(prop)){
			fetchValuesToJsonObject(prop, objectJson, key);
		}
		return objectJson.toStringJson();
	}

	private static void fetchValuesToJsonObject(Map<String, String> prop, ObjectJson objectJson, String key) {
		String[] fields = key.split(DOT);
		ObjectJson currentObjectJson = objectJson;
		for (int i=0; i< fields.length; i++){
            String field = fields[i];
            if (i==fields.length-1){ // jezeli ostatni jest to typ prosty

                if (currentObjectJson.containsField(field)){
                    throw new ParsePropertiesException("already key is resonable as object not as primitive Type, error propertieskey: "+ key);
                }

                String propValue = prop.get(key);
                if (isInteger(propValue)){
                    currentObjectJson.addField(field, new IntegerJson(getInt(propValue)));
                } else {
                    currentObjectJson.addField(field, new StringJson(propValue));
                }
            } else {
                if (currentObjectJson.containsField(field)){
                    AbstractJsonType jsonType = currentObjectJson.getJsonTypeByFieldName(field); // idzie po nastepny object json
                    if (jsonType.getClass().equals(ObjectJson.class)){
                        currentObjectJson = (ObjectJson) jsonType;
                    } else {
                        throw new ParsePropertiesException("already given key is resonale as primitive type not as Object, error key: "+key);
                    }

                }else {
                    ObjectJson nextObjectJson = new ObjectJson();
                    currentObjectJson.addField(field, nextObjectJson);
                    currentObjectJson = nextObjectJson;
                }
            }
        }
	}

	private static Set<String> getAllKeyFromMap(Map<String, String> prop) {
		return prop.keySet();
	}


}
