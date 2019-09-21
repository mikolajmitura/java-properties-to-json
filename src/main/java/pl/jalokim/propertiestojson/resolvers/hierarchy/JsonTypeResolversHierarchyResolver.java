package pl.jalokim.propertiestojson.resolvers.hierarchy;

import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.adapter.PrimitiveJsonTypeResolverToNewApiAdapter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static pl.jalokim.propertiestojson.resolvers.primitives.object.NullToJsonTypeConverter.NULL_TO_JSON_RESOLVER;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.CANNOT_FIND_TYPE_RESOLVER_MSG;

/**
 * It looks for sufficient resolver, firstly will looks for exactly match class type provided by method {@link PrimitiveJsonTypeResolver#getClassesWhichCanResolve()}
 * if not then will looks for closets parent class or parent interface.
 * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
 * If will find only closets super interfaces (at the same level) then will throw exception...
 */
public class JsonTypeResolversHierarchyResolver {

    private final Map<Class<?>, List<ObjectToJsonTypeResolver<?>>> resolversByType = new HashMap<>();
    private final HierarchyClassResolver hierarchyClassResolver;

    public JsonTypeResolversHierarchyResolver(List<ObjectToJsonTypeResolver> resolvers) {
        for(ObjectToJsonTypeResolver<?> resolver : resolvers) {
            for(Class<?> canResolveType : resolver.getClassesWhichCanResolve()) {
                List<ObjectToJsonTypeResolver<?>> resolversByClass = resolversByType.get(canResolveType);
                if (resolversByClass == null) {
                    List<ObjectToJsonTypeResolver<?>> newResolvers = new ArrayList<>();
                    newResolvers.add(resolver);
                    resolversByType.put(canResolveType, newResolvers);
                } else {
                    resolversByClass.add(resolver);
                }
            }
        }
        List<Class<?>> typesWhichCanResolve = new ArrayList<>();
        for(ObjectToJsonTypeResolver<?> resolver : resolvers) {
            typesWhichCanResolve.addAll(resolver.getClassesWhichCanResolve());
        }
        hierarchyClassResolver = new HierarchyClassResolver(typesWhichCanResolve);
    }

    public ObjectToJsonTypeResolver<?> returnConcreteResolver(Object instance) {
        if (instance == null) {
            return NULL_TO_JSON_RESOLVER;
        }
        Class<?> instanceClass = instance.getClass();
        List<ObjectToJsonTypeResolver<?>> resolvers = resolversByType.get(instanceClass);
        if (resolvers == null) {
            Class<?> typeWhichCanResolve = hierarchyClassResolver.searchResolverClass(instance);
            if (typeWhichCanResolve == null) {
                throw new ParsePropertiesException(format(CANNOT_FIND_TYPE_RESOLVER_MSG, instanceClass));
            }
            resolvers = resolversByType.get(typeWhichCanResolve);
        }

        if (resolvers.size() > 1 && instanceClass != String.class) {
            if (resolvers.stream().anyMatch(resolver -> resolver instanceof PrimitiveJsonTypeResolverToNewApiAdapter)) {
                List<Class<?>> resolversClasses = resolvers.stream()
                                                          .map(resolver-> {
                                                              if (resolver instanceof PrimitiveJsonTypeResolverToNewApiAdapter) {
                                                                  PrimitiveJsonTypeResolverToNewApiAdapter adapter = (PrimitiveJsonTypeResolverToNewApiAdapter) resolver;
                                                                  PrimitiveJsonTypeResolver oldImplementation = adapter.getOldImplementation();
                                                                  return oldImplementation.getClass();
                                                              }
                                                              return resolver.getClass();
                                                          }).collect(toList());
                throw new ParsePropertiesException("Found: " + new ArrayList<>(resolversClasses) + " for type" + instanceClass + " expected only one!");
            }

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
