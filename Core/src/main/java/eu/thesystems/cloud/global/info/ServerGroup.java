package eu.thesystems.cloud.global.info;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ServerGroup extends ProcessGroup {
    public ServerGroup(String name, Collection<String> availableLaunchers, int maxMemory, int minServers, String groupType, Collection<Template> templates, JsonObject availableGroupData) {
        super(name, availableLaunchers, maxMemory, minServers, groupType, templates, availableGroupData);
    }
}
