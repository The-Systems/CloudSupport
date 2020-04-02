package eu.thesystems.cloud.cloudnet2.bridge;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.DefaultType;
import eu.thesystems.cloud.cloudnet2.CloudNet2;
import eu.thesystems.cloud.cloudnet2.bridge.database.CloudNet2BridgeDatabaseProvider;
import eu.thesystems.cloud.cloudnet2.network.PacketInMasterChannelMessage;
import eu.thesystems.cloud.cloudnet2.network.PacketOutMasterChannelMessage;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.exception.CloudSupportException;
import eu.thesystems.cloud.database.DatabaseProvider;
import eu.thesystems.cloud.info.ProxyGroup;
import eu.thesystems.cloud.info.ProxyInfo;
import eu.thesystems.cloud.info.ServerGroup;
import eu.thesystems.cloud.info.ServerInfo;
import eu.thesystems.cloud.permission.PermissionUser;
import eu.thesystems.cloud.modules.ModuleManager;
import eu.thesystems.cloud.proxy.ProxyManagement;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class CloudNet2Bridge extends CloudNet2 {

    private final CloudAPI cloudAPI = CloudAPI.getInstance();
    private final CloudNet2BridgeChannelMessenger channelMessenger = new CloudNet2BridgeChannelMessenger(this, this.cloudAPI);
    private final DatabaseProvider databaseProvider = new CloudNet2BridgeDatabaseProvider(this.cloudAPI, this);
    private final ProxyManagement proxyManagement = new CloudNet2BridgeProxyManagement();

    public CloudNet2Bridge(SupportedCloudSystem supportedCloudSystem) {
        super(supportedCloudSystem, "CloudNet2-Bridge");
        this.cloudAPI.getNetworkHandlerProvider().registerHandler(new CloudNet2BridgeEventCaller(this.getEventManager(), this, this.getConverter()));
        this.cloudAPI.getNetworkConnection().getPacketManager().registerHandler(PacketOutMasterChannelMessage.ID, PacketInMasterChannelMessage.class);
    }

    public CloudAPI getCloudAPI() {
        return this.cloudAPI;
    }

    @Override
    public String getOwnComponentName() {
        return this.cloudAPI.getServerId();
    }

    @Override
    public ModuleManager getModuleManager() {
        throw new CloudSupportException(this);
    }

    @Override
    public DatabaseProvider getDatabaseProvider() {
        return this.databaseProvider;
    }

    @Override
    public CloudNet2BridgeChannelMessenger getChannelMessenger() {
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
        return this.cloudAPI.getServers().stream().map(this.getConverter()::convertServerInfo).collect(Collectors.toList());
    }

    @Override
    public Collection<ProxyInfo> getProxies() {
        return this.cloudAPI.getProxys().stream().map(this.getConverter()::convertProxyInfo).collect(Collectors.toList());
    }

    @Override
    public ServerInfo getServer(String name) {
        return this.getConverter().convertServerInfo(this.cloudAPI.getServerInfo(name));
    }

    @Override
    public ProxyInfo getProxy(String name) {
        return this.getConverter().convertProxyInfo(this.cloudAPI.getProxys().stream().filter(proxyInfo -> proxyInfo.getServiceId().getServerId().equals(name)).findFirst().orElse(null));
    }

    @Override
    public Collection<ServerInfo> getServersByGroup(String group) {
        return this.cloudAPI.getServers(group).stream().map(this.getConverter()::convertServerInfo).collect(Collectors.toList());
    }

    @Override
    public Collection<ProxyInfo> getProxiesByGroup(String group) {
        return this.cloudAPI.getProxys(group).stream().map(this.getConverter()::convertProxyInfo).collect(Collectors.toList());
    }

    @Override
    public Collection<ServerGroup> getServerGroups() {
        return this.cloudAPI.getServerGroupMap().values().stream()
                .map(simpleServerGroup -> this.cloudAPI.getServerGroup(simpleServerGroup.getName()))
                .map(this.getConverter()::convertServerGroup)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProxyGroup> getProxyGroups() {
        return this.cloudAPI.getProxyGroupMap().values().stream()
                .map(this.getConverter()::convertProxyGroup)
                .collect(Collectors.toList());
    }

    @Override
    public ServerGroup getServerGroup(String name) {
        return this.getConverter().convertServerGroup(this.cloudAPI.getServerGroup(name));
    }

    @Override
    public ProxyGroup getProxyGroup(String name) {
        return this.getConverter().convertProxyGroup(this.cloudAPI.getProxyGroupData(name));
    }

    @Override
    public void sendCommandLine(String processName, String commandLine) {
        boolean isServer = this.cloudAPI.getServerInfo(processName) != null;
        this.cloudAPI.sendConsoleMessage(isServer ? DefaultType.BUKKIT : DefaultType.BUNGEE_CORD, processName, commandLine);
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
