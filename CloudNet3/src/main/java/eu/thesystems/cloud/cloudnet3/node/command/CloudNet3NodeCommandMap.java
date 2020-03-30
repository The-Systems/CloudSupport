package eu.thesystems.cloud.cloudnet3.node.command;
/*
 * Created by derrop on 16.11.2019
 */

import eu.thesystems.cloud.cloudnet3.node.CloudNet3Node;
import eu.thesystems.cloud.global.command.CloudCommand;
import eu.thesystems.cloud.global.command.CommandMap;
import eu.thesystems.cloud.global.command.EmptyCommandMap;

public class CloudNet3NodeCommandMap extends EmptyCommandMap {

    private CloudNet3Node cloudNet3Node;

    public CloudNet3NodeCommandMap(CloudNet3Node cloudNet3Node) {
        this.cloudNet3Node = cloudNet3Node;
    }

    @Override
    public void registerCommand(CloudCommand command) {
        super.registerCommand(command);
        this.cloudNet3Node.getCloudNet().getCommandMap().registerCommand(new CloudNet3CommandWrapper(command));
    }
}
