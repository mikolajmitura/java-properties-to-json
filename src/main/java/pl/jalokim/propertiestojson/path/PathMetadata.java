package pl.jalokim.propertiestojson.path;

import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.NORMAL_DOT;

import lombok.Data;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.exception.NotLeafValueException;
import pl.jalokim.propertiestojson.object.AbstractJsonType;

@Data
public class PathMetadata {

    private static final String NUMBER_PATTERN = "([1-9]\\d*)|0";
    public static final String INDEXES_PATTERN = "\\s*(\\[\\s*((" + NUMBER_PATTERN + ")|\\*)\\s*]\\s*)+";

    private static final String WORD_PATTERN = "(.)*";

    private final String originalPropertyKey;
    private PathMetadata parent;
    private String fieldName;
    private String originalFieldName;
    private PathMetadata child;
    private PropertyArrayHelper propertyArrayHelper;
    private Object rawValue;
    private AbstractJsonType jsonValue;

    public boolean isLeaf() {
        return child == null;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public String getCurrentFullPath() {
        return parent == null ? originalFieldName : parent.getCurrentFullPath() + NORMAL_DOT + originalFieldName;
    }

    public PathMetadata getLeaf() {
        PathMetadata current = this;
        while (current.getChild() != null) {
            current = current.getChild();
        }
        return current;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
        if (fieldName.matches(WORD_PATTERN + INDEXES_PATTERN)) {
            propertyArrayHelper = new PropertyArrayHelper(fieldName);
            this.fieldName = propertyArrayHelper.getArrayFieldName();
        }
    }

    public void setRawValue(Object rawValue) {
        if (!isLeaf()) {
            throw new NotLeafValueException("Cannot set value for not leaf: " + getCurrentFullPath());
        }
        this.rawValue = rawValue;
    }

    public String getOriginalPropertyKey() {
        return originalPropertyKey;
    }

    public PathMetadata getRoot() {
        PathMetadata current = this;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }

    public String getCurrentFullPathWithoutIndexes() {
        String parentFullPath = isRoot() ? EMPTY_STRING : getParent().getCurrentFullPath() + NORMAL_DOT;
        return parentFullPath + getFieldName();
    }

    public AbstractJsonType getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(AbstractJsonType jsonValue) {
        if (!isLeaf()) {
            throw new NotLeafValueException("Cannot set value for not leaf: " + getCurrentFullPath());
        }
        this.jsonValue = jsonValue;
    }

    @Override
    public String toString() {
        return "field='" + fieldName + '\''
            + ", rawValue=" + rawValue
            + ", fullPath='" + getCurrentFullPath() + '}';
    }

    public boolean isArrayField() {
        return propertyArrayHelper != null;
    }
}
