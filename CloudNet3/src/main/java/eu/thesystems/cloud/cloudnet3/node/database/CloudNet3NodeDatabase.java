package eu.thesystems.cloud.cloudnet3.node.database;
/*
 * Created by derrop on 26.10.2019
 */

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.database.IDatabase;
import eu.thesystems.cloud.GsonUtil;
import eu.thesystems.cloud.database.Database;

public class CloudNet3NodeDatabase implements Database {

    private IDatabase database;

    public CloudNet3NodeDatabase(IDatabase database) {
        this.database = database;
    }

    @Override
    public void insert(String key, JsonObject data) {
        this.database.insert(key, JsonDocument.newDocument(data.toString()));
    }

    @Override
    public void update(String key, JsonObject data) {
        this.database.update(key, JsonDocument.newDocument(data.toString()));
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
        return document != null ? GsonUtil.parseString(document.toJson()).getAsJsonObject() : null;
    }

    @Override
    public String getName() {
        return this.database.getName();
    }
}
