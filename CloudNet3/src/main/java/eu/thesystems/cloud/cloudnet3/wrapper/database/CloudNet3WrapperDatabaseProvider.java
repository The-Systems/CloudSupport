package eu.thesystems.cloud.cloudnet3.wrapper.database;
/*
 * Created by derrop on 26.10.2019
 */

import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.global.database.Database;
import eu.thesystems.cloud.global.database.DatabaseProvider;

import java.util.Collection;

public class CloudNet3WrapperDatabaseProvider implements DatabaseProvider {//todo
    private CloudNet3 cloudNet3;
    private Wrapper wrapper;

    public CloudNet3WrapperDatabaseProvider(CloudNet3 cloudNet3, Wrapper wrapper) {
        this.cloudNet3 = cloudNet3;
        this.wrapper = wrapper;
    }

    @Override
    public Database getDatabase(String name) {
        return new CloudNet3WrapperDatabase(this.cloudNet3, this.wrapper, this.wrapper.getDatabaseProvider().getDatabase(name));
    }

    @Override
    public Collection<String> getDatabases() {
        return null;
    }
}
