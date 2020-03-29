package eu.thesystems.cloud.addon;
/*
 * Created by derrop on 16.11.2019
 */

public class CloudAddonFactory {

    public CloudAddon createAddon(Class<?> clazz, CloudAddonInfo addonInfo) throws ReflectiveOperationException {
        CloudAddon cloudAddon = (CloudAddon) clazz.getConstructor().newInstance();
        cloudAddon.addonInfo = addonInfo;
        return cloudAddon;
    }

    public void setEnabled(CloudAddon cloudAddon, boolean enabled) {
        cloudAddon.enabled = enabled;
    }

}
