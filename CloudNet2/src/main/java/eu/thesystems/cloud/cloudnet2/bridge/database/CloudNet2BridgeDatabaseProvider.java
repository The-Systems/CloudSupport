package eu.thesystems.cloud.cloudnet2.bridge.database;
/*
 * Created by derrop on 26.10.2019
 */

import de.dytanic.cloudnet.api.CloudAPI;
import eu.thesystems.cloud.cloudnet2.CloudNet2;
import eu.thesystems.cloud.cloudnet2.database.CloudNet2Database;
import eu.thesystems.cloud.exception.CloudSupportException;
import eu.thesystems.cloud.global.database.Database;
import eu.thesystems.cloud.global.database.DatabaseProvider;

import java.util.Collection;

public class CloudNet2BridgeDatabaseProvider implements DatabaseProvider { //todo not tested

    private CloudAPI cloudAPI;
    private CloudNet2 cloudNet2;

    public CloudNet2BridgeDatabaseProvider(CloudAPI cloudAPI, CloudNet2 cloudNet2) {
        this.cloudAPI = cloudAPI;
        this.cloudNet2 = cloudNet2;
    }

    @Override
    public Database getDatabase(String name) {
        return new CloudNet2Database(this.cloudNet2, name, this.cloudAPI.getDatabaseManager().getDatabase(name));
    }

    @Override
    public Collection<String> getDatabases() {
        throw new CloudSupportException(this.cloudNet2);
    }
}
