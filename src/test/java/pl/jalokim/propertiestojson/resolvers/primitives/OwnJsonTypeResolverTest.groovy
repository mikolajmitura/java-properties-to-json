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
import java.util.concurrent.atomic.AtomicInteger

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

    def "resolver will serve for 3 classes and extends all methods from old api resolver from beans"() {
        def jsonSlurper = new JsonSlurper()
        when:
        SecureResolver tested = new SecureResolver()
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                tested,
                new BooleanJsonTypeResolver()
        )

        String secHash = " space _ _ xx "
        Properties properties = new Properties()
        properties.put("object.sec1", new SecureBean1("test1", "test2"))
        properties.put("object.sec2", new SecureBean2(secHash))
        properties.put("object.sec3", new SecureBean3("hash_*@#"))
        properties.put("object.boolean1", true)
        properties.put("object.numberAsText", "12")

        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.sec1 == "hash1:#1:hash2:#2" + ":MAIN_HASH: " + SecureResolver.HASH
        jsonObject.object.sec2 == "hash:" + secHash + "#1" + ":MAIN_HASH: " + SecureResolver.HASH
        jsonObject.object.sec3 == "hash3:#3" + ":MAIN_HASH: " + SecureResolver.HASH
        jsonObject.object.boolean1 == true
        jsonObject.object.numberAsText == "12"
        assertInvocations(tested, "resolveTypeOfResolver", 2)
        assertInvocations(tested, "returnJsonType", 3)
        assertInvocations(tested, "returnConcreteValueWhenCanBeResolved", 0)
        assertInvocations(tested, "returnConvertedValueForClearedText", 0)
        assertInvocations(tested, "returnConcreteJsonType", 3)
        assertInvocations(tested, "getClassesWhichCanResolve", 2)
    }

    def "resolver will serve for 3 classes and extends all methods from old api resolver from raw text"() {
        def jsonSlurper = new JsonSlurper()
        when:
        SecureResolver tested = new SecureResolver()
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                tested,
                new BooleanJsonTypeResolver()
        )

        Map<String, String> properties = new HashMap<>()
        properties.put("object.sec_1", "sec_hash: _^ _^ _ ")
        properties.put("object.sec_2", "sec_hash: _^1_^2_ ")
        properties.put("object.boolean1", "true")
        properties.put("object.numberAsText", "12")

        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.sec_1 == "hash:" +  " _^ _^ _ " + "#1" + ":MAIN_HASH: " + SecureResolver.HASH
        jsonObject.object.sec_2 == "hash:" +  " _^1_^2_ " + "#1" + ":MAIN_HASH: " + SecureResolver.HASH
        jsonObject.object.boolean1 == true
        jsonObject.object.numberAsText == "12"
        assertInvocations(tested, "resolveTypeOfResolver", 2)
        assertInvocations(tested, "returnJsonType", 2)
        assertInvocations(tested, "returnConcreteValueWhenCanBeResolved", 4)
        assertInvocations(tested, "returnConvertedValueForClearedText", 4)
        assertInvocations(tested, "returnConcreteJsonType", 2)
        assertInvocations(tested, "getClassesWhichCanResolve", 2)
    }

    private static boolean assertInvocations(SecureResolver instance, String method, int expected) {
        println(method + " " + instance.invocationMap.get(method))
        return instance.invocationMap.get(method).get() == expected
    }

    private static class SecureResolver extends PrimitiveJsonTypeResolver {

        static final String HASH = "sfgskdjg09384"
        static final Map<SecureResolver, AtomicInteger> resolveTypeOfResolverMap = new HashMap<>()
        private Map<String, AtomicInteger> invocationMap = invocationMap()

        Map<String, AtomicInteger> invocationMap() {
            Map<String, AtomicInteger> invocationMap = new HashMap<>()
            invocationMap.put("resolveTypeOfResolver", resolveTypeOfResolverMap.get(this))
            invocationMap.put("returnJsonType", new AtomicInteger())
            invocationMap.put("returnConcreteValueWhenCanBeResolved", new AtomicInteger())
            invocationMap.put("returnConvertedValueForClearedText", new AtomicInteger())
            invocationMap.put("returnConcreteJsonType", new AtomicInteger())
            invocationMap.put("getClassesWhichCanResolve", new AtomicInteger())
            return invocationMap
        }

        @Override
        Class<?> resolveTypeOfResolver() {
            resolveTypeOfResolverMap.putIfAbsent(this, new AtomicInteger())
            resolveTypeOfResolverMap.get(this).incrementAndGet()
            return Object.class
        }

        @Override
        AbstractJsonType returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                        Object propertyValue,
                                        String propertyKey) {
            invocationMap.get("returnJsonType").incrementAndGet()
            if (propertyValue instanceof SecureBean1 ||
                    propertyValue instanceof SecureBean2 ||
                    propertyValue instanceof SecureBean3) {
                return super.returnJsonType(primitiveJsonTypesResolver, propertyValue, propertyKey)
            }
            throw new UnsupportedOperationException("Cannot support type: " + propertyValue.getClass())
        }

        @Override
        AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object convertedValue, String propertyKey) {
            invocationMap.get("returnConcreteJsonType").incrementAndGet()
            return new StringJsonType(convertedValue.toString() + ":MAIN_HASH: " + HASH)
        }

        @Override
        protected Optional<Object> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                                        String propertyValue,
                                                                        String propertyKey) {
            invocationMap.get("returnConcreteValueWhenCanBeResolved").incrementAndGet()
            if (propertyValue.contains("sec_hash:")) {
                return Optional.of(new SecureBean2(propertyValue.replace("sec_hash:", "")))
            }
            return Optional.empty()
        }

        @Override
        Optional<Object> returnConvertedValueForClearedText(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                            String propertyValue,
                                                            String propertyKey) {
            invocationMap.get("returnConvertedValueForClearedText").incrementAndGet()
            if (propertyValue == null) {
                return Optional.empty()
            }
            return returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }


        @Override
        List<Class<?>> getClassesWhichCanResolve() {
            invocationMap.get("getClassesWhichCanResolve").incrementAndGet()
            return Arrays.asList(SecureBean1.class, SecureBean2.class, SecureBean3.class)
        }
    }

    private static class SecureBean1 {
        private final String hash1
        private final String hash2

        SecureBean1(String hash1, String hash2) {
            this.hash1 = hash1
            this.hash2 = hash2
        }


        @Override
        String toString() {
            return "hash1:#1:hash2:#2"
        }
    }

    private static class SecureBean2 {
        private final String hash

        SecureBean2(String hash) {
            this.hash = hash
        }

        @Override
        String toString() {
            return "hash:" + hash + "#1"
        }
    }

    private static class SecureBean3 {
        private final String hash3

        SecureBean3(String hash3) {
            this.hash3 = hash3
        }

        @Override
        String toString() {
            return "hash3:#3"
        }
    }
}
