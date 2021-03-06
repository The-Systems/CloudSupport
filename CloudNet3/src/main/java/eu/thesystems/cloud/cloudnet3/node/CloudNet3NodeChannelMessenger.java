package eu.thesystems.cloud.cloudnet3.node;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.event.cluster.NetworkChannelAuthClusterNodeSuccessEvent;
import de.dytanic.cloudnet.event.network.NetworkChannelAuthCloudServiceSuccessEvent;
import eu.thesystems.cloud.cloudnet3.CloudNet3ChannelMessenger;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketReceiver;
import eu.thesystems.cloud.cloudnet3.cluster.PacketSendResult;
import eu.thesystems.cloud.cloudnet3.network.PacketInNodeChannelMessage;
import eu.thesystems.cloud.cloudnet3.network.PacketOutNodeChannelMessage;
import eu.thesystems.cloud.exception.CloudSupportException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CloudNet3NodeChannelMessenger extends CloudNet3ChannelMessenger {
    private CloudNet3Node node;

    public CloudNet3NodeChannelMessenger(CloudNet3Node node) {
        super(node.getCloudNet());
        this.node = node;
    }

    @Override
    public void sendChannelMessageToCloud(String channel, String message, JsonObject data) {
        throw new CloudSupportException(this.node);
    }

    @EventListener
    public void handleServiceAuthSuccess(NetworkChannelAuthCloudServiceSuccessEvent event) {
        event.getChannel().getPacketRegistry().addListener(PacketOutNodeChannelMessage.CHANNEL, new PacketInNodeChannelMessage(this.node));
    }

    @EventListener
    public void handleNodeAuthSuccess(NetworkChannelAuthClusterNodeSuccessEvent event) {
        event.getChannel().getPacketRegistry().addListener(PacketOutNodeChannelMessage.CHANNEL, new PacketInNodeChannelMessage(this.node));
    }

    @Override
    public CompletableFuture<JsonObject> sendQueryChannelMessage(String targetServer, String channel, String message, JsonObject data) {
        UUID queryId = UUID.randomUUID();
        if (this.node.getClusterPacketProvider().sendPacket(
                new ClusterPacketReceiver(DriverEnvironment.WRAPPER, targetServer),
                new PacketOutNodeChannelMessage(channel, message, data, true, queryId)) != PacketSendResult.SUCCESS) {
            return CompletableFuture.completedFuture(null);
        }
        return super.beginQuery(queryId);
    }

    @Override
    public CompletableFuture<JsonObject> sendQueryChannelMessageToCloud(String channel, String message, JsonObject data) {
        throw new CloudSupportException(this.node);
    }
}
