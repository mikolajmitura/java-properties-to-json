Java Properties to JSON
---------------------------------------------

From 5 version is not backward compatible!
The way creation of own json type resolvers has been changed!
But if you used only simple uses cases, migration (from 4.0) should pass without problems.

How to generate JSON from Java properties?

You can generate Json from:
- from Java properties (java.util.Properties)
- from Map<String,String> (import java.util.Map)
- from Map<String,Object> (import java.util.Map)
- from InputStream with properties (java.io.InputStream)
- from File with properties (java.io.File)
- from given file localization with properties 

below variable "properties" as one of the above types:

Simple Example
---------------------------------------------
code snippet:
```java

import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter;

...
// properties as Map<String,String>, java.util.Properties, java.io.InputStream
String json = new PropertiesToJsonConverter().convertToJson(properties);

// convert from file
String jsonFromProperties = new PropertiesToJsonConverter().convertPropertiesFromFileToJson("/home/user/file.properties");
String jsonFromProperties2 = new PropertiesToJsonConverter().convertPropertiesFromFileToJson(new File("/home/user/file.properties"));

// for map with Object as value, String as key
Map<String,Object> valuesAsObjectMap = new HashMap<>();
String jsonFromProperties3 = new PropertiesToJsonConverter().convertFromValuesAsObjectMap(valuesAsObjectMap);

// converter Instance can be gathered through PropertiesToJsonConverterBuilder class, it has a few method for customization
PropertiesToJsonConverter propsToJsonConverter = PropertiesToJsonConverterBuilder.builder().build();
```

example properties:
```bash
object.man.name=John
object.man.surname=Doe
object.type=SOMETYPE
object.doubleNumber=1.2345
object.integerNumber=12
object.booleanValue1=true
object.booleanValue2=True
object.booleanValue3=false
object2.simpleArray[0]=value1
object2.simpleArray[1]=value2
object2.simpleArray[2]=value3
object2.simpleArray[3]=value4
object2.objectArray[0].field1=value1
object2.objectArray[0].field2=value2
object2.objectArray[0].field3=value3
object3.arrayWithDelimeter=value1,value2,value3
object3.simpleString=stringValue
object3.emptyValue=
object3.nullValue=null
multiDimArray[0][0]=00
multiDimArray[0][1]=01
multiDimArray[1][0]=10
multiDimArray[1][1]=11
objectFromText={"fieldName": "value"}
objectFromText.anotherField=anotherField_value
anotherMultiDimArray=[[12, true], [12, 123]]
```

Will result:

```json
{
  "object2": {
    "simpleArray": [
      "value1",
      "value2",
      "value3",
      "value4"
    ],
    "objectArray": [
      {
        "field1": "value1",
        "field3": "value3",
        "field2": "value2"
      }
    ]
  },
  "object3": {
    "emptyValue": "",
    "arrayWithDelimeter": [
      "value1",
      "value2",
      "value3"
    ],
    "simpleString": "stringValue",
    "nullValue": null
  },
  "anotherMultiDimArray": [
    [
      12,
      true
    ],
    [
      12,
      123
    ]
  ],
  "objectFromText": {
    "fieldName": "value",
    "anotherField": "anotherField_value"
  },
  "multiDimArray": [
    [
      "00",
      "01"
    ],
    [
      10,
      11
    ]
  ],
  "object": {
    "booleanValue3": false,
    "booleanValue1": true,
    "booleanValue2": true,
    "integerNumber": 12,
    "doubleNumber": 1.2345,
    "man": {
      "surname": "Doe",
      "name": "John"
    },
    "type": "SOMETYPE"
  }
}
```

Example of use different primitive json type resolvers:
---------------------------------------------
The default constructor of class PropertiesToJsonConverter has default implementation of resolvers and converter

<h4>Resolvers for first conversion phase (from raw text to some concrete java object) order is important</h4>

```
TextToJsonNullReferenceResolver
TextToEmptyStringResolver
TextToElementsResolver
TextToObjectResolver
TextToNumberResolver
TextToCharacterResolver
TextToBooleanResolver
TextToStringResolver
```

<h4>Converters for second conversion phase (from object to concrete json object, field) </h4>

