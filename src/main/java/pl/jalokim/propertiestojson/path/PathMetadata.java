package pl.jalokim.propertiestojson.path;

import lombok.Data;
import pl.jalokim.propertiestojson.PropertyArrayHelper;

import static pl.jalokim.propertiestojson.Constants.NORMAL_DOT;


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
    private Object value;

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

    public void setValue(Object value) {
        if (!isLeaf()) {
            throw new RuntimeException("Cannot set value for not leaf: " + getCurrentFullPath());
        }
        this.value = value;
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

    @Override
    public String toString() {
        return "field='" + fieldName + '\'' +
               ", value=" + value +
               ", fullPath='" + getCurrentFullPath() + '}';
    }

    public boolean isArrayField() {
        return propertyArrayHelper != null;
    }
}
