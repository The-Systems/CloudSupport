package eu.thesystems.cloud.events.process.proxy;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.CloudSystem;
import eu.thesystems.cloud.events.process.CloudProcessUpdateEvent;
import eu.thesystems.cloud.info.ProxyInfo;

/**
 * This event is called when a proxy info is updated in the network.
 * <p>
 * This event is only called on clouds that distinguish between servers and proxies.
 *
 * @see CloudSystem#distinguishesProxiesAndServers()
 */
public class CloudProxyUpdateEvent extends CloudProcessUpdateEvent {

    public CloudProxyUpdateEvent(ProxyInfo proxyInfo) {
        super(proxyInfo);
    }

    public ProxyInfo getProxyInfo() {
        return (ProxyInfo) super.getProcessInfo();
    }
}
