package eu.thesystems.cloud.cloudnet3.node;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.cloudnet3.node.command.CloudNet3NodeCommandMap;
import eu.thesystems.cloud.cloudnet3.node.database.CloudNet3NodeDatabaseProvider;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.global.command.CommandMap;
import eu.thesystems.cloud.global.database.DatabaseProvider;

public class CloudNet3Node extends CloudNet3 {

    private final CloudNet cloudNet = CloudNet.getInstance();

    private CommandMap commandMap = new CloudNet3NodeCommandMap(this);
    private DatabaseProvider databaseProvider = new CloudNet3NodeDatabaseProvider(this.cloudNet);

    public CloudNet3Node() {
        super(SupportedCloudSystem.CLOUDNET_3_NODE, "CloudNet3-Node", CloudNet.class.getPackage().getImplementationTitle() + "-" + CloudNet.class.getPackage().getImplementationVersion());
    }

    public CloudNet getCloudNet() {
        return this.cloudNet;
    }

    @Override
    public DatabaseProvider getDatabaseProvider() {
        return this.databaseProvider;
    }

    @Override
    public CommandMap getCommandMap() {
        return this.commandMap;
    }
}
