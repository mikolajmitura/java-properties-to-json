package pl.jalokim.propertiestojson.resolvers.hierarchy;

import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static pl.jalokim.propertiestojson.resolvers.primitives.JsonNullReferenceTypeResolver.NULL_RESOLVER;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.CANNOT_FIND_TYPE_RESOLVER_MSG;

public class JsonTypeResolversHierarchyResolver {

    private final Map<Class<?>, List<PrimitiveJsonTypeResolver<?>>> resolversByType = new HashMap<>();
    private final HierarchyClassResolver hierarchyClassResolver;

    public JsonTypeResolversHierarchyResolver(List<PrimitiveJsonTypeResolver> resolvers) {
        for(PrimitiveJsonTypeResolver<?> resolver : resolvers) {
            for(Class<?> canResolveType : resolver.getClassesWhichCanResolve()) {
                List<PrimitiveJsonTypeResolver<?>> resolversByClass = resolversByType.get(canResolveType);
                if (resolversByClass == null) {
                    List<PrimitiveJsonTypeResolver<?>> newResolvers = new ArrayList<>();
                    newResolvers.add(resolver);
                    resolversByType.put(canResolveType, newResolvers);
                } else {
                    resolversByClass.add(resolver);
                }
            }
        }
        List<Class<?>> typesWhichCanResolve = new ArrayList<>();
        for(PrimitiveJsonTypeResolver<?> resolver : resolvers) {
            typesWhichCanResolve.addAll(resolver.getClassesWhichCanResolve());
        }
        hierarchyClassResolver = new HierarchyClassResolver(typesWhichCanResolve);
    }

    public PrimitiveJsonTypeResolver<?> returnConcreteResolver(Object instance) {
        if (instance == null) {
            return NULL_RESOLVER;
        }
        Class<?> instanceClass = instance.getClass();
        List<PrimitiveJsonTypeResolver<?>> resolvers = resolversByType.get(instanceClass);
        if (resolvers == null) {
            Class<?> typeWhichCanResolve = hierarchyClassResolver.searchResolverClass(instance);
            if (typeWhichCanResolve == null) {
                throw new ParsePropertiesException(format(CANNOT_FIND_TYPE_RESOLVER_MSG, instanceClass));
            }
            resolvers = resolversByType.get(typeWhichCanResolve);
        }

        if (resolvers.size() > 1 && instanceClass != String.class) {
            // TODO test this
            throw new RuntimeException("Found: " + resolvers + " for type" + instanceClass);
        }

        if (resolvers.size() == 1 || instanceClass == String.class) {
            return resolvers.get(0);
        }

        /*
         * this one should never occurred!
         */
        throw new UnsupportedOperationException("resolvers: " + resolvers + " and instance class: " + instanceClass);
    }
}
