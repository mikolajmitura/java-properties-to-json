package pl.jalokim.propertiestojson.resolvers.primitives;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

public class PrimitiveJsonTypeResolverTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void cannotResolveGenericTypeOfClass() {
        // then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage("Cannot find generic type for resolver: " + SomePrimitiveJsonTypeResolver.class + " You can resolve it by one of below:"
                                 + "\n 1. override method resolveTypeOfResolver() for provide explicit class type " +
                                 "\n 2. add generic type during extension of PrimitiveJsonTypeResolver "
                                 + "'class " + SomePrimitiveJsonTypeResolver.class.getSimpleName() + " extends PrimitiveJsonTypeResolver<GIVEN_TYPE>'");
        // when
        new SomePrimitiveJsonTypeResolver();
    }

    private static class SomePrimitiveJsonTypeResolver extends PrimitiveJsonTypeResolver {

        @Override
        protected Object returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
            return null;
        }

        @Override
        public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey) {
            return null;
        }
    }

}