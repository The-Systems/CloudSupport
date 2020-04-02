package eu.thesystems.cloud.cloudnet3.cluster;

import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import org.jetbrains.annotations.NotNull;

public interface ClusterPacketProvider { // todo not tested

    default ClusterPacketReceiver getReceiver(ServiceInfoSnapshot serviceInfoSnapshot) {
        return new ClusterPacketReceiver(DriverEnvironment.WRAPPER, serviceInfoSnapshot.getName());
    }

    default ClusterPacketReceiver getReceiver(String nodeUniqueId) {
        return new ClusterPacketReceiver(DriverEnvironment.CLOUDNET, nodeUniqueId);
    }

    @NotNull
    ClusterPacketReceiver getLocalPacketSender();

    @NotNull
    PacketSendResult sendPacket(ClusterPacketReceiver packetSender, ClusterPacketReceiver receiver, IPacket packet);

    @NotNull
    PacketSendResult[] sendMultiplePackets(ClusterPacketReceiver packetSender, ClusterPacketReceiver[] receivers, IPacket packet);

    @NotNull
    default PacketSendResult sendPacket(ClusterPacketReceiver receiver, IPacket packet) {
        return this.sendPacket(this.getLocalPacketSender(), receiver, packet);
    }

    @NotNull
    default PacketSendResult[] sendMultiplePackets(ClusterPacketReceiver[] receivers, IPacket packet) {
        return this.sendMultiplePackets(this.getLocalPacketSender(), receivers, packet);
    }

}