```
ElementsToJsonTypeConverter
SuperObjectToJsonTypeConverter
NumberToJsonTypeConverter
CharacterToJsonTypeConverter
BooleanToJsonTypeConverter
StringToJsonTypeConverter
NullToJsonTypeConverter
```

<h3>What does mean first conversion phase? </h3>
Does mean when this tool builds json from properties where value always is raw text String then will try resolve concrete Java object, when value can be number then try create BigDecimal or BigInteger.
Order of looking for certain resolver will pass from first to last resolver. Will be used first implementation of TextToConcreteObjectResolver which can convert from String to certain Java Object.


<h3>What does mean second conversion phase? <h3>
Does mean when this tool have some concrete objects then try converts it to certain json object, numbers as numbers, boolean as boolean, java bean as json object, collection or array as json array.
Order here is important only when Converters convert from the same Java Object (through method List<Class<?>> getClassesWhichCanResolve() in AbstractObjectToJsonTypeConverter<T> class)

<h3>When will be invoked conversion phases? <h3>
Important to note here is that if you want convert properties to json from below properties source (it invoke first and second conversion phase):
- InputSteam
- File
- Map<String,String>


Then will be call returnObjectWhenCanBeResolved(..) method from TextToConcreteObjectResolver<T>.
It method tries convert from String to some concrete object.
Next will build Map<String,Object> and in next step will call convertToJsonTypeOrEmpty(..) for concrete Object on sufficient concrete instance of ObjectToJsonTypeConverter<T>.


If you will convert from below properties source (it invoke only second processing phase in resolver):
- Properties (under the hood is Map<Object, Object> will convert to Map<String, Object>)
- Map<String, Object>

Then will be called only methods convertToJsonTypeOrEmpty in ObjectToJsonTypeConverter<T> after found sufficient resolver.


<h3>Override order Resolvers for first conversion phase</h3>
The order is important when try convert from raw text value (First phase) to some object. When cannot convert then will try with next implementation of TextToConcreteObjectResolver<T>.
If you don't want to use some of them then you can pass resolvers which you want to use.
You can pass your own Resolver too. To do this you need to implement interface TextToConcreteObjectResolver and provide generic type (T).

You need to implement method:
* Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey)

If your resolver cannot convert from text then should return empty Optional as a result in override method returnObjectWhenCanBeResolved(..).

Example code below for custom TextToConcreteObjectResolver which converts from text to LocalDate

```java
    import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
    import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver;

    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;
    import java.util.Optional;

    /**
     * results of this resolver you can see in test class LocalDateTimeResolverTest
     *
     * @see <a href="https://github.com/mikolajmitura/java-properties-to-json/blob/v5.1.0/src/test/java/pl/jalokim/propertiestojson/resolvers/primitives/custom/TextToLocalDateResolverTest.java">LocalDateTimeResolverTest</a>
     */
    public class TextToLocalDateResolver implements TextToConcreteObjectResolver {

        private static final String DATE_FORMAT = "dd-MM-yyyy";
        private final DateTimeFormatter formatter;

        public TextToLocalDateResolver() {
            this(DATE_FORMAT);
        }


        public TextToLocalDateResolver(String formatOfDate) {
            formatter = DateTimeFormatter.ofPattern(formatOfDate);
        }

        /**
         * This method will be called in first conversion phase
         * if your condition is true then return concrete value of Object.
         * if it not meets your's condition then return Optional.empty() for allow go to others type resolver in order.
         * This will be called only for read properties from Map&lt;String,String&gt;, File with properties, InputStream with properties
         *
         * @param primitiveJsonTypesResolver primitiveJsonTypesResolver
         * @param propertyValue              currently processing property value
         * @param propertyKey                currently processing property key
         * @return optional value
         */

        @Override
        public Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                              String propertyValue,
                                                              String propertyKey) {
            try {
                return Optional.ofNullable(LocalDate.parse(propertyValue, formatter)); // if parse then will return LocalDate
            } catch(Exception ex) {
                return Optional.empty(); // if not, then allow another resolvers to crate java object from String
            }
        }
    }
```

Code snipped for add your own implementations of TextToConcreteObjectResolver to default ones

```java
     String json = PropertiesToJsonConverterBuilder.builder()
                                                           .defaultAndCustomTextToObjectResolvers(
                                                                   new OwnCustomTypeResolver1(),
                                                                   new OwnCustomTypeResolver2())
                                                           .build()
                                                           .convertToJson(properties);
```

