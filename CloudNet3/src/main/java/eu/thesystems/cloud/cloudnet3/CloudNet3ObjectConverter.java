package eu.thesystems.cloud.cloudnet3;
/*
 * Created by derrop on 25.10.2019
 */

import com.google.gson.Gson;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.module.ModuleConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.player.ICloudOfflinePlayer;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.NetworkConnectionInfo;
import de.dytanic.cloudnet.ext.bridge.player.ServicePlayer;
import eu.thesystems.cloud.cloudnet3.player.CloudNet3Player;
import eu.thesystems.cloud.converter.CloudObjectConverter;
import eu.thesystems.cloud.info.*;
import eu.thesystems.cloud.modules.DefaultModuleInfo;
import eu.thesystems.cloud.modules.ModuleInfo;
import eu.thesystems.cloud.network.NetworkAddress;
import eu.thesystems.cloud.player.OfflinePlayer;
import eu.thesystems.cloud.player.OnlinePlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class CloudNet3ObjectConverter implements CloudObjectConverter {

    private final Gson gson = new Gson();
    private CloudNet3 cloudNet3;

    public CloudNet3ObjectConverter(CloudNet3 cloudNet3) {
        this.cloudNet3 = cloudNet3;
    }

    @Override
    public ProcessInfo convertProcessInfo(Object cloudProcessInfo) {
        if (cloudProcessInfo == null) {
            return null;
        }
        ServiceInfoSnapshot serviceInfoSnapshot = (ServiceInfoSnapshot) cloudProcessInfo;
        Collection<ServicePlayer> players = serviceInfoSnapshot.getProperty(BridgeServiceProperty.PLAYERS).orElse(Collections.emptyList());
        return new ProcessInfo(
                serviceInfoSnapshot.getServiceId().getTaskName(),
                serviceInfoSnapshot.getServiceId().getName(),
                serviceInfoSnapshot.getServiceId().getUniqueId().toString(),
                serviceInfoSnapshot.getServiceId().getNodeUniqueId(),
                new NetworkAddress(serviceInfoSnapshot.getAddress().getHost(), serviceInfoSnapshot.getAddress().getPort()),
                Arrays.stream(serviceInfoSnapshot.getConfiguration().getTemplates()).map(this::mapTemplate).collect(Collectors.toList()),
                players.stream().map(ServicePlayer::getName).collect(Collectors.toList()),
                this.gson.toJsonTree(serviceInfoSnapshot).getAsJsonObject()
        );
    }

    @Override
    public ServerInfo convertServerInfo(Object cloudServerInfo) {
        if (cloudServerInfo == null) {
            return null;
        }
        ServiceInfoSnapshot serviceInfoSnapshot = (ServiceInfoSnapshot) cloudServerInfo;
        if (serviceInfoSnapshot.getConfiguration().getProcessConfig().getEnvironment().isMinecraftJavaServer() ||
                serviceInfoSnapshot.getConfiguration().getProcessConfig().getEnvironment().isMinecraftBedrockServer()) {
            Collection<ServicePlayer> players = serviceInfoSnapshot.getProperty(BridgeServiceProperty.PLAYERS).orElse(Collections.emptyList());
            return new ServerInfo(
                    serviceInfoSnapshot.getServiceId().getTaskName(),
                    serviceInfoSnapshot.getServiceId().getName(),
                    serviceInfoSnapshot.getServiceId().getUniqueId().toString(),
                    serviceInfoSnapshot.getServiceId().getNodeUniqueId(),
                    new NetworkAddress(serviceInfoSnapshot.getAddress().getHost(), serviceInfoSnapshot.getAddress().getPort()),
                    Arrays.stream(serviceInfoSnapshot.getConfiguration().getTemplates()).map(this::mapTemplate).collect(Collectors.toList()),
                    players.stream().map(ServicePlayer::getName).collect(Collectors.toList()),
                    serviceInfoSnapshot.getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(0),
                    this.gson.toJsonTree(serviceInfoSnapshot).getAsJsonObject(),
                    serviceInfoSnapshot.getProperty(BridgeServiceProperty.MOTD).orElse(null),
                    serviceInfoSnapshot.getProperty(BridgeServiceProperty.STATE).orElse(null)
            );
        }
        return null;
    }

    @Override
    public ServerGroup convertServerGroup(Object cloudServerGroup) {
        if (cloudServerGroup == null) {
            return null;
        }
        ServiceTask task = (ServiceTask) cloudServerGroup;
        if (task.getProcessConfiguration().getEnvironment().isMinecraftJavaServer() ||
                task.getProcessConfiguration().getEnvironment().isMinecraftBedrockServer()) {
            return new ServerGroup(
                    task.getName(),
                    task.getAssociatedNodes(),
                    task.getProcessConfiguration().getMaxHeapMemorySize(),
                    task.getMinServiceCount(),
                    task.getProcessConfiguration().getEnvironment().toString(),
                    task.getTemplates().stream().map(this::mapTemplate).collect(Collectors.toList()),
                    this.gson.toJsonTree(task).getAsJsonObject()
            );
        }
        return null;
    }

    @Override
    public ProcessGroup convertProcessGroup(Object cloudProcessGroup) {
        if (cloudProcessGroup == null) {
            return null;
        }
        ServiceTask task = (ServiceTask) cloudProcessGroup;
        return new ProcessGroup(
                task.getName(),
                task.getAssociatedNodes(),
                task.getProcessConfiguration().getMaxHeapMemorySize(),
                task.getMinServiceCount(),
                task.getProcessConfiguration().getEnvironment().toString(),
                task.getTemplates().stream().map(this::mapTemplate).collect(Collectors.toList()),
                this.gson.toJsonTree(task).getAsJsonObject()
        );
    }

    @Override
    public ProxyInfo convertProxyInfo(Object cloudProxyInfo) {
        if (cloudProxyInfo == null) {
            return null;
        }
        ServiceInfoSnapshot serviceInfoSnapshot = (ServiceInfoSnapshot) cloudProxyInfo;
        if (serviceInfoSnapshot.getConfiguration().getProcessConfig().getEnvironment().isMinecraftJavaProxy() ||
                serviceInfoSnapshot.getConfiguration().getProcessConfig().getEnvironment().isMinecraftBedrockProxy()) {
            Collection<ServicePlayer> players = serviceInfoSnapshot.getProperty(BridgeServiceProperty.PLAYERS).orElse(Collections.emptyList());
            return new ProxyInfo(
                    serviceInfoSnapshot.getServiceId().getTaskName(),
                    serviceInfoSnapshot.getServiceId().getName(),
                    serviceInfoSnapshot.getServiceId().getUniqueId().toString(),
                    serviceInfoSnapshot.getServiceId().getNodeUniqueId(),
                    new NetworkAddress(serviceInfoSnapshot.getAddress().getHost(), serviceInfoSnapshot.getAddress().getPort()),
                    Arrays.stream(serviceInfoSnapshot.getConfiguration().getTemplates()).map(this::mapTemplate).collect(Collectors.toList()),
                    players.stream().map(ServicePlayer::getName).collect(Collectors.toList()),
                    this.gson.toJsonTree(serviceInfoSnapshot).getAsJsonObject()
            );
        }
        return null;
    }

    @Override
    public ProxyGroup convertProxyGroup(Object cloudProxyGroup) {
        if (cloudProxyGroup == null) {
            return null;
        }
        ServiceTask task = (ServiceTask) cloudProxyGroup;
        if (task.getProcessConfiguration().getEnvironment().isMinecraftJavaProxy() ||
                task.getProcessConfiguration().getEnvironment().isMinecraftBedrockProxy()) {
            return new ProxyGroup(
                    task.getName(),
                    task.getAssociatedNodes(),
                    task.getProcessConfiguration().getMaxHeapMemorySize(),
                    task.getMinServiceCount(),
                    task.getProcessConfiguration().getEnvironment().toString(),
                    task.getTemplates().stream().map(this::mapTemplate).collect(Collectors.toList()),
                    this.gson.toJsonTree(task).getAsJsonObject(),
                    task.getStartPort()
            );
        }
        return null;
    }

    private Template mapTemplate(ServiceTemplate cloudnetTemplate) {
        return new Template(cloudnetTemplate.getPrefix(), cloudnetTemplate.getName(), cloudnetTemplate.getTemplatePath(), this.gson.toJsonTree(cloudnetTemplate).getAsJsonObject());
    }

    @Override
    public ModuleInfo convertModuleInfo(Object cloudModuleInfo) {
        ModuleConfiguration moduleConfiguration = (ModuleConfiguration) cloudModuleInfo;
        return new DefaultModuleInfo(moduleConfiguration.getName(), new String[]{moduleConfiguration.getAuthor()}, moduleConfiguration.getVersion(), moduleConfiguration.getMain(), null, this.gson.toJsonTree(moduleConfiguration).getAsJsonObject());
    }

    @Override
    public OnlinePlayer convertOnlinePlayer(Object cloudPlayer) {
        if (cloudPlayer instanceof ICloudPlayer) {
            ICloudPlayer player = (ICloudPlayer) cloudPlayer;
            return new CloudNet3Player(
                    this.cloudNet3,
                    player.getUniqueId(),
                    player.getName(),
                    new NetworkAddress(
                            player.getLastNetworkConnectionInfo().getAddress().getHost(),
                            player.getLastNetworkConnectionInfo().getAddress().getPort()
                    ),
                    player.getFirstLoginTimeMillis(),
                    player.getLastLoginTimeMillis(),
                    new NetworkAddress(
                            player.getNetworkConnectionInfo().getAddress().getHost(),
                            player.getNetworkConnectionInfo().getAddress().getPort()
                    )
            );
        } else if (cloudPlayer instanceof NetworkConnectionInfo) {
            NetworkConnectionInfo connectionInfo = (NetworkConnectionInfo) cloudPlayer;
            return new CloudNet3Player(
                    this.cloudNet3,
                    connectionInfo.getUniqueId(),
                    connectionInfo.getName(),
                    new NetworkAddress(
                            connectionInfo.getAddress().getHost(),
                            connectionInfo.getAddress().getPort()
                    ),
                    -1,
                    -1,
                    null
            );
        }
        return null;
    }

    @Override
    public OfflinePlayer convertOfflinePlayer(Object cloudOfflinePlayer) {
        ICloudOfflinePlayer player = (ICloudOfflinePlayer) cloudOfflinePlayer;
        return new OfflinePlayer(
                player.getUniqueId(),
                player.getName(),
                new NetworkAddress(
                        player.getLastNetworkConnectionInfo().getAddress().getHost(),
                        player.getLastNetworkConnectionInfo().getAddress().getPort()
                ),
                player.getFirstLoginTimeMillis(),
                player.getLastLoginTimeMillis()
        );
    }

    @Override
    public boolean isOnlinePlayer(Object cloudPlayer) {
        return cloudPlayer instanceof ICloudPlayer || cloudPlayer instanceof OnlinePlayer;
    }
}
