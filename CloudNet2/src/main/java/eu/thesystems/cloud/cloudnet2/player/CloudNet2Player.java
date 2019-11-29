package eu.thesystems.cloud.cloudnet2.player;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.thesystems.cloud.global.network.NetworkAddress;
import eu.thesystems.cloud.global.player.OnlinePlayer;

import java.util.UUID;

public class CloudNet2Player extends OnlinePlayer {
    public CloudNet2Player(UUID uniqueId, String name, NetworkAddress lastAddress, long firstLogin, long lastLogin, NetworkAddress address) {
        super(uniqueId, name, lastAddress, firstLogin, lastLogin, address);
    }

    @Override
    public void sendMessage(String message) {
        CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "sendMessage", new Document("uniqueId", this.getUniqueId()).append("message", message));
    }

    @Override
    public void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "sendTitle", new Document("uniqueId", this.getUniqueId()).append("title", title).append("subTitle", subTitle).append("stay", stay).append("fadeIn", fadeIn).append("fadeOut", fadeOut));
    }

    @Override
    public void sendActionBar(String message) {
        CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "sendActionbar", new Document("message", message).append("uniqueId", this.getUniqueId()));
    }

    @Override
    public void sendToServer(String server) {
        CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "sendPlayer", new Document("uniqueId", this.getUniqueId()).append("server", server));
    }

    @Override
    public void kick(String reason) {
        CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "kickPlayer", new Document("uniqueId", this.getUniqueId()).append("reason", reason));
    }

}
