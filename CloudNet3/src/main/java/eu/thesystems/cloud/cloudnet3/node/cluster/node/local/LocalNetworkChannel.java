package eu.thesystems.cloud.cloudnet3.node.cluster.node.local;

import de.dytanic.cloudnet.driver.network.HostAndPort;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.INetworkChannelHandler;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListenerRegistry;
import org.jetbrains.annotations.NotNull;

public class LocalNetworkChannel implements INetworkChannel {

    private static final HostAndPort LOCAL_ADDRESS = new HostAndPort("127.0.0.1", 1410);

    private INetworkChannelHandler handler = new NetworkChannelHandlerAdapter();
    private IPacketListenerRegistry packetRegistry;

    public LocalNetworkChannel(IPacketListenerRegistry packetRegistry) {
        this.packetRegistry = packetRegistry;
    }

    @Override
    public long getChannelId() {
        return 0;
    }

    @Override
    public HostAndPort getServerAddress() {
        return LOCAL_ADDRESS;
    }

    @Override
    public HostAndPort getClientAddress() {
        return LOCAL_ADDRESS;
    }

    @Override
    public INetworkChannelHandler getHandler() {
        return this.handler;
    }

    @Override
    public void setHandler(INetworkChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public IPacketListenerRegistry getPacketRegistry() {
        return this.packetRegistry;
    }

    @Override
    public boolean isClientProvidedChannel() {
        return false;
    }

    @Override
    public void sendPacket(@NotNull IPacket packet) {
        this.getPacketRegistry().handlePacket(this, packet);
    }

    @Override
    public void sendPacket(@NotNull IPacket... packets) {
        for (IPacket packet : packets) {
            this.sendPacket(packet);
        }
    }

    @Override
    public void close() throws Exception {
    }
}
