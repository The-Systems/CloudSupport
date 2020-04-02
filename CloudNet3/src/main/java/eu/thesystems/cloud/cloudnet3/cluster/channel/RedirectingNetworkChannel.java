package eu.thesystems.cloud.cloudnet3.cluster.channel;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.network.HostAndPort;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.INetworkChannelHandler;
import de.dytanic.cloudnet.driver.network.cluster.NetworkClusterNodeInfoSnapshot;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListenerRegistry;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketReceiver;
import eu.thesystems.cloud.cloudnet3.cluster.packet.PacketClusterOutRedirectPacket;
import org.jetbrains.annotations.NotNull;

public class RedirectingNetworkChannel implements INetworkChannel {

    private CloudNetDriver cloudNetDriver;
    private INetworkChannel targetChannel;

    private ClusterPacketReceiver receiver;
    private NetworkClusterNodeInfoSnapshot nodeInfoSnapshot;
    private ServiceInfoSnapshot serviceInfoSnapshot;

    private HostAndPort clientAddress;

    public RedirectingNetworkChannel(@NotNull CloudNetDriver cloudNetDriver, @NotNull INetworkChannel targetChannel, @NotNull NetworkClusterNodeInfoSnapshot nodeInfoSnapshot) {
        this.cloudNetDriver = cloudNetDriver;
        this.targetChannel = targetChannel;
        this.nodeInfoSnapshot = nodeInfoSnapshot;

        this.receiver = new ClusterPacketReceiver(DriverEnvironment.CLOUDNET, nodeInfoSnapshot.getNode().getUniqueId());

        this.clientAddress = nodeInfoSnapshot.getNode().getListeners()[0];
    }

    public RedirectingNetworkChannel(@NotNull CloudNetDriver cloudNetDriver, @NotNull INetworkChannel targetChannel, @NotNull ServiceInfoSnapshot serviceInfoSnapshot) {
        this.cloudNetDriver = cloudNetDriver;
        this.targetChannel = targetChannel;
        this.serviceInfoSnapshot = serviceInfoSnapshot;

        this.receiver = new ClusterPacketReceiver(DriverEnvironment.WRAPPER, serviceInfoSnapshot.getName());

        this.clientAddress = new HostAndPort(serviceInfoSnapshot.getAddress().getHost(), targetChannel.getClientAddress().getPort());
    }

    @Override
    public long getChannelId() {
        return this.targetChannel.getChannelId();
    }

    @Override
    public HostAndPort getServerAddress() {
        return this.targetChannel.getServerAddress();
    }

    @Override
    public HostAndPort getClientAddress() {
        return this.clientAddress;
    }

    @Override
    public INetworkChannelHandler getHandler() {
        return this.targetChannel.getHandler();
    }

    @Override
    public void setHandler(INetworkChannelHandler handler) {
        this.targetChannel.setHandler(handler);
    }

    @Override
    public IPacketListenerRegistry getPacketRegistry() {
        return this.targetChannel.getPacketRegistry();
    }

    @Override
    public boolean isClientProvidedChannel() {
        return this.targetChannel.isClientProvidedChannel();
    }

    @Override
    public void sendPacket(@NotNull IPacket packet) {
        this.targetChannel.sendPacket(new PacketClusterOutRedirectPacket(this.receiver, packet));
    }

    @Override
    public void sendPacket(@NotNull IPacket... packets) {
        for (IPacket packet : packets) {
            this.sendPacket(packet);
        }
    }

    @Override
    public void close() throws Exception {
        if (this.receiver.getTargetType() == DriverEnvironment.WRAPPER) {
            this.cloudNetDriver.getCloudServiceProvider(this.receiver.getTargetIdentifier()).delete();
        }
    }
}
