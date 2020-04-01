package eu.thesystems.cloud.cloudnet3.cluster;

import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;

public interface ClusterPacketProvider { // todo not tested

    default ClusterPacketReceiver getReceiver(ServiceInfoSnapshot serviceInfoSnapshot) {
        return new ClusterPacketReceiver(DriverEnvironment.WRAPPER, serviceInfoSnapshot.getName());
    }

    default ClusterPacketReceiver getReceiver(String nodeUniqueId) {
        return new ClusterPacketReceiver(DriverEnvironment.CLOUDNET, nodeUniqueId);
    }

    INetworkChannel getLocalNetworkChannel();

    PacketSendResult sendPacket(INetworkChannel packetSender, ClusterPacketReceiver receiver, IPacket packet);

    PacketSendResult[] sendMultiplePackets(INetworkChannel packetSender, ClusterPacketReceiver[] receivers, IPacket packet);

    default PacketSendResult sendPacket(ClusterPacketReceiver receiver, IPacket packet) {
        return this.sendPacket(this.getLocalNetworkChannel(), receiver, packet);
    }

    default PacketSendResult[] sendMultiplePackets(ClusterPacketReceiver[] receivers, IPacket packet) {
        return this.sendMultiplePackets(this.getLocalNetworkChannel(), receivers, packet);
    }

}
