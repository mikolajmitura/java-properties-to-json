Java Properties to JSON
---------------------------------------------

This 5.0.0 version is not backward compatible!
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
The default constructor of class PropertiesToJsonConverter has default implementation of
json type resolvers with below order:  
```
JsonNullReferenceTypeResolver
EmptyStringJsonTypeResolver
PrimitiveArrayJsonTypeResolver
ObjectFromTextJsonTypeResolver
NumberJsonTypeResolver
BooleanJsonTypeResolver
StringJsonTypeResolver
```


The order is important when try convert from raw property value to some object. When cannot convert then will try with next Resolver.
If you don't want to use some of them then you can pass resolvers which you want to use.
You can pass your own Resolver too. To do this you need to extend class PrimitiveJsonTypeResolver<T> and provide generic type (T).

You need to implement two methods:
* T returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey);
* AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, T convertedValue, String propertyKey);

If your resolver cannot convert from text then should return null as a result in override method returnConcreteValueWhenCanBeResolved(..).

Example code below for custom Resolver type which converts from text to LocalDate (only in first processing phase) and from LocalDate to field contains only timestamp for UTC (for second processing phase) or to json object:

```java
    import pl.jalokim.propertiestojson.object.AbstractJsonType;
    import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
    import pl.jalokim.propertiestojson.object.NumberJsonType;
    import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
    import pl.jalokim.propertiestojson.resolvers.hierarchy.JsonTypeResolversHierarchyResolver;
    import pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver;
    import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;

    import java.time.LocalDate;
    import java.time.ZoneOffset;
    import java.time.format.DateTimeFormatter;
    import java.util.Optional;


    /**
     * results of this resolver you can see in test class LocalDateTimeResolverTest
     *
     * @see <a href="https://github.com/mikolajmitura/java-properties-to-json/blob/65da56f621fdef705f72061f86a356f036fa1ecd/src/test/java/pl/jalokim/propertiestojson/example/LocalDateTimeResolverTest.java">LocalDateTimeResolverTest</a>
     */
    public class LocalDateTimeResolver extends PrimitiveJsonTypeResolver<LocalDate> {

        private static final String DATE_FORMAT = "dd-MM-yyyy";
        private final DateTimeFormatter formatter;
        private final boolean asTimestampInUTC;

        LocalDateTimeResolver() {
            this(DATE_FORMAT, false);
        }

        LocalDateTimeResolver(boolean asTimestampInUTC) {
            this(DATE_FORMAT, asTimestampInUTC);
        }

        LocalDateTimeResolver(String formatOfDate) {
            this(formatOfDate, false);
        }

        LocalDateTimeResolver(String formatOfDate, boolean asTimestampInUTC) {
            formatter = DateTimeFormatter.ofPattern(formatOfDate);
            this.asTimestampInUTC = asTimestampInUTC;
        }

        /**
         * This method will be called in first phase processing step (from raw text to some object)
         * if your condition is true then return concrete value of Object.
         * if it not meets your's condition then return Optional.empty() for allow go to others type resolver in order.
         * This will be called only for read properties from Map<String, String>, File with properties, InputStream with properties
         */

        @Override
        protected Optional<LocalDate> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                                           String propertyValue,
                                                                           String propertyKey) {
            try {
                return Optional.ofNullable(LocalDate.parse(propertyValue, formatter)); // if parse then will return LocalDate
            } catch(Exception ex) {
                return Optional.empty(); // if not
            }
        }

        /**
         * This method will be called in second phase processing step (from some java Object to some implementation of AbstractJsonType)
         * it will be called during read properties from Map<String, Object>, Properties (without first processing step) or after first
         * processing step (while reading properties from file, Map<String, String>, inputStream)
         * <p>
         * But resolvers order (provided in PropertiesToJsonConverter(PrimitiveJsonTypeResolver... primitiveResolvers) constructor) doesn't have importance here as in first processing phase.
         * The hierarchy of classes plays a central role here
         * It looks for sufficient resolver, firstly will looks for exactly match class type provided by method {@link PrimitiveJsonTypeResolver#getClassesWhichCanResolve()}
         * More here  {@link JsonTypeResolversHierarchyResolver}
         * <p>
         * AbstractJsonType should contains converted data and provides implementation for "toStringJson()" method if you provide you own...
         * or you can return instance of existence one implementation in package 'pl.jalokim.propertiestojson.object'...  number, boolean, text, primitive array, json objects...
         * or simply convert Java object to instance ObjectJsonType by static method: public static AbstractJsonType convertFromObjectToJson(Object propertyValue, String propertyKey)
         * {@link ObjectFromTextJsonTypeResolver#convertFromObjectToJson(Object propertyValue, String propertyKey)}
         * Or if you want return null json object then return instance of {@link JsonNullReferenceType#NULL_OBJECT}
         */

        @Override
        public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                       LocalDate convertedValue,
                                                       String propertyKey) {
            if (asTimestampInUTC) {
                return new NumberJsonType(convertedValue.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
            }
            return ObjectFromTextJsonTypeResolver.convertFromObjectToJson(convertedValue, propertyKey);
        }
    }
```

