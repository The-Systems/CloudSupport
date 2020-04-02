package eu.thesystems.cloud.database;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.JsonObject;

public interface Database {

    void insert(String key, JsonObject data);

    void update(String key, JsonObject data);

    void delete(String key);

    void clear();

    JsonObject get(String key);

    String getName();

}
