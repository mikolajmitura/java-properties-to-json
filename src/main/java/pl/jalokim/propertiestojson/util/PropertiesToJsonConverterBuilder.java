package pl.jalokim.propertiestojson.util;

import pl.jalokim.propertiestojson.object.AbstractJsonType;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

import static pl.jalokim.propertiestojson.resolvers.primitives.object.NullToJsonTypeConverter.NULL_TO_JSON_RESOLVER;
import static pl.jalokim.propertiestojson.resolvers.primitives.string.TextToEmptyStringResolver.EMPTY_TEXT_RESOLVER;
import static pl.jalokim.propertiestojson.resolvers.primitives.string.TextToJsonNullReferenceResolver.TEXT_TO_NULL_JSON_RESOLVER;

/**
 * Builder class for PropertiesToJsonConverter
 */
public class PropertiesToJsonConverterBuilder {

    static final List<TextToConcreteObjectResolver<?>> TO_OBJECT_RESOLVERS = defaultResolvers();
    static final List<ObjectToJsonTypeConverter<?>> TO_JSON_TYPE_CONVERTERS = defaultConverters();

    private final List<TextToConcreteObjectResolver<?>> resolvers = new ArrayList<>();
    private final List<ObjectToJsonTypeConverter<?>> converters = new ArrayList<>();

    private NullToJsonTypeConverter nullToJsonConverter = NULL_TO_JSON_RESOLVER;
    private TextToJsonNullReferenceResolver textToJsonNullResolver = TEXT_TO_NULL_JSON_RESOLVER;
    private TextToEmptyStringResolver textToEmptyStringResolver = EMPTY_TEXT_RESOLVER;
    private Charset charset;
    private boolean skipNulls = false;
    private boolean onlyCustomConverters = false;
    private boolean onlyCustomResolvers = false;

    /**
     * Default list of resolvers from text to java Object... Order here is important.
     *
     * @return list
     */
    static List<TextToConcreteObjectResolver<?>> defaultResolvers() {
        List<TextToConcreteObjectResolver<?>> toObjectResolvers = new ArrayList<>();
        toObjectResolvers.add(new TextToElementsResolver());
        toObjectResolvers.add(new TextToObjectResolver());
        toObjectResolvers.add(new TextToNumberResolver());
        toObjectResolvers.add(new TextToCharacterResolver());
        toObjectResolvers.add(new TextToBooleanResolver());
        return Collections.unmodifiableList(toObjectResolvers);
    }

    /**
     * Default list of converters from java Object to some instance of {@link AbstractJsonType}
     *
     * @return list
     */
    static List<ObjectToJsonTypeConverter<?>> defaultConverters() {
        List<ObjectToJsonTypeConverter<?>> toJsonTypeConverters = new ArrayList<>();
        toJsonTypeConverters.add(new ElementsToJsonTypeConverter());
        toJsonTypeConverters.add(new SuperObjectToJsonTypeConverter());
        toJsonTypeConverters.add(new NumberToJsonTypeConverter());
        toJsonTypeConverters.add(new CharacterToJsonTypeConverter());
        toJsonTypeConverters.add(new BooleanToJsonTypeConverter());
        return Collections.unmodifiableList(toJsonTypeConverters);
    }

    /**
     * Returns new instance of builder.
     *
     * @return builder instance
     */
    public static PropertiesToJsonConverterBuilder builder() {
        return new PropertiesToJsonConverterBuilder();
    }

    /**
     * Will build PropertiesToJsonConverter only with instances provided in argument. Order of resolvers has meaning. Order is crucial.
     *
     * @param resolvers it override default list of {@link PropertiesToJsonConverterBuilder#TO_OBJECT_RESOLVERS}
     * @return builder instance
     */
    public PropertiesToJsonConverterBuilder onlyCustomTextToObjectResolvers(TextToConcreteObjectResolver<?>... resolvers) {
        onlyCustomResolvers = true;
        this.resolvers.addAll(Arrays.asList(resolvers));
        return this;
    }

