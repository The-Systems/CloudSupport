package eu.thesystems.cloud.cloudnet2.master;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import eu.thesystems.cloud.ChannelMessenger;
import eu.thesystems.cloud.cloudnet2.CloudNet2;
import eu.thesystems.cloud.cloudnet2.master.command.CloudNet2MasterCommandMap;
import eu.thesystems.cloud.cloudnet2.master.database.CloudNet2MasterDatabaseProvider;
import eu.thesystems.cloud.cloudnet2.master.module.CloudNet2ModuleManager;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.global.command.CommandMap;
import eu.thesystems.cloud.global.database.DatabaseProvider;
import eu.thesystems.cloud.global.info.ProxyGroup;
import eu.thesystems.cloud.global.info.ProxyInfo;
import eu.thesystems.cloud.global.info.ServerGroup;
import eu.thesystems.cloud.global.info.ServerInfo;
import eu.thesystems.cloud.global.permission.PermissionUser;
import eu.thesystems.cloud.loader.CloudNet2MasterLoader;
import eu.thesystems.cloud.modules.ModuleManager;
import eu.thesystems.cloud.proxy.ProxyManagement;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class CloudNet2Master extends CloudNet2 {

    private final CloudNet cloudNet = CloudNet.getInstance();

    private final CommandMap commandMap = new CloudNet2MasterCommandMap(this);
    private final ChannelMessenger channelMessenger = new CloudNet2MasterChannelMessenger();
    private final ModuleManager moduleManager = new CloudNet2ModuleManager(this.cloudNet, this);
    private final DatabaseProvider databaseProvider = new CloudNet2MasterDatabaseProvider(this, this.cloudNet);
    private final ProxyManagement proxyManagement = new CloudNet2MasterProxyManagement(this.cloudNet);
    private CloudNet2MasterEventCaller eventCaller = new CloudNet2MasterEventCaller();

    public CloudNet2Master() {
        super(SupportedCloudSystem.CLOUDNET_2_MASTER, "CloudNet2-Master");
    }

    public void init(CloudNet2MasterLoader loader) {
        this.eventCaller.register(loader, this, this.getEventManager(), this.cloudNet);
    }

    public CloudNet getCloudNet() {
        return this.cloudNet;
    }

    @Override
    public CommandMap getCommandMap() {
        return this.commandMap;
    }

    @Override
    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    @Override
    public DatabaseProvider getDatabaseProvider() {
        return this.databaseProvider;
    }

    @Override
    public ChannelMessenger getChannelMessenger() {
        return this.channelMessenger;
    }

    @Override
    public ProxyManagement getProxyManagement() {
        return this.proxyManagement;
    }

    @Override
    public boolean distinguishesProxiesAndServers() {
        return true;
    }

    @Override
    public Collection<ServerInfo> getServers() {
        return this.cloudNet.getServers().values().stream()
                .map(minecraftServer -> this.getConverter().convertServerInfo(minecraftServer.getServerInfo()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProxyInfo> getProxies() {
        return this.cloudNet.getProxys().values().stream()
                .map(proxyServer -> this.getConverter().convertProxyInfo(proxyServer.getProxyInfo()))
                .collect(Collectors.toList());
    }

    @Override
    public ServerInfo getServer(String name) {
        MinecraftServer minecraftServer = this.cloudNet.getServer(name);
        return minecraftServer != null ? this.getConverter().convertServerInfo(minecraftServer.getServerInfo()) : null;
    }

    @Override
    public ProxyInfo getProxy(String name) {
        ProxyServer proxyServer = this.cloudNet.getProxy(name);
        return proxyServer != null ? this.getConverter().convertProxyInfo(proxyServer.getProxyInfo()) : null;
    }

    @Override
    public Collection<ServerInfo> getServersByGroup(String group) {
        return this.cloudNet.getServers(group).stream()
                .map(minecraftServer -> this.getConverter().convertServerInfo(minecraftServer.getServerInfo()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProxyInfo> getProxiesByGroup(String group) {
        return this.cloudNet.getProxys(group).stream()
                .map(proxyServer -> this.getConverter().convertProxyInfo(proxyServer.getProxyInfo()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ServerGroup> getServerGroups() {
        return this.cloudNet.getServerGroups().values().stream()
                .map(this.getConverter()::convertServerGroup)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProxyGroup> getProxyGroups() {
        return this.cloudNet.getProxyGroups().values().stream()
                .map(this.getConverter()::convertProxyGroup)
                .collect(Collectors.toList());
    }

    @Override
    public ServerGroup getServerGroup(String name) {
        return this.getConverter().convertServerGroup(this.cloudNet.getServerGroup(name));
    }

    @Override
    public ProxyGroup getProxyGroup(String name) {
        return this.getConverter().convertProxyGroup(this.cloudNet.getProxyGroup(name));
    }

    @Override
    public void sendCommandLine(String processName, String commandLine) {
        MinecraftServer minecraftServer = this.cloudNet.getServer(processName);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().writeServerCommand(commandLine, minecraftServer.getServerInfo());
        } else {
            ProxyServer proxyServer = this.cloudNet.getProxy(processName);
            if (proxyServer != null) {
                proxyServer.getWrapper().writeProxyCommand(commandLine, proxyServer.getProxyInfo());
            }
        }
    }

    @Override
    public PermissionUser getPermissionUser(String name) {
        return null;
    }

    @Override
    public PermissionUser getPermissionUser(UUID uniqueId) {
        return null;
    }

}
