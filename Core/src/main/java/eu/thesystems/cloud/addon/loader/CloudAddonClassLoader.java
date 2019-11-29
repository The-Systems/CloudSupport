package eu.thesystems.cloud.addon.loader;
/*
 * Created by derrop on 16.11.2019
 */

import eu.thesystems.cloud.addon.CloudAddonInfo;

import java.net.URL;
import java.net.URLClassLoader;

public class CloudAddonClassLoader extends URLClassLoader {
    private CloudAddonInfo addonInfo;

    public CloudAddonClassLoader(URL[] urls, CloudAddonInfo addonInfo, ClassLoader parent) {
        super(urls, parent);
        addonInfo.setClassLoader(this);
        this.addonInfo = addonInfo;
    }

    public CloudAddonInfo getAddonInfo() {
        return this.addonInfo;
    }
}
