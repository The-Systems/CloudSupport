package eu.thesystems.cloud.addon.loader;
/*
 * Created by derrop on 16.11.2019
 */

import eu.thesystems.cloud.addon.CloudAddon;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;

public interface CloudAddonManager {

    void loadAddons(Path directory);

    void loadAddons();

    void enableAddons();

    void disableAddons();

    void unloadAddons();

    CloudAddon loadAddon(Path path) throws MalformedURLException;

    CloudAddon loadAddon(URL url);

    void enableAddon(CloudAddon addon);

    void disableAddon(CloudAddon addon);

    void unloadAddon(CloudAddon addon);

    Collection<CloudAddon> getLoadedAddons();

}
