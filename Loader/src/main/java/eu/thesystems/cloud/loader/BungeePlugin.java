package eu.thesystems.cloud.loader;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.global.command.player.BungeePlayerCommandSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {
    @Override
    public void onEnable() {
        if (!CloudSupport.getInstance().selectCloudSystem()) {
            System.out.println("[CloudSupport] Cannot find cloud system");
            return;
        }

        CloudSupport.getInstance().getSelectedCloudSystem().getCommandMap()
                .onCommandRegistered(cloudCommand -> super.getProxy().getPluginManager().registerCommand(this,
                        new Command(cloudCommand.getName(), cloudCommand.getPermission(), cloudCommand.getAliases()) {
                            @Override
                            public void execute(CommandSender sender, String[] args) {
                                if (sender instanceof ProxiedPlayer) {
                                    cloudCommand.execute(new BungeePlayerCommandSender((ProxiedPlayer) sender), args);
                                } else {
                                    sender.sendMessage(TextComponent.fromLegacyText("§cYou don't have the permission to execute this command"));
                                }
                            }
                        }));
    }
}
