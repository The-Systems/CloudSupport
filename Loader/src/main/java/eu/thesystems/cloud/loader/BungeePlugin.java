package eu.thesystems.cloud.loader;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.CloudSupport;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {
    @Override
    public void onEnable() {
        CloudSupport.getInstance().selectCloudSystem();
    }
}
