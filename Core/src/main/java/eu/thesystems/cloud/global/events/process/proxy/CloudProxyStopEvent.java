package eu.thesystems.cloud.global.events.process.proxy;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.CloudSystem;
import eu.thesystems.cloud.global.events.process.CloudProcessStopEvent;
import eu.thesystems.cloud.global.info.ProxyInfo;

/**
 * This event is called when a proxy is stopped in the network.
 * <p>
 * This event is only called on clouds that distinguish between servers and proxies.
 *
 * @see CloudSystem#distinguishesProxiesAndServers()
 */
public class CloudProxyStopEvent extends CloudProcessStopEvent {
    public CloudProxyStopEvent(ProxyInfo proxyInfo) {
        super(proxyInfo);
    }

    public ProxyInfo getProxyInfo() {
        return (ProxyInfo) super.getProcessInfo();
    }
}
