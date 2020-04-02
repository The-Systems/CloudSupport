package eu.thesystems.cloud.cloudnet2;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.Gson;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.modules.ModuleConfig;
import eu.thesystems.cloud.cloudnet2.permission.CloudNet2PermissionUser;
import eu.thesystems.cloud.cloudnet2.player.CloudNet2Player;
import eu.thesystems.cloud.converter.CloudObjectConverter;
import eu.thesystems.cloud.info.*;
import eu.thesystems.cloud.network.NetworkAddress;
import eu.thesystems.cloud.permission.PermissionUser;
import eu.thesystems.cloud.player.OfflinePlayer;
import eu.thesystems.cloud.player.OnlinePlayer;
import eu.thesystems.cloud.modules.DefaultModuleInfo;
import eu.thesystems.cloud.modules.ModuleInfo;

import java.util.Collections;
import java.util.stream.Collectors;

public class CloudNet2ObjectConverter implements CloudObjectConverter {

    private final Gson gson = new Gson();

    @Override
    public ProcessInfo convertProcessInfo(Object cloudProcessInfo) {
        return cloudProcessInfo instanceof de.dytanic.cloudnet.lib.server.info.ServerInfo ?
                this.convertServerInfo(cloudProcessInfo) :
                cloudProcessInfo instanceof de.dytanic.cloudnet.lib.server.info.ProxyInfo ?
                        this.convertProxyInfo(cloudProcessInfo) :
                        null;
    }

    @Override
    public ServerInfo convertServerInfo(Object cloudServerInfo) {
        if (cloudServerInfo == null) {
            return null;
        }
        de.dytanic.cloudnet.lib.server.info.ServerInfo serverInfo = (de.dytanic.cloudnet.lib.server.info.ServerInfo) cloudServerInfo;
        return new ServerInfo(
                serverInfo.getServiceId().getGroup(),
                serverInfo.getServiceId().getServerId(),
                serverInfo.getServiceId().getUniqueId().toString(),
                serverInfo.getServiceId().getWrapperId(),
                new NetworkAddress(serverInfo.getHost(), serverInfo.getPort()),
                Collections.singletonList(this.mapTemplate(serverInfo.getServiceId().getGroup(), serverInfo.getTemplate())),
                serverInfo.getPlayers(),
                serverInfo.getMaxPlayers(),
                this.gson.toJsonTree(serverInfo).getAsJsonObject(),
                serverInfo.getMotd(),
                serverInfo.getServerState().toString()
        );
    }

    @Override
    public ServerGroup convertServerGroup(Object cloudServerGroup) {
        if (cloudServerGroup == null) {
            return null;
        }
        de.dytanic.cloudnet.lib.server.ServerGroup serverGroup = (de.dytanic.cloudnet.lib.server.ServerGroup) cloudServerGroup;
        return new ServerGroup(
                serverGroup.getName(),
                serverGroup.getWrapper(),
                serverGroup.getMemory(),
                serverGroup.getMinOnlineServers(),
                serverGroup.getGroupMode().toString(),
                serverGroup.getTemplates().stream()
                        .map(template -> this.mapTemplate(serverGroup.getName(), template))
                        .collect(Collectors.toList()),
                this.gson.toJsonTree(serverGroup).getAsJsonObject()
        );
    }

    @Override
    public ProcessGroup convertProcessGroup(Object cloudProcessGroup) {
        return cloudProcessGroup instanceof de.dytanic.cloudnet.lib.server.ServerGroup ?
                this.convertServerGroup(cloudProcessGroup) :
                cloudProcessGroup instanceof de.dytanic.cloudnet.lib.server.ProxyGroup ?
                        this.convertProxyGroup(cloudProcessGroup) :
                        null;
    }

