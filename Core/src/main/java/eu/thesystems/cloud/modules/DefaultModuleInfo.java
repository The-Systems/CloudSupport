package eu.thesystems.cloud.modules;
/*
 * Created by derrop on 26.10.2019
 */

import com.google.gson.JsonObject;
import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.exception.CloudSupportException;

import java.nio.file.Path;

public class DefaultModuleInfo implements ModuleInfo {
    private String name;
    private String[] authors;
    private String version;
    private String main;
    private Path file;
    private JsonObject availableModuleData;

    public DefaultModuleInfo(String name, String[] authors, String version, String main, Path file, JsonObject availableModuleData) {
        this.name = name;
        this.authors = authors;
        this.version = version;
        this.main = main;
        this.file = file;
        this.availableModuleData = availableModuleData;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String[] getAuthors() {
        return this.authors;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getMain() {
        return this.main;
    }

    @Override
    public Path getFile() {
        if (this.file == null) {
            throw new CloudSupportException(CloudSupport.getInstance().getSelectedCloudSystem());
        }
        return this.file;
    }

    @Override
    public JsonObject getAvailableModuleData() {
        return this.availableModuleData;
    }
}
