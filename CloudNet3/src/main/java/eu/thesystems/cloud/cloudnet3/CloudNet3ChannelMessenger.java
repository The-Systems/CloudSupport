package eu.thesystems.cloud.cloudnet3;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import eu.thesystems.cloud.ChannelMessenger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class CloudNet3ChannelMessenger implements ChannelMessenger {

    private CloudNetDriver cloudNetDriver;

    private Map<UUID, CompletableFuture<JsonObject>> pendingQueries = new ConcurrentHashMap<>();

    public CloudNet3ChannelMessenger(CloudNetDriver cloudNetDriver) {
        this.cloudNetDriver = cloudNetDriver;
    }

    public boolean completeQuery(UUID queryId, JsonObject result) {
        CompletableFuture<JsonObject> future = this.pendingQueries.remove(queryId);
        if (future != null) {
            future.complete(result);
        }
        return future != null;
    }

    protected CompletableFuture<JsonObject> beginQuery(UUID queryId) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        this.pendingQueries.put(queryId, future);
        return future;
    }

    @Override
    public void sendChannelMessage(String channel, String message, JsonObject data) {
        this.cloudNetDriver.getMessenger().sendChannelMessage(channel, message, JsonDocument.newDocument(data.toString()));
    }

    @Override
    public void sendProxyChannelMessage(String channel, String message, JsonObject data) {
        this.cloudNetDriver.getMessenger().sendChannelMessage(ServiceEnvironmentType.BUNGEECORD, channel, message, JsonDocument.newDocument(data.toString()));
    }

    @Override
    public void sendServerChannelMessage(String channel, String message, JsonObject data) {
        this.cloudNetDriver.getMessenger().sendChannelMessage(ServiceEnvironmentType.MINECRAFT_SERVER, channel, message, JsonDocument.newDocument(data.toString()));
    }

    @Override
    public void sendChannelMessageToServer(String targetServer, String channel, String message, JsonObject data) {
        this.cloudNetDriver.getCloudServiceProvider(targetServer).getServiceInfoSnapshotAsync()
                .onComplete(serviceInfoSnapshot -> {
                    if (serviceInfoSnapshot != null) {
                        this.cloudNetDriver.getMessenger().sendChannelMessage(serviceInfoSnapshot, channel, message, JsonDocument.newDocument(data.toString()));
                    }
                });
    }

    @Override
    public void sendChannelMessageToGroup(String targetGroup, String channel, String message, JsonObject data) {
        this.cloudNetDriver.getServiceTaskProvider().getServiceTaskAsync(targetGroup)
                .onComplete(serviceTask -> {
                    if (serviceTask != null) {
                        this.cloudNetDriver.getMessenger().sendChannelMessage(serviceTask, channel, message, JsonDocument.newDocument(data.toString()));
                    }
                });
    }

}
