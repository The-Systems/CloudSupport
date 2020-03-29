package eu.thesystems.cloud;

import com.google.gson.JsonObject;

public interface ChannelMessenger { // todo not tested

    void sendChannelMessage(String channel, String message, JsonObject data);

    void sendProxyChannelMessage(String channel, String message, JsonObject data);

    void sendServerChannelMessage(String channel, String message, JsonObject data);

    void sendChannelMessageToServer(String targetServer, String channel, String message, JsonObject data);

    void sendChannelMessageToGroup(String targetGroup, String channel, String message, JsonObject data);

}
