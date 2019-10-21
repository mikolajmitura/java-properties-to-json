package pl.jalokim.propertiestojson.object;

import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.util.StringToJsonStringWrapper;
import pl.jalokim.propertiestojson.util.exception.CannotOverrideFieldException;

import java.util.HashMap;
import java.util.Map;

import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_END;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_START;
import static pl.jalokim.propertiestojson.Constants.NEW_LINE_SIGN;
import static pl.jalokim.propertiestojson.object.MergableObject.mergeObjectIfPossible;
import static pl.jalokim.utils.collection.CollectionUtils.getLastIndex;

public class ObjectJsonType extends AbstractJsonType implements MergableObject<ObjectJsonType> {

    private Map<String, AbstractJsonType> fields = new HashMap<>();

    public void addField(final String field, final AbstractJsonType object, PathMetadata currentPathMetaData) {
        if(object instanceof SkipJsonField) {
            return;
        }

        AbstractJsonType oldFieldValue = fields.get(field);
        if(oldFieldValue != null) {
            if(oldFieldValue instanceof MergableObject && object instanceof MergableObject) {
                mergeObjectIfPossible(oldFieldValue, object, currentPathMetaData);
            } else {
                throw new CannotOverrideFieldException(currentPathMetaData.getCurrentFullPath(),
                                                       oldFieldValue,
                                                       currentPathMetaData.getOriginalPropertyKey());
            }
        } else {
            fields.put(field, object);
        }
    }

    public boolean containsField(String field) {
        return fields.containsKey(field);
    }

    public AbstractJsonType getField(String field) {
        return fields.get(field);
    }

    public ArrayJsonType getJsonArray(String field) {
        return (ArrayJsonType) fields.get(field);
    }

    @Override
    public String toStringJson() {
        StringBuilder result = new StringBuilder().append(JSON_OBJECT_START);
        int index = 0;
        int lastIndex = getLastIndex(fields.keySet());
        for(String fieldName : fields.keySet()) {
            AbstractJsonType object = fields.get(fieldName);
            String lastSign = index == lastIndex ? EMPTY_STRING : NEW_LINE_SIGN;
            result.append(StringToJsonStringWrapper.wrap(fieldName))
                  .append(":")
                  .append(object.toStringJson())
                  .append(lastSign);
            index++;
        }
        result.append(JSON_OBJECT_END);
        return result.toString();
    }

    @Override
    public void merge(ObjectJsonType mergeWith, PathMetadata currentPathMetadata) {
        for(String fieldName : mergeWith.fields.keySet()) {
            addField(fieldName, mergeWith.getField(fieldName), currentPathMetadata);
        }
    }
}
