package eu.thesystems.cloud.cloudnet3.node.command;
/*
 * Created by derrop on 16.11.2019
 */

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.ICommandSender;
import de.dytanic.cloudnet.command.ITabCompleter;
import de.dytanic.cloudnet.common.Properties;
import eu.thesystems.cloud.global.command.CloudCommand;

import java.util.Collection;

public class CloudNet3CommandWrapper extends Command implements ITabCompleter {

    private CloudCommand wrapped;

    public CloudNet3CommandWrapper(CloudCommand wrapped) {
        super(getNames(wrapped.getName(), wrapped.getAliases()), wrapped.getPermission());
        this.wrapped = wrapped;
    }

    private static String[] getNames(String name, String[] aliases) {
        if (aliases == null || aliases.length == 0) {
            return new String[]{name};
        }
        String[] names = new String[1 + aliases.length];
        names[0] = name;
        System.arraycopy(aliases, 0, names, 1, aliases.length);
        return names;
    }

    @Override
    public void execute(ICommandSender sender, String command, String[] args, String line, Properties properties) {
        this.wrapped.execute(new CloudNet3CommandSenderWrapper(sender), args);
    }

    @Override
    public Collection<String> complete(String line, String[] args, Properties properties) {
        return this.wrapped.tabComplete(args);
    }
}
