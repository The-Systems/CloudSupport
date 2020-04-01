package eu.thesystems.cloud.cloudnet3.node;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListenerRegistry;
import de.dytanic.cloudnet.ext.bridge.node.player.NodePlayerManager;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.cloudnet3.CloudNet3ChannelMessenger;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketProvider;
import eu.thesystems.cloud.cloudnet3.cluster.node.NodeClusterPacketProvider;
import eu.thesystems.cloud.cloudnet3.node.command.CloudNet3NodeCommandMap;
import eu.thesystems.cloud.cloudnet3.node.database.CloudNet3NodeDatabaseProvider;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.global.database.DatabaseProvider;

public class CloudNet3Node extends CloudNet3 {

    private final CloudNet cloudNet = CloudNet.getInstance();

    private final DatabaseProvider databaseProvider = new CloudNet3NodeDatabaseProvider(this.cloudNet);
    private final CloudNet3ChannelMessenger channelMessenger = new CloudNet3NodeChannelMessenger(this);
    private final ClusterPacketProvider clusterPacketProvider = new NodeClusterPacketProvider(this);

    public CloudNet3Node() {
        super(
                SupportedCloudSystem.CLOUDNET_3_NODE,
                "CloudNet3-Node",
                CloudNet.class.getPackage().getImplementationTitle() + "-" + CloudNet.class.getPackage().getImplementationVersion(),
                NodePlayerManager.getInstance()
        );
        super.commandMap = new CloudNet3NodeCommandMap(this);
        this.cloudNet.getEventManager().registerListener(this.clusterPacketProvider); // todo unregister?
        this.cloudNet.getEventManager().registerListener(this.getChannelMessenger()); // todo unregister?
    }

    public CloudNet getCloudNet() {
        return this.cloudNet;
    }

    @Override
    public String getOwnComponentName() {
        return this.cloudNet.getCurrentNetworkClusterNodeInfoSnapshot().getNode().getUniqueId();
    }

    @Override
    public DatabaseProvider getDatabaseProvider() {
        return this.databaseProvider;
    }

    @Override
    public ClusterPacketProvider getClusterPacketProvider() {
        return this.clusterPacketProvider;
    }

    @Override
    public CloudNet3ChannelMessenger getChannelMessenger() {
        return this.channelMessenger;
    }
}
