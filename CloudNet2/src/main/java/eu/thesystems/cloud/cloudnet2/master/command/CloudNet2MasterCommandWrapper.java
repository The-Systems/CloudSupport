package eu.thesystems.cloud.cloudnet2.master.command;
/*
 * Created by derrop on 16.11.2019
 */

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import eu.thesystems.cloud.global.command.CloudCommand;

public class CloudNet2MasterCommandWrapper extends Command {
    private CloudCommand wrapped;

    protected CloudNet2MasterCommandWrapper(CloudCommand wrapped) {
        super(wrapped.getName(), wrapped.getPermission(), wrapped.getAliases() != null ? wrapped.getAliases() : new String[0]);
        this.wrapped = wrapped;
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        this.wrapped.execute(new CloudNet2MasterCommandSenderWrapper(sender), args);
    }
}
