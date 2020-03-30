package eu.thesystems.cloud.cloudnet3;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import eu.thesystems.cloud.ChannelMessenger;

import java.util.concurrent.CompletableFuture;

public class CloudNet3ChannelMessenger implements ChannelMessenger {

    private CloudNetDriver cloudNetDriver;

    public CloudNet3ChannelMessenger(CloudNetDriver cloudNetDriver) {
        this.cloudNetDriver = cloudNetDriver;
    }

    @Override
    public void sendChannelMessage(String channel, String message, JsonObject data) {
        CloudNetDriver.getInstance().getMessenger().sendChannelMessage(channel, message, JsonDocument.newDocument(data.toString()));
    }

    @Override
    public void sendProxyChannelMessage(String channel, String message, JsonObject data) {
        CloudNetDriver.getInstance().getMessenger().sendChannelMessage(ServiceEnvironmentType.BUNGEECORD, channel, message, JsonDocument.newDocument(data.toString()));
    }

    @Override
    public void sendServerChannelMessage(String channel, String message, JsonObject data) {
        CloudNetDriver.getInstance().getMessenger().sendChannelMessage(ServiceEnvironmentType.MINECRAFT_SERVER, channel, message, JsonDocument.newDocument(data.toString()));
    }

    @Override
    public void sendChannelMessageToServer(String targetServer, String channel, String message, JsonObject data) {
        CloudNetDriver.getInstance().getCloudServiceProvider(targetServer).getServiceInfoSnapshotAsync()
                .onComplete(serviceInfoSnapshot -> {
                    if (serviceInfoSnapshot != null) {
                        CloudNetDriver.getInstance().getMessenger().sendChannelMessage(serviceInfoSnapshot, channel, message, JsonDocument.newDocument(data.toString()));
                    }
                });
    }

    @Override
    public void sendChannelMessageToGroup(String targetGroup, String channel, String message, JsonObject data) {
        CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTaskAsync(targetGroup)
                .onComplete(serviceTask -> {
                    if (serviceTask != null) {
                        CloudNetDriver.getInstance().getMessenger().sendChannelMessage(serviceTask, channel, message, JsonDocument.newDocument(data.toString()));
                    }
                });
    }

    @Override
    public CompletableFuture<JsonObject> sendQueryChannelMessage(String targetServer, String channel, String message, JsonObject data) {
        return null;
    }

    @Override
    public CompletableFuture<JsonObject> sendQueryChannelMessageToCloud(String channel, String message, JsonObject data) {
        return null;
    }
}
