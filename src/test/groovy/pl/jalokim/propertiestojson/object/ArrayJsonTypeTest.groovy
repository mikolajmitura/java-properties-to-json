package pl.jalokim.propertiestojson.object

import spock.lang.Specification

class ArrayJsonTypeTest extends Specification {

    def "put to 200 index"() {
        given:
        ArrayJsonType arrayJsonType = new ArrayJsonType()
        when:
        arrayJsonType.addElement(200, new StringJsonType("test"), null)
        then:
        arrayJsonType.getElement(200).toStringJson() == "\"test\""
    }
}
