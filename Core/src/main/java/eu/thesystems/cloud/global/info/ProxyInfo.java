package eu.thesystems.cloud.global.info;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.JsonObject;
import eu.thesystems.cloud.global.network.NetworkAddress;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ProxyInfo extends ProcessInfo {
    public ProxyInfo(String group, String name, String uniqueId, String launcher, NetworkAddress host, Collection<Template> templates, Collection<String> onlinePlayers, JsonObject availableProxyData) {
        super(group, name, uniqueId, launcher, host, templates, onlinePlayers, availableProxyData);
    }
}
