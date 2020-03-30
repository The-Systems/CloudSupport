package eu.thesystems.cloud.cloudnet2.master;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.result.Result;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.INetworkComponent;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutCustomSubChannelMessage;
import eu.thesystems.cloud.ChannelMessenger;
import eu.thesystems.cloud.cloudnet2.network.PacketOutMasterQueryChannelMessage;
import eu.thesystems.cloud.exception.CloudSupportException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudNet2MasterChannelMessenger implements ChannelMessenger {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private CloudNet cloudNet;
    private CloudNet2Master master;

    public CloudNet2MasterChannelMessenger(CloudNet cloudNet, CloudNet2Master master) {
        this.cloudNet = cloudNet;
        this.master = master;
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
        INetworkComponent component = this.getServerOrProxy(targetServer);

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

    @Override
    public CompletableFuture<JsonObject> sendQueryChannelMessage(String targetServer, String channel, String message, JsonObject data) {
        INetworkComponent component = this.getServerOrProxy(targetServer);
        if (component == null) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        this.executorService.execute(() -> {
            Result result = this.cloudNet.getPacketManager().sendQuery(new PacketOutMasterQueryChannelMessage(channel, message, data), component);
            future.complete(result.getResult().contains("data") ? result.getResult().get("data").getAsJsonObject() : null);
        });

        return future;
    }

    @Override
    public CompletableFuture<JsonObject> sendQueryChannelMessageToCloud(String channel, String message, JsonObject data) {
        throw new CloudSupportException(this.master);
    }

    private Packet createPacket(String channel, String message, JsonObject data) {
        return new PacketOutCustomSubChannelMessage(channel, message, Document.load(data.toString()));
    }

    private INetworkComponent getServerOrProxy(String name) {
        INetworkComponent component = this.cloudNet.getServer(name);
        if (component == null) {
            component = this.cloudNet.getProxy(name);
        }
        return component;
    }

}
