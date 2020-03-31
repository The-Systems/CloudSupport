package eu.thesystems.cloud;

import com.google.gson.JsonObject;
import eu.thesystems.cloud.global.events.channel.ChannelMessageReceiveEvent;

import java.util.concurrent.CompletableFuture;

public interface ChannelMessenger { // todo not tested (except for the queries in CloudNet 2)

    void sendChannelMessage(String channel, String message, JsonObject data);

    void sendProxyChannelMessage(String channel, String message, JsonObject data);

    void sendServerChannelMessage(String channel, String message, JsonObject data);

    void sendChannelMessageToServer(String targetServer, String channel, String message, JsonObject data);

    void sendChannelMessageToGroup(String targetGroup, String channel, String message, JsonObject data);

    void sendChannelMessageToCloud(String channel, String message, JsonObject data);

    /**
     * Sends a channel message to a specific process (calling the {@link ChannelMessageReceiveEvent}) and waits
     * for the result by the server. On some CloudSystems, this might take a few seconds (e. g. on CloudNet 2, it times
     * out after 5 seconds, most likely because the CloudSupport plugin is not installed on the receiver).
     *
     * @param targetServer the name of the process which should receive this message
     * @param channel      the channel to identify your request
     * @param message      the message to identify your request
     * @param data         the data of your request
     * @return a future for the result of the receiver
     */
    CompletableFuture<JsonObject> sendQueryChannelMessage(String targetServer, String channel, String message, JsonObject data);

    /**
     * Sends a channel message to the cloud (calling the {@link ChannelMessageReceiveEvent}) and waits
     * for the result by the server. On some CloudSystems, this might take a few seconds (e. g. on CloudNet 2, it times
     * out after 5 seconds, most likely because the CloudSupport plugin is not installed on the receiver).
     *
     * @param channel the channel to identify your request
     * @param message the message to identify your request
     * @param data    the data of your request
     * @return a future for the result of the receiver
     */
    CompletableFuture<JsonObject> sendQueryChannelMessageToCloud(String channel, String message, JsonObject data);

}
