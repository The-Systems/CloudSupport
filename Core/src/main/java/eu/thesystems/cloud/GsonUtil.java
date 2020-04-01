package eu.thesystems.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GsonUtil {

    private GsonUtil() {
        throw new UnsupportedOperationException();
    }

    private static final JsonParser PARSER = new JsonParser();

    public static JsonElement parseString(String input) {
        return PARSER.parse(input);
    }

    public static JsonObject parseStringAsObject(String input) {
        return parseString(input).getAsJsonObject();
    }

}
