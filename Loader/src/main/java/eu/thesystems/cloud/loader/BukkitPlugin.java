package eu.thesystems.cloud.loader;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.CloudSupport;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        if (!CloudSupport.getInstance().selectCloudSystem()) {
            System.out.println("[CloudSupport] Cannot find cloud system, disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }
}
