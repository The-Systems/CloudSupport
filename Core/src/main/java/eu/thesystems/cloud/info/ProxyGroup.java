package eu.thesystems.cloud.info;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ProxyGroup extends ProcessGroup {
    private int startPort;

    public ProxyGroup(String name, Collection<String> availableLaunchers, int maxMemory, int minServers, String groupType, Collection<Template> templates, JsonObject availableGroupData, int startPort) {
        super(name, availableLaunchers, maxMemory, minServers, groupType, templates, availableGroupData);
        this.startPort = startPort;
    }
}
