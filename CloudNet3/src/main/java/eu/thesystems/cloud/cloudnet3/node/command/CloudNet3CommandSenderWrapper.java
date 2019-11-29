package eu.thesystems.cloud.cloudnet3.node.command;
/*
 * Created by derrop on 16.11.2019
 */

import de.dytanic.cloudnet.command.ICommandSender;
import eu.thesystems.cloud.global.command.CloudCommandSender;

public class CloudNet3CommandSenderWrapper implements CloudCommandSender {

    private ICommandSender sender;

    public CloudNet3CommandSenderWrapper(ICommandSender sender) {
        this.sender = sender;
    }

    public ICommandSender getSender() {
        return sender;
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
