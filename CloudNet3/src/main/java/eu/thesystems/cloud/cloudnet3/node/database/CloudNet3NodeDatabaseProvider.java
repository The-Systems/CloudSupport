package eu.thesystems.cloud.cloudnet3.node.database;
/*
 * Created by derrop on 26.10.2019
 */

import de.dytanic.cloudnet.CloudNet;
import eu.thesystems.cloud.global.database.Database;
import eu.thesystems.cloud.global.database.DatabaseProvider;

import java.util.Collection;

public class CloudNet3NodeDatabaseProvider implements DatabaseProvider {

    private CloudNet cloudNet;

    public CloudNet3NodeDatabaseProvider(CloudNet cloudNet) {
        this.cloudNet = cloudNet;
    }

    @Override
    public Database getDatabase(String name) {
        return new CloudNet3NodeDatabase(this.cloudNet.getDatabaseProvider().getDatabase(name));
    }

    @Override
    public Collection<String> getDatabases() {
        return this.cloudNet.getDatabaseProvider().getDatabaseNames();
    }
}
