package eu.thesystems.cloud.converter;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.global.info.*;
import eu.thesystems.cloud.global.permission.PermissionUser;
import eu.thesystems.cloud.global.player.OfflinePlayer;
import eu.thesystems.cloud.global.player.OnlinePlayer;
import eu.thesystems.cloud.modules.ModuleInfo;

/**
 * This class is used to convert the cloud-specific objects to our own objects which are compatible with every cloud we support.
 */
public interface CloudObjectConverter {
    ProcessInfo convertProcessInfo(Object cloudProcessInfo);

    ServerInfo convertServerInfo(Object cloudServerInfo);

    ServerGroup convertServerGroup(Object cloudServerGroup);

    ProcessGroup convertProcessGroup(Object cloudProcessGroup);

    ProxyInfo convertProxyInfo(Object cloudProxyInfo);

    ProxyGroup convertProxyGroup(Object cloudProxyGroup);

    PermissionUser convertPermissionUser(Object cloudPermissionUser);

    ModuleInfo convertModuleInfo(Object cloudModuleInfo);

    OnlinePlayer convertOnlinePlayer(Object cloudPlayer);

    OfflinePlayer convertOfflinePlayer(Object cloudOfflinePlayer);

    boolean isOnlinePlayer(Object cloudPlayer);

}
