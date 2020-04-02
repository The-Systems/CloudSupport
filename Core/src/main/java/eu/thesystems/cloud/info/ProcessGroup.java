package eu.thesystems.cloud.info;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.JsonObject;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProcessGroup {

    private String name;
    private Collection<String> availableLaunchers;
    private int maxMemory;
    private int minServers;
    private String groupType;
    private Collection<Template> templates;
    /**
     * The different server groups are different in the clouds, so I can't implement everything in this object,
     * this json object contains EVERYTHING of the server info object that I get from the cloud,
     * but this is not the same on the clouds,
     * so, if you want to support every cloud that I support, you CAN'T USE THIS
     */
    private JsonObject availableGroupData;

}
