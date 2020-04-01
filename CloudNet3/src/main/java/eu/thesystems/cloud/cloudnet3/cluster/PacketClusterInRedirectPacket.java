package eu.thesystems.cloud.cloudnet3.cluster;

import com.google.common.base.Preconditions;
import de.dytanic.cloudnet.cluster.IClusterNodeServer;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListener;
import de.dytanic.cloudnet.driver.network.protocol.Packet;
import eu.thesystems.cloud.cloudnet3.cluster.channel.RedirectingNetworkChannel;
import eu.thesystems.cloud.cloudnet3.node.CloudNet3Node;

public class PacketClusterInRedirectPacket implements IPacketListener {

    private CloudNet3Node node;

    public PacketClusterInRedirectPacket(CloudNet3Node node) {
        this.node = node;
    }

    @Override
    public void handle(INetworkChannel channel, IPacket packet) throws Exception {
        IPacket redirectedPacket = new Packet(packet.getHeader().getInt("channel"), packet.getHeader().getDocument("header"), packet.getBody());

        ClusterPacketReceiver[] receivers = packet.getHeader().get("receivers", ClusterPacketReceiver[].class);

        if (channel.isClientProvidedChannel()) { // the channel is from a client which means that the packet was sent from a node
            IClusterNodeServer server = this.node.getCloudNet().getClusterNodeServerProvider().getNodeServer(channel);
            Preconditions.checkNotNull(server, "no node server found (Packet sent by the node server on the other node?)");

            this.node.getClusterPacketProvider().sendMultiplePackets(
                    new RedirectingNetworkChannel(this.node.getCloudNetDriver(), channel, server.getNodeInfoSnapshot()),
                    receivers, packet
            );
        } else { // the channel is from a server which means that the packet was sent from a wrapper (or from a nodes server, but we don't send this packet from there), we just redirect all of them
            this.node.getClusterPacketProvider().sendMultiplePackets(channel, receivers, redirectedPacket);
        }
    }
}
