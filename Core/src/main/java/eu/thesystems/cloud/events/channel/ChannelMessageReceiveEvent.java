package eu.thesystems.cloud.events.channel;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import eu.thesystems.cloud.event.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelMessageReceiveEvent extends CloudEvent {
    private String channel;
    private String message;
    private JsonObject data;
    private boolean query;
    private JsonObject queryResult;

    public void setQueryResult(JsonObject queryResult) {
        Preconditions.checkArgument(this.query, "cannot set query result of non-query channel message");
        this.queryResult = queryResult;
    }
}
