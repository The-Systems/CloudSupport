package eu.thesystems.cloud.cloudnet2.master;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.INetworkComponent;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutCustomSubChannelMessage;
import eu.thesystems.cloud.ChannelMessenger;

public class CloudNet2MasterChannelMessenger implements ChannelMessenger {

    private CloudNet cloudNet;

    public CloudNet2MasterChannelMessenger(CloudNet cloudNet) {
        this.cloudNet = cloudNet;
    }

    @Override
    public void sendChannelMessage(String channel, String message, JsonObject data) {
        this.cloudNet.getNetworkManager().sendAll(this.createPacket(channel, message, data));
    }

    @Override
    public void sendProxyChannelMessage(String channel, String message, JsonObject data) {
        this.cloudNet.getNetworkManager().sendToProxy(this.createPacket(channel, message, data));
    }

    @Override
    public void sendServerChannelMessage(String channel, String message, JsonObject data) {
        this.cloudNet.getNetworkManager().sendAll(this.createPacket(channel, message, data), component -> component instanceof MinecraftServer);
    }

    @Override
    public void sendChannelMessageToServer(String targetServer, String channel, String message, JsonObject data) {
        INetworkComponent component = this.cloudNet.getServer(targetServer);
        if (component == null) {
            component = this.cloudNet.getProxy(targetServer);
        }

        if (component != null) {
            component.sendPacket(this.createPacket(channel, message, data));
        }
    }

    @Override
    public void sendChannelMessageToGroup(String targetGroup, String channel, String message, JsonObject data) {
        Packet packet = this.createPacket(channel, message, data);

        for (ProxyServer proxy : this.cloudNet.getProxys(targetGroup)) {
            proxy.sendPacket(packet);
        }
        for (MinecraftServer server : this.cloudNet.getServers(targetGroup)) {
            server.sendPacket(packet);
        }
    }

    private Packet createPacket(String channel, String message, JsonObject data) {
        return new PacketOutCustomSubChannelMessage(channel, message, Document.load(data.toString()));
    }

}
