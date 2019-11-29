package eu.thesystems.cloud.addon;
/*
 * Created by derrop on 16.11.2019
 */

import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.CloudSystem;

public abstract class CloudAddon {

    CloudAddonInfo addonInfo;
    boolean enabled = false;

    public abstract void onEnable();

    public abstract void onDisable();

    public final CloudAddonInfo getAddonInfo() {
        return this.addonInfo;
    }

    public final CloudSystem getCloud() {
        return CloudSupport.getInstance().getSelectedCloudSystem();
    }

    public final boolean isEnabled() {
        return this.enabled;
    }
}
