package eu.thesystems.cloud.cloudnet3.cluster.channel;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.network.HostAndPort;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.INetworkChannelHandler;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListenerRegistry;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketReceiver;
import eu.thesystems.cloud.cloudnet3.cluster.packet.PacketClusterOutRedirectPacket;
import org.jetbrains.annotations.NotNull;

public class RedirectingNetworkChannel implements INetworkChannel {

    private static final HostAndPort LOCAL_ADDRESS = new HostAndPort("127.0.0.1", 0);

    private CloudNetDriver cloudNetDriver;
    private INetworkChannel targetChannel;

    private ClusterPacketReceiver sender;
    private ClusterPacketReceiver receiver;

    public RedirectingNetworkChannel(@NotNull ClusterPacketReceiver sender, @NotNull CloudNetDriver cloudNetDriver, @NotNull INetworkChannel targetChannel, @NotNull ClusterPacketReceiver receiver) {
        this.sender = sender;
        this.cloudNetDriver = cloudNetDriver;
        this.targetChannel = targetChannel;
        this.receiver = receiver;
    }

    public ClusterPacketReceiver getTarget() {
        return this.receiver;
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
        return LOCAL_ADDRESS;
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
        this.targetChannel.sendPacket(new PacketClusterOutRedirectPacket(this.receiver, this.sender, packet));
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
