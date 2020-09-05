package pl.jalokim.propertiestojson.util;

import com.google.gson.JsonObject;
import pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper;

public class JsonCheckerUtil {

    public static boolean leafOfPathIsNotPresent(String jsonPath, String jsonAsText) {
        JsonObject jsonObject = JsonObjectHelper.toJsonElement(jsonAsText).getAsJsonObject();
        String[] jsonPaths = jsonPath.split("\\.");

        for (int i = 0; i < jsonPath.length(); i++) {
            if (i == jsonPaths.length - 1) {
                return !jsonObject.has(jsonPaths[i]);
            }
            jsonObject = jsonObject.getAsJsonObject(jsonPaths[i]);
        }
        throw new IllegalArgumentException("cannot find path: " + jsonPath + " in json" + jsonAsText);
    }

    public static boolean leafOfPathHasNullValue(String jsonPath, String jsonAsText) {
        JsonObject jsonObject = JsonObjectHelper.toJsonElement(jsonAsText).getAsJsonObject();
        String[] jsonPaths = jsonPath.split("\\.");

        for (int i = 0; i < jsonPath.length(); i++) {
            if (i == jsonPaths.length - 1) {
                return jsonObject.get(jsonPaths[i]).isJsonNull();
            }
            jsonObject = jsonObject.getAsJsonObject(jsonPaths[i]);
        }
        throw new IllegalArgumentException("cannot find path: " + jsonPath + " in json" + jsonAsText);
    }
}
