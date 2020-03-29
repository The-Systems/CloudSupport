package eu.thesystems.cloud.cloudnet3;

import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.network.HostAndPort;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.service.*;
import de.dytanic.cloudnet.ext.bridge.*;
import de.dytanic.cloudnet.ext.bridge.player.*;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.proxylayout.*;
import de.dytanic.cloudnet.lib.server.*;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

public class CloudNet2EmulatorConverter {

    public ICloudPlayer convertToV3OnlinePlayer(CloudPlayer cloudPlayer) {
        ServiceInfoSnapshot proxy = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByName(cloudPlayer.getProxy());
        ServiceInfoSnapshot server = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByName(cloudPlayer.getServer());
        if (proxy == null || server == null) {
            return null;
        }
        return new de.dytanic.cloudnet.ext.bridge.player.CloudPlayer(
                cloudPlayer.getUniqueId(),
                cloudPlayer.getName(),
                null,
                cloudPlayer.getFirstLogin(),
                cloudPlayer.getLastLogin(),
                this.convertPlayerConnection(cloudPlayer.getLastPlayerConnection()),
                new NetworkServiceInfo(
                        ServiceEnvironmentType.BUNGEECORD,
                        proxy.getServiceId(),
                        proxy.getConfiguration().getGroups()
                ),
                new NetworkServiceInfo(
                        ServiceEnvironmentType.MINECRAFT_SERVER,
                        server.getServiceId(),
                        server.getConfiguration().getGroups()
                ),
                this.convertPlayerConnection(cloudPlayer.getPlayerConnection()),
                new NetworkPlayerServerInfo(
                        cloudPlayer.getUniqueId(),
                        cloudPlayer.getName(),
                        null,
                        -1,
                        -1,
                        -1,
                        -1,
                        null,
                        null,
                        new NetworkServiceInfo(
                                ServiceEnvironmentType.MINECRAFT_SERVER,
                                server.getServiceId(),
                                server.getConfiguration().getGroups()
                        )
                ),
                JsonDocument.newDocument(cloudPlayer.getMetaData().convertToJsonString())
        );
    }

    public ICloudOfflinePlayer convertToV3OfflinePlayer(OfflinePlayer offlinePlayer) {
        return new CloudOfflinePlayer(
                offlinePlayer.getUniqueId(),
                offlinePlayer.getName(),
                null,
                offlinePlayer.getFirstLogin(),
                offlinePlayer.getLastLogin(),
                this.convertPlayerConnection(offlinePlayer.getLastPlayerConnection())
        );
    }

    public CloudPlayer convertToV2OnlinePlayer(ICloudPlayer cloudPlayer, IPermissionUser permissionUser) {
        CloudPlayer resultPlayer = new CloudPlayer(
                this.convertToV2OfflinePlayer(cloudPlayer, permissionUser),
                this.convertToPlayerConnection(cloudPlayer.getNetworkConnectionInfo()),
                cloudPlayer.getLoginService().getServerName()
        );
        resultPlayer.setPlayerExecutor(PlayerExecutorBridge.INSTANCE);
        return resultPlayer;
    }

    public OfflinePlayer convertToV2OfflinePlayer(ICloudOfflinePlayer offlinePlayer, IPermissionUser permissionUser) {
        return new OfflinePlayer(
                offlinePlayer.getUniqueId(),
                offlinePlayer.getName(),
                Document.load(offlinePlayer.getProperties().toJson()),
                offlinePlayer.getLastLoginTimeMillis(),
                offlinePlayer.getFirstLoginTimeMillis(),
                this.convertToPlayerConnection(offlinePlayer.getLastNetworkConnectionInfo()),
                permissionUser == null ? null : new PermissionEntity(
                        permissionUser.getUniqueId(),
                        permissionUser.getPermissions()
                                .stream()
                                .collect(Collectors.toMap(permission -> permission.getName().toLowerCase(), permission -> permission.getPotency() >= 0)),
                        null,
                        null,
                        permissionUser.getGroups()
                                .stream()
                                .map(groupInfo -> new GroupEntityData(groupInfo.getGroup(), groupInfo.getTimeOutMillis()))
                                .collect(Collectors.toList())
                )
        );
    }

    public NetworkConnectionInfo convertPlayerConnection(PlayerConnection connection) {
        return new NetworkConnectionInfo(
                connection.getUniqueId(),
                connection.getName(),
                connection.getVersion(),
                new HostAndPort(
                        connection.getHost(),
                        connection.getPort()
                ),
                null,
                connection.isOnlineMode(),
                connection.isLegacy(),
                null
        );
    }

