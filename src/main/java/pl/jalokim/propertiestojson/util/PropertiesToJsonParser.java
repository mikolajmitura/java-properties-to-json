package pl.jalokim.propertiestojson.util;

import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;
import pl.jalokim.propertiestojson.util.exception.ReadInputException;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Please consider use below class because this parser is not thread safe and whole configuration had static context.
 * @see PropertiesToJsonConverter
 * to view more please visit
 * @see <a href="https://github.com/mikolajmitura/java-properties-to-json">https://github.com/mikolajmitura/java-properties-to-json</a>
 */
@Deprecated
public class PropertiesToJsonParser {

    private static PropertiesToJsonConverter converter = new PropertiesToJsonConverter();

    /**
     * Generate Json by given path to file with properties with only included domain keys.
     *
     * @param pathToFile        path to File
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parsePropertiesFromFileToJson(String pathToFile, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
        return converter.parsePropertiesFromFileToJson(pathToFile, includeDomainKeys);
    }

    /**
     * Generate Json by given path to file with properties.
     *
     * @param pathToFile path to File
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parsePropertiesFromFileToJson(String pathToFile) throws ReadInputException, ParsePropertiesException {
        return converter.parsePropertiesFromFileToJson(pathToFile);
    }

    /**
     * Generate Json by given file with properties with only included domain keys.
     *
     * @param file              file with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parsePropertiesFromFileToJson(File file, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
        return converter.parsePropertiesFromFileToJson(file, includeDomainKeys);
    }

    /**
     * Generate Json by given file with properties.
     *
     * @param file file with properties
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parsePropertiesFromFileToJson(File file) throws ReadInputException, ParsePropertiesException {
        return converter.parsePropertiesFromFileToJson(file);
    }

    /**
     * generate Json by given InputStream and given filter.
     *
     * @param inputStream       InputStream with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(InputStream inputStream, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
        return converter.parseToJson(inputStream, includeDomainKeys);
    }

    /**
     * generate Json by given InputStream.
     *
     * @param inputStream InputStream with properties
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(InputStream inputStream) throws ReadInputException, ParsePropertiesException {
        return converter.parseToJson(inputStream);
    }

    /**
     * generate Json by given Java Properties
     *
     * @param properties Java Properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Properties properties) throws ParsePropertiesException {
        return converter.parseToJson(properties);
    }

    /**
     * generate Json by given Map&lt;String,String&gt;
     *
     * @param properties Java Map with properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Map<String, String> properties) throws ParsePropertiesException {
        return converter.parseToJson(properties);
    }

    /**
     * generate Json by given Map&lt;String,String&gt; and given filter
     *
     * @param properties        Java Map with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Map<String, String> properties, String... includeDomainKeys) throws ParsePropertiesException {
        return converter.parseToJson(properties, includeDomainKeys);
    }

    /**
     * generate Json by given Java Properties and given filter
     *
     * @param properties        Java Properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @param includeDomainKeys domain head keys which should be parsed to json
     * @return Simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Properties properties, String... includeDomainKeys) throws ParsePropertiesException {
        return converter.parseToJson(properties, includeDomainKeys);
    }

}
