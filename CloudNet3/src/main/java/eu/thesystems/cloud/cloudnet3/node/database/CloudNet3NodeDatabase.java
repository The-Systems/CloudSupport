package eu.thesystems.cloud.cloudnet3.node.database;
/*
 * Created by derrop on 26.10.2019
 */

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.database.IDatabase;
import eu.thesystems.cloud.global.database.Database;

public class CloudNet3NodeDatabase implements Database {

    private IDatabase database;

    public CloudNet3NodeDatabase(IDatabase database) {
        this.database = database;
    }

    @Override
    public void insert(String key, JsonObject data) {
        this.database.insert(key, new JsonDocument(data));
    }

    @Override
    public void update(String key, JsonObject data) {
        this.database.update(key, new JsonDocument(data));
    }

    @Override
    public void delete(String key) {
        this.database.delete(key);
    }

    @Override
    public void clear() {
        this.database.clear();
    }

    @Override
    public JsonObject get(String key) {
        JsonDocument document = this.database.get(key);
        return document != null ? document.toJsonObject() : null;
    }

    @Override
    public String getName() {
        return this.database.getName();
    }
}
