package de.dytanic.cloudnet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.database.DatabaseManager;
import de.dytanic.cloudnet.api.handlers.NetworkHandlerProvider;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.network.HostAndPort;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.service.*;
import de.dytanic.cloudnet.ext.bridge.*;
import de.dytanic.cloudnet.ext.bridge.player.*;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.network.NetworkConnection;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.network.protocol.packet.result.Result;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.proxylayout.*;
import de.dytanic.cloudnet.lib.server.*;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.service.ICloudService;
import de.dytanic.cloudnet.wrapper.Wrapper;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class CloudAPI {

    private static CloudAPI instance;

    private ICloudService cloudService = null;

    //Init
    private NetworkHandlerProvider networkHandlerProvider = new NetworkHandlerProvider();

    private DatabaseManager databaseManager;

    public CloudAPI() {
        instance = this;
    }

    /**
     * Returns the instance of the CloudAPI
     */
    public static CloudAPI getInstance() {
        return instance;
    }

    @Deprecated
    public void bootstrap() {
        this.databaseManager = new DatabaseManager(Wrapper.getInstance().getDatabaseProvider());
    }

    @Deprecated
    public void shutdown() {
        Wrapper.getInstance().stop();
        System.exit(0);
    }

    private void warnUnavailableFeature(String feature) {
        System.err.println("The " + feature + " is not available in the CloudNet 3 Implementation!");
    }

    private void warnUnavailableFeature(String feature, String reason) {
        System.err.println("The " + feature + " is not available in the CloudNet 3 Implementation! Reason: " + reason);
    }

    private ICloudPlayer convertToV3OnlinePlayer(CloudPlayer cloudPlayer) {
        return new de.dytanic.cloudnet.ext.bridge.player.CloudPlayer(
                cloudPlayer.getUniqueId(),
                cloudPlayer.getName(),
                null,
                cloudPlayer.getFirstLogin(),
                cloudPlayer.getLastLogin(),
                this.convertPlayerConnection(cloudPlayer.getLastPlayerConnection()),
                new NetworkServiceInfo(
                        ServiceEnvironmentType.BUNGEECORD,
                        null,
                        cloudPlayer.getProxy()
                ),
                new NetworkServiceInfo(
                        ServiceEnvironmentType.MINECRAFT_SERVER,
                        null,
                        cloudPlayer.getServer()
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
                                null,
                                cloudPlayer.getServer()
                        )
                ),
                JsonDocument.newDocument(cloudPlayer.getMetaData().convertToJsonString())
        );
    }

    private ICloudOfflinePlayer convertToV3OfflinePlayer(OfflinePlayer offlinePlayer) {
        return new CloudOfflinePlayer(
                offlinePlayer.getUniqueId(),
                offlinePlayer.getName(),
                null,
                offlinePlayer.getFirstLogin(),
                offlinePlayer.getLastLogin(),
                this.convertPlayerConnection(offlinePlayer.getLastPlayerConnection())
        );
    }

    private CloudPlayer convertToV2OnlinePlayer(ICloudPlayer cloudPlayer, IPermissionUser permissionUser) {
        CloudPlayer resultPlayer = new CloudPlayer(
                this.convertToV2OfflinePlayer(cloudPlayer, permissionUser),
                this.convertToPlayerConnection(cloudPlayer.getNetworkConnectionInfo()),
                cloudPlayer.getLoginService().getServerName()
        );
        resultPlayer.setPlayerExecutor(PlayerExecutorBridge.INSTANCE);
        return resultPlayer;
    }

    private OfflinePlayer convertToV2OfflinePlayer(ICloudOfflinePlayer offlinePlayer, IPermissionUser permissionUser) {
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

    private NetworkConnectionInfo convertPlayerConnection(PlayerConnection connection) {
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

    private PlayerConnection convertToPlayerConnection(NetworkConnectionInfo connectionInfo) {
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

    private Collection<ServiceRemoteInclusion> convertToIncludes(Collection<ServerInstallablePlugin> plugins) {
        return plugins.stream()
                .filter(serverInstallablePlugin -> serverInstallablePlugin.getUrl() != null)
                .map(serverInstallablePlugin -> new ServiceRemoteInclusion(serverInstallablePlugin.getUrl(), "plugins/" + serverInstallablePlugin.getName() + ".jar"))
                .collect(Collectors.toList());
    }

    private ProxyGroup convertToProxyGroup(ServiceTask serviceTask) {
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

    private ServerGroup convertToServerGroup(ServiceTask serviceTask) {
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

    private ServiceTask convertToTask(ServerGroup serverGroup) {
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

    private ServiceTask convertToTask(ProxyGroup proxyGroup) {
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

    private ServiceInfoSnapshot convertServerInfo(ServerInfo serverInfo) {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudService(serverInfo.getServiceId().getUniqueId());
    }

    private ServerInfo convertToServerInfo(ServiceInfoSnapshot serviceInfoSnapshot) {
        Collection<JsonDocument> players = ServiceInfoSnapshotUtil.getPlayers(serviceInfoSnapshot);
        ServerState state = this.convertServerState(ServiceInfoSnapshotUtil.getState(serviceInfoSnapshot));

        return new ServerInfo(
                this.convertServiceId(serviceInfoSnapshot.getServiceId()),
                serviceInfoSnapshot.getAddress().getHost(),
                serviceInfoSnapshot.getAddress().getPort(),
                ServiceInfoSnapshotUtil.isOnline(serviceInfoSnapshot),
                players == null ? Collections.emptyList() : players.stream().map(document -> document.getString("name")).collect(Collectors.toList()),
                serviceInfoSnapshot.getConfiguration().getProcessConfig().getMaxHeapMemorySize(),
                ServiceInfoSnapshotUtil.getMotd(serviceInfoSnapshot),
                ServiceInfoSnapshotUtil.getOnlineCount(serviceInfoSnapshot),
                ServiceInfoSnapshotUtil.getMaxPlayers(serviceInfoSnapshot),
                state,
                new de.dytanic.cloudnet.lib.server.ServerConfig(
                        state == ServerState.INGAME,
                        "",
                        new Document(),
                        serviceInfoSnapshot.getCreationTime()
                ),
                Arrays.stream(serviceInfoSnapshot.getConfiguration().getTemplates())
                        .findFirst()
                        .map(template -> new Template(template.getName(), TemplateResource.LOCAL, null, new String[0], Collections.emptyList()))
                        .orElse(null)
        );
    }

    private ProxyInfo convertProxyInfo(ServiceInfoSnapshot serviceInfoSnapshot) {
        Collection<JsonDocument> players = ServiceInfoSnapshotUtil.getPlayers(serviceInfoSnapshot);
        return new ProxyInfo(
                this.convertServiceId(serviceInfoSnapshot.getServiceId()),
                serviceInfoSnapshot.getAddress().getHost(),
                serviceInfoSnapshot.getAddress().getPort(),
                ServiceInfoSnapshotUtil.isOnline(serviceInfoSnapshot),
                players == null ? Collections.emptyList() :
                        players.stream().map(document -> new MultiValue<>(document.get("uniqueId", UUID.class), document.getString("name")))
                                .collect(Collectors.toList()),
                serviceInfoSnapshot.getConfiguration().getProcessConfig().getMaxHeapMemorySize(),
                ServiceInfoSnapshotUtil.getOnlineCount(serviceInfoSnapshot)
        );
    }

    private ServiceId convertServiceId(de.dytanic.cloudnet.driver.service.ServiceId serviceId) {
        return new ServiceId(
                serviceId.getTaskName(),
                serviceId.getTaskServiceId(),
                serviceId.getUniqueId(),
                serviceId.getNodeUniqueId(),
                serviceId.getName()
        );
    }

    private ServerState convertServerState(String state) {
        if (state == null) {
            return ServerState.OFFLINE;
        }
        state = state.toLowerCase();
        if (state.contains("ingame") || state.contains("running") || state.contains("playing")) {
            return ServerState.INGAME;
        }
        return ServerState.LOBBY;
    }

    public CloudAPI update(ServerInfo serverInfo) {
        //this didn't even work in CloudNet 2.1, it has always updated the given server info to the sender of the packet
        this.warnUnavailableFeature("update ServerInfo", "Didn't even work in CloudNet 2.1");
        return this;
    }

    public CloudAPI update(ProxyInfo proxyInfo) {
        //this didn't even work in CloudNet 2.1, it has always updated the given server info to the sender of the packet
        this.warnUnavailableFeature("update ProxyInfo", "Didn't even work in CloudNet 2.1");
        return this;
    }

    /**
     * Returns synchronized the OnlineCount from the group
     */
    public int getOnlineCount(String group) {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesByGroup(group)
                .stream()
                .mapToInt(ServiceInfoSnapshotUtil::getOnlineCount)
                .sum();
    }

    /**
     * Returns all serverInfos from group #group
     *
     * @param group
     */
    public Collection<ServerInfo> getServers(String group) {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesByGroup(group)
                .stream()
                .map(this::convertToServerInfo)
                .collect(Collectors.toList());
    }

    @Deprecated
    public ICloudService getCloudService() {
        return this.cloudService;
    }

    @Deprecated
    public void setCloudService(ICloudService cloudService) {
        this.cloudService = cloudService;
    }

    /**
     * Returns a simple cloudnetwork information base
     */
    public CloudNetwork getCloudNetwork() {
        CloudNetwork cloudNetwork = new CloudNetwork();
        cloudNetwork.setMessages(new Document(Document.GSON.toJsonTree(BridgeConfigurationProvider.load().getMessages()).getAsJsonObject()));
        cloudNetwork.setModules(new Document());
        cloudNetwork.setOnlineCount(this.getOnlineCount());
        cloudNetwork.setProxyGroups(this.getProxyGroupMap());
        cloudNetwork.setRegisteredPlayerCount(this.getRegisteredPlayerCount());
        cloudNetwork.setServerGroups(this.getServerGroupMap());
        cloudNetwork.setWebPort(-1);
        cloudNetwork.setWrappers(this.getWrappers());
        return cloudNetwork;
    }

    /**
     * Internal CloudNetwork update set
     *
     * @param cloudNetwork
     */
    public void setCloudNetwork(CloudNetwork cloudNetwork) {
    }

    /**
     * Returns the network server manager from cloudnet
     */
    public NetworkHandlerProvider getNetworkHandlerProvider() {
        return this.networkHandlerProvider;
    }

    /**
     * Returns the internal network connection to the cloudnet root
     */
    public NetworkConnection getNetworkConnection() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the cloud prefix
     */
    public String getPrefix() {
        return BridgeConfigurationProvider.load().getPrefix();
    }

    /**
     * Returns the memory from this instance calc by Wrapper
     */
    public int getMemory() {
        return Wrapper.getInstance().getCurrentServiceInfoSnapshot().getConfiguration().getProcessConfig().getMaxHeapMemorySize();
    }

    /**
     * Returns the ServiceId from this instance
     */
    public ServiceId getServiceId() {
        return this.convertServiceId(Wrapper.getInstance().getServiceId());
    }

    /**
     * Returns the Database Manager for the CloudNetDB functions
     */
    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    /**
     * Returns the group name from this instance
     */
    public String getGroup() {
        return Wrapper.getInstance().getServiceId().getTaskName();
    }

    /**
     * Returns the UUID from this instance
     */
    public UUID getUniqueId() {
        return Wrapper.getInstance().getServiceId().getUniqueId();
    }

    /**
     * Returns the serverId (Lobby-1)
     */
    public String getServerId() {
        return Wrapper.getInstance().getServiceId().getName();
    }

    /**
     * Returns the Id (Lobby-1 -> "1")
     */
    public int getGroupInitId() {
        return Wrapper.getInstance().getServiceId().getTaskServiceId();
    }

    /**
     * Returns the wrapperid from this instance
     */
    public String getWrapperId() {
        return Wrapper.getInstance().getServiceId().getNodeUniqueId();
    }

    /**
     * Returns the SimpleServerGroup of the parameter
     *
     * @param group
     */
    public SimpleServerGroup getServerGroupData(String group) {
        ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(group);
        return serviceTask != null ? this.convertToServerGroup(serviceTask).toSimple() : null;
    }

    /**
     * Returns the ProxyGroup of the parameter
     *
     * @param group
     */
    public ProxyGroup getProxyGroupData(String group) {
        ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(group);
        return serviceTask != null ? this.convertToProxyGroup(serviceTask) : null;
    }

    /**
     * Returns the global onlineCount
     */
    public int getOnlineCount() {
        return 0;
        //return BridgePlayerManager.getInstance().getOnlineCount(); todo CloudNet 3.2
    }

    /**
     * Returns the amount of players that are registered in the Cloud
     */
    public int getRegisteredPlayerCount() {
        return 0;
        //return BridgePlayerManager.getInstance().getRegisteredPlayerCount(); todo CloudNet 3.2
    }

    /**
     * Returns all the module properties
     *
     * @return
     */
    public Document getModuleProperties() {
        this.warnUnavailableFeature("module properties");
        return new Document(); //empty as this is not available in CloudNet 3
    }

    /**
     * Returns the permissionPool of the cloudnetwork
     */
    public PermissionPool getPermissionPool() {
        //return cloudNetwork.getModules().getObject("permissionPool", PermissionPool.TYPE); todo
        return null;
    }

    /**
     * Returns all active wrappers on cloudnet
     */
    public Collection<WrapperInfo> getWrappers() {
        //return cloudNetwork.getWrappers(); todo
        return null;
    }

    /**
     * Returns the permission group from the permissions-system
     */
    public PermissionGroup getPermissionGroup(String group) {
        /*if (cloudNetwork.getModules().contains("permissionPool")) { todo
            return ((PermissionPool) cloudNetwork.getModules().getObject("permissionPool", PermissionPool.TYPE)).getGroups().get(group);
        }*/
        return null;
    }

    /**
     * Returns one of the wrapper infos
     *
     * @param wrapperId
     */
    public WrapperInfo getWrapper(String wrapperId) {
        /*return CollectionWrapper.filter(cloudNetwork.getWrappers(), new Acceptable<WrapperInfo>() { todo
            @Override
            public boolean isAccepted(WrapperInfo value) {
                return value.getServerId().equalsIgnoreCase(wrapperId);
            }
        });*/
        return null;
    }

    /**
     * Sends the data of the custom channel message to all proxys
     */
    public void sendCustomSubProxyMessage(String channel, String message, Document value) {
        //CloudNetDriver.getInstance().getMessenger().sendChannelMessage(); todo CloudNet 3.2 with the ServiceEnvironments
    }

    /**
     * Sends the data of the custom channel message to all server
     */
    public void sendCustomSubServerMessage(String channel, String message, Document value) {
        //CloudNetDriver.getInstance().getMessenger().sendChannelMessage(); todo CloudNet 3.2 with the ServiceEnvironments
    }

    /**
     * Sends the data of the custom channel message to one server
     */
    public void sendCustomSubServerMessage(String channel, String message, Document value, String serverName) {
        //CloudNetDriver.getInstance().getMessenger().sendChannelMessage(); todo CloudNet 3.2 with the ServiceEnvironments
    }

    /**
     * Sends the data of the custom channel message to proxy server
     */
    public void sendCustomSubProxyMessage(String channel, String message, Document value, String serverName) {
        //CloudNetDriver.getInstance().getMessenger().sendChannelMessage(); todo CloudNet 3.2 with the ServiceEnvironments
    }

    /**
     * Update the server group
     *
     * @param serverGroup
     */
    public void updateServerGroup(ServerGroup serverGroup) {
        CloudNetDriver.getInstance().getServiceTaskProvider().addPermanentServiceTask(this.convertToTask(serverGroup));
    }

    /**
     * Update the permission group
     */
    public void updatePermissionGroup(PermissionGroup permissionGroup) {
        /*this.logger.logp(Level.FINEST, todo
                         this.getClass().getSimpleName(),
                         "updatePermissionGroup",
                         String.format("Updating permission group: %s", permissionGroup));
        networkConnection.sendPacket(new PacketOutUpdatePermissionGroup(permissionGroup));*/
    }

    /**
     * Update the proxy group
     *
     * @param proxyGroup
     */
    public void updateProxyGroup(ProxyGroup proxyGroup) {
        CloudNetDriver.getInstance().getServiceTaskProvider().addPermanentServiceTask(this.convertToTask(proxyGroup));
    }

    /**
     * Dispatch a command on cloudnet-core
     */
    public void sendCloudCommand(String commandLine) {
        CloudNetDriver.getInstance().getNodeInfoProvider().sendCommandLineAsync(commandLine);
    }

    /**
     * Dispatch a console message
     *
     * @param output
     */
    public void dispatchConsoleMessage(String output) {
        //todo maybe in 3.2 available?
    }

    /**
     * Writes into the console of the server/proxy the command line
     *
     * @param defaultType
     * @param serverId
     * @param commandLine
     */
    public void sendConsoleMessage(DefaultType defaultType, String serverId, String commandLine) {
        CloudNetDriver.getInstance().getCloudServiceProvider(serverId).runCommandAsync(commandLine);
    }

    public Map<String, SimpleServerGroup> getServerGroupMap() {
        return CloudNetDriver.getInstance().getServiceTaskProvider().getPermanentServiceTasks()
                .stream()
                .map(serviceTask -> this.convertToServerGroup(serviceTask).toSimple())
                .collect(Collectors.toMap(SimpleServerGroup::getName, simpleServerGroup -> simpleServerGroup));
    }

    public Map<String, ProxyGroup> getProxyGroupMap() {
        return CloudNetDriver.getInstance().getServiceTaskProvider().getPermanentServiceTasks()
                .stream()
                .map(this::convertToProxyGroup)
                .collect(Collectors.toMap(ProxyGroup::getName, proxyGroup -> proxyGroup));
    }

    /**
     * Stop a game server with the parameter of the serverId
     *
     * @param serverId the server-id to stop
     */
    public void stopServer(String serverId) {
        CloudNetDriver.getInstance().getCloudServiceProvider(serverId).stopAsync();
    }

    /*=====================================================================================*/

    /**
     * Stop a BungeeCord proxy server with the id @proxyId
     */
    public void stopProxy(String proxyId) {
        CloudNetDriver.getInstance().getCloudServiceProvider(proxyId).stopAsync();
    }

    /**
     * Creates a custom server log url for one server screen
     */
    public String createServerLogUrl(String serverId) {
        //this is no more available in CloudNet 3
        this.warnUnavailableFeature("server logs");
        return null;
    }

    private void startService(ServiceInfoSnapshot serviceInfoSnapshot) {
        CloudNetDriver.getInstance().getCloudServiceProvider(serviceInfoSnapshot).startAsync();
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(ProxyGroup proxyGroup) {
        CloudNetDriver.getInstance().getCloudServiceFactory().createCloudServiceAsync(this.convertToTask(proxyGroup))
                .onComplete(this::startService);
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(ProxyGroup proxyGroup, int memory, String[] processParameters) {
        startProxy(proxyGroup, memory, processParameters, null, new ArrayList<>(), new Document());
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(ProxyGroup proxyGroup,
                           int memory,
                           String[] processParameters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document properties) {
        CloudNetDriver.getInstance().getCloudServiceFactory().createCloudServiceAsync(
                proxyGroup.getName(),
                "jvm",
                true,
                proxyGroup.getProxyGroupMode() == ProxyGroupMode.STATIC,
                plugins.stream()
                        .filter(serverInstallablePlugin -> serverInstallablePlugin.getUrl() != null)
                        .map(serverInstallablePlugin -> new ServiceRemoteInclusion(serverInstallablePlugin.getUrl(), "plugins/" + serverInstallablePlugin.getName() + ".jar"))
                        .collect(Collectors.toList()),
                Collections.singletonList(new ServiceTemplate(proxyGroup.getName(), proxyGroup.getTemplate().getName(), "local")),
                Collections.emptyList(),
                Collections.singletonList(proxyGroup.getName()),
                new ProcessConfiguration(
                        ServiceEnvironmentType.BUNGEECORD,
                        memory,
                        Arrays.asList(processParameters)
                ),
                JsonDocument.newDocument(properties.convertToJsonString()),
                proxyGroup.getStartPort()
        ).onComplete(this::startService);
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(ProxyGroup proxyGroup, int memory, String[] processParameters, Document document) {
        startProxy(proxyGroup, memory, processParameters, null, new ArrayList<>(), document);
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(WrapperInfo wrapperInfo, ProxyGroup proxyGroup) {
        startProxy(wrapperInfo, proxyGroup, proxyGroup.getMemory(), new String[] {});
    }

    /*=====================================================================================*/

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(WrapperInfo wrapperInfo, ProxyGroup proxyGroup, int memory, String[] processParameters) {
        startProxy(wrapperInfo, proxyGroup, memory, processParameters, null, new ArrayList<>(), new Document());
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(WrapperInfo wrapperInfo,
                           ProxyGroup proxyGroup,
                           int memory,
                           String[] processParameters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document properties) {
        CloudNetDriver.getInstance().getCloudServiceFactory().createCloudServiceAsync(
                wrapperInfo.getServerId(),
                1,
                proxyGroup.getName(),
                "jvm",
                true,
                proxyGroup.getProxyGroupMode() == ProxyGroupMode.STATIC,
                this.convertToIncludes(plugins),
                Collections.singletonList(new ServiceTemplate(proxyGroup.getName(), proxyGroup.getTemplate().getName(), "local")),
                Collections.emptyList(),
                Collections.singletonList(proxyGroup.getName()),
                new ProcessConfiguration(
                        ServiceEnvironmentType.BUNGEECORD,
                        memory,
                        Arrays.asList(processParameters)
                ),
                JsonDocument.newDocument(properties.convertToJsonString()),
                proxyGroup.getStartPort()
        ).onComplete(serviceInfoSnapshots -> {
            for (ServiceInfoSnapshot serviceInfoSnapshot : serviceInfoSnapshots) {
                this.startService(serviceInfoSnapshot);
            }
        });
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(WrapperInfo wrapperInfo, ProxyGroup proxyGroup, int memory, String[] processParameters, Document document) {
        startProxy(wrapperInfo, proxyGroup, memory, processParameters, null, new ArrayList<>(), document);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup) {
        startGameServer(simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()));
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig) {
        startGameServer(simpleServerGroup, serverConfig, false);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, boolean priorityStop) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, int memory, boolean priorityStop) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties());
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties) {
        startGameServer(simpleServerGroup,
                        serverConfig,
                        memory,
                        new String[] {},
                        null,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>());
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup
     * @param serverConfig
     * @param memory
     * @param processParameters
     * @param template
     * @param onlineMode
     * @param priorityStop
     * @param properties
     * @param url
     * @param plugins
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                String[] processParameters,
                                Template template,
                                String customServerName,
                                boolean onlineMode,
                                boolean priorityStop,
                                Properties properties,
                                String url,
                                Collection<ServerInstallablePlugin> plugins) {
        this.startGameServer(
                simpleServerGroup,
                serverConfig,
                memory,
                processParameters,
                template,
                customServerName,
                onlineMode,
                priorityStop,
                properties,
                url,
                plugins,
                null
        );
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, String serverId) {
        startGameServer(simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()), serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, String serverId) {
        startGameServer(simpleServerGroup, serverConfig, false, serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, boolean priorityStop, String serverId) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop, serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                String serverId) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties(), serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties,
                                String serverId) {
        startGameServer(simpleServerGroup,
                        serverConfig,
                        memory,
                        new String[] {},
                        null,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>(),
                        serverId);
    }

    /*==================================================================*/

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup
     * @param serverConfig
     * @param memory
     * @param processParameters
     * @param template
     * @param onlineMode
     * @param priorityStop
     * @param properties
     * @param url
     * @param plugins
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                String[] processParameters,
                                Template template,
                                String customServerName,
                                boolean onlineMode,
                                boolean priorityStop,
                                Properties properties,
                                String url,
                                Collection<ServerInstallablePlugin> plugins,
                                String serverId) {
        CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTaskAsync(simpleServerGroup.getName())
                .onComplete(
                        serviceTask -> CloudNetDriver.getInstance().getCloudServiceFactory().createCloudServiceAsync(
                                simpleServerGroup.getName(),
                                "jvm",
                                true,
                                simpleServerGroup.getMode() == ServerGroupMode.STATIC || simpleServerGroup.getMode() == ServerGroupMode.STATIC_LOBBY,
                                this.convertToIncludes(plugins),
                                Collections.singletonList(new ServiceTemplate(simpleServerGroup.getName(), template.getName(), "local")),
                                Collections.emptyList(),
                                Collections.singletonList(simpleServerGroup.getName()),
                                new ProcessConfiguration(
                                        ServiceEnvironmentType.MINECRAFT_SERVER,
                                        memory,
                                        Arrays.asList(processParameters)
                                ),
                                JsonDocument.newDocument(serverConfig.getProperties().convertToJsonString()),
                                serviceTask != null ? serviceTask.getStartPort() : 44955
                        ).onComplete(this::startService)
                );
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, Template template) {
        startGameServer(simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()), template);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, Template template) {
        startGameServer(simpleServerGroup, serverConfig, false, template);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, boolean priorityStop, Template template) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop, template);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Template template) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties(), template);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties,
                                Template template) {
        startGameServer(simpleServerGroup,
                        serverConfig,
                        memory,
                        new String[] {},
                        template,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>());
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, Template template, String serverId) {
        startGameServer(simpleServerGroup, serverConfig, false, template, serverId);
    }

    /*==================================================================*/

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                boolean priorityStop,
                                Template template,
                                String serverId) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop, template, serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Template template,
                                String serverId) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties(), template, serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties,
                                Template template,
                                String serverId) {
        startGameServer(simpleServerGroup,
                        serverConfig,
                        memory,
                        new String[] {},
                        template,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>(),
                        serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo, SimpleServerGroup simpleServerGroup) {
        startGameServer(wrapperInfo, simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()));
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo, SimpleServerGroup simpleServerGroup, ServerConfig serverConfig) {
        startGameServer(wrapperInfo, simpleServerGroup, serverConfig, false);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                boolean priorityStop) {
        startGameServer(wrapperInfo, simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop) {
        startGameServer(wrapperInfo, simpleServerGroup, serverConfig, memory, priorityStop, new Properties());
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties) {
        startGameServer(wrapperInfo,
                        simpleServerGroup,
                        serverConfig,
                        memory,
                        new String[] {},
                        null,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>());
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup
     * @param serverConfig
     * @param memory
     * @param processParameters
     * @param template
     * @param onlineMode
     * @param priorityStop
     * @param properties
     * @param url
     * @param plugins
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                String[] processParameters,
                                Template template,
                                String customServerName,
                                boolean onlineMode,
                                boolean priorityStop,
                                Properties properties,
                                String url,
                                Collection<ServerInstallablePlugin> plugins) {
        this.startGameServer(
                wrapperInfo,
                simpleServerGroup,
                null,
                serverConfig,
                memory,
                processParameters,
                template,
                customServerName,
                onlineMode,
                priorityStop,
                properties,
                url,
                plugins
        );
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties,
                                Template template) {
        startGameServer(wrapperInfo,
                        simpleServerGroup,
                        serverConfig,
                        memory,
                        new String[] {},
                        template,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>());
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup
     * @param serverConfig
     * @param memory
     * @param processParameters
     * @param template
     * @param onlineMode
     * @param priorityStop
     * @param properties
     * @param url
     * @param plugins
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                String serverId,
                                ServerConfig serverConfig,
                                int memory,
                                String[] processParameters,
                                Template template,
                                String customServerName,
                                boolean onlineMode,
                                boolean priorityStop,
                                Properties properties,
                                String url,
                                Collection<ServerInstallablePlugin> plugins) {
        CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTaskAsync(simpleServerGroup.getName())
                .onComplete(
                        serviceTask -> CloudNetDriver.getInstance().getCloudServiceFactory().createCloudServiceAsync(
                                wrapperInfo.getServerId(),
                                1,
                                simpleServerGroup.getName(),
                                "jvm",
                                true,
                                simpleServerGroup.getMode() == ServerGroupMode.STATIC || simpleServerGroup.getMode() == ServerGroupMode.STATIC_LOBBY,
                                this.convertToIncludes(plugins),
                                Collections.singletonList(new ServiceTemplate(simpleServerGroup.getName(), template.getName(), "local")),
                                Collections.emptyList(),
                                Collections.singletonList(simpleServerGroup.getName()),
                                new ProcessConfiguration(
                                        ServiceEnvironmentType.MINECRAFT_SERVER,
                                        memory,
                                        Arrays.asList(processParameters)
                                ),
                                JsonDocument.newDocument(serverConfig.getProperties().convertToJsonString()),
                                serviceTask != null ? serviceTask.getStartPort() : 44955
                        ).onComplete(serviceInfoSnapshots -> {
                            for (ServiceInfoSnapshot serviceInfoSnapshot : serviceInfoSnapshots) {
                                this.startService(serviceInfoSnapshot);
                            }
                        })
                );
    }

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(WrapperInfo wrapperInfo, String serverName, int memory, boolean priorityStop) {
        this.warnUnavailableFeature("CloudServers", "Didn't work good in CloudNet 2.1 and wasn't used often");
    }

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(WrapperInfo wrapperInfo, String serverName, ServerConfig serverConfig, int memory, boolean priorityStop) {
        this.warnUnavailableFeature("CloudServers", "Didn't work good in CloudNet 2.1 and wasn't used often");
    }

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(WrapperInfo wrapperInfo,
                                 String serverName,
                                 ServerConfig serverConfig,
                                 int memory,
                                 boolean priorityStop,
                                 String[] processPreParameters,
                                 Collection<ServerInstallablePlugin> plugins,
                                 Properties properties,
                                 ServerGroupType serverGroupType) {
        this.warnUnavailableFeature("CloudServers", "Didn't work good in CloudNet 2.1 and wasn't used often");
    }

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(String serverName, int memory, boolean priorityStop) {
        this.warnUnavailableFeature("CloudServers", "Didn't work good in CloudNet 2.1 and wasn't used often");
    }

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(String serverName, ServerConfig serverConfig, int memory, boolean priorityStop) {
        this.warnUnavailableFeature("CloudServers", "Didn't work good in CloudNet 2.1 and wasn't used often");
    }

    /*==========================================================================*/

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(String serverName,
                                 ServerConfig serverConfig,
                                 int memory,
                                 boolean priorityStop,
                                 String[] processPreParameters,
                                 Collection<ServerInstallablePlugin> plugins,
                                 Properties properties,
                                 ServerGroupType serverGroupType) {
        this.warnUnavailableFeature("CloudServers", "Didn't work good in CloudNet 2.1 and wasn't used often");
    }

    /**
     * Update the CloudPlayer objective
     *
     * @param cloudPlayer
     */
    public void updatePlayer(CloudPlayer cloudPlayer) {
        BridgePlayerManager.getInstance().updateOnlinePlayer(this.convertToV3OnlinePlayer(cloudPlayer));
    }

    /**
     * Updates a offlinePlayer Objective on the database
     *
     * @param offlinePlayer
     */
    public void updatePlayer(OfflinePlayer offlinePlayer) {
        BridgePlayerManager.getInstance().updateOfflinePlayer(this.convertToV3OfflinePlayer(offlinePlayer));
    }

    /**
     * Returns all servers on network
     */
    public Collection<ServerInfo> getServers() {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(ServiceEnvironmentType.MINECRAFT_SERVER)
                .stream()
                .map(this::convertToServerInfo)
                .collect(Collectors.toList());
    }

    /**
     * Returns the ServerInfo from all CloudGameServers
     */
    public Collection<ServerInfo> getCloudServers() {
        this.warnUnavailableFeature("CloudServers", "Didn't work good in CloudNet 2.1 and wasn't used often");
        return Collections.emptyList();
    }

    /**
     * Returns all proxyInfos on network
     */
    public Collection<ProxyInfo> getProxys() {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(ServiceEnvironmentType.BUNGEECORD)
                .stream()
                .map(this::convertProxyInfo)
                .collect(Collectors.toList());
    }

    /**
     * Returns the ProxyInfos from all proxys in the group #group
     *
     * @param group
     */
    public Collection<ProxyInfo> getProxys(String group) {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(ServiceEnvironmentType.BUNGEECORD)
                .stream()
                .filter(serviceInfoSnapshot -> serviceInfoSnapshot.getServiceId().getTaskName().equalsIgnoreCase(group))
                .map(this::convertProxyInfo)
                .collect(Collectors.toList());
    }

    /**
     * Returns all OnlinePlayers on Network
     */
    public Collection<CloudPlayer> getOnlinePlayers() {
        return BridgePlayerManager.getInstance().getOnlinePlayers()
                .stream()
                .map(cloudPlayer -> {
                    IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionProvider().getUser(cloudPlayer.getUniqueId());
                    return this.convertToV2OnlinePlayer(cloudPlayer, permissionUser);
                })
                .collect(Collectors.toList());
    }

    /**
     * Retuns a online CloudPlayer on network or null if the player isn't online
     */
    public CloudPlayer getOnlinePlayer(UUID uniqueId) {
        ICloudPlayer cloudPlayer = BridgePlayerManager.getInstance().getOnlinePlayer(uniqueId);
        if (cloudPlayer == null) {
            return null;
        }

        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionProvider().getUser(cloudPlayer.getUniqueId());

        return this.convertToV2OnlinePlayer(cloudPlayer, permissionUser);
    }

    /**
     * Returns a offline player which registerd or null
     *
     * @param uniqueId
     */
    public OfflinePlayer getOfflinePlayer(UUID uniqueId) {
        ICloudOfflinePlayer cloudPlayer = BridgePlayerManager.getInstance().getOfflinePlayer(uniqueId);
        if (cloudPlayer == null) {
            return null;
        }

        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionProvider().getUser(cloudPlayer.getUniqueId());

        return this.convertToV2OfflinePlayer(cloudPlayer, permissionUser);
    }

    /**
     * Returns a offline player which registerd or null
     *
     * @param name
     */
    public OfflinePlayer getOfflinePlayer(String name) {
        ICloudOfflinePlayer cloudPlayer = BridgePlayerManager.getInstance().getOfflinePlayer(name).stream().findFirst().orElse(null); //todo Replace with getFirstOfflinePlayer(String) in CloudNet 3.2
        if (cloudPlayer == null) {
            return null;
        }

        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionProvider().getUser(cloudPlayer.getUniqueId());

        return this.convertToV2OfflinePlayer(cloudPlayer, permissionUser);
    }

    /**
     * Returns the ServerGroup from the name or null
     *
     * @param name
     */
    public ServerGroup getServerGroup(String name) {
        ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(name);
        return serviceTask != null ? this.convertToServerGroup(serviceTask) : null;
    }

    /**
     * Returns from a registerd Player the uniqueId or null if the player doesn't exists
     */
    public UUID getPlayerUniqueId(String name) {
        return BridgePlayerManager.getInstance().getOfflinePlayer(name).stream().findFirst().map(ICloudOfflinePlayer::getUniqueId).orElse(null); //todo use getFirstOfflinePlayer(String) (CloudNet 3.2)
    }

    /**
     * Returns from a registerd Player the name or null if the player doesn't exists
     */
    public String getPlayerName(UUID uniqueId) {
        ICloudOfflinePlayer offlinePlayer = BridgePlayerManager.getInstance().getOfflinePlayer(uniqueId);
        return offlinePlayer != null ? offlinePlayer.getName() : null;
    }

    /**
     * Returns the ServerInfo from one gameServer where serverName = serverId
     */
    public ServerInfo getServerInfo(String serverName) {
        ServiceInfoSnapshot serviceInfoSnapshot = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByName(serverName);
        return serviceInfoSnapshot != null ? this.convertToServerInfo(serviceInfoSnapshot) : null;
    }

    /**
     * Returns a Document with all collected statistics
     *
     * @return
     */
    public Document getStatistics() {
        this.warnUnavailableFeature("Statistics");
        return new Document();
    }

    /*================================================================================*/

    /**
     *
     */
    public void copyDirectory(ServerInfo serverInfo, String directory) {
        this.warnUnavailableFeature("Copy specific directory");
    }

    /**
     * Unsafe Method
     */
    @Deprecated
    private Map<UUID, OfflinePlayer> getRegisteredPlayers() {
        return BridgePlayerManager.getInstance().getRegisteredPlayers()
                .stream()
                .map(offlinePlayer -> this.convertToV2OfflinePlayer(
                        offlinePlayer,
                        CloudNetDriver.getInstance().getPermissionProvider().getUser(offlinePlayer.getUniqueId())
                ))
                .collect(Collectors.toMap(OfflinePlayer::getUniqueId, offlinePlayer -> offlinePlayer));
    }

    public Logger getLogger() {
        this.warnUnavailableFeature("CloudAPI Logger");
        return null;
    }

    public void setLogger(Logger logger) {
        this.warnUnavailableFeature("CloudAPI Logger");
    }

    public boolean isDebug() {
        this.warnUnavailableFeature("Debug");
        return false;
    }

    public void setDebug(boolean debug) {
        this.warnUnavailableFeature("Debug");
    }
}
