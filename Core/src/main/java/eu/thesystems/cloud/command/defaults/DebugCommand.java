package eu.thesystems.cloud.command.defaults;

import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.command.CloudCommand;
import eu.thesystems.cloud.command.CloudCommandSender;

public class DebugCommand extends CloudCommand {
    public DebugCommand() {
        super("cs-debug", "cloudsupport.command.debug");
    }

    @Override
    public void execute(CloudCommandSender sender, String[] args) {
        CloudSupport.getInstance().setDebugging(!CloudSupport.getInstance().isDebugging());
        sender.sendMessage("Debugging was successfully " + (CloudSupport.getInstance().isDebugging() ? "enabled" : "disabled"));
    }
}
