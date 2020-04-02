package eu.thesystems.cloud.cloudnet3.cluster.packet;

import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListener;
import de.dytanic.cloudnet.driver.network.protocol.Packet;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketReceiver;

public class PacketClusterInRedirectPacket implements IPacketListener {

    private CloudNet3 cloudNet;

    public PacketClusterInRedirectPacket(CloudNet3 cloudNet) {
        this.cloudNet = cloudNet;
    }

    @Override
    public void handle(INetworkChannel channel, IPacket packet) throws Exception {
        IPacket redirectedPacket = new Packet(packet.getHeader().getInt("channel"), packet.getHeader().getDocument("header"), packet.getBody());

        ClusterPacketReceiver[] receivers = packet.getHeader().get("receivers", ClusterPacketReceiver[].class);
        ClusterPacketReceiver sender = packet.getHeader().get("sender", ClusterPacketReceiver.class);

        this.cloudNet.getClusterPacketProvider().sendMultiplePackets(sender, receivers, redirectedPacket);
    }
}