..Or add your own order of all instances of TextToConcreteObjectResolver. Then will be used only that which was provided...

```java
     String json = PropertiesToJsonConverterBuilder.builder()
                                                           .onlyCustomTextToObjectResolvers(
                                                                   new OwnCustomTypeResolver1(),
                                                                   new TextToNumberResolver(),
                                                                   new TextToBooleanResolver())
                                                           .build()
                                                           .convertToJson(properties);
```

Important to note here is that always will 2 two resolvers before all resolvers provided by you,
and StringJsonTypeResolver will be as last always.

So The real order will be:
```bash
TextToJsonNullReferenceResolver <- you can override it via overrideTextToJsonNullResolver(..) in PropertiesToJsonConverterBuilder
TextToEmptyStringResolver <- you can override it via overrideTextToEmptyStringResolver(..) in PropertiesToJsonConverterBuilder
..............
OwnCustomTypeResolver1
TextToNumberResolver
TextToBooleanResolver
..............
TextToStringResolver
```

Short description how works every default implementation of TextToConcreteObjectResolver<T>
---------------------------------------------

<h4>1) TextToJsonNullReferenceResolver </h4>
When propertyValue is equals "null" or is null reference

```java
Map<String, String> properties = new HashMap<>();
properties.put("main.field", null);
or 
properties.put("main.field", "null");
```

Or from file with properties

```bash
main.field=null
```
Then will return Optional of JsonNullReferenceType instance

<h4>2) TextToEmptyStringResolver </h4>
For first it trim value and next check is equals empty String value, not null

```java
properties.put("main.field", " ");
```

Or from file with properties

```bash
main.field=
```

Then will return Optional of empty new String("") instance

<h4>3) TextToElementsResolver </h4>

This type resolver when will encounter separator (default one is comma ',') then will try convert to java java.util.List. Important here to note is that is working with other resolvers.
It will try parse every part to some type from your resolver list. The TextToElementsResolver has constructor with custom separator by which will split text to array elements.
By default constructor Every array element will be try parse by others primitive type resolvers.

```bash
arraytexts=1,23.0,5,false,text
or
arraytexts=[1,23.0,5,false,text]
```
Will result (when DoubleJsonTypeResolver, IntegerJsonTypeResolver, BooleanJsonTypeResolver are added to resolvers)
Then will return Optional with java.util.List with values: [1, 23.0, 5, false, "text"]

When created this resolver by "new TextToElementsResolver(false)", then it will not try resolve types of array
Then will result (when DoubleJsonTypeResolver, IntegerJsonTypeResolver, BooleanJsonTypeResolver are added to resolvers)
Then will return Optional with java.util.List with values: ["1", "23.0", "5", "false", "text"]

<h4>4) TextToObjectResolver </h4>

This type resolver will try convert from text to json when as first letter will encounter "{" and as last letter "}" and first letter will encounter "[" and as last letter "]"
If it will have invalid json structure then it will go to next type resolver.
This resolver during convert from text to json object will use only own resolvers list for primitive types.
The setup of resolvers in PropertiesToJsonConverter(List<TextToConcreteObjectResolver> toObjectsResolvers, ...) constructor will not have impact of those list.
And configured resolvers list via defaultAndCustomObjectToJsonTypeConverters() or onlyCustomTextToObjectResolvers in PropertiesToJsonConverterBuilder too.

From properties file for example

```bash
jsonObject={"fieldName":2, "text":"textValue"}
jsonArray=[123,1234,,""]
```

Then will return Optional of ObjectJsonType instance which will store json like below:
```json
{
"jsonObject": {
    "fieldName": 2,
    "text": "textValue"
  },
  "jsonArray": [
      123,
      1234,
      null,
      ""
    ]
}
```


<h4>5) TextToNumberResolver </h4>
If can convert from text to number

```bash
some.valueLong=2
some.valueFloat=2.0
```
Then will return Optional of Number (BigDecimal or BigInteger)

