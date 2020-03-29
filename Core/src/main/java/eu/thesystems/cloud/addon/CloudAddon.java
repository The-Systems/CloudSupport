package eu.thesystems.cloud.addon;
/*
 * Created by derrop on 16.11.2019
 */

import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.CloudSystem;

import java.nio.file.Path;

public abstract class CloudAddon {

    CloudAddonInfo addonInfo;
    boolean enabled = false;

    Path dataDirectory;

    private AddonConfiguration configuration = new AddonConfiguration(this);

    public abstract void onEnable();

    public abstract void onDisable();

    public final CloudAddonInfo getAddonInfo() {
        return this.addonInfo;
    }

    public final Path getDataDirectory() {
        return this.dataDirectory;
    }

    public final AddonConfiguration getConfiguration() {
        return this.configuration;
    }

    public final boolean isEnabled() {
        return this.enabled;
    }

    public final CloudSystem getCloud() {
        return CloudSupport.getInstance().getSelectedCloudSystem();
    }
}
