package eu.thesystems.cloud.cloudnet2.database;
/*
 * Created by derrop on 26.10.2019
 */

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.thesystems.cloud.cloudsystem.cloudnet.CloudNet;
import eu.thesystems.cloud.exception.CloudSupportException;
import eu.thesystems.cloud.database.Database;

public class CloudNet2Database implements Database {

    private CloudNet cloudNet;
    private String name;
    private de.dytanic.cloudnet.lib.database.Database database;

    public CloudNet2Database(CloudNet cloudNet, String name, de.dytanic.cloudnet.lib.database.Database database) {
        this.cloudNet = cloudNet;
        this.name = name;
        this.database = database;
    }

    @Override
    public void insert(String key, JsonObject data) {
        this.database.insert(new Document(data).append(de.dytanic.cloudnet.lib.database.Database.UNIQUE_NAME_KEY, key));
    }

    @Override
    public void update(String key, JsonObject data) {
        this.database.insert(new Document(data).append(de.dytanic.cloudnet.lib.database.Database.UNIQUE_NAME_KEY, key));
    }

    @Override
    public void delete(String key) {
        this.database.delete(key);
    }

    @Override
    public void clear() {
        throw new CloudSupportException(this.cloudNet);
    }

    @Override
    public JsonObject get(String key) {
        Document document = this.database.getDocument(key);
        return document != null ? document.obj() : null;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