```
<h4>6) TextToCharacterResolver </h4>
If string contains only one char then will use that resolver.

```bash
some.value=c
```
Then will return Optional of Character with 'c' value

<h4>7) TextToBooleanResolver </h4>
If can convert from string to boolean then will use that resolver.

```bash
some.value=true
```
Then will return Optional of Boolean with true value

<h4>8) TextToStringResolver </h4>

Important to note here is that this type resolver will always convert to text!
After this type resolver other type resolver would be omitted!
So you cannot add any other resolvers after this. It will be always added as last by default.

```bash
some.value=2.0
```
Simply will return Optional of String with "2.0" value

<h3>Override order Resolvers for second conversion phase</h3>
The order is important only when converters can convert from the same java Class. When cannot convert then will try with next implementation of AbstractObjectToJsonTypeConverter<T>.
If you don't want to use some defaults converters then you can pass converters which you want to use.
You can pass your own implementation of AbstractObjectToJsonTypeConverter<T> too. To do this you need to extends class AbstractObjectToJsonTypeConverter<T> and provide generic type (T).

You need to implement method:
* Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, LocalDate convertedValue, String propertyKey)

If your converter cannot convert from this object (due to property key or others) then should return empty Optional as a result in override method convertToJsonTypeOrEmpty(..).
Then other converter which can handle this java class will try with that...

Example code below for custom ObjectToJsonTypeConverter<T> which converts from text to LocalDate

```java
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
import pl.jalokim.propertiestojson.object.NumberJsonType;
import pl.jalokim.propertiestojson.object.SkipJsonField;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.hierarchy.JsonTypeResolversHierarchyResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.AbstractObjectToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.SuperObjectToJsonTypeConverter;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * results of this resolver you can see in those test classes:
 *
 * @see <a href="https://github.com/mikolajmitura/java-properties-to-json/blob/v5.1.0/src/test/java/pl/jalokim/propertiestojson/resolvers/primitives/custom/LocalDateToJsonTypeConverterTest.java">LocalDateTimeResolverTest</a>
 * @see <a href="https://github.com/mikolajmitura/java-properties-to-json/blob/v5.1.0/src/test/java/pl/jalokim/propertiestojson/resolvers/primitives/custom/LocalDateConvertersTest.java">LocalDateTimeResolverTest</a>
 */
public class LocalDateToJsonTypeConverter extends AbstractObjectToJsonTypeConverter<LocalDate> {

    private final boolean asTimestampInUTC;

    public LocalDateToJsonTypeConverter() {
        this(false);
    }

    public LocalDateToJsonTypeConverter(boolean asTimestampInUTC) {
        this.asTimestampInUTC = asTimestampInUTC;
    }

    /**
     * This method will be called in second phase conversion step (from some java Object to some implementation of AbstractJsonType)
     * it will be called during read properties from Map&lt;String,Object&gt;, Properties (without first processing step) or after first
     * conversion phase (while reading properties from file, Map&lt;String,String&gt;, inputStream)
     * <p>
     * But converters order (provided in PropertiesToJsonConverter(PrimitiveJsonTypeResolver... primitiveResolvers) constructor) doesn't have importance here as in first processing phase,
     * it is important only when some of implementation of {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter} can convert from the same java class.
     * The hierarchy of classes plays a main role here
     * It looks for sufficient resolver, firstly will looks for exactly match class type provided by method {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#getClassesWhichCanResolve()}
     * if find a few resolvers for the same class then it will looks for firs converter which properly convert java object to AbstractJsonType (here converters order does it matter).
     * More here  {@link JsonTypeResolversHierarchyResolver}
     * <p>
     *
     * AbstractJsonType should contains converted data and provides implementation for "toStringJson()" method if you provide your own...
     * or you can return instance of existence one implementation in package 'pl.jalokim.propertiestojson.object'...  number, boolean, text, primitive array, json objects...
     * or simply convert Java object to instance ObjectJsonType by static method: public static AbstractJsonType convertFromObjectToJson(Object propertyValue, String propertyKey)
     * {@link SuperObjectToJsonTypeConverter#convertFromObjectToJson(Object propertyValue, String propertyKey)}
     * Or if you want return null json object then return instance of {@link JsonNullReferenceType#NULL_OBJECT}
     * Or if you want to skip this json leaf then return instance of {@link SkipJsonField#SKIP_JSON_FIELD} then it will not add it to json with null value.
     *
     * @param primitiveJsonTypesResolver primitiveJsonTypesResolver
     * @param convertedValue             currently processing property value but as generic type
     * @param propertyKey                currently processing property key
     * @return optional value
     */

