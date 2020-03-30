package de.dytanic.cloudnet.api;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;

import java.util.Map;
import java.util.UUID;

public interface ICloudService {

    CloudPlayer getCachedPlayer(UUID uniqueId);

    CloudPlayer getCachedPlayer(String name);

    boolean isProxyInstance();

    Map<String, ServerInfo> getServers();

}
