package eu.thesystems.cloud.cloudnet2.master.database;
/*
 * Created by derrop on 26.10.2019
 */

import de.dytanic.cloudnetcore.CloudNet;
import eu.thesystems.cloud.cloudnet2.CloudNet2;
import eu.thesystems.cloud.cloudnet2.database.CloudNet2Database;
import eu.thesystems.cloud.database.Database;
import eu.thesystems.cloud.database.DatabaseProvider;

import java.util.Collection;

public class CloudNet2MasterDatabaseProvider implements DatabaseProvider {
    private CloudNet2 cloudNet2;
    private CloudNet cloudNet;

    public CloudNet2MasterDatabaseProvider(CloudNet2 cloudNet2, CloudNet cloudNet) {
        this.cloudNet2 = cloudNet2;
        this.cloudNet = cloudNet;
    }

    @Override
    public Database getDatabase(String name) {
        return new CloudNet2Database(this.cloudNet2, name, this.cloudNet.getDatabaseManager().getDatabase(name));
    }

    @Override
    public Collection<String> getDatabases() {
        return this.cloudNet.getDatabaseManager().getDatabases();
    }
}
