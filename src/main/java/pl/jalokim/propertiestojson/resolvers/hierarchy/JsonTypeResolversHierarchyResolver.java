package pl.jalokim.propertiestojson.resolvers.hierarchy;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.CANNOT_FIND_JSON_TYPE_OBJ;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.CANNOT_FIND_TYPE_RESOLVER_MSG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.adapter.PrimitiveJsonTypeResolverToNewApiAdapter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

/**
 * It looks for sufficient resolver, firstly will looks for exactly match class type provided by method {@link
 * PrimitiveJsonTypeResolver#getClassesWhichCanResolve()} if not then will looks for closets parent class or parent interface. If will find resolver for parent
 * class or parent interface at the same level, then will get parent super class as first. If will find only closets super interfaces (at the same level) then
 * will throw exception...
 */
public class JsonTypeResolversHierarchyResolver {

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final Map<Class<?>, List<ObjectToJsonTypeConverter<?>>> resolversByType = new HashMap<>();
    private final HierarchyClassResolver hierarchyClassResolver;

    public JsonTypeResolversHierarchyResolver(List<ObjectToJsonTypeConverter<?>> resolvers) {
        for (ObjectToJsonTypeConverter<?> resolver : resolvers) {
            for (Class<?> typeWhichCanBeResolved : resolver.getClassesWhichCanResolve()) {
                List<ObjectToJsonTypeConverter<?>> resolversByClass = resolversByType.get(typeWhichCanBeResolved);
                if (resolversByClass == null) {
                    List<ObjectToJsonTypeConverter<?>> newResolvers = new ArrayList<>();
                    newResolvers.add(resolver);
                    resolversByType.put(typeWhichCanBeResolved, newResolvers);
                } else {
                    resolversByClass.add(resolver);
                }
            }
        }
        List<Class<?>> typesWhichCanResolve = new ArrayList<>();
        for (ObjectToJsonTypeConverter<?> resolver : resolvers) {
            typesWhichCanResolve.addAll(resolver.getClassesWhichCanResolve());
        }
        hierarchyClassResolver = new HierarchyClassResolver(typesWhichCanResolve);
    }

    public AbstractJsonType returnConcreteJsonTypeObject(PrimitiveJsonTypesResolver mainResolver,
        Object instance,
        String propertyKey) {
        Objects.requireNonNull(instance);
        Class<?> instanceClass = instance.getClass();
        List<ObjectToJsonTypeConverter<?>> resolvers = resolversByType.get(instanceClass);
        if (resolvers == null) {
            Class<?> typeWhichCanResolve = hierarchyClassResolver.searchResolverClass(instance);
            if (typeWhichCanResolve == null) {
                throw new ParsePropertiesException(format(CANNOT_FIND_TYPE_RESOLVER_MSG, instanceClass));
            }
            resolvers = resolversByType.get(typeWhichCanResolve);
        }

        if (!resolvers.isEmpty()) {
            if (instanceClass != String.class && resolvers.size() > 1
                && resolvers.stream().anyMatch(resolver -> resolver instanceof PrimitiveJsonTypeResolverToNewApiAdapter)) {
                List<Class<?>> resolversClasses = resolvers.stream()
                    .map(resolver -> {
                        if (resolver instanceof PrimitiveJsonTypeResolverToNewApiAdapter) {
                            PrimitiveJsonTypeResolverToNewApiAdapter adapter = (PrimitiveJsonTypeResolverToNewApiAdapter) resolver;
                            PrimitiveJsonTypeResolver<?> oldImplementation = adapter.getOldImplementation();
                            return oldImplementation.getClass();
                        }
                        return resolver.getClass();
                    }).collect(toList());
                throw new ParsePropertiesException("Found: " + new ArrayList<>(resolversClasses) + " for type" + instanceClass + " expected only one!");
            }

            for (ObjectToJsonTypeConverter<?> resolver : resolvers) {
                Optional<AbstractJsonType> abstractJsonType = resolver.returnOptionalJsonType(mainResolver, instance, propertyKey);
                if (abstractJsonType.isPresent()) {
                    return abstractJsonType.get();
                }
            }
        }

        throw new ParsePropertiesException(format(CANNOT_FIND_JSON_TYPE_OBJ, instanceClass, propertyKey, instance));
    }
}
