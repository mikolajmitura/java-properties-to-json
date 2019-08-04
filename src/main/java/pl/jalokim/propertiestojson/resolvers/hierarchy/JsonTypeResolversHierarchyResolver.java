package pl.jalokim.propertiestojson.resolvers.hierarchy;

import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static pl.jalokim.propertiestojson.resolvers.primitives.JsonNullReferenceTypeResolver.NULL_RESOLVER;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.CANNOT_FIND_TYPE_RESOLVER_MSG;

/**
 * It looks for sufficient resolver, firstly will looks for exactly match class type provided by method {@link PrimitiveJsonTypeResolver#getClassesWhichCanResolve()}
 * if not then will looks for closets parent class or parent interface.
 * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
 * If will find only closets super interfaces (at the same level) then will throw exception...
 */
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
            throw new ParsePropertiesException("Found: " + resolvers.stream()
                                                                    .map(object -> object.getClass())
                                                                    .collect(toList()) + " for type" + instanceClass + " expected only one!");
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
