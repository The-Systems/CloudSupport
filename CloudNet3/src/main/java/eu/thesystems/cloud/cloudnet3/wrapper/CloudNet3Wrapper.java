package eu.thesystems.cloud.cloudnet3.wrapper;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.driver.network.protocol.IPacketListenerRegistry;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.thesystems.cloud.ChannelMessenger;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.cloudnet3.CloudNet3ChannelMessenger;
import eu.thesystems.cloud.cloudnet3.node.cluster.ClusterPacketProvider;
import eu.thesystems.cloud.cloudnet3.node.cluster.wrapper.WrapperClusterPacketProvider;
import eu.thesystems.cloud.cloudnet3.wrapper.database.CloudNet3WrapperDatabaseProvider;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.global.database.DatabaseProvider;

public class CloudNet3Wrapper extends CloudNet3 {

    private final Wrapper wrapper = Wrapper.getInstance();

    private final DatabaseProvider databaseProvider = new CloudNet3WrapperDatabaseProvider(this.wrapper);
    private final CloudNet3ChannelMessenger channelMessenger = new CloudNet3WrapperChannelMessenger(this.wrapper);
    private final ClusterPacketProvider clusterPacketProvider = new WrapperClusterPacketProvider();

    public CloudNet3Wrapper(SupportedCloudSystem supportedCloudSystem) {
        super(
                supportedCloudSystem,
                "CloudNet3-Wrapper",
                Wrapper.class.getPackage().getImplementationTitle() + "-" + Wrapper.class.getPackage().getImplementationVersion(),
                BridgePlayerManager.getInstance()
        );
    }

    @Override
    public String getOwnComponentName() {
        return this.wrapper.getServiceId().getName();
    }

    @Override
    public DatabaseProvider getDatabaseProvider() {
        return this.databaseProvider;
    }

    @Override
    public IPacketListenerRegistry getPacketRegistry() {
        return this.wrapper.getNetworkClient().getPacketRegistry();
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
