package eu.thesystems.cloud.reformcloud1.velocity;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.global.database.DatabaseProvider;
import eu.thesystems.cloud.global.info.*;
import eu.thesystems.cloud.global.permission.PermissionUser;
import eu.thesystems.cloud.modules.ModuleManager;
import eu.thesystems.cloud.reformcloud1.ReformCloud1;

import java.util.Collection;
import java.util.UUID;

public class ReformCloud1Velocity extends ReformCloud1 {
    public ReformCloud1Velocity() {
        super(SupportedCloudSystem.REFORMCLOUD_1_VELOCITY, "ReformCloud1-Velocity");
    }

    @Override
    public ModuleManager getModuleManager() {
        return null;
    }

    @Override
    public DatabaseProvider getDatabaseProvider() {
        return null;
    }

    @Override
    public Collection<ServerInfo> getServers() {
        return null;
    }

    @Override
    public Collection<ProxyInfo> getProxies() {
        return null;
    }

    @Override
    public ServerInfo getServer(String name) {
        return null;
    }

    @Override
    public ProxyInfo getProxy(String name) {
        return null;
    }

    @Override
    public ProcessInfo getProcess(String name) {
        return null;
    }

    @Override
    public Collection<ServerInfo> getServersByGroup(String group) {
        return null;
    }

    @Override
    public Collection<ProxyInfo> getProxiesByGroup(String group) {
        return null;
    }

    @Override
    public Collection<ServerGroup> getServerGroups() {
        return null;
    }

    @Override
    public Collection<ProxyGroup> getProxyGroups() {
        return null;
    }

    @Override
    public ServerGroup getServerGroup(String name) {
        return null;
    }

    @Override
    public ProxyGroup getProxyGroup(String name) {
        return null;
    }

    @Override
    public void sendCommandLine(String processName, String commandLine) {

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
