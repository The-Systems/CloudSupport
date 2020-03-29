package eu.thesystems.cloud.cloudnet2.bridge;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.thesystems.cloud.ChannelMessenger;

public class CloudNet2BridgeChannelMessenger implements ChannelMessenger {

    private CloudAPI cloudAPI;

    public CloudNet2BridgeChannelMessenger(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
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
}
