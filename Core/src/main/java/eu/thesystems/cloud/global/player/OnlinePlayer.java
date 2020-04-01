package eu.thesystems.cloud.global.player;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.exception.CloudSupportException;
import eu.thesystems.cloud.global.network.NetworkAddress;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public abstract class OnlinePlayer extends OfflinePlayer {

    private NetworkAddress address;

    public OnlinePlayer(UUID uniqueId, String name, NetworkAddress lastAddress, long firstLogin, long lastLogin, NetworkAddress address) {
        super(uniqueId, name, lastAddress, firstLogin, lastLogin);
        this.address = address;
    }

    /**
     * Sends a message to this player
     * <p>
     * Works currently on every supported cloud (CloudNet 2/3 and ReformCloud 2), but this may change later with more supported cloud systems.
     *
     * @param message the message to be sent to the player
     * @throws CloudSupportException if the selected cloud system does not support this method
     */
    public abstract void sendMessage(String message);

    /**
     * Sends a title to this player
     * <p>
     * Does not work on every cloud.
     *
     * @param title    the title to be sent to the player
     * @param subTitle the sub title to be sent to the player
     * @param fadeIn   the time in ticks the title should fade in (0 for no fade)
     * @param stay     the time in ticks the title should stay statically in
     * @param fadeOut  the time in ticks the title should fade in (0 for no fade)
     * @throws CloudSupportException if the selected cloud system does not support this method
     */
    public abstract void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut);

    /**
     * Sends an action bar to this player
     * <p>
     * Does not work on every cloud.
     *
     * @param message the message to be sent to the player
     * @throws CloudSupportException if the selected cloud system does not support this method
     */
    public abstract void sendActionBar(String message);

    /**
     * Connects this player to the given server, it if exists and is running in the network
     * <p>
     * Works currently on every supported cloud (CloudNet 2/3 and ReformCloud 2), but this may change later with more supported cloud systems.
     *
     * @param server the name of the server to send this player to
     * @throws CloudSupportException if the selected cloud system does not support this method
     */
    public abstract void sendToServer(String server);

    /**
     * Kicks this player from the proxy with the given kick reason
     * <p>
     * Works currently on every supported cloud (CloudNet 2/3 and ReformCloud 2), but this may change later with more supported cloud systems.
     *
     * @param reason the reason to kick the player with
     * @throws CloudSupportException if the selected cloud system does not support this method
     */
    public abstract void kick(String reason);
}
