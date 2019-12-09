package eu.thesystems.cloud;
/*
 * Created by derrop on 09.12.2019
 */

import org.bukkit.plugin.java.JavaPlugin;

public class CloudNet2Emulator extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("This plugin does nothing, except that it lets all plugins that depend on the \"CloudNetAPI\" plugin of CloudNet 2 load after the \"CloudSupport\" plugin to support CloudNet 2 plugins on CloudNet 3");
    }

    @Override
    public void onDisable() {
    }
}
