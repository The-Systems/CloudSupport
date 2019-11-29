package eu.thesystems.cloud.cloudnet2.bridge;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.thesystems.cloud.converter.CloudObjectConverter;
import eu.thesystems.cloud.event.EventManager;
import eu.thesystems.cloud.global.events.channel.ChannelMessageReceiveEvent;
import eu.thesystems.cloud.global.events.player.*;
import eu.thesystems.cloud.global.events.process.CloudProcessStartEvent;
import eu.thesystems.cloud.global.events.process.CloudProcessStopEvent;
import eu.thesystems.cloud.global.events.process.CloudProcessUpdateEvent;
import eu.thesystems.cloud.global.events.process.proxy.CloudProxyStartEvent;
import eu.thesystems.cloud.global.events.process.proxy.CloudProxyStopEvent;
import eu.thesystems.cloud.global.events.process.proxy.CloudProxyUpdateEvent;
import eu.thesystems.cloud.global.events.process.server.CloudServerStartEvent;
import eu.thesystems.cloud.global.events.process.server.CloudServerStopEvent;
import eu.thesystems.cloud.global.events.process.server.CloudServerUpdateEvent;

import java.util.UUID;

public class CloudNet2BridgeEventCaller implements NetworkHandler {

    private EventManager eventManager;
    private CloudObjectConverter converter;

    public CloudNet2BridgeEventCaller(EventManager eventManager, CloudObjectConverter converter) {
        this.eventManager = eventManager;
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
        this.eventManager.callEvent(new CloudProcessStartEvent(this.converter.convertServerInfo(proxyInfo)));
    }

    @Override
    public void onProxyInfoUpdate(ProxyInfo proxyInfo) {
        this.eventManager.callEvent(new CloudProxyUpdateEvent(this.converter.convertProxyInfo(proxyInfo)));
        this.eventManager.callEvent(new CloudProcessUpdateEvent(this.converter.convertServerInfo(proxyInfo)));
    }

    @Override
    public void onProxyRemove(ProxyInfo proxyInfo) {
        this.eventManager.callEvent(new CloudProxyStopEvent(this.converter.convertProxyInfo(proxyInfo)));
        this.eventManager.callEvent(new CloudProcessStopEvent(this.converter.convertServerInfo(proxyInfo)));
    }

    @Override
    public void onCloudNetworkUpdate(CloudNetwork cloudNetwork) {
    }

    @Override
    public void onCustomChannelMessageReceive(String channel, String message, Document document) {
    }

    @Override
    public void onCustomSubChannelMessageReceive(String channel, String message, Document document) {
        this.eventManager.callEvent(new ChannelMessageReceiveEvent(channel, message, document.obj()));
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
