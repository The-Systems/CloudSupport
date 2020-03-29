/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.player;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 27.08.2017.
 */
public class PlayerExecutorBridge extends PlayerExecutor {

    public static final PlayerExecutorBridge INSTANCE = new PlayerExecutorBridge();

    private static final String CHANNEL_NAME = "cloudnet_internal";

    public PlayerExecutorBridge() {
        this.available = true;
    }

    @Override
    public void sendPlayer(CloudPlayer cloudPlayer, String server) {
        if (cloudPlayer == null || server == null) {
            return;
        }

        BridgePlayerManager.getInstance().getPlayerExecutor(cloudPlayer.getUniqueId()).connect(server);
    }

    @Override
    public void kickPlayer(CloudPlayer cloudPlayer, String reason) {
        if (cloudPlayer == null || reason == null) {
            return;
        }

        BridgePlayerManager.getInstance().getPlayerExecutor(cloudPlayer.getUniqueId()).kick(reason);
    }

    @Override
    public void sendMessage(CloudPlayer cloudPlayer, String message) {
        if (cloudPlayer == null || message == null) {
            return;
        }

        BridgePlayerManager.getInstance().getPlayerExecutor(cloudPlayer.getUniqueId()).sendChatMessage(message);
    }

    @Override
    public void sendActionbar(CloudPlayer cloudPlayer, String message) {
        if (cloudPlayer == null || message == null) {
            return;
        }

        //todo maybe in a later CloudNet version?
    }

    @Override
    public void sendTitle(CloudPlayer cloudPlayer, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        //todo maybe in a later CloudNet version?
    }
}
