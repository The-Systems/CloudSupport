package eu.thesystems.cloud.global.info;
/*
 * Created by derrop on 14.11.2019
 */

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Template {
    private String prefix;
    private String name;
    private String fullPath;
    /**
     * The different server groups are different in the clouds, so I can't implement everything in this object,
     * this json object contains EVERYTHING of the server info object that I get from the cloud,
     * but this is not the same on the clouds,
     * so, if you want to support every cloud that I support, you CAN'T USE THIS
     */
    private JsonObject availableTemplateData;
}
