package eu.thesystems.cloud.addon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class AddonConfiguration {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private CloudAddon addon;
    private JsonObject config;

    public Gson getGson() {
        return this.gson;
    }

    public final JsonObject getConfig() {
        if (this.config == null) {
            this.reloadConfig();
        }
        return this.config;
    }

    public void setConfig(JsonObject config) {
        this.config = config;
    }

    public final <T> T getConfigAs(Class<T> tClass) {
        return this.getGson().fromJson(this.getConfig(), tClass);
    }

    public final void saveConfig() {
        if (this.config == null) {
            this.config = new JsonObject();
        }
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(this.addon.dataDirectory.resolve("config.json")), StandardCharsets.UTF_8)) {
            this.getGson().toJson(this.config, writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public final void reloadConfig() {
        this.reloadConfig(new JsonObject());
    }

    public final <T> void reloadConfig(T defaultConfig) {
        this.reloadConfig(this.getGson().toJsonTree(defaultConfig));
    }

    public final void reloadConfig(JsonObject defaultConfig) {
        Path path = this.addon.dataDirectory.resolve("config.json");
        if (!Files.exists(path)) {
            this.config = defaultConfig;
            this.saveConfig();
            return;
        }

        try (Reader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
            this.config = this.getGson().fromJson(reader, JsonObject.class);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
