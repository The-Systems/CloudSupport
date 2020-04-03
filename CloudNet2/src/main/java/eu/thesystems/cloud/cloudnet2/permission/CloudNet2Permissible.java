package eu.thesystems.cloud.cloudnet2.permission;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import eu.thesystems.cloud.GsonUtil;
import eu.thesystems.cloud.permission.Permissible;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CloudNet2Permissible implements Permissible {

    protected static final Type OBJECT_MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();

    protected Map<String, Object> asMap(JsonObject object) {
        return object == null ? new HashMap<>() : GsonUtil.GSON.fromJson(object, OBJECT_MAP_TYPE);
    }

    protected Collection<String> mapPermissions(Map<String, Boolean> permissions) {
        return permissions.entrySet().stream()
                .map(entry -> entry.getValue() ? entry.getKey() : "-" + entry.getKey())
                .collect(Collectors.toList());
    }

}
