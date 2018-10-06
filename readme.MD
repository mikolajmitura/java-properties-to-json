Java Properties to JSON
---------------------------------------------

This 4.0 version is not backward compatible!
The way of develop own json type resolver changed.  

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

String jsonFromProperties = new PropertiesToJsonConverter().convertPropertiesFromFileToJson("/home/user/file.properties");
String jsonFromProperties2 = new PropertiesToJsonConverter().convertPropertiesFromFileToJson(new File("/home/user/file.properties"));

// valuesAsObjectMap is Map<String, Object>
String jsonFromProperties2 = new PropertiesToJsonConverter().convertFromValuesAsObjectMap(valuesAsObjectMap);
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
      "simpleString":"stringValue",
      "emptyValue":"",
      "nullValue":null
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
JsonNullReferenceTypeResolver
EmptyStringJsonTypeResolver
PrimitiveArrayJsonTypeResolver
ObjectFromTextJsonTypeResolver
NumberJsonTypeResolver
BooleanJsonTypeResolver
StringJsonTypeResolver
```

The order is important. When cannot parse then try with next Resolver. 
If you don't use some of them then you can pass resolvers which you want to use. 
You can pass your own Resolver too. To do this you need to extend class PrimitiveJsonTypeResolver<T> and provide type.
If your resolver cannot parse then should return null as a result in override method returnConcreteValueWhenCanBeResolved(..). 

pseudo code below for custom Resolver type:

```java
    import pl.jalokim.propertiestojson.object.AbstractJsonType;
    import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
    
    public class OwnCustomTypeResolver extends PrimitiveJsonTypeResolver<Object> {
    
        // if your condition is true then return concrete value of Object.
        // if it not meets your's condition then return null for allow go to others type resolver in order.
        // For read properties from Map<String, String>, File with properties, InputStream with properties
        @Override
        protected Object returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
            return null;
        }
    
        // For read properties from Map<String, Object>, Properties
        // AbstractJsonType should contains data parsed and provide implementation for "toStringJson()" method
        // To invoke concrete resolver every resolver will be check by method canResolveThisObject(Class<?>) in PrimitiveJsonTypeResolver.   
        @Override
        public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue) {
            return null;
        }
    }
```

Important to note here is that if you want convert properties to json from:
- File
- Map<String,String>

Then will be call returnConcreteValueWhenCanBeResolved method from PrimitiveJsonTypeResolver.
It method tries convert from String to some concrete object.
Next will build Map<String,Object> and in next step will call returnConcreteJsonType for concrete Object,
when method canResolveThisObject(Class<?>) will return true. 
 

If you will convert from below:
- Properties (under the hood is Map<Object, Object> will convert to Map<String, Object>) 
- Map<String, Object>

Then in resolvers will be call only method returnConcreteJsonType while canResolveThisObject will return true.

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
 
This type resolver will try parse from text to json when as first letter will encounter "{" and as last letter "}" and first letter will encounter "[" and as last letter "]"
If it will have invalid json structure then it will go to next type resolver. 
This resolver can parse from Pojo object to json. Example when you put 

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
String json = new PropertiesToJsonConverter().parseToJson(properties);
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


Important to note here is that this type resolver will always parse to text! 
After this type resolver other type resolver would be omitted!
So you cannot add any other resolvers after this. It will be always add as last by default.  

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
        <version>4.0</version>
    </dependency>
```

Build from sources
---------------------------------------------

How to build from sources
```bash
mvn clean install
```