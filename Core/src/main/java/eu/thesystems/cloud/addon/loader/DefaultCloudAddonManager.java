package eu.thesystems.cloud.addon.loader;
/*
 * Created by derrop on 16.11.2019
 */

import eu.thesystems.cloud.addon.CloudAddon;
import eu.thesystems.cloud.addon.CloudAddonFactory;
import eu.thesystems.cloud.addon.dependency.DependencyLoader;
import eu.thesystems.cloud.addon.dependency.PersistableDependencyLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultCloudAddonManager implements CloudAddonManager {

    private final DependencyLoader dependencyLoader = new PersistableDependencyLoader(Paths.get(System.getProperty("cloudsupport.dependency.persistable.baseDirectory", "cloudsupport/dependencies")));
    private final CloudAddonFactory factory = new CloudAddonFactory();
    private final CloudAddonLoader addonLoader = new CloudAddonLoader(this.dependencyLoader, this.factory, this.getClass().getClassLoader());
    private final Path defaultAddonDirectory = Paths.get(System.getProperty("cloudsupport.addon.directory", "cloudsupport/addons"));
    private final Collection<CloudAddon> addons = new CopyOnWriteArrayList<>();

    @Override
    public void loadAddons() {
        this.loadAddons(this.defaultAddonDirectory);
    }

    @Override
    public void loadAddons(Path directory) {
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                return;
            }
            Files.list(directory)
                    .filter(path -> path.getFileName().toString().endsWith(".jar"))
                    .forEach(path -> {
                        try {
                            this.loadAddon(path);
                        } catch (MalformedURLException exception) {
                            exception.printStackTrace();
                        }
                    });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void enableAddons() {
        for (CloudAddon addon : this.addons) {
            this.enableAddon(addon);
        }
    }

    @Override
    public void disableAddons() {
        for (CloudAddon addon : this.addons) {
            this.disableAddon(addon);
        }
    }

    @Override
    public void unloadAddons() {
        for (CloudAddon addon : this.addons) {
            this.unloadAddon(addon);
        }
    }

    @Override
    public CloudAddon loadAddon(Path path) throws MalformedURLException {
        return this.loadAddon(path.toUri().toURL());
    }

    @Override
    public CloudAddon loadAddon(URL url) {
        try {
            CloudAddon addon = this.addonLoader.loadAddon(url);
            if (addon != null) {
                this.addons.add(addon);
                return addon;
            }
        } catch (IOException | IllegalAccessException | InstantiationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void enableAddon(CloudAddon addon) {
        if (addon.isEnabled()) {
            return;
        }
        try {
            addon.onEnable();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        this.factory.setEnabled(addon, true);
    }

    @Override
    public void disableAddon(CloudAddon addon) {
        if (!addon.isEnabled()) {
            return;
        }
        try {
            addon.onDisable();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        this.factory.setEnabled(addon, false);
    }

    @Override
    public void unloadAddon(CloudAddon addon) {
        if (addon.isEnabled()) {
            this.disableAddon(addon);
        }
        try {
            addon.getAddonInfo().getClassLoader().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.addons.remove(addon);
    }

    @Override
    public Collection<CloudAddon> getLoadedAddons() {
        return Collections.unmodifiableCollection(this.addons);
    }
}
