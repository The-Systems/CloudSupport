package eu.thesystems.cloud.cloudnet3.wrapper;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.thesystems.cloud.cloudnet3.CloudNet3ChannelMessenger;

import java.util.concurrent.CompletableFuture;

public class CloudNet3WrapperChannelMessenger extends CloudNet3ChannelMessenger { // todo
    public CloudNet3WrapperChannelMessenger(CloudNetDriver cloudNetDriver) {
        super(cloudNetDriver);
    }

    @Override
    public void sendChannelMessageToCloud(String channel, String message, JsonObject data) {

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
