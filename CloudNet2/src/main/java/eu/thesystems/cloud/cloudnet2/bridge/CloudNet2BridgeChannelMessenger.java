package eu.thesystems.cloud.cloudnet2.bridge;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.protocol.packet.result.Result;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.thesystems.cloud.ChannelMessenger;
import eu.thesystems.cloud.cloudnet2.CloudNet2;
import eu.thesystems.cloud.cloudnet2.CloudNet2ChannelMessageType;
import eu.thesystems.cloud.cloudnet2.network.PacketOutMasterChannelMessage;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudNet2BridgeChannelMessenger implements ChannelMessenger {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private CloudNet2Bridge bridge;
    private CloudAPI cloudAPI;

    private Map<UUID, CompletableFuture<JsonObject>> pendingQueries = new ConcurrentHashMap<>();

    public CloudNet2BridgeChannelMessenger(CloudNet2Bridge bridge, CloudAPI cloudAPI) {
        this.bridge = bridge;
        this.cloudAPI = cloudAPI;
    }

    public Map<UUID, CompletableFuture<JsonObject>> getPendingQueries() {
        return this.pendingQueries;
    }

    @Override
    public void sendChannelMessage(String channel, String message, JsonObject data) {
        this.cloudAPI.sendCustomSubServerMessage(channel, message, Document.load(data.toString()));
        this.cloudAPI.sendCustomSubProxyMessage(channel, message, Document.load(data.toString()));
    }

    @Override
    public void sendProxyChannelMessage(String channel, String message, JsonObject data) {
        this.cloudAPI.sendCustomSubProxyMessage(channel, message, Document.load(data.toString()));
    }

    @Override
    public void sendServerChannelMessage(String channel, String message, JsonObject data) {
        this.cloudAPI.sendCustomSubServerMessage(channel, message, Document.load(data.toString()));
    }

    @Override
    public void sendChannelMessageToServer(String targetServer, String channel, String message, JsonObject data) {
        this.cloudAPI.sendCustomSubServerMessage(channel, message, Document.load(data.toString()), targetServer);
        this.cloudAPI.sendCustomSubProxyMessage(channel, message, Document.load(data.toString()), targetServer);
    }

    @Override
    public void sendChannelMessageToGroup(String targetGroup, String channel, String message, JsonObject data) {
        for (ProxyInfo proxy : this.cloudAPI.getProxys(targetGroup)) {
            this.cloudAPI.sendCustomSubProxyMessage(channel, message, Document.load(data.toString()), proxy.getServiceId().getServerId());
        }
        for (ServerInfo server : this.cloudAPI.getServers(targetGroup)) {
            this.cloudAPI.sendCustomSubServerMessage(channel, message, Document.load(data.toString()), server.getServiceId().getServerId());
        }
    }

    @Override
    public void sendChannelMessageToCloud(String channel, String message, JsonObject data) {
        this.cloudAPI.getNetworkConnection().sendPacket(new PacketOutMasterChannelMessage(channel, message, data, false));
    }

    @Override
    public CompletableFuture<JsonObject> sendQueryChannelMessage(String targetServer, String channel, String message, JsonObject data) {
        UUID queryId = UUID.randomUUID();

        Document finalData = new Document()
                .append("queryId", queryId)
                .append("type", CloudNet2ChannelMessageType.QUERY_REQUEST)
                .append("cChannel", channel)
                .append("target", CloudAPI.getInstance().getServerId())
                .append("data", data)
                .append("targetType", this.bridge.getComponentType() == SupportedCloudSystem.CLOUDNET_2_BUKKIT ? DefaultType.BUKKIT : DefaultType.BUNGEE_CORD);

        this.cloudAPI.sendCustomSubServerMessage(CloudNet2.CLOUD_SUPPORT_CHANNEL, message, finalData, targetServer);
        this.cloudAPI.sendCustomSubProxyMessage(CloudNet2.CLOUD_SUPPORT_CHANNEL, message, finalData, targetServer);

        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        this.pendingQueries.put(queryId, future);
        return future;
    }

    @Override
    public CompletableFuture<JsonObject> sendQueryChannelMessageToCloud(String channel, String message, JsonObject data) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        this.executorService.execute(() -> {
            Result result = this.cloudAPI.getNetworkConnection().getPacketManager().sendQuery(
                    new PacketOutMasterChannelMessage(channel, message, data, true),
                    this.cloudAPI.getNetworkConnection()
            );
            future.complete(result.getResult().contains("data") ? result.getResult().get("data").getAsJsonObject() : null);
        });
        return future;
    }
}
