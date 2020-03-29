package eu.thesystems.cloud;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.converter.CloudObjectConverter;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.event.EventManager;
import eu.thesystems.cloud.exception.CloudSupportException;
import eu.thesystems.cloud.global.command.CommandMap;
import eu.thesystems.cloud.global.database.DatabaseProvider;
import eu.thesystems.cloud.global.info.*;
import eu.thesystems.cloud.global.permission.PermissionUser;
import eu.thesystems.cloud.modules.ModuleManager;
import eu.thesystems.cloud.proxy.ProxyManagement;

import java.util.Collection;
import java.util.UUID;

public interface CloudSystem {

    SupportedCloudSystem getComponentType();

    /**
     * Gets the name of this cloud (e. g. "CloudNet3-Node")
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return the name of the cloud
     */
    String getName();

    /**
     * Gets the version of this cloud (e. g. "Tsunami 3.0.0-RELEASE-964f47f")
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return the version of the cloud
     */
    String getInstalledVersion();

    /**
     * Gets the command map of this cloud to register console commands.
     * <p>
     * This method is available on every cloud, but not on any spigot, bungee, etc.
     *
     * @return the {@link CommandMap} instance for this cloud
     */
    CommandMap getCommandMap();

    /**
     * Gets the module manager instance which is used to manage the modules loaded in the cloud
     * <p>
     * This method is available in CloudNet 2 Master; CloudNet 3 every component; ReformCloud 1 Controller and Client;
     * ReformCloud 2 Node, Controller and Client.
     *
     * @return the final instance of the module manager
     * @throws CloudSupportException if the selected cloud does not support modules on the current network component
     */
    ModuleManager getModuleManager();

    /**
     * Gets the event manager instance which is used to call and listen to events called on this component.
     * <p>
     * This method is available on every component of every cloud
     *
     * @return the final {@link EventManager} instance
     */
    EventManager getEventManager();

    /**
     * Gets the database provider instance which is used to manage the internal database of the cloud.
     * <p>
     * This method is available on every component of every cloud
     *
     * @return the final {@link EventManager} instance
     */
    DatabaseProvider getDatabaseProvider();

    /**
     * Gets the channel messenger which is used to send channel messages over the whole network.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return the final {@link ChannelMessenger} instance
     */
    ChannelMessenger getChannelMessenger();

    /**
     * Gets the proxy manager which is used to get and modify the motd and tablist configurations for the proxies.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return the final {@link ProxyManagement} instance
     */
    ProxyManagement getProxyManagement();

    /**
     * Gets the converter instance which converts all cloud-specific objects to our global objects
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return the final {@link CloudObjectConverter} instance
     */
    CloudObjectConverter getConverter();

    /**
     * Checks if this cloud distinguishes between servers and proxies.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return {@code true} if it does, {@code false} if not
     */
    boolean distinguishesProxiesAndServers();

    /**
     * Gets all processes of the servers/proxies in the network.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return a list with the infos of all processes in the network
     */
    Collection<ProcessInfo> getProcesses();

    /**
     * Gets all processes of the servers in the network.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return a list with the infos of all servers in the network
     * @see CloudSystem#distinguishesProxiesAndServers()
     */
    Collection<ServerInfo> getServers();

    /**
     * Gets all processes of the proxies in the network.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return a list with the infos of all proxies in the network
     * @see CloudSystem#distinguishesProxiesAndServers()
     */
    Collection<ProxyInfo> getProxies();

    /**
     * Gets the info of the server by the name.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @param name the name of the server
     * @return the server or null, if there is no server with that name in the network
     * @see CloudSystem#distinguishesProxiesAndServers()
     */
    ServerInfo getServer(String name);

    /**
     * Gets the info of the proxy by the name.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @param name the name of the proxy
     * @return the proxy or null, if there is no proxy with that name in the network
     * @see CloudSystem#distinguishesProxiesAndServers()
     */
    ProxyInfo getProxy(String name);

    /**
     * Gets the info of the process by the name.
     * <p>
     * This works on every component of every cloud.
     *
     * @param name the name of the process
     * @return the process or null, if there is no process with that name in the network
     */
    ProcessInfo getProcess(String name);

    /**
     * Gets all processes of the servers/proxies in the network by a specified group.
     * <p>
     * This works on every component of every cloud.
     *
     * @param group the name of the group
     * @return a list with the infos of all processes in the network
     * @see CloudSystem#distinguishesProxiesAndServers()
     */
    Collection<ProcessInfo> getProcessesByGroup(String group);

    /**
     * Gets all processes of the servers in the network by a specified group.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @param group the name of the group
     * @return a list with the infos of all servers in the network
     * @see CloudSystem#distinguishesProxiesAndServers()
     */
    Collection<ServerInfo> getServersByGroup(String group);

    /**
     * Gets all processes of the proxies in the network by a specified group.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @param group the name of the group
     * @return a list with the infos of all proxies in the network
     * @see CloudSystem#distinguishesProxiesAndServers()
     */
    Collection<ProxyInfo> getProxiesByGroup(String group);

    /**
     * Gets the info of all server groups in the cloud.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return a list with all server groups registered in the network
     * @see CloudSystem#distinguishesProxiesAndServers()
     */
    Collection<ServerGroup> getServerGroups();

    /**
     * Gets the info of all proxy groups in the cloud.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @return a list with all proxy groups registered in the network
     * @see CloudSystem#distinguishesProxiesAndServers()
     */
    Collection<ProxyGroup> getProxyGroups();

    /**
     * Gets the info of all groups in the cloud.
     * <p>
     * This works on every component of every cloud.
     *
     * @return a list with the info of all groups registered in the network
     */
    Collection<ProcessGroup> getGroups();

    /**
     * Gets the info of a server group by its name.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @param name the name of the group
     * @return the group object or null, if it does not exist
     */
    ServerGroup getServerGroup(String name);

    /**
     * Gets the info of a proxy group by its name.
     * <p>
     * This method is available on every component of every cloud.
     *
     * @param name the name of the group
     * @return the group object or null, if it does not exist
     */
    ProxyGroup getProxyGroup(String name);

    /**
     * Gets the info of a group by its name.
     * <p>
     * This works on every component of every cloud.
     *
     * @param name the name of the group
     * @return the group object or null, if it does not exist
     */
    ProcessGroup getGroup(String name);

    void sendCommandLine(String processName, String commandLine);

    PermissionUser getPermissionUser(String name);

    PermissionUser getPermissionUser(UUID uniqueId);

}
