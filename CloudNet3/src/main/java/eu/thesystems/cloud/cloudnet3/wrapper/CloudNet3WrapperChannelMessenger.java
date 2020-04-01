package eu.thesystems.cloud.cloudnet3.wrapper;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.network.NetworkChannelInitEvent;
import de.dytanic.cloudnet.event.cluster.NetworkChannelAuthClusterNodeSuccessEvent;
import de.dytanic.cloudnet.event.network.NetworkChannelAuthCloudServiceSuccessEvent;
import eu.thesystems.cloud.cloudnet3.CloudNet3ChannelMessenger;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketReceiver;
import eu.thesystems.cloud.cloudnet3.cluster.PacketSendResult;
import eu.thesystems.cloud.cloudnet3.network.PacketInNodeChannelMessage;
import eu.thesystems.cloud.cloudnet3.network.PacketOutNodeChannelMessage;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CloudNet3WrapperChannelMessenger extends CloudNet3ChannelMessenger { // todo

    private CloudNet3Wrapper wrapper;

    public CloudNet3WrapperChannelMessenger(CloudNet3Wrapper wrapper) {
        super(wrapper.getCloudNetDriver());
        this.wrapper = wrapper;
        this.wrapper.getCloudNetDriver().getNetworkClient().getPacketRegistry().addListener(PacketOutNodeChannelMessage.CHANNEL, new PacketInNodeChannelMessage(this.wrapper));
    }

    @Override
    public void sendChannelMessageToCloud(String channel, String message, JsonObject data) {
        this.wrapper.getCloudNetDriver().getNetworkClient().sendPacket(new PacketOutNodeChannelMessage(channel, message, data, false, null));
    }

    @Override
    public CompletableFuture<JsonObject> sendQueryChannelMessage(String targetServer, String channel, String message, JsonObject data) {
        UUID queryId = UUID.randomUUID();
        if (this.wrapper.getClusterPacketProvider().sendPacket(new ClusterPacketReceiver(DriverEnvironment.WRAPPER, targetServer),
                new PacketOutNodeChannelMessage(channel, message, data, true, queryId)) != PacketSendResult.SUCCESS) {
            return CompletableFuture.completedFuture(null);
        }
        return super.beginQuery(queryId);
    }

    @Override
    public CompletableFuture<JsonObject> sendQueryChannelMessageToCloud(String channel, String message, JsonObject data) {
        UUID queryId = UUID.randomUUID();
        this.wrapper.getCloudNetDriver().getNetworkClient().sendPacket(new PacketOutNodeChannelMessage(channel, message, data, true, queryId));
        return super.beginQuery(queryId);
    }
}