    public PlayerConnection convertToPlayerConnection(NetworkConnectionInfo connectionInfo) {
        return new PlayerConnection(
                connectionInfo.getUniqueId(),
                connectionInfo.getName(),
                connectionInfo.getVersion(),
                connectionInfo.getAddress().getHost(),
                connectionInfo.getAddress().getPort(),
                connectionInfo.isOnlineMode(),
                connectionInfo.isLegacy()
        );
    }

    public Collection<ServiceRemoteInclusion> convertToIncludes(Collection<ServerInstallablePlugin> plugins) {
        return plugins.stream()
                .filter(serverInstallablePlugin -> serverInstallablePlugin.getUrl() != null)
                .map(serverInstallablePlugin -> new ServiceRemoteInclusion(serverInstallablePlugin.getUrl(), "plugins/" + serverInstallablePlugin.getName() + ".jar"))
                .collect(Collectors.toList());
    }

    public ProxyGroup convertToProxyGroup(ServiceTask serviceTask) {
        BridgeConfiguration bridgeConfiguration = BridgeConfigurationProvider.load();

        ProxyFallbackConfiguration fallbackConfiguration = bridgeConfiguration.getBungeeFallbackConfigurations()
                .stream()
                .filter(configuration -> serviceTask.getGroups().contains(configuration.getTargetGroup()))
                .findFirst().orElse(null);

        return new ProxyGroup(
                serviceTask.getName(),
                serviceTask.getAssociatedNodes(),
                serviceTask.getTemplates().stream().findFirst()
                        .map(template -> new Template(template.getName(), TemplateResource.LOCAL, null, new String[0], Collections.emptyList()))
                        .orElseThrow(() -> new IllegalStateException("No template provided for the task " + serviceTask.getName())),
                ProxyVersion.BUNGEECORD,
                serviceTask.getStartPort(),
                serviceTask.getMinServiceCount(),
                serviceTask.getProcessConfiguration().getMaxHeapMemorySize(),
                new ProxyConfig(
                        false,
                        false,
                        Collections.emptyList(),
                        new Motd("", ""),
                        "",
                        -1,
                        false,
                        new AutoSlot(
                                0,
                                false
                        ),
                        new TabList(
                                false,
                                "",
                                ""
                        ),
                        new String[0],
                        Collections.emptyList(),
                        new DynamicFallback(
                                fallbackConfiguration == null ? null : fallbackConfiguration.getDefaultFallbackTask(),
                                fallbackConfiguration == null ? null : fallbackConfiguration.getFallbacks().stream()
                                        .map(proxyFallback -> new ServerFallback(proxyFallback.getTask(), proxyFallback.getPermission()))
                                        .collect(Collectors.toList())
                        )
                ),
                serviceTask.isStaticServices() ? ProxyGroupMode.STATIC : ProxyGroupMode.DYNAMIC,
                serviceTask.getProperties().toInstanceOf(WrappedMap.class)
        );
    }

    public ServerGroup convertToServerGroup(ServiceTask serviceTask) {
        return new ServerGroup(
                serviceTask.getName(),
                serviceTask.getAssociatedNodes(),
                true,
                serviceTask.getProcessConfiguration().getMaxHeapMemorySize(),
                0,
                0,
                serviceTask.isMaintenance(),
                serviceTask.getMinServiceCount(),
                0,
                0,
                300,
                0,
                100,
                100,
                ServerGroupType.BUKKIT,
                serviceTask.isStaticServices() ? ServerGroupMode.STATIC : ServerGroupMode.DYNAMIC, //todo when signs enabled, use lobby mode?
                serviceTask.getTemplates()
                        .stream()
                        .map(template -> new Template(template.getName(), TemplateResource.LOCAL, null, new String[0], Collections.emptyList()))
                        .collect(Collectors.toList()),
                new AdvancedServerConfig(
                        true,
                        true,
                        true,
                        !serviceTask.isStaticServices()
                )
        );
    }

    public ServiceTask convertToTask(ServerGroup serverGroup) {
        return new ServiceTask(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                serverGroup.getName(),
                "jvm",
                serverGroup.isMaintenance(),
                true,
                serverGroup.getGroupMode() == ServerGroupMode.STATIC || serverGroup.getGroupMode() == ServerGroupMode.STATIC_LOBBY,
                serverGroup.getWrapper(),
                Collections.singletonList(serverGroup.getName()),
                Collections.emptyList(),
                new ProcessConfiguration(
                        ServiceEnvironmentType.MINECRAFT_SERVER,
                        serverGroup.getMemory(),
                        Collections.emptyList()
                ),
                41570,
                serverGroup.getMinOnlineServers()
        );
    }

