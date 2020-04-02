package eu.thesystems.cloud.cloudnet3.wrapper.database;
/*
 * Created by derrop on 26.10.2019
 */

import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.thesystems.cloud.database.Database;
import eu.thesystems.cloud.database.DatabaseProvider;

import java.util.Collection;

public class CloudNet3WrapperDatabaseProvider implements DatabaseProvider {//todo not tested
    private Wrapper wrapper;

    public CloudNet3WrapperDatabaseProvider(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public Database getDatabase(String name) {
        return new CloudNet3WrapperDatabase(this.wrapper.getDatabaseProvider().getDatabase(name));
    }

    @Override
    public Collection<String> getDatabases() {
        return this.wrapper.getDatabaseProvider().getDatabaseNames();
    }
}
