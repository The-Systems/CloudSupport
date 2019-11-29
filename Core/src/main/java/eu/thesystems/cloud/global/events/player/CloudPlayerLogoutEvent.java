package eu.thesystems.cloud.global.events.player;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.event.CloudEvent;
import eu.thesystems.cloud.global.player.OnlinePlayer;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This class contains the player OR the uniqueId, depends on what the cloud gives us
 */
@Getter
@Setter
public class CloudPlayerLogoutEvent extends CloudEvent {
    private OnlinePlayer player;
    private UUID uniqueId;

    public CloudPlayerLogoutEvent(OnlinePlayer player) {
        this.player = player;
    }

    public CloudPlayerLogoutEvent(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
