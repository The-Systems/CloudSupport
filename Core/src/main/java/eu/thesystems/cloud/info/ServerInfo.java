package eu.thesystems.cloud.info;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.JsonObject;
import eu.thesystems.cloud.network.NetworkAddress;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ServerInfo extends ProcessInfo {

    private String motd;
    private String state;
    private int maxPlayers;

    public ServerInfo(String group, String name, String uniqueId, String launcher, NetworkAddress host, Collection<Template> templates, Collection<String> onlinePlayers, int maxPlayers, JsonObject availableProxyData, String motd, String state) {
        super(group, name, uniqueId, launcher, host, templates, onlinePlayers, availableProxyData);
        this.motd = motd;
        this.state = state;
        this.maxPlayers = maxPlayers;
    }
}
