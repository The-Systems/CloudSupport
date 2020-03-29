package eu.thesystems.cloud.addon;
/*
 * Created by derrop on 16.11.2019
 */

import java.nio.file.Path;

public class CloudAddonFactory {

    public CloudAddon createAddon(Class<?> clazz, CloudAddonInfo addonInfo, Path directory) throws ReflectiveOperationException {
        CloudAddon cloudAddon = (CloudAddon) clazz.getConstructor().newInstance();
        cloudAddon.addonInfo = addonInfo;
        cloudAddon.dataDirectory = directory;
        return cloudAddon;
    }

    public void setEnabled(CloudAddon cloudAddon, boolean enabled) {
        cloudAddon.enabled = enabled;
    }

}
