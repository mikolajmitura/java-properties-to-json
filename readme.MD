Java Properties to JSON
---------------------------------------------

How to generate JSON from Java properties?

You can generate Json from:
- from Java properties (java.util.Properties)
- from Map<String,String> (import java.util.Map)
- from InputStream with properties (java.io.InputStream)

below variable properties as one of the above types:

code snippet:
```bash

import pl.jalokim.propertiestojson.util.PropertiesToJsonParser;
---
String json = PropertiesToJsonParser.parseToJson(properties);

```

example properties:
```bash
object.man.name=John
object.man.surname=Doe
object.type=SOMETYPE
object.simpleNumber=1.2345
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

```bash
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
      "simpleNumber":1.2345,
      "man":{
         "surname":"Doe",
         "name":"John"
      },
      "type":"SOMETYPE"
   }
}
```

Requirements
---------------------------------------------

- java 1.8

- properties structure must have structure compatible with json

For example properties from below will throws ParsePropertiesException!
```bash
object.man.name=John
object.man=simpleValue

explain: in first key 'man' is consider as json object type and so on.

object.array[0]=simpleValue
object.array[1].someField=someObjectSomeFieldValue
object.array[1].someField2=someObjectSomeFieldValue2

explain: in first key 'array' is consider as array for primitive type not for objects and so on.


```


Build from sources
---------------------------------------------


How to build from sources
```bash
mvn clean install
```