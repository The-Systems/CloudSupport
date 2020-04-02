package eu.thesystems.cloud.loader;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.command.player.BukkitPlayerCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;

public class BukkitPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        if (!CloudSupport.getInstance().selectCloudSystem()) {
            System.out.println("[CloudSupport] Cannot find cloud system, disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            CommandMap bukkitCommandMap = (CommandMap) super.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(super.getServer());

            CloudSupport.getInstance().getSelectedCloudSystem().getCommandMap()
                    .onCommandRegistered(cloudCommand -> bukkitCommandMap.register("cloud", new Command(
                            cloudCommand.getName(),
                            "", "",
                            cloudCommand.getAliases() != null ? Arrays.asList(cloudCommand.getAliases()) : Collections.emptyList()) {
                        @Override
                        public boolean execute(CommandSender sender, String s, String[] args) {
                            if (sender instanceof Player) {
                                if (cloudCommand.getPermission() != null && !sender.hasPermission(cloudCommand.getPermission())) {
                                    sender.sendMessage("§cYou don't have the permission to execute this command");
                                    return false;
                                }

                                cloudCommand.execute(new BukkitPlayerCommandSender((Player) sender), args);
                            } else {
                                sender.sendMessage("This command is only available for players");
                            }
                            return false;
                        }
                    }));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            exception.printStackTrace();
        }

        CloudSupport.getInstance().startAddons();
    }

    @Override
    public void onDisable() {
        CloudSupport.getInstance().stopAddons();
    }
}
