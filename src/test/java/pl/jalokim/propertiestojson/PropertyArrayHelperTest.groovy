package pl.jalokim.propertiestojson

import spock.lang.Specification
import spock.lang.Unroll

class PropertyArrayHelperTest extends Specification {

    def "get expected name from array field"(String arrayFieldName, String expectedArrayName) {
        when:
        String result = PropertyArrayHelper.getNameFromArray(arrayFieldName)
        then:
        expectedArrayName == result
        where:
        arrayFieldName | expectedArrayName
        "array [12] [12]" | "array"
        "array [12]" | "array"
        "array_test !@# [12]" | "array_test !@#"
    }

    @Unroll
    def "get expected indexes from array fields"(String arrayFieldName, int[] expectedIndexes) {
        when:
        def resultIndexes = PropertyArrayHelper.getIndexesFromArrayField(arrayFieldName)
        then:
        resultIndexes == expectedIndexes
        where:
        arrayFieldName | expectedIndexes
        "arrayField [12] [ 13]" | [12, 13]
        "arrayField[_] [11]" | [11]
        "arrayField [12] [ 13] [ 11 ] " | [12, 13, 11]
    }
}
