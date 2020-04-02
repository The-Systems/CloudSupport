package eu.thesystems.cloud.command.player;

import eu.thesystems.cloud.command.CloudCommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePlayerCommandSender implements CloudCommandSender {

    private ProxiedPlayer player;

    public BungeePlayerCommandSender(ProxiedPlayer player) {
        this.player = player;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessage(String... messages) {
        this.sendMessage(String.join("\n", messages));
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.player.hasPermission(permission);
    }
}
