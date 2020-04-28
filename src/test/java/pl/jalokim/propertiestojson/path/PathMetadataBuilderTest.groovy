package pl.jalokim.propertiestojson.path

import org.assertj.core.api.Assertions
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicReference

class PathMetadataBuilderTest extends Specification {

    def "return expected list of property parts"() {
        given:
        String propertyKey = "field.nextfield.array[12][12].anotherField[external.key.leaf].anotherField[[external.key.leaf][some.strange.things]].fieldLeaf"
        when:
        def parts = PathMetadataBuilder.getPropertyParts(propertyKey)
        then:
        parts[0] == "field"
        parts[1] == "nextfield"
        parts[2] == "array[12][12]"
        parts[3] == "anotherField[external.key.leaf]"
        parts[4] == "anotherField[[external.key.leaf][some.strange.things]]"
        parts[5] == "fieldLeaf"
    }

    def "create Expected path metadata"() {
        given:
        String propertyKey = "field.nextfield.array[12][12].fieldLeaf"
        when:
        PathMetadata root = PathMetadataBuilder.createRootPathMetaData(propertyKey)
        AtomicReference<PathMetadata> reference = new AtomicReference(root)
        then:
        hasExpectedValues(reference, true, false, "field", "field", propertyKey)
        hasExpectedValues(reference, false, false, "nextfield", "field.nextfield", propertyKey)
        hasExpectedValues(reference, false, false, "array[12][12]", "field.nextfield.array[12][12]", propertyKey)
        hasExpectedValues(reference, false, true, "fieldLeaf", "field.nextfield.array[12][12].fieldLeaf", propertyKey)

        root.getLeaf().getOriginalPropertyKey() == propertyKey
        root.getLeaf().getFieldName() == "fieldLeaf"
        root.getLeaf().getRoot().getFieldName() == "field"
    }

    private static boolean hasExpectedValues(AtomicReference<PathMetadata> currentReference, boolean isRoot, boolean isLeaf,
                                             String fieldName, String currentFullPath, String originalPropertyKey) {
        PathMetadata pathMetadata = currentReference.get()
        currentReference.set(pathMetadata.getChild())

        Assertions.assertThat(pathMetadata.isRoot()).isEqualTo(isRoot)
        Assertions.assertThat(pathMetadata.isLeaf()).isEqualTo(isLeaf)
        Assertions.assertThat(pathMetadata.getOriginalFieldName()).isEqualTo(fieldName)
        Assertions.assertThat(pathMetadata.getCurrentFullPath()).isEqualTo(currentFullPath)
        Assertions.assertThat(pathMetadata.getOriginalPropertyKey()).isEqualTo(originalPropertyKey)

        return true
    }

}

