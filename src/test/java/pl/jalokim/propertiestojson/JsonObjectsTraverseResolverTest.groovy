package pl.jalokim.propertiestojson

import pl.jalokim.propertiestojson.path.PathMetadata
import spock.lang.Specification
import spock.lang.Unroll

class JsonObjectsTraverseResolverTest extends Specification {

    @Unroll
    def "all given field after test has expected result"(String arrayFieldName, boolean expected) {
        when:
        PathMetadata pathMetadata = new PathMetadata()
        pathMetadata.setFieldName(arrayFieldName)
        boolean result = pathMetadata.isArrayField()
        then:
        result == expected
        where:
        arrayFieldName           | expected
        "array_name[12][13]"     | true
        "array_name[12][13][0]"  | true
        "array_name[12][13][11]" | true
        "array_name[12]"         | true
        "array_name[12][a]"      | false
        "array_name[12][12]a"    | false
        "array_name[0][01]"      | false
        "array_name[01][12]"     | true
        "array_[12]name"         | false
        "array_[]name"           | false
        "array_[12name"          | false
        "array]name"             | false
        "array[name]" | false

    }
}
