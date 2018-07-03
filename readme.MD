Java Properties to JSON
---------------------------------------------

How to generate JSON from Java properties?

You can generate Json from:
- from Java properties (java.util.Properties)
- from Map<String,String> (import java.util.Map)
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
String json = new PropertiesToJsonConverter().parseToJson(properties);

String jsonFromProperties = new PropertiesToJsonConverter().parsePropertiesFromFileToJson("/home/user/file.properties");
String jsonFromProperties2 = new PropertiesToJsonConverter().parsePropertiesFromFileToJson(new File("/home/user/file.properties"));
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
```

Will result:

```json
{
   "object2":{
      "simpleArray":[
         "value1",
         "value2",
         "value3",
         "value4"
      ],
      "objectArray":[
         {
            "field1":"value1",
            "field3":"value3",
            "field2":"value2"
         }
      ]
   },
   "object3":{
      "arrayWithDelimeter":[
         "value1",
         "value2",
         "value3"
      ],
      "simpleString":"stringValue"
   },
   "object":{
      "doubleNumber":1.2345,
      "integerNumber":12,
      "booleanValue1":true,
      "booleanValue2":true,
      "booleanValue3":false,
      "man":{
         "surname":"Doe",
         "name":"John"
      },
      "type":"SOMETYPE"
   }
}
```

Example of use different primitive json type resolvers:
---------------------------------------------
The default constructor of class PropertiesToJsonConverter has default implementation of
json type resolvers with below order:  
```
ObjectFromTextJsonTypeResolver
PrimitiveArrayJsonTypeResolver
DoubleJsonTypeResolver
IntegerJsonTypeResolver
BooleanJsonTypeResolver
StringJsonTypeResolver
```

The order is important. When cannot parse then try with next Resolver. 
If you don't use some of them then you can pass resolvers which you want to use. 
You can pass your own Resolver too. To do this you need to extend class PrimitiveJsonTypeResolver. 
If your resolver cannot parse then should return null as a result in override method returnJsonTypeWhenCanBeParsed(..). 

pseudo code below for custom Resolver type:

```java
    import pl.jalokim.propertiestojson.object.AbstractJsonType;
    import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
    
    public class OwnCustomTypeResolver extends PrimitiveJsonTypeResolver {
        @Override
        public AbstractJsonType returnJsonTypeWhenCanBeParsed(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
            // if your condition is true then return your own implementation of AbstractJsonType with parsed value from propertyValue
            // AbstractJsonType should contain data parsed and provide implementation for "toStringJson()" method
            // if it not meets your's condition then return null for allow go to others type resolver in order.
            return null;
        }
    }
```

code snipped with you own json type resolvers order 
```java
    String json = new PropertiesToJsonConverter(
            new OwnCustomTypeResolver(),
            new IntegerJsonTypeResolver(),
            new BooleanJsonTypeResolver(),
            new StringJsonTypeResolver()
    ).parseToJson(properties);
```

Short describe how works every of default resolver
---------------------------------------------

<h4>1) ObjectFromTextJsonTypeResolver </h4>
 
This type resolver will try parse from text to json when as first letter will encounter "{" and as last letter "}" and first letter will encounter "[" and as last letter "]"
If it will have invalid json structure then it will go to next type resolver. 

```bash
jsonObject={"fieldName":2, "text":"textValue"}
jsonArray=[123,1234]
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
    1234
  ]
}
```
<h4>2) PrimitiveArrayJsonTypeResolver </h4>
 
This type resolver when will encounter separator (default one is comma ',') then will try parse as array. Important here to note is that is working with other resolvers.
It will try parse every part to some type from your resolver list. The PrimitiveArrayJsonTypeResolver has constructor with custom separator by which will split text to array elements. 
Every array element will be try parse by others primitive type resolvers.

```bash
arraytexts=1,23.0,5,false,text
```
Will result (when DoubleJsonTypeResolver, IntegerJsonTypeResolver, BooleanJsonTypeResolver are enabled)
```json
{
"arraytexts": [1, 23.0, 5, false, "text" ]
}
```

<h4>3) DoubleJsonTypeResolver </h4>

```bash
some.value=2.0
```
Will result
```json
{
  "some": {
    "value": 2.0
  }
}
```
<h4>4) IntegerJsonTypeResolver </h4>

```bash
some.value=2
```
Will result
```json
{
  "some": {
    "value": 2
  }
}
```
<h4>5) BooleanJsonTypeResolver </h4>

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
<h4>6) StringJsonTypeResolver </h4>


Important to note here is that this type resolver will always parse to text! 
After this type resolver other type resolver will be omitted!

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

Example of how use filters:
---------------------------------------------

code snippet:
```java

import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter;

...
String json = new PropertiesToJsonConverter().parseToJson(properties, "man.groups", "man.hoobies", "insurance.cost");
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

- java 7

- properties structure must have structure compatible with json

For example properties from below will throws ParsePropertiesException!
```properties
object.man.name=John
object.man=simpleValue

## reason: in first key 'man' is consider as json object type and so on.

object.array[0]=simpleValue
object.array[0].someField=someObjectSomeFieldValue
object.array[0].someField2=someObjectSomeFieldValue2

## reason: in first key 'object.array[0]' is consider as array element with primitive type not for Json Object. The key 'object.array[0].someField' try add object in array with index 0 where already is primitive JSON type with value: 'simpleValue'.


```
Maven dependency
---------------------------------------------

```xml
    <dependency>
        <groupId>pl.jalokim.propertiestojson</groupId>
        <artifactId>java-properties-to-json</artifactId>
        <version>3.1</version>
    </dependency>
```

Build from sources
---------------------------------------------

How to build from sources
```bash
mvn clean install
```