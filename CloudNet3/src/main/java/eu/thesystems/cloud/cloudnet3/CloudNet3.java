package eu.thesystems.cloud.cloudnet3;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import eu.thesystems.cloud.CloudSystem;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketProvider;
import eu.thesystems.cloud.cloudnet3.module.CloudNet3ModuleManager;
import eu.thesystems.cloud.cloudsystem.cloudnet.CloudNet;
import eu.thesystems.cloud.converter.CloudObjectConverter;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.event.EventManager;
import eu.thesystems.cloud.event.defaults.DefaultEventManager;
import eu.thesystems.cloud.info.*;
import eu.thesystems.cloud.permission.PermissionUser;
import eu.thesystems.cloud.modules.ModuleManager;
import eu.thesystems.cloud.proxy.ProxyManagement;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This is the implementation for CloudNet 3.
 * <p>
 * In CloudNet 3, what I called "groups" in {@link CloudSystem}, are "tasks",
 * and the "processes" are the "services"
 */
public abstract class CloudNet3 extends CloudNet {

    private final CloudNetDriver cloudNetDriver = CloudNetDriver.getInstance();
    private final CloudObjectConverter cloudObjectConverter = new CloudNet3ObjectConverter(this.cloudNetDriver, this);

    private final EventManager eventManager = new DefaultEventManager();
    private final ProxyManagement proxyManagement = new CloudNet3ProxyManagement();
    private final ModuleManager moduleManager = new CloudNet3ModuleManager(this, this.cloudNetDriver.getModuleProvider());

    public CloudNet3(SupportedCloudSystem componentType, String name, String version, IPlayerManager playerManager) {
        super(componentType, name, version);
        this.cloudNetDriver.getEventManager().registerListener(new CloudNet3EventCaller(this, this.eventManager, playerManager));
    }

    public abstract ClusterPacketProvider getClusterPacketProvider();

    public CloudNetDriver getCloudNetDriver() {
        return this.cloudNetDriver;
    }

    @Override
    public boolean distinguishesProxiesAndServers() {
        return false;
    }

    @Override
    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public ProxyManagement getProxyManagement() {
        return this.proxyManagement;
    }

    @Override
    public abstract CloudNet3ChannelMessenger getChannelMessenger();

    @Override
    public CloudObjectConverter getConverter() {
        return this.cloudObjectConverter;
    }

    @Override
    public ProcessInfo getProcess(String name) {
        return this.getConverter().convertProcessInfo(this.cloudNetDriver.getCloudServiceProvider().getCloudServiceByName(name));
    }

    @Override
    public Collection<ProcessInfo> getProcessesByGroup(String task) {
        return this.cloudNetDriver.getCloudServiceProvider().getCloudServices(task).stream()
                .map(this.getConverter()::convertProcessInfo)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProcessInfo> getProcesses() {
        return this.cloudNetDriver.getCloudServiceProvider().getCloudServices().stream()
                .map(this.getConverter()::convertProcessInfo)
                .collect(Collectors.toList());
    }

    @Override
    public ProcessGroup getGroup(String name) {
        return this.getConverter().convertProcessGroup(this.cloudNetDriver.getServiceTaskProvider().getServiceTask(name));
    }

    @Override
    public Collection<ProcessGroup> getGroups() {
        return this.cloudNetDriver.getServiceTaskProvider().getPermanentServiceTasks().stream()
                .map(this.getConverter()::convertProcessGroup)
                .collect(Collectors.toList());
    }

    @Override
    public ServerInfo getServer(String name) {
        return this.getConverter().convertServerInfo(this.cloudNetDriver.getCloudServiceProvider().getCloudServiceByName(name));
    }

    @Override
    public ProxyInfo getProxy(String name) {
        return this.getConverter().convertProxyInfo(this.cloudNetDriver.getCloudServiceProvider().getCloudServiceByName(name));
    }

    @Override
    public Collection<ServerInfo> getServers() {
        return this.cloudNetDriver.getCloudServiceProvider().getCloudServices()
                .stream()
                .map(this.getConverter()::convertServerInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProxyInfo> getProxies() {
        return this.cloudNetDriver.getCloudServiceProvider().getCloudServices()
                .stream()
                .map(this.getConverter()::convertProxyInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ServerInfo> getServersByGroup(String group) {
        return this.cloudNetDriver.getCloudServiceProvider().getCloudServices(group)
                .stream()
                .map(this.getConverter()::convertServerInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProxyInfo> getProxiesByGroup(String group) {
        return this.cloudNetDriver.getCloudServiceProvider().getCloudServices(group)
                .stream()
                .map(this.getConverter()::convertProxyInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ServerGroup> getServerGroups() {
        return this.cloudNetDriver.getServiceTaskProvider().getPermanentServiceTasks()
                .stream()
                .map(this.getConverter()::convertServerGroup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ProxyGroup> getProxyGroups() {
        return this.cloudNetDriver.getServiceTaskProvider().getPermanentServiceTasks()
                .stream()
                .map(this.getConverter()::convertProxyGroup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public ServerGroup getServerGroup(String name) {
        return this.getConverter().convertServerGroup(this.cloudNetDriver.getServiceTaskProvider().getServiceTask(name));
    }

    @Override
    public ProxyGroup getProxyGroup(String name) {
        return this.getConverter().convertProxyGroup(this.cloudNetDriver.getServiceTaskProvider().getServiceTask(name));
    }

    @Override
    public PermissionUser getPermissionUser(String name) {
        List<IPermissionUser> users = this.cloudNetDriver.getPermissionProvider().getUsers(name);
        return users.isEmpty() ? null : this.getConverter().convertPermissionUser(users.get(0));
    }

    @Override
    public PermissionUser getPermissionUser(UUID uniqueId) {
        return this.getConverter().convertPermissionUser(this.cloudNetDriver.getPermissionProvider().getUser(uniqueId));
    }

    @Override
    public void sendCommandLine(String processName, String commandLine) {
        this.cloudNetDriver.getCloudServiceProvider(processName).runCommand(commandLine);
    }
}
