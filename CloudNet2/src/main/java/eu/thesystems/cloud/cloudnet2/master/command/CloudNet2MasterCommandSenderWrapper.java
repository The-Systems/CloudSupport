package eu.thesystems.cloud.cloudnet2.master.command;
/*
 * Created by derrop on 16.11.2019
 */

import de.dytanic.cloudnet.command.CommandSender;
import eu.thesystems.cloud.global.command.CloudCommandSender;

public class CloudNet2MasterCommandSenderWrapper implements CloudCommandSender {

    private CommandSender sender;

    public CloudNet2MasterCommandSenderWrapper(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

    @Override
    public void sendMessage(String... messages) {
        this.sender.sendMessage(messages);
    }

    @Override
    public String getName() {
        return this.sender.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }
}