Important to note here is that if you want convert properties to json from (it invoke first and second processing phase in resolver):
- InputSteam
- File
- Map<String,String>

Then will be call returnConcreteValueWhenCanBeResolved method from PrimitiveJsonTypeResolver.
It method tries convert from String to some concrete object.
Next will build Map<String,Object> and in next step will call returnConcreteJsonType for concrete Object on sufficient concrete instance of PrimitiveJsonTypeResolver.
 

If you will convert from below (it invoke only second processing phase in resolver):
- Properties (under the hood is Map<Object, Object> will convert to Map<String, Object>) 
- Map<String, Object>

Then in resolvers will be call only method returnConcreteJsonType after found sufficient resolver.

Code snipped with your own json type resolvers order 
 
```java
    String json = new PropertiesToJsonConverter(
            new OwnCustomTypeResolver(),
            new NumberJsonTypeResolver(),
            new BooleanJsonTypeResolver()
    ).convertToJson(properties);
```

Important to note here is that always will 2 two json type resolvers before first given by you, 
and StringJsonTypeResolver will be as last always.
 
So The real order will be: 
```bash
JsonNullReferenceTypeResolver 
EmptyStringJsonTypeResolver
.............. 
OwnCustomTypeResolver
NumberJsonTypeResolver
BooleanJsonTypeResolve
..............
StringJsonTypeResolver
```

Short describe how works every of default resolver
---------------------------------------------

<h4>1) JsonNullReferenceTypeResolver </h4>
When you give null object as value in Properties or Map then 
 
```java
properties.put("main.field", null);
``` 

Or from file with properties

```bash
main.field=null
```

will result
```json
{  
   "main":{  
      "field":null
   }
}
```

<h4>2) EmptyStringJsonTypeResolver </h4>

```java
properties.put("main.field", " ");
``` 

Or from file with properties

```bash
main.field=
```

will result
```json
{  
   "main":{  
      "field":""
   }
}
```
<h4>3) PrimitiveArrayJsonTypeResolver </h4>
 
This type resolver when will encounter separator (default one is comma ',') then will try parse as array. Important here to note is that is working with other resolvers.
It will try parse every part to some type from your resolver list. The PrimitiveArrayJsonTypeResolver has constructor with custom separator by which will split text to array elements. 
By default constructor Every array element will be try parse by others primitive type resolvers.

```bash
arraytexts=1,23.0,5,false,text
or
arraytexts=[1,23.0,5,false,text]
```
Will result (when DoubleJsonTypeResolver, IntegerJsonTypeResolver, BooleanJsonTypeResolver are added to resolvers)
```json
{
"arraytexts": [1, 23.0, 5, false, "text" ]
}
```

When created this resolver by new PrimitiveArrayJsonTypeResolver(false)
Then will result (when DoubleJsonTypeResolver, IntegerJsonTypeResolver, BooleanJsonTypeResolver are added to resolvers)
```json
{
"arraytexts": ["1", "23.0", "5", "false", "text" ]
}
```

<h4>4) ObjectFromTextJsonTypeResolver </h4>
 
This type resolver will try convert from text to json when as first letter will encounter "{" and as last letter "}" and first letter will encounter "[" and as last letter "]"
If it will have invalid json structure then it will go to next type resolver.
This resolver during convert from text to json object will use only own resolvers list for primitive types.
The setup of resolvers in PropertiesToJsonConverter(PrimitiveJsonTypeResolver... primitiveResolvers) constructor will not have impact of those list.
This resolver can convert from Pojo object to json. Example when you put

``` java
class MyOwnPojo {

   MyOwnPojo (String field1, String field2) {
        this.field1 = field1;
        this.field2 = field2;
   }

 private String field1;
 private String field2;
}

....
Properties properties = new Properties();
properties.put("some.field", new MyOwnPojo("text1", "text2"));
// this MyOwnPojo object will be converted to json too by Gson.
String json = new PropertiesToJsonConverter().convertToJson(properties);
```

Will result
```json
{  
   "some":{  
      "field":{  
         "field1":"text1",
         "field2":"text2"
      }
   }
}
```

From properties file for example

```bash
jsonObject={"fieldName":2, "text":"textValue"}
jsonArray=[123,1234,,""]
```
Will result
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


<h4>5) NumberJsonTypeResolver </h4>

```bash
some.valueLong=2
some.valueFloat=2.0
```
Will result
```json
{
  "some": {
    "valueLong": 2,
    "valueFloat": 2.0
  }
}
```
<h4>6) BooleanJsonTypeResolver </h4>

```bash
some.value=true
```
Will result
```json
{
  "some": {
    "value": true
  }
}
```
<h4>7) StringJsonTypeResolver </h4>


Important to note here is that this type resolver will always convert to text!
After this type resolver other type resolver would be omitted!
So you cannot add any other resolvers after this. It will be always added as last by default.

```bash
some.value=2.0
```
Will result
```json
{
  "some": {
    "value": "2.0"
  }
}
```

Example of how use filters (inclusion of property key or first part of property keys):
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
        <version>5.0.0</version>
    </dependency>
```

Build from sources
---------------------------------------------

How to build from sources
```bash
mvn clean install
```