package eu.thesystems.cloud.addon.loader;
/*
 * Created by derrop on 16.11.2019
 */

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import eu.thesystems.cloud.addon.CloudAddon;
import eu.thesystems.cloud.addon.CloudAddonFactory;
import eu.thesystems.cloud.addon.CloudAddonInfo;
import eu.thesystems.cloud.addon.dependency.DependencyLoader;
import eu.thesystems.cloud.addon.dependency.MavenDependency;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CloudAddonLoader {

    private final Gson gson = new Gson();

    private DependencyLoader dependencyLoader;
    private CloudAddonFactory addonFactory;
    private ClassLoader parentClassLoader;

    public CloudAddonLoader(DependencyLoader dependencyLoader, CloudAddonFactory addonFactory, ClassLoader parentClassLoader) {
        this.dependencyLoader = dependencyLoader;
        this.addonFactory = addonFactory;
        this.parentClassLoader = parentClassLoader;
    }

    public CloudAddonInfo loadAddonInfo(URL url) throws IOException, InvalidAddonInfoException {
        CloudAddonInfo addonInfo = null;

        try (InputStream addonStream = url.openStream();
             ZipInputStream inputStream = new ZipInputStream(addonStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = inputStream.getNextEntry()) != null) {
                if (zipEntry.getName().equals("addon.json")) {
                    Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    try {
                        addonInfo = this.gson.fromJson(reader, CloudAddonInfo.class);
                    } catch (JsonSyntaxException exception) {
                        throw new InvalidAddonInfoException(url, "addon.json contains malformed json", exception);
                    }
                }

                inputStream.closeEntry();
            }
        }

        if (addonInfo == null) {
            throw new InvalidAddonInfoException(url, "no addon.json found");
        }
        if (addonInfo.getName() == null) {
            throw new InvalidAddonInfoException(url, "addon.json does not contain a \"name\" entry");
        }
        if (addonInfo.getVersion() == null) {
            throw new InvalidAddonInfoException(url, "addon.json does not contain a \"version\" entry");
        }
        if (addonInfo.getAuthors() == null) {
            throw new InvalidAddonInfoException(url, "addon.json does not contain an \"authors\" entry");
        }
        if (addonInfo.getMain() == null) {
            throw new InvalidAddonInfoException(url, "addon.json does not contain a \"main\" entry");
        }

        addonInfo.setUrl(url);

        return addonInfo;
    }

    public CloudAddonInfo loadAddonInfo(Path path) throws IOException, InvalidAddonInfoException {
        return this.loadAddonInfo(path.toUri().toURL());
    }

    public CloudAddon loadAddon(URL url, Function<CloudAddonInfo, Path> dataDirectoryProvider) throws IOException, InvalidAddonInfoException, ReflectiveOperationException {
        CloudAddonInfo addonInfo = this.loadAddonInfo(url);
        System.out.println("Loading addon " + addonInfo.getName() + " (Version " + addonInfo.getVersion() + " by " + String.join(", ", addonInfo.getAuthors()) + ") from " + url + "...");

        List<URL> urls = new ArrayList<>();
        if (addonInfo.getDependencies() != null) {
            for (MavenDependency dependency : addonInfo.getDependencies()) {
                URL dependencyURL = this.dependencyLoader.loadDependency(dependency);
                if (dependencyURL != null) {
                    urls.add(dependencyURL);
                }
            }
        }
        urls.add(url);

        URLClassLoader classLoader = new CloudAddonClassLoader(urls.toArray(new URL[0]), addonInfo, this.parentClassLoader);
        Class<?> mainClazz = null;
        try {
            mainClazz = classLoader.loadClass(addonInfo.getMain());
        } catch (ClassNotFoundException exception) {
            throw new Error("main class " + addonInfo.getMain() + " for addon " + addonInfo.getName() + " not found", exception);
        }
        if (!CloudAddon.class.isAssignableFrom(mainClazz)) {
            throw new AddonLoadException(addonInfo, classLoader, "Main class \"" + addonInfo.getMain() + "\" has to extend from CloudAddon");
        }

        Path dataDirectory = dataDirectoryProvider.apply(addonInfo);
        Preconditions.checkNotNull(dataDirectory, "dataDirectory");

        CloudAddon addon = this.addonFactory.createAddon(mainClazz, addonInfo, dataDirectory);
        Files.createDirectories(dataDirectory);
        return addon;
    }

    public CloudAddon loadAddon(Path path, Function<CloudAddonInfo, Path> dataDirectoryProvider) throws IOException, InvalidAddonInfoException, ReflectiveOperationException {
        return this.loadAddon(path.toUri().toURL(), dataDirectoryProvider);
    }

}
