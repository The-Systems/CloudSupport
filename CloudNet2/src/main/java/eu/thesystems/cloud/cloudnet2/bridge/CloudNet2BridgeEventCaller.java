package eu.thesystems.cloud.cloudnet2.bridge;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.thesystems.cloud.cloudnet2.CloudNet2;
import eu.thesystems.cloud.cloudnet2.CloudNet2ChannelMessageType;
import eu.thesystems.cloud.converter.CloudObjectConverter;
import eu.thesystems.cloud.event.EventManager;
import eu.thesystems.cloud.events.channel.ChannelMessageReceiveEvent;
import eu.thesystems.cloud.events.player.*;
import eu.thesystems.cloud.events.process.CloudProcessStartEvent;
import eu.thesystems.cloud.events.process.CloudProcessStopEvent;
import eu.thesystems.cloud.events.process.CloudProcessUpdateEvent;
import eu.thesystems.cloud.events.process.proxy.CloudProxyStartEvent;
import eu.thesystems.cloud.events.process.proxy.CloudProxyStopEvent;
import eu.thesystems.cloud.events.process.proxy.CloudProxyUpdateEvent;
import eu.thesystems.cloud.events.process.server.CloudServerStartEvent;
import eu.thesystems.cloud.events.process.server.CloudServerStopEvent;
import eu.thesystems.cloud.events.process.server.CloudServerUpdateEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CloudNet2BridgeEventCaller implements NetworkHandler {

    private EventManager eventManager;
    private CloudNet2Bridge bridge;
    private CloudObjectConverter converter;

    public CloudNet2BridgeEventCaller(EventManager eventManager, CloudNet2Bridge bridge, CloudObjectConverter converter) {
        this.eventManager = eventManager;
        this.bridge = bridge;
        this.converter = converter;
    }

    @Override
    public void onServerAdd(ServerInfo serverInfo) {
        this.eventManager.callEvent(new CloudServerStartEvent(this.converter.convertServerInfo(serverInfo)));
        this.eventManager.callEvent(new CloudProcessStartEvent(this.converter.convertServerInfo(serverInfo)));
    }

    @Override
    public void onServerInfoUpdate(ServerInfo serverInfo) {
        this.eventManager.callEvent(new CloudServerUpdateEvent(this.converter.convertServerInfo(serverInfo)));
        this.eventManager.callEvent(new CloudProcessUpdateEvent(this.converter.convertServerInfo(serverInfo)));
    }

    @Override
    public void onServerRemove(ServerInfo serverInfo) {
        this.eventManager.callEvent(new CloudServerStopEvent(this.converter.convertServerInfo(serverInfo)));
        this.eventManager.callEvent(new CloudProcessStopEvent(this.converter.convertServerInfo(serverInfo)));
    }

    @Override
    public void onProxyAdd(ProxyInfo proxyInfo) {
        this.eventManager.callEvent(new CloudProxyStartEvent(this.converter.convertProxyInfo(proxyInfo)));
        this.eventManager.callEvent(new CloudProcessStartEvent(this.converter.convertProxyInfo(proxyInfo)));
    }

    @Override
    public void onProxyInfoUpdate(ProxyInfo proxyInfo) {
        this.eventManager.callEvent(new CloudProxyUpdateEvent(this.converter.convertProxyInfo(proxyInfo)));
        this.eventManager.callEvent(new CloudProcessUpdateEvent(this.converter.convertProxyInfo(proxyInfo)));
    }

    @Override
    public void onProxyRemove(ProxyInfo proxyInfo) {
        this.eventManager.callEvent(new CloudProxyStopEvent(this.converter.convertProxyInfo(proxyInfo)));
        this.eventManager.callEvent(new CloudProcessStopEvent(this.converter.convertProxyInfo(proxyInfo)));
    }

    @Override
    public void onCloudNetworkUpdate(CloudNetwork cloudNetwork) {
    }

    @Override
    public void onCustomChannelMessageReceive(String channel, String message, Document document) {
    }

    @Override
    public void onCustomSubChannelMessageReceive(String channel, String message, Document document) {
        if (channel.equals(CloudNet2.CLOUD_SUPPORT_CHANNEL)) {
            CloudNet2ChannelMessageType type = document.getObject("type", CloudNet2ChannelMessageType.class);
            if (type.equals(CloudNet2ChannelMessageType.QUERY_REQUEST)) {
                String cloudChannel = document.getString("cChannel");
                String target = document.getString("target");
                DefaultType targetType = DefaultType.valueOf(document.getString("targetType"));
                JsonObject data = document.contains("data") ? document.get("data").getAsJsonObject() : null;
                UUID queryId = document.getObject("queryId", UUID.class);

                JsonObject result = this.eventManager.callEvent(new ChannelMessageReceiveEvent(cloudChannel, message, data, true, null)).getQueryResult();
                Document resultDoc = new Document()
                        .append("queryId", queryId)
                        .append("type", CloudNet2ChannelMessageType.QUERY_RESPONSE)
                        .append("data", result);

                switch (targetType) {
                    case BUKKIT:
                        this.bridge.getCloudAPI().sendCustomSubServerMessage(CloudNet2.CLOUD_SUPPORT_CHANNEL, "", resultDoc, target);
                        return;

                    case BUNGEE_CORD:
                        this.bridge.getCloudAPI().sendCustomSubProxyMessage(CloudNet2.CLOUD_SUPPORT_CHANNEL, "", resultDoc, target);
                        return;
                }
            } else if (type.equals(CloudNet2ChannelMessageType.QUERY_RESPONSE)) {
                UUID queryId = document.getObject("queryId", UUID.class);

                CompletableFuture<JsonObject> future = this.bridge.getChannelMessenger().getPendingQueries().remove(queryId);
                if (future != null) {
                    future.complete(document.contains("data") ? document.get("data").getAsJsonObject() : null);
                }
            }
        } else {
            this.eventManager.callEvent(new ChannelMessageReceiveEvent(channel, message, document.obj(), false, null));
        }
    }

    @Override
    public void onPlayerLoginNetwork(CloudPlayer cloudPlayer) {
        this.eventManager.callEvent(new CloudPlayerLoginEvent(this.converter.convertOnlinePlayer(cloudPlayer)));
    }

    @Override
    public void onPlayerDisconnectNetwork(CloudPlayer cloudPlayer) {
        this.eventManager.callEvent(new CloudPlayerLogoutEvent(this.converter.convertOnlinePlayer(cloudPlayer)));
    }

    @Override
    public void onPlayerDisconnectNetwork(UUID uniqueId) {
        this.eventManager.callEvent(new CloudPlayerLogoutEvent(uniqueId));
    }

    @Override
    public void onPlayerUpdate(CloudPlayer cloudPlayer) {
        this.eventManager.callEvent(new CloudPlayerUpdateEvent(this.converter.convertOnlinePlayer(cloudPlayer)));
    }

    @Override
    public void onOfflinePlayerUpdate(OfflinePlayer offlinePlayer) {
        this.eventManager.callEvent(new CloudOfflinePlayerUpdateEvent(this.converter.convertOfflinePlayer(offlinePlayer)));
    }

    @Override
    public void onUpdateOnlineCount(int newOnlineCount) {
        this.eventManager.callEvent(new CloudPlayerUpdateOnlineCountEvent(newOnlineCount));
    }
}
