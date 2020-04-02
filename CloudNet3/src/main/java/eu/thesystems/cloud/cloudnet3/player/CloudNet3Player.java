package eu.thesystems.cloud.cloudnet3.player;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.thesystems.cloud.CloudSystem;
import eu.thesystems.cloud.exception.CloudSupportException;
import eu.thesystems.cloud.network.NetworkAddress;
import eu.thesystems.cloud.player.OnlinePlayer;

import java.util.UUID;

public class CloudNet3Player extends OnlinePlayer {
    private CloudSystem cloudSystem;

    public CloudNet3Player(CloudSystem cloudSystem, UUID uniqueId, String name, NetworkAddress lastAddress, long firstLogin, long lastLogin, NetworkAddress address) {
        super(uniqueId, name, lastAddress, firstLogin, lastLogin, address);
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void sendMessage(String message) {
        CloudNetDriver.getInstance().getMessenger().sendChannelMessage("cloudnet-bridge-channel-player-api", "send_message_to_proxy_player", new JsonDocument().append("uniqueId", this.getUniqueId()).append("message", message));
    }

    @Override
    public void sendToServer(String server) {
        CloudNetDriver.getInstance().getMessenger().sendChannelMessage("cloudnet-bridge-channel-player-api", "send_on_proxy_player_to_server", new JsonDocument().append("uniqueId", this.getUniqueId()).append("serviceName", server));
    }

    @Override
    public void kick(String reason) {
        CloudNetDriver.getInstance().getMessenger().sendChannelMessage("cloudnet-bridge-channel-player-api", "kick_on_proxy_player_from_network", new JsonDocument().append("uniqueId", this.getUniqueId()).append("kickMessage", reason));
    }

    @Override
    public void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        throw new CloudSupportException(this.cloudSystem);
    }

    @Override
    public void sendActionBar(String message) {
        throw new CloudSupportException(this.cloudSystem);
    }
}