    @Override
    public ProxyInfo convertProxyInfo(Object cloudProxyInfo) {
        if (cloudProxyInfo == null) {
            return null;
        }
        de.dytanic.cloudnet.lib.server.info.ProxyInfo proxyInfo = (de.dytanic.cloudnet.lib.server.info.ProxyInfo) cloudProxyInfo;
        return new ProxyInfo(
                proxyInfo.getServiceId().getGroup(),
                proxyInfo.getServiceId().getServerId(),
                proxyInfo.getServiceId().getUniqueId().toString(),
                proxyInfo.getServiceId().getWrapperId(),
                new NetworkAddress(proxyInfo.getHost(), proxyInfo.getPort()),
                Collections.singletonList(
                        this.mapTemplate(proxyInfo.getServiceId().getGroup(), new de.dytanic.cloudnet.lib.server.template.Template("default", TemplateResource.LOCAL, null, new String[0], Collections.emptyList()))
                ),
                proxyInfo.getPlayers().stream().map(MultiValue::getSecond).collect(Collectors.toList()),
                this.gson.toJsonTree(proxyInfo).getAsJsonObject()
        );
    }

    @Override
    public ProxyGroup convertProxyGroup(Object cloudProxyGroup) {
        if (cloudProxyGroup == null) {
            return null;
        }
        de.dytanic.cloudnet.lib.server.ProxyGroup proxyGroup = (de.dytanic.cloudnet.lib.server.ProxyGroup) cloudProxyGroup;
        return new ProxyGroup(
                proxyGroup.getName(),
                proxyGroup.getWrapper(),
                proxyGroup.getMemory(),
                proxyGroup.getStartup(),
                proxyGroup.getProxyGroupMode().toString(),
                Collections.singletonList(new Template(proxyGroup.getName(), proxyGroup.getTemplate().getName(), proxyGroup.getName() + "/" + proxyGroup.getTemplate().getName(), this.gson.toJsonTree(proxyGroup.getTemplate()).getAsJsonObject())),
                this.gson.toJsonTree(proxyGroup).getAsJsonObject(),
                proxyGroup.getStartPort()
        );
    }

    private Template mapTemplate(String name, de.dytanic.cloudnet.lib.server.template.Template cloudnetTemplate) {
        return new Template(name, cloudnetTemplate.getName(), name + "/" + cloudnetTemplate.getName(), this.gson.toJsonTree(cloudnetTemplate).getAsJsonObject());
    }

    @Override
    public PermissionUser convertPermissionUser(Object cloudPermissionUser) {
        return new CloudNet2PermissionUser((User) cloudPermissionUser);
    }

    @Override
    public ModuleInfo convertModuleInfo(Object cloudModuleInfo) {
        ModuleConfig moduleConfig = (ModuleConfig) cloudModuleInfo;
        return new DefaultModuleInfo(moduleConfig.getName(), new String[]{moduleConfig.getAuthor()}, moduleConfig.getVersion(), moduleConfig.getMain(), moduleConfig.getFile().toPath(), this.gson.toJsonTree(moduleConfig).getAsJsonObject());
    }

    @Override
    public OnlinePlayer convertOnlinePlayer(Object cloudPlayer) {
        if (cloudPlayer == null) {
            return null;
        }
        CloudPlayer player = (CloudPlayer) cloudPlayer;
        return new CloudNet2Player(
                player.getUniqueId(),
                player.getName(),
                new NetworkAddress(player.getLastPlayerConnection().getHost(), player.getLastPlayerConnection().getPort()),
                player.getFirstLogin(),
                player.getLastLogin(),
                new NetworkAddress(player.getPlayerConnection().getHost(), player.getPlayerConnection().getPort())
        );
    }

    @Override
    public OfflinePlayer convertOfflinePlayer(Object cloudOfflinePlayer) {
        if (cloudOfflinePlayer == null) {
            return null;
        }
        de.dytanic.cloudnet.lib.player.OfflinePlayer player = (de.dytanic.cloudnet.lib.player.OfflinePlayer) cloudOfflinePlayer;
        return new OfflinePlayer(
                player.getUniqueId(),
                player.getName(),
                new NetworkAddress(player.getLastPlayerConnection().getHost(), player.getLastPlayerConnection().getPort()),
                player.getFirstLogin(),
                player.getLastLogin()
        );
    }

    @Override
    public boolean isOnlinePlayer(Object cloudPlayer) {
        return cloudPlayer instanceof CloudPlayer || cloudPlayer instanceof OnlinePlayer;
    }
}
