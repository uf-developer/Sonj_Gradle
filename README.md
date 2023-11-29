# Sonj Gradle v1.3.2
## Description
#### Json Util & Json File Generator
## Adding a Dependency
#### Gradle
```groovy
dependencies {
    implementation 'io.github.uf-developer:SonjGradle:2.0.0'
}
```
#### Gradle (Kotlin)
```kotlin
dependencies {
    implementation('io.github.uf-developer:SonjGradle:2.0.0')
}
```
#### Maven
```xml
<dependency>
    <groupId>io.github.uf-developer</groupId>
    <artifactId>SonjGradle</artifactId>
    <version>2.0.0</version>
</dependency>
```
## How to Use
#### Example Code
```java
import com.i_uf.jsonutils.JsonUtils;
import java.util.List;
import java.util.Map;

public class Example {
    public static void main(String[] args) {
        String text = "{\"string\": \"Hello, World!\",\"number\": 12345,\"boolean\": true,\"null\":null,\"array\": [\"element1\", \"element2\", \"element3\"],\"object\": {\"property1\": \"value1\",\"property2\": \"value2\"}}";
        System.out.println(JsonUtils.tab(text) + "-> tab");
        System.out.println(JsonUtils.checkMap(text) + "-> checkMap");
        System.out.println(JsonUtils.checkList(text) + "-> checkMap");
        Map<String, Object> map = JsonUtils.jsonStringToMap(text);
        System.out.println(map.get("string") + "-> map.get string");
        List<?> list = (List<?>) map.get("array");
        System.out.println(JsonUtils.listToJsonString(list) + "-> array");
    }
}
```
#### Result
```
{
	"string": "Hello, World!",
	"number": 12345,
	"boolean": true,
	"null": null,
	"array": [
		"element1",
		"element2",
		"element3"
	],
	"object": {
		"property1": "value1",
		"property2": "value2"
	}
}-> tab
true-> checkMap
false-> checkMap
Hello, World!-> map.get string
[
	"element1",
	"element2",
	"element3"
]-> array
```
## Features
#### JsonUtils
- tab(String text)
- tab(String text, boolean file)
- jsonStringToMap(String text)
- jsonStringToMap(String text, boolean file)
- jsonStringToList(String text)
- jsonStringToList(String text, boolean file)
- mapToJsonString(String text)
- mapToJsonString(String text, boolean file)
- ListToJsonString(String text)
- ListToJsonString(String text, boolean file)
- checkMap(String text)
- checkMap(String text, boolean file)
- checkList(String text)
- checkList(String text, boolean file)