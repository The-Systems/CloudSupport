package eu.thesystems.cloud.modules;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.JsonObject;

import java.nio.file.Path;

public interface ModuleInfo {

    String getName();

    String[] getAuthors();

    String getVersion();

    String getMain();

    Path getFile();

    JsonObject getAvailableModuleData();

}
