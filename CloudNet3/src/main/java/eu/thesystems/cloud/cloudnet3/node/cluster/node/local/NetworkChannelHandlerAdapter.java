package eu.thesystems.cloud.cloudnet3.node.cluster.node.local;

import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.INetworkChannelHandler;
import de.dytanic.cloudnet.driver.network.protocol.Packet;

public class NetworkChannelHandlerAdapter implements INetworkChannelHandler {
    @Override
    public void handleChannelInitialize(INetworkChannel channel) throws Exception {

    }

    @Override
    public boolean handlePacketReceive(INetworkChannel channel, Packet packet) throws Exception {
        return false;
    }

    @Override
    public void handleChannelClose(INetworkChannel channel) throws Exception {

    }
}
