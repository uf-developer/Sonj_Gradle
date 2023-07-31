# Sonj Gradle
## Description
##### Json Util &amp; Json File Generator
#### Last version : Release 1.3.0
## Import
File `'build.gradle'`
```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.uf-developer:SonjGradle:1.3.0'
}
```
## How to Use
### Example Code
```java
import com.uf.jsonutils.JSONFile;
import com.uf.jsonutils.JSONString;

public class Example {
    public static void main(String[] args) {
        String str = "{\"strkey\":\"value\",\"numkey\":1.0,\"boolkey\":false,\"compoundkey\":{\"listkey\":[1,2,3],\"numkey\":1.0}}";
        System.out.println("JSONString.Tab(str, false) -> " + JSONString.Tab(str, false));
        JSONObject object = new JSONParser(str).parseObject();
        JSONFile.setFileNotExistEvent(() -> System.out.println("File does not exist"));
        JSONFile.setFileLoadEvent(() -> System.out.println("File loaded"));
        JSONFile.setDirectory(JSONFile.APPDATA + "file.json");
        JSONFile.load();
        JSONFile.save();
        JSONFile.save("newkey", "newvalue");
        JSONArray array = new JSONParser("[\"value\",123,false,[{\"key\":\"value\"}]").parseArray();
        object.put("newarray", array);
        JSONFile.putAll(object);
        System.out.println("JSONFile.get(\"newkey\") -> " + JSONFile.get("newkey"));
        JSONFile.overwrite(object);
        System.out.println("JSONFile.containsKey(\"newkey\") -> " + JSONFile.containsKey("newkey"));
        JSONFile.remove("newarray");
        System.out.println("JSONFile.containsKey(\"newarray\") -> " + JSONFile.containsKey("newarray"));
    }
}
```
### Result
```
JSONString.Tab(str, false) -> {
	"strkey": "value",
	"numkey": 1.0,
	"boolkey": false,
	"compoundkey": {
		"listkey": [
			1,
			2,
			3
		],
		"numkey": 1.0
	}
}
File does not exist
File loaded
File loaded
JSONFile.get("newkey") -> newvalue
JSONFile.containsKey("newkey") -> false
JSONFile.containsKey("newarray") -> false
```
File `'%APPDATA%/file.json'`
```json
{
	"numkey": 1.0,
	"compoundkey": {
		"numkey": 1.0,
		"listkey": [
			1,
			2,
			3
		]
	},
	"strkey": "value",
	"boolkey": false
}
```
------------------------------------------------------
### Features
##### JSONString.Tab(String, Boolean) - Automatic indentation & Automatic word wrapping
##### JSONFile.setDirectory(String) - SetFileDirectory & Load json file
##### JSONFile.get(String) - Get value from json file
##### JSONFile.remove(String) - Remove value from json file
##### JSONFile.save() - Save json file
##### JSONFile.save(String, Object) - Save json file & Put the value into a json file
##### JSONFile.containsKey(String) - Check if json file contains key
##### JSONFile.putAll(JSONObject) - Put all the values into a json file
##### JSONFile.overwrite(JSONObject) - Overwrite the json file with the new values
##### JSONObject & JSONArray - Usage is the same as for Map and Array, Using toString will output in json syntax
##### JSONParser$parseObject & JSONParser$parseArray - String to JSONObject & JSONArray
