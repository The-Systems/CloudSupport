package eu.thesystems.cloud.command.player;

import eu.thesystems.cloud.command.CloudCommandSender;
import org.bukkit.entity.Player;

public class BukkitPlayerCommandSender implements CloudCommandSender {

    private Player player;

    public BukkitPlayerCommandSender(Player player) {
        this.player = player;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(message);
    }

    @Override
    public void sendMessage(String... messages) {
        this.player.sendMessage(messages);
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
