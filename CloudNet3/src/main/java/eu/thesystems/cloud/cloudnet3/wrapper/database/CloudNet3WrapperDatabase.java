package eu.thesystems.cloud.cloudnet3.wrapper.database;
/*
 * Created by derrop on 26.10.2019
 */

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.dytanic.cloudnet.wrapper.database.IDatabase;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.global.database.Database;

public class CloudNet3WrapperDatabase implements Database {

    private CloudNet3 cloudNet3;
    private Wrapper wrapper;
    private IDatabase database;

    public CloudNet3WrapperDatabase(CloudNet3 cloudNet3, Wrapper wrapper, IDatabase database) {
        this.cloudNet3 = cloudNet3;
        this.wrapper = wrapper;
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

    }

    @Override
    public JsonObject get(String key) {
        return null;
    }

    @Override
    public String getName() {
        return this.database.getName();
    }
}
