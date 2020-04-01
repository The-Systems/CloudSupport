package eu.thesystems.cloud.cloudnet3.cluster.wrapper;

import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketProvider;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketReceiver;
import eu.thesystems.cloud.cloudnet3.cluster.PacketClusterOutRedirectPacket;
import eu.thesystems.cloud.cloudnet3.cluster.PacketSendResult;

import java.util.Arrays;

public class WrapperClusterPacketProvider implements ClusterPacketProvider {

    private Wrapper wrapper;

    public WrapperClusterPacketProvider(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public INetworkChannel getLocalNetworkChannel() {
        return this.wrapper.getNetworkClient().getChannels().iterator().next();
    }

    @Override
    public PacketSendResult sendPacket(INetworkChannel packetSender, ClusterPacketReceiver receiver, IPacket packet) {
        if (receiver.getTargetType() == DriverEnvironment.WRAPPER && receiver.getTargetIdentifier().equals(this.wrapper.getServiceId().getName())) {
            this.wrapper.getNetworkClient().getPacketRegistry().handlePacket(packetSender, packet);
            return PacketSendResult.SUCCESS;
        }

        if (receiver.getTargetType() == DriverEnvironment.CLOUDNET && receiver.getTargetIdentifier().equals(this.wrapper.getServiceId().getNodeUniqueId())) {
            this.getLocalNetworkChannel().sendPacket(packet);
            return PacketSendResult.SUCCESS;
        }

        this.wrapper.getNetworkClient().sendPacket(new PacketClusterOutRedirectPacket(receiver, packet));

        return PacketSendResult.SUCCESS; // todo implement results as a query to the node?
    }

    @Override
    public PacketSendResult[] sendMultiplePackets(INetworkChannel packetSender, ClusterPacketReceiver[] receivers, IPacket packet) {
        PacketSendResult[] results = new PacketSendResult[receivers.length];
        Arrays.fill(results, PacketSendResult.SUCCESS); // todo implement results as a query to the node?

        this.wrapper.getNetworkClient().sendPacket(new PacketClusterOutRedirectPacket(receivers, packet));

        return results;
    }

}
