package eu.thesystems.cloud.cloudnet2.master;
/*
 * Created by derrop on 26.10.2019
 */

import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;
import de.dytanic.cloudnetcore.api.event.server.*;
import eu.thesystems.cloud.cloudnet2.CloudNet2;
import eu.thesystems.cloud.converter.CloudObjectConverter;
import eu.thesystems.cloud.event.EventManager;
import eu.thesystems.cloud.global.events.process.proxy.CloudProxyStartEvent;
import eu.thesystems.cloud.global.events.process.proxy.CloudProxyStopEvent;
import eu.thesystems.cloud.global.events.process.proxy.CloudProxyUpdateEvent;
import eu.thesystems.cloud.global.events.process.server.CloudServerStartEvent;
import eu.thesystems.cloud.global.events.process.server.CloudServerStopEvent;
import eu.thesystems.cloud.global.events.process.server.CloudServerUpdateEvent;

import java.util.Arrays;

public class CloudNet2MasterEventCaller {

    private CloudNet2 cloudNet2;
    private EventManager eventManager;

    public void register(CoreModule selfModule, CloudNet2 cloudNet2, EventManager eventManager, CloudNet cloudNet) {
        this.cloudNet2 = cloudNet2;
        this.eventManager = eventManager;
        Arrays.asList(
                new ServerAdd(), new ServerUpdate(), new ServerRemove(),
                new ProxyAdd(), new ProxyUpdate(), new ProxyRemove()
        ).forEach(eventListener -> cloudNet.getEventManager().registerListener(selfModule, eventListener));
    }

    private CloudObjectConverter converter() {
        return this.cloudNet2.getConverter();
    }

    private final class ServerAdd implements IEventListener<ServerAddEvent> {
        @Override
        public void onCall(ServerAddEvent event) {
            eventManager.callEvent(new CloudServerStartEvent(converter().convertServerInfo(event.getMinecraftServer().getServerInfo())));
        }
    }

    private final class ServerRemove implements IEventListener<ServerRemoveEvent> {
        @Override
        public void onCall(ServerRemoveEvent event) {
            eventManager.callEvent(new CloudServerStopEvent(converter().convertServerInfo(event.getMinecraftServer().getServerInfo())));
        }
    }

    private final class ServerUpdate implements IEventListener<ServerInfoUpdateEvent> {
        @Override
        public void onCall(ServerInfoUpdateEvent event) {
            eventManager.callEvent(new CloudServerUpdateEvent(converter().convertServerInfo(event.getServerInfo())));
        }
    }

    private final class ProxyAdd implements IEventListener<ProxyAddEvent> {
        @Override
        public void onCall(ProxyAddEvent event) {
            eventManager.callEvent(new CloudProxyStartEvent(converter().convertProxyInfo(event.getProxyServer().getProxyInfo())));
        }
    }

    private final class ProxyUpdate implements IEventListener<ProxyInfoUpdateEvent> {
        @Override
        public void onCall(ProxyInfoUpdateEvent event) {
            eventManager.callEvent(new CloudProxyUpdateEvent(converter().convertProxyInfo(event.getProxyInfo())));
        }
    }
    private final class ProxyRemove implements IEventListener<ProxyRemoveEvent> {
        @Override
        public void onCall(ProxyRemoveEvent event) {
            eventManager.callEvent(new CloudProxyStopEvent(converter().convertProxyInfo(event.getProxyServer().getProxyInfo())));
        }
    }

}