    /**
     * Will build PropertiesToJsonConverter with combined list of {@link PropertiesToJsonConverterBuilder#TO_OBJECT_RESOLVERS} and instances provided in
     * argument.
     *
     * @param resolvers added to {@link PropertiesToJsonConverterBuilder#TO_OBJECT_RESOLVERS}
     * @return builder instance
     */
    public PropertiesToJsonConverterBuilder defaultAndCustomTextToObjectResolvers(TextToConcreteObjectResolver<?>... resolvers) {
        onlyCustomResolvers = false;
        this.resolvers.addAll(Arrays.asList(resolvers));
        return this;
    }

    /**
     * Will build PropertiesToJsonConverter only with instances provided in argument. Order of converters has meaning only when converters can convert from the
     * same java class.
     *
     * @param converters it override default list of {@link PropertiesToJsonConverterBuilder#TO_JSON_TYPE_CONVERTERS}
     * @return builder instance
     */
    public PropertiesToJsonConverterBuilder onlyCustomObjectToJsonTypeConverters(ObjectToJsonTypeConverter<?>... converters) {
        onlyCustomConverters = true;
        this.converters.addAll(Arrays.asList(converters));
        return this;
    }

    /**
     * Will build PropertiesToJsonConverter with combined list of {@link PropertiesToJsonConverterBuilder#TO_JSON_TYPE_CONVERTERS} and instances provided in
     * argument.
     *
     * @param converters added to {@link PropertiesToJsonConverterBuilder#TO_JSON_TYPE_CONVERTERS}
     * @return builder instance
     */
    public PropertiesToJsonConverterBuilder defaultAndCustomObjectToJsonTypeConverters(ObjectToJsonTypeConverter<?>... converters) {
        onlyCustomConverters = false;
        this.converters.addAll(Arrays.asList(converters));
        return this;
    }

    /**
     * It override default conversion of json null reference for something what you need. (return of this instance will be simply concated to json)
     *
     * @param nullToJsonConverter new implementation of NullToJsonTypeConverter
     * @return builder instance
     */
    public PropertiesToJsonConverterBuilder overrideNullToJsonConverter(NullToJsonTypeConverter nullToJsonConverter) {
        this.nullToJsonConverter = nullToJsonConverter;
        return this;
    }

    /**
     * It override default behavior for resolving of text "null" or java.lang.String with null reference.
     *
     * @param textToJsonNullResolver new implementation of TextToJsonNullReferenceResolver
     * @return builder instance
     */
    public PropertiesToJsonConverterBuilder overrideTextToJsonNullResolver(TextToJsonNullReferenceResolver textToJsonNullResolver) {
        this.textToJsonNullResolver = textToJsonNullResolver;
        return this;
    }

    /**
     * It override default behavior for resolving of empty text in java.lang.String.
     *
     * @param textToEmptyStringResolver new implementation of TextToEmptyStringResolver
     * @return builder instance
     */
    public PropertiesToJsonConverterBuilder overrideTextToEmptyStringResolver(TextToEmptyStringResolver textToEmptyStringResolver) {
        this.textToEmptyStringResolver = textToEmptyStringResolver;
        return this;
    }

    /**
     * It will set the charset of the reader.
     *
     * @param charset charset to use
     * @return builder instance
     */
    public PropertiesToJsonConverterBuilder charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * It will skip every leaf in json object which is null, not skip null in arrays.
     *
     * @return PropertiesToJsonConverterBuilder instance
     */
    public PropertiesToJsonConverterBuilder skipNulls() {
        skipNulls = true;
        return this;
    }

    /**
     * It creates new converter instance.
     *
     * @return instance of PropertiesToJsonConverter
     */
    public PropertiesToJsonConverter build() {
        List<ObjectToJsonTypeConverter<?>> resultConverters = new ArrayList<>();
        if (onlyCustomConverters) {
            resultConverters.addAll(converters);
        } else {
            resultConverters.addAll(converters);
            resultConverters.addAll(TO_JSON_TYPE_CONVERTERS);
        }

        List<TextToConcreteObjectResolver<?>> resultResolvers = new ArrayList<>();
        if (onlyCustomResolvers) {
            resultResolvers.addAll(resolvers);
        } else {
            resultResolvers.addAll(resolvers);
            resultResolvers.addAll(TO_OBJECT_RESOLVERS);
        }

        return new PropertiesToJsonConverter(resultResolvers,
            resultConverters,
            nullToJsonConverter,
            textToJsonNullResolver,
            textToEmptyStringResolver,
            skipNulls,
            charset);
    }
}
