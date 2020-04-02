package eu.thesystems.cloud.cloudnet3.cluster.wrapper;

import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketProvider;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketReceiver;
import eu.thesystems.cloud.cloudnet3.cluster.channel.RedirectingNetworkChannel;
import eu.thesystems.cloud.cloudnet3.cluster.packet.PacketClusterInRedirectPacket;
import eu.thesystems.cloud.cloudnet3.cluster.packet.PacketClusterOutRedirectPacket;
import eu.thesystems.cloud.cloudnet3.cluster.PacketSendResult;
import eu.thesystems.cloud.cloudnet3.wrapper.CloudNet3Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class WrapperClusterPacketProvider implements ClusterPacketProvider {

    private ClusterPacketReceiver localPacketSender;
    private Wrapper wrapper;

    public WrapperClusterPacketProvider(CloudNet3Wrapper wrapper) {
        this.wrapper = wrapper.getWrapper();
        this.localPacketSender = new ClusterPacketReceiver(DriverEnvironment.WRAPPER, this.wrapper.getServiceId().getName());
        this.wrapper.getNetworkClient().getPacketRegistry().addListener(PacketClusterOutRedirectPacket.CHANNEL, new PacketClusterInRedirectPacket(wrapper)); // todo unregister?
    }

    @NotNull
    @Override
    public ClusterPacketReceiver getLocalPacketSender() {
        return localPacketSender;
    }

    @NotNull
    @Override
    public PacketSendResult sendPacket(ClusterPacketReceiver packetSender, ClusterPacketReceiver receiver, IPacket packet) {
        if (receiver.getTargetType() == DriverEnvironment.WRAPPER && receiver.getTargetIdentifier().equals(this.wrapper.getServiceId().getName())) {
            this.wrapper.getNetworkClient().getPacketRegistry().handlePacket(new RedirectingNetworkChannel(packetSender, this.wrapper, this.wrapper.getNetworkClient().getChannels().iterator().next(), receiver), packet);
            return PacketSendResult.SUCCESS;
        }

        if (receiver.getTargetType() == DriverEnvironment.CLOUDNET && receiver.getTargetIdentifier().equals(this.wrapper.getServiceId().getNodeUniqueId())) {
            this.wrapper.getNetworkClient().sendPacket(packet);
            return PacketSendResult.SUCCESS;
        }

        this.wrapper.getNetworkClient().sendPacket(new PacketClusterOutRedirectPacket(packetSender, receiver, packet));

        return PacketSendResult.SUCCESS; // todo implement results as a query to the node?
    }

    @NotNull
    @Override
    public PacketSendResult[] sendMultiplePackets(ClusterPacketReceiver packetSender, ClusterPacketReceiver[] receivers, IPacket packet) {
        if (receivers.length == 0) {
            return new PacketSendResult[0];
        }
        if (receivers.length == 1) {
            return new PacketSendResult[]{this.sendPacket(packetSender, receivers[0], packet)};
        }

        PacketSendResult[] results = new PacketSendResult[receivers.length];
        Arrays.fill(results, PacketSendResult.SUCCESS); // todo implement results as a query to the node?

        this.wrapper.getNetworkClient().sendPacket(new PacketClusterOutRedirectPacket(packetSender, receivers, packet));

        return results;
    }

}
