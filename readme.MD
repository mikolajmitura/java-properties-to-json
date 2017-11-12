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

import pl.jalokim.propertiestojson.util.PropertiesToJsonParser;

...
String json = PropertiesToJsonParser.parseToJson(properties);

String jsonFromProperties = PropertiesToJsonParser.parsePropertiesFromFileToJson("/home/user/file.properties");
String jsonFromProperties2 = PropertiesToJsonParser.parsePropertiesFromFileToJson(new File("/home/user/file.properties"));
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
Example of use filters:
---------------------------------------------

code snippet:
```java

import pl.jalokim.propertiestojson.util.PropertiesToJsonParser;

...
String json = PropertiesToJsonParser.parseToJson(properties, "man.groups", "man.hoobies", "insurance.cost");
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
        <version>2.0</version>
    </dependency>
```

Build from sources
---------------------------------------------

How to build from sources
```bash
mvn clean install
```