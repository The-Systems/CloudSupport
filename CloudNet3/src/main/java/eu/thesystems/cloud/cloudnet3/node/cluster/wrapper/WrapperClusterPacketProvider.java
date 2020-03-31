package eu.thesystems.cloud.cloudnet3.node.cluster.wrapper;

import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.Packet;
import eu.thesystems.cloud.cloudnet3.node.cluster.ClusterPacketProvider;
import eu.thesystems.cloud.cloudnet3.node.cluster.ClusterPacketReceiver;
import eu.thesystems.cloud.cloudnet3.node.cluster.PacketSendResult;

public class WrapperClusterPacketProvider implements ClusterPacketProvider { // todo
    @Override
    public INetworkChannel getLocalNetworkChannel() {
        return null;
    }

    @Override
    public PacketSendResult sendPacket(INetworkChannel packetSender, ClusterPacketReceiver receiver, Packet packet) {
        return null;
    }

    @Override
    public PacketSendResult[] sendMultiplePackets(INetworkChannel packetSender, ClusterPacketReceiver[] receivers, Packet packet) {
        return new PacketSendResult[0];
    }
}
