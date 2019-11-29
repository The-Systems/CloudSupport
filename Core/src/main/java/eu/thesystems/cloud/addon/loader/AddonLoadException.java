package eu.thesystems.cloud.addon.loader;
/*
 * Created by derrop on 16.11.2019
 */

import eu.thesystems.cloud.addon.CloudAddonInfo;

public class AddonLoadException extends RuntimeException {

    private CloudAddonInfo addonInfo;
    private ClassLoader classLoader;

    public AddonLoadException(CloudAddonInfo addonInfo, ClassLoader classLoader, String message) {
        super(message);
        this.addonInfo = addonInfo;
        this.classLoader = classLoader;
    }

    public CloudAddonInfo getAddonInfo() {
        return this.addonInfo;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
}
