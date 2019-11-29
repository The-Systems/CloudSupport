package eu.thesystems.cloud.modules;
/*
 * Created by derrop on 25.10.2019
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public interface ModuleManager {

    ModuleInfo loadModule(Path file);

    void unloadModule(ModuleInfo moduleInfo);

    void enableModule(Module module);

    void disableModule(ModuleInfo moduleInfo);

    void deleteModule(ModuleInfo moduleInfo);

    ModuleInfo getModuleInfoFromClassloader(ClassLoader classLoader);

    default ModuleInfo installModule(String url, Path path) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, path);
            }
            connection.disconnect();

            return this.loadModule(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