    @Override
    public Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                               LocalDate convertedValue,
                                                               String propertyKey) {
        if(asTimestampInUTC) {
            return Optional.of(new NumberJsonType(convertedValue.atStartOfDay(ZoneOffset.UTC).toEpochSecond()));
        } else if(!propertyKey.contains("asText")) {
            return Optional.of(SuperObjectToJsonTypeConverter.convertFromObjectToJson(convertedValue, propertyKey));
        }
        return Optional.empty(); // allow to go to another converter which will convert LocalDate to AbstractJsonType...
    }
}

```

 // TODO how to create converter order.. itp
// TODO add info about default converter

Example how to use PropertiesToJsonConverterBuilder
---------------------------------------------
// TODO add more info about builder

Example how to use filters (inclusion of property key or first part of property keys):
---------------------------------------------

code snippet:
```java

import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter;

...
String json = new PropertiesToJsonConverter().convertToJson(properties, "man.groups", "man.hoobies", "insurance.cost");
```

example properties:
```bash
man.groups[0].type=Commercial
man.groups[0].name=group1
man.groups[1].type=Free
man.groups[1].name=group2
man.groups[2].type=Commercial
man.groups[2].name=group3
man.hoobies[0]=cars
man.hoobies[1]=science
man.hoobies[2]=women
man.hoobies[3]=computers
man.insurance.cost=126.543
man.address.street=Jp2
man.address.city=Waraw
man.emails= example@gg.com ,example2@cc.com, example3@gg.com,example3@gg.com
man.name=John
man.surname=Surname
insurance.type=Medical
insurance.cost=123
field1=someValue2
field2=someValue3

```

Will result
```json
{
   "insurance":{
      "cost":123.0
   },
   "man":{
      "hoobies":[
         "cars",
         "science",
         "women",
         "computers"
      ],
      "groups":[
         {
            "name":"group1",
            "type":"Commercial"
         },
         {
            "name":"group2",
            "type":"Free"
         },
         {
            "name":"group3",
            "type":"Commercial"
         }
      ]
   }
}
```

Requirements
---------------------------------------------

- java 8

- properties structure must have structure compatible with json

For example properties from below will throws CannotOverrideFieldException!
```properties
object.man.name=John
object.man=simpleValue

## reason: in first key 'man' is consider as json object and has field 'name'

object.array[0]=simpleValue
object.array[0].someField=someObjectSomeFieldValue
object.array[0].someField2=someObjectSomeFieldValue2

## reason: in first key 'object.array[0]' contains primitive value 'simpleValue' doesn't have json object. The key 'object.array[0].someField' try add new field 'someField' to element at object.array[0] which is primitive type now.
```


Json Object merge or Json Array merge
---------------------------------------------

<h4>1) primitive array merge and indexed property key </h4>

```properties
object.array=[0, 1, 2, "3"]
object.array[4]=4
```

it gives a result
```json
{
  "object": {
    "array": [
      0,
      1,
      2,
      "3",
      4
    ]
  }
}
```

but for properties

```properties
object.array=[0, 1, 2, "3"]
object.array[3]=4
```

it throws a exception because was try of override index in array:

```bash
CannotOverrideFieldException: Cannot override value at path: 'object.array[3]', current value is: '"3"', problematic property key: 'object.array[3]'
```

<h4>2) merge of json object from text (json object was provided from property value) and normal property </h4>

```properties
object.someAnotherObject={"field1":"fieldValue"}
object.someAnotherObject.numberField=3
```

```json
{
  "object": {
    "someAnotherObject": {
      "field1": "fieldValue",
      "numberField": 3
    }
  }
}
```

It can merge arrays and json objects... In another cases will throw CannotOverrideFieldException or ParsePropertiesException...

Maven dependency
---------------------------------------------

```xml
    <dependency>
        <groupId>pl.jalokim.propertiestojson</groupId>
        <artifactId>java-properties-to-json</artifactId>
        <version>5.1.0</version>
    </dependency>
```

Build from sources
---------------------------------------------

How to build from sources
```bash
mvn clean install
```