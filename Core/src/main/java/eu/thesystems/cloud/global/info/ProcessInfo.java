package eu.thesystems.cloud.global.info;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.JsonObject;
import eu.thesystems.cloud.global.network.NetworkAddress;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProcessInfo {
    private String group;
    private String name;
    private String uniqueId;
    private String launcher;
    private NetworkAddress host;
    private Collection<Template> templates;
    private Collection<String> onlinePlayers;
    /**
     * The different server infos are different in the clouds, so I can't implement everything in this object,
     * this json object contains EVERYTHING of the server info object that I get from the cloud,
     * but this is not the same on the clouds,
     * so, if you want to support every cloud that I support, you CAN'T USE THIS
     */
    private JsonObject availableProxyData;
}
