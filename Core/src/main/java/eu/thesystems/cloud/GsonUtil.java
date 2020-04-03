package eu.thesystems.cloud;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GsonUtil {

    private GsonUtil() {
        throw new UnsupportedOperationException();
    }

    private static final JsonParser PARSER = new JsonParser();
    public static final Gson GSON = new Gson();

    public static JsonElement parseString(String input) {
        return input == null ? new JsonObject() : PARSER.parse(input);
    }

    public static JsonObject parseStringAsObject(String input) {
        return parseString(input).getAsJsonObject();
    }

}
