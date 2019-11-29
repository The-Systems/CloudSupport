package eu.thesystems.cloud.global.events.player;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.event.CloudEvent;
import eu.thesystems.cloud.global.player.OnlinePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CloudPlayerUpdateEvent extends CloudEvent {
    private OnlinePlayer player;
}