    public ServiceTask convertToTask(ProxyGroup proxyGroup) {
        return new ServiceTask(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                proxyGroup.getName(),
                "jvm",
                proxyGroup.getProxyConfig().isEnabled() && proxyGroup.getProxyConfig().isMaintenance(),
                true,
                proxyGroup.getProxyGroupMode() == ProxyGroupMode.STATIC,
                proxyGroup.getWrapper(),
                Collections.singletonList(proxyGroup.getName()),
                Collections.emptyList(),
                new ProcessConfiguration(
                        ServiceEnvironmentType.BUNGEECORD,
                        proxyGroup.getMemory(),
                        Collections.emptyList()
                ),
                proxyGroup.getStartPort(),
                proxyGroup.getStartup()
        );
    }

    public ServiceInfoSnapshot convertServerInfo(ServerInfo serverInfo) {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(serverInfo.getServiceId().getUniqueId());
    }

    public ServerInfo convertToServerInfo(ServiceInfoSnapshot serviceInfoSnapshot) {
        Collection<ServicePlayer> players = serviceInfoSnapshot.getProperty(BridgeServiceProperty.PLAYERS).orElse(Collections.emptyList());
        ServerState state = this.convertServerState(serviceInfoSnapshot.getProperty(BridgeServiceProperty.STATE).orElse(null));

        return new ServerInfo(
                this.convertServiceId(serviceInfoSnapshot.getServiceId()),
                serviceInfoSnapshot.getAddress().getHost(),
                serviceInfoSnapshot.getAddress().getPort(),
                serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_ONLINE).orElse(false),
                players.stream().map(ServicePlayer::getName).collect(Collectors.toList()),
                serviceInfoSnapshot.getConfiguration().getProcessConfig().getMaxHeapMemorySize(),
                serviceInfoSnapshot.getProperty(BridgeServiceProperty.MOTD).orElse(null),
                serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0),
                serviceInfoSnapshot.getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(0),
                state,
                this.convertToServerConfig(serviceInfoSnapshot),
                this.convertToTemplate(serviceInfoSnapshot)
        );
    }

    public Template convertToTemplate(ServiceInfoSnapshot serviceInfoSnapshot) {
        return Arrays.stream(serviceInfoSnapshot.getConfiguration().getTemplates())
                .findFirst()
                .map(template -> new Template(template.getName(), TemplateResource.LOCAL, null, new String[0], Collections.emptyList()))
                .orElse(null);
    }

    public ServerConfig convertToServerConfig(ServiceInfoSnapshot serviceInfoSnapshot) {
        return new ServerConfig(
                serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_IN_GAME).orElse(false),
                serviceInfoSnapshot.getProperty(BridgeServiceProperty.EXTRA).orElse(null),
                Document.load(serviceInfoSnapshot.getProperties().toJson()),
                serviceInfoSnapshot.getConnectedTime()
        );
    }

    public ProxyInfo convertProxyInfo(ServiceInfoSnapshot serviceInfoSnapshot) {
        Collection<ServicePlayer> players = serviceInfoSnapshot.getProperty(BridgeServiceProperty.PLAYERS).orElse(Collections.emptyList());
        return new ProxyInfo(
                this.convertServiceId(serviceInfoSnapshot.getServiceId()),
                serviceInfoSnapshot.getAddress().getHost(),
                serviceInfoSnapshot.getAddress().getPort(),
                serviceInfoSnapshot.getProperty(BridgeServiceProperty.IS_ONLINE).orElse(false),
                players.stream().map(player -> new MultiValue<>(player.getUniqueId(), player.getName()))
                        .collect(Collectors.toList()),
                serviceInfoSnapshot.getConfiguration().getProcessConfig().getMaxHeapMemorySize(),
                serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0)
        );
    }

    public de.dytanic.cloudnet.lib.service.ServiceId convertServiceId(de.dytanic.cloudnet.driver.service.ServiceId serviceId) {
        return new de.dytanic.cloudnet.lib.service.ServiceId(
                serviceId.getTaskName(),
                serviceId.getTaskServiceId(),
                serviceId.getUniqueId(),
                serviceId.getNodeUniqueId(),
                serviceId.getName()
        );
    }

    public ServerState convertServerState(String state) {
        if (state == null) {
            return ServerState.OFFLINE;
        }
        state = state.toLowerCase();
        if (state.contains("ingame") || state.contains("running") || state.contains("playing")) {
            return ServerState.INGAME;
        }
        return ServerState.LOBBY;
    }

}
