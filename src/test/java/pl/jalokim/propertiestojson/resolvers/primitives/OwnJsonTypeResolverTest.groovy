package pl.jalokim.propertiestojson.resolvers.primitives

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.object.AbstractJsonType
import pl.jalokim.propertiestojson.object.NumberJsonType
import pl.jalokim.propertiestojson.object.ObjectJsonType
import pl.jalokim.propertiestojson.object.StringJsonType
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver
import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter
import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneId

class OwnJsonTypeResolverTest extends Specification {

    def "from text will convert to expected json, will invoke custom resolver"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new OwnBeanPrimitiveJsonTypeResolver(),
                new ObjectFromTextJsonTypeResolver(),
                new BooleanJsonTypeResolver()
        )

        Map<String, String> properties = new HashMap<>()
        properties.put("object.ownBean", "package.OwnBean:text_value,2019")
        properties.put("object.booleanAsTest", "true")
        properties.put("object.number", "12")

        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.ownBean.textField == "text_value"
        jsonObject.object.ownBean.timestamp == 1547247600
        jsonObject.object.booleanAsTest == true
        jsonObject.object.number == "12"
    }

    def "from concrete OwnBean will convert to expected json, will invoke custom resolver"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new OwnBeanPrimitiveJsonTypeResolver(),
                new ObjectFromTextJsonTypeResolver(),
                new BooleanJsonTypeResolver()
        )

        Properties properties = new Properties()
        properties.put("object.ownBean", new OwnBean("text_value", LocalDate.of(2019, 1, 12)))
        properties.put("object.booleanAsTest", true)
        properties.put("object.number", "12")

        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.ownBean.textField == "text_value"
        jsonObject.object.ownBean.timestamp == 1547247600
        jsonObject.object.booleanAsTest == true
        jsonObject.object.number == "12"
    }

    def "throw exception while will not convert deprecated property key during process simple text in OwnBeanPrimitiveJsonTypeResolver"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new OwnBeanPrimitiveJsonTypeResolver()
        )

        Map<String, String> properties = new HashMap<>()
        properties.put("deprecated.one", "12")

        converter.convertToJson(properties)
        then:
        RuntimeException ex = thrown()
        ex.getMessage() == "property: deprecated.one is not supported!"
    }

    def "throw exception while will not convert deprecated property key during process OwnBean"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new OwnBeanPrimitiveJsonTypeResolver()
        )

        Properties properties = new Properties()
        properties.put("deprecated.two", new OwnBean("text_value", LocalDate.of(2019, 1, 12)))

        converter.convertToJson(properties)
        then:
        RuntimeException ex = thrown()
        ex.getMessage() == "property: deprecated.two is not supported!"
    }

    private static class OwnBeanPrimitiveJsonTypeResolver extends PrimitiveJsonTypeResolver<OwnBean> {

        @Override
        protected Optional<OwnBean> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
            if (propertyKey == ("deprecated.one")) {
                throw new RuntimeException("property: deprecated.one is not supported!")
            }

            if (propertyValue.matches("^package.OwnBean:.*,.*")) {
                String[] values = propertyValue.replaceFirst("^package.OwnBean:", "").split(",")
                return Optional.ofNullable(new OwnBean(values[0], LocalDate.of(Integer.parseInt(values[1]), 1, 12)))
            }
            return Optional.empty()
        }

        @Override
        AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, OwnBean propertyValue, String propertyKey) {
            if (propertyKey == ("deprecated.two")) {
                throw new RuntimeException("property: deprecated.two is not supported!")
            }
            return new OwnBeanJsonType(propertyValue.textField,
                    propertyValue.localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond())
        }
    }

    private static class OwnBean {
        private final String textField
        private final LocalDate localDate

        OwnBean(String textField, LocalDate localDate) {
            this.textField = textField
            this.localDate = localDate
        }
    }

    private static class OwnBeanJsonType extends ObjectJsonType {
        OwnBeanJsonType(String textField, Long timestamp) {
            addField("textField", new StringJsonType(textField), null)
            addField("timestamp", new NumberJsonType(timestamp), null)
        }
    }
}
