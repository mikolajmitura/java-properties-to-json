package pl.jalokim.propertiestojson.path;

import com.google.common.annotations.VisibleForTesting;
import java.util.List;

public final class PathMetadataBuilder {

    private PathMetadataBuilder() {
    }

    public static PathMetadata createRootPathMetaData(String propertyKey) {
        List<String> fields = getPropertyParts(propertyKey);
        PathMetadata currentPathMetadata = null;

        for (String field : fields) {
            PathMetadata nextPathMetadata = new PathMetadata(propertyKey);
            nextPathMetadata.setParent(currentPathMetadata);
            nextPathMetadata.setFieldName(field);
            nextPathMetadata.setOriginalFieldName(field);

            if (currentPathMetadata != null) {
                currentPathMetadata.setChild(nextPathMetadata);
            }
            currentPathMetadata = nextPathMetadata;
        }
        return currentPathMetadata.getRoot();
    }

    @VisibleForTesting
    static List<String> getPropertyParts(String property) {
        char[] chars = property.toCharArray();
        ParserContext parserContext = new ParserContext();
        for (char currentChar : chars) {
            parserContext.parseNextChar(currentChar);
        }
        return parserContext.getPropertiesParts();
    }
}
