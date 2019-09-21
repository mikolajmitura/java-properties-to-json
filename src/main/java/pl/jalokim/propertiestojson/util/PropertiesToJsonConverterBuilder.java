package pl.jalokim.propertiestojson.util;

import pl.jalokim.propertiestojson.resolvers.primitives.object.BooleanToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.CharacterToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.ElementsToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.NullToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.NumberToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.SuperObjectToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToBooleanResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToCharacterResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToElementsResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToEmptyStringResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToJsonNullReferenceResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToNumberResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToObjectResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pl.jalokim.propertiestojson.resolvers.primitives.object.NullToJsonTypeConverter.NULL_TO_JSON_RESOLVER;
import static pl.jalokim.propertiestojson.resolvers.primitives.string.TextToEmptyStringResolver.EMPTY_TEXT_RESOLVER;
import static pl.jalokim.propertiestojson.resolvers.primitives.string.TextToJsonNullReferenceResolver.TEXT_TO_NULL_JSON_RESOLVER;

public class PropertiesToJsonConverterBuilder {

    static final List<ObjectToJsonTypeConverter> TO_JSON_TYPE_CONVERTERS = defaultConverters();
    static final List<TextToConcreteObjectResolver> TO_OBJECT_RESOLVERS = defaultResolvers();

    static List<ObjectToJsonTypeConverter> defaultConverters() {
        List<ObjectToJsonTypeConverter> toJsonTypeConverters = new ArrayList<>();
        toJsonTypeConverters.add(new ElementsToJsonTypeConverter());
        toJsonTypeConverters.add(new SuperObjectToJsonTypeConverter());
        toJsonTypeConverters.add(new NumberToJsonTypeConverter());
        toJsonTypeConverters.add(new CharacterToJsonTypeConverter());
        toJsonTypeConverters.add(new BooleanToJsonTypeConverter());
        return Collections.unmodifiableList(toJsonTypeConverters);
    }

    static List<TextToConcreteObjectResolver> defaultResolvers() {
        // order is crucial
        List<TextToConcreteObjectResolver> toObjectResolvers = new ArrayList<>();
        toObjectResolvers.add(new TextToElementsResolver());
        toObjectResolvers.add(new TextToObjectResolver());
        toObjectResolvers.add(new TextToNumberResolver());
        toObjectResolvers.add(new TextToCharacterResolver());
        toObjectResolvers.add(new TextToBooleanResolver());
        return Collections.unmodifiableList(toObjectResolvers);
    }

    private final List<ObjectToJsonTypeConverter> converters = new ArrayList<>();
    private final List<TextToConcreteObjectResolver> resolvers = new ArrayList<>();

    private NullToJsonTypeConverter nullToJsonConverter = NULL_TO_JSON_RESOLVER;
    private TextToJsonNullReferenceResolver textToJsonNullResolver = TEXT_TO_NULL_JSON_RESOLVER;
    private TextToEmptyStringResolver textToEmptyStringResolver = EMPTY_TEXT_RESOLVER;
    private boolean skipNul = false;

    public static PropertiesToJsonConverterBuilder builder() {
        return new PropertiesToJsonConverterBuilder();
    }

    public PropertiesToJsonConverterBuilder onlyCustomConverters(ObjectToJsonTypeConverter... converters) {
        this.converters.addAll(Arrays.asList(converters));
        return this;
    }

    public PropertiesToJsonConverterBuilder defaultAndCustomConverters(ObjectToJsonTypeConverter... converters) {
        this.converters.addAll(Arrays.asList(converters));
        this.converters.addAll(TO_JSON_TYPE_CONVERTERS);
        return this;
    }

    public PropertiesToJsonConverterBuilder onlyCustomConverters(TextToConcreteObjectResolver... resolvers) {
        this.resolvers.addAll(Arrays.asList(resolvers));
        return this;
    }

    public PropertiesToJsonConverterBuilder defaultAndCustomConverters(TextToConcreteObjectResolver... resolvers) {
        this.resolvers.addAll(Arrays.asList(resolvers));
        this.resolvers.addAll(TO_OBJECT_RESOLVERS);
        return this;
    }

    public PropertiesToJsonConverterBuilder overrideNullToJsonConverter(NullToJsonTypeConverter nullToJsonConverter) {
        this.nullToJsonConverter = nullToJsonConverter;
        return this;
    }

    public PropertiesToJsonConverterBuilder overrideTextToJsonNullResolver(TextToJsonNullReferenceResolver textToJsonNullResolver) {
        this.textToJsonNullResolver = textToJsonNullResolver;
        return this;
    }

    public PropertiesToJsonConverterBuilder overridetTextToEmptyStringResolver(TextToEmptyStringResolver textToEmptyStringResolver) {
        this.textToEmptyStringResolver = textToEmptyStringResolver;
        return this;
    }

    public PropertiesToJsonConverterBuilder skipNulls() {
        skipNul = true;
        return this;
    }

    public PropertiesToJsonConverter build() {
        return new PropertiesToJsonConverter(converters,
                                             resolvers,
                                             nullToJsonConverter,
                                             textToJsonNullResolver,
                                             textToEmptyStringResolver,
                                             skipNul);
    }
}
