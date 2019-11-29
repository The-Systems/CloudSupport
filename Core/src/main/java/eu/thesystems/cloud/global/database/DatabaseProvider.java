package eu.thesystems.cloud.global.database;
/*
 * Created by derrop on 25.10.2019
 */

import java.util.Collection;

public interface DatabaseProvider {

    Database getDatabase(String name);

    Collection<String> getDatabases();

}
