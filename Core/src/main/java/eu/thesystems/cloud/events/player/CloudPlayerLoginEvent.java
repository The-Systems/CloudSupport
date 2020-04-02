package eu.thesystems.cloud.events.player;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.event.CloudEvent;
import eu.thesystems.cloud.player.OnlinePlayer;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class CloudPlayerLoginEvent extends CloudEvent {
    private OnlinePlayer player;
}
