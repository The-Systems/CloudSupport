package eu.thesystems.cloud.cloudnet3.node.cluster;

import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.Packet;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;

public interface ClusterPacketProvider { // todo not tested

    default ClusterPacketReceiver getReceiver(ServiceInfoSnapshot serviceInfoSnapshot) {
        return new ClusterPacketReceiver(DriverEnvironment.WRAPPER, serviceInfoSnapshot.getName());
    }

    default ClusterPacketReceiver getReceiver(String nodeUniqueId) {
        return new ClusterPacketReceiver(DriverEnvironment.CLOUDNET, nodeUniqueId);
    }

    INetworkChannel getLocalNetworkChannel();

    PacketSendResult sendPacket(INetworkChannel packetSender, ClusterPacketReceiver receiver, Packet packet);

    PacketSendResult[] sendMultiplePackets(INetworkChannel packetSender, ClusterPacketReceiver[] receivers, Packet packet);

    default PacketSendResult sendPacket(ClusterPacketReceiver receiver, Packet packet) {
        return this.sendPacket(this.getLocalNetworkChannel(), receiver, packet);
    }

    default PacketSendResult[] sendMultiplePackets(ClusterPacketReceiver[] receivers, Packet packet) {
        return this.sendMultiplePackets(this.getLocalNetworkChannel(), receivers, packet);
    }

}
