package de.dytanic.cloudnet.api;

import de.dytanic.cloudnet.api.database.DatabaseManager;
import de.dytanic.cloudnet.api.handlers.NetworkHandlerProvider;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.service.*;
import de.dytanic.cloudnet.ext.bridge.BridgeConfigurationProvider;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.player.ICloudOfflinePlayer;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.network.NetworkConnection;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.server.*;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.thesystems.cloud.cloudnet3.CloudNet2EmulatorConverter;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class CloudAPI {

    private static CloudAPI instance;

    private ICloudService cloudService = null;

    private CloudNet2EmulatorConverter converter = new CloudNet2EmulatorConverter();

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

    public void warnUnavailableFeature(String feature) {
        System.err.println("The " + feature + " is not available in the CloudNet 3 Implementation!");
    }

    public void warnUnavailableFeature(String feature, String reason) {
        System.err.println("The " + feature + " is not available in the CloudNet 3 Implementation! Reason: " + reason);
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
                .mapToInt(serviceInfoSnapshot -> serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0))
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
                .map(this.converter::convertToServerInfo)
                .collect(Collectors.toList());
    }

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

    public CloudNet2EmulatorConverter getConverter() {
        return this.converter;
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
        return this.converter.convertServiceId(Wrapper.getInstance().getServiceId());
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
        return serviceTask != null ? this.converter.convertToServerGroup(serviceTask).toSimple() : null;
    }

    /**
     * Returns the ProxyGroup of the parameter
     *
     * @param group
     */
    public ProxyGroup getProxyGroupData(String group) {
        ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(group);
        return serviceTask != null ? this.converter.convertToProxyGroup(serviceTask) : null;
    }

    /**
     * Returns the global onlineCount
     */
    public int getOnlineCount() {
        return BridgePlayerManager.getInstance().getOnlineCount();
    }

    /**
     * Returns the amount of players that are registered in the Cloud
     */
    public int getRegisteredPlayerCount() {
        return (int) BridgePlayerManager.getInstance().getRegisteredCount();
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
        //CloudNetDriver.getInstance().getMessenger().sendChannelMessage(); todo CloudNet 3.3 with the ServiceEnvironments
    }

    /**
     * Sends the data of the custom channel message to all server
     */
    public void sendCustomSubServerMessage(String channel, String message, Document value) {
        //CloudNetDriver.getInstance().getMessenger().sendChannelMessage(); todo CloudNet 3.3 with the ServiceEnvironments
    }

    /**
     * Sends the data of the custom channel message to one server
     */
    public void sendCustomSubServerMessage(String channel, String message, Document value, String serverName) {
        CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByNameAsync(serverName).onComplete(serviceInfoSnapshot -> {
            if (serviceInfoSnapshot != null) {
                CloudNetDriver.getInstance().getMessenger().sendChannelMessage(serviceInfoSnapshot, channel, message, JsonDocument.newDocument(value.convertToJsonString()));
            }
        });
    }

    /**
     * Sends the data of the custom channel message to proxy server
     */
    public void sendCustomSubProxyMessage(String channel, String message, Document value, String serverName) {
        this.sendCustomSubServerMessage(channel, message, value, serverName);
    }

    /**
     * Update the server group
     *
     * @param serverGroup
     */
    public void updateServerGroup(ServerGroup serverGroup) {
        CloudNetDriver.getInstance().getServiceTaskProvider().addPermanentServiceTask(this.converter.convertToTask(serverGroup));
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
        CloudNetDriver.getInstance().getServiceTaskProvider().addPermanentServiceTask(this.converter.convertToTask(proxyGroup));
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
                .map(serviceTask -> this.converter.convertToServerGroup(serviceTask).toSimple())
                .collect(Collectors.toMap(SimpleServerGroup::getName, simpleServerGroup -> simpleServerGroup));
    }

    public Map<String, ProxyGroup> getProxyGroupMap() {
        return CloudNetDriver.getInstance().getServiceTaskProvider().getPermanentServiceTasks()
                .stream()
                .map(this.converter::convertToProxyGroup)
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
        CloudNetDriver.getInstance().getCloudServiceFactory().createCloudServiceAsync(this.converter.convertToTask(proxyGroup))
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
                this.converter.convertToIncludes(plugins),
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
                                this.converter.convertToIncludes(plugins),
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
                                this.converter.convertToIncludes(plugins),
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
        BridgePlayerManager.getInstance().updateOnlinePlayer(this.converter.convertToV3OnlinePlayer(cloudPlayer));
    }

    /**
     * Updates a offlinePlayer Objective on the database
     *
     * @param offlinePlayer
     */
    public void updatePlayer(OfflinePlayer offlinePlayer) {
        BridgePlayerManager.getInstance().updateOfflinePlayer(this.converter.convertToV3OfflinePlayer(offlinePlayer));
    }

    /**
     * Returns all servers on network
     */
    public Collection<ServerInfo> getServers() {
        return CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(ServiceEnvironmentType.MINECRAFT_SERVER)
                .stream()
                .map(this.converter::convertToServerInfo)
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
                .map(this.converter::convertProxyInfo)
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
                .map(this.converter::convertProxyInfo)
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
                    return this.converter.convertToV2OnlinePlayer(cloudPlayer, permissionUser);
                })
                .collect(Collectors.toList());
    }

    /**
     * Retunrs an online CloudPlayer on network or null if the player isn't online
     */
    public CloudPlayer getOnlinePlayer(UUID uniqueId) {
        ICloudPlayer cloudPlayer = BridgePlayerManager.getInstance().getOnlinePlayer(uniqueId);
        if (cloudPlayer == null) {
            return null;
        }

        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionProvider().getUser(cloudPlayer.getUniqueId());

        return this.converter.convertToV2OnlinePlayer(cloudPlayer, permissionUser);
    }

    /**
     * Retunrs an online CloudPlayer on network or null if the player isn't online
     */
    public CloudPlayer getOnlinePlayer(String name) {
        ICloudPlayer cloudPlayer = BridgePlayerManager.getInstance().getFirstOnlinePlayer(name);
        if (cloudPlayer == null) {
            return null;
        }

        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionProvider().getUser(cloudPlayer.getUniqueId());

        return this.converter.convertToV2OnlinePlayer(cloudPlayer, permissionUser);
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

        return this.converter.convertToV2OfflinePlayer(cloudPlayer, permissionUser);
    }

    /**
     * Returns a offline player which registerd or null
     *
     * @param name
     */
    public OfflinePlayer getOfflinePlayer(String name) {
        ICloudOfflinePlayer cloudPlayer = BridgePlayerManager.getInstance().getFirstOfflinePlayer(name);
        if (cloudPlayer == null) {
            return null;
        }

        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionProvider().getUser(cloudPlayer.getUniqueId());

        return this.converter.convertToV2OfflinePlayer(cloudPlayer, permissionUser);
    }

    /**
     * Returns the ServerGroup from the name or null
     *
     * @param name
     */
    public ServerGroup getServerGroup(String name) {
        ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(name);
        return serviceTask != null ? this.converter.convertToServerGroup(serviceTask) : null;
    }

    /**
     * Returns from a registerd Player the uniqueId or null if the player doesn't exists
     */
    public UUID getPlayerUniqueId(String name) {
        ICloudOfflinePlayer offlinePlayer = BridgePlayerManager.getInstance().getFirstOfflinePlayer(name);
        return offlinePlayer != null ? offlinePlayer.getUniqueId() : null;
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
        return serviceInfoSnapshot != null ? this.converter.convertToServerInfo(serviceInfoSnapshot) : null;
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
                .map(offlinePlayer -> this.converter.convertToV2OfflinePlayer(
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
