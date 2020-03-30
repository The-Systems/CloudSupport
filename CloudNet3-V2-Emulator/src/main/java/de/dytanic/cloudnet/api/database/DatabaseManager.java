/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.database;

import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.wrapper.database.IDatabaseProvider;

import java.util.Collection;
import java.util.Collections;


/**
 * Created by Tareko on 20.08.2017.
 */
public class DatabaseManager {

    private IDatabaseProvider databaseProvider;

    public DatabaseManager(IDatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    public Collection<Database> getCachedDatabases() {
        return Collections.emptyList();
    }

    public Database getDatabase(String name) {
        return new CloudNet2Database(this.databaseProvider.getDatabase(name));
    }
}
