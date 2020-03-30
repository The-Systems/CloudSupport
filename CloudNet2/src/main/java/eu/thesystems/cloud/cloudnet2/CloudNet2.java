package eu.thesystems.cloud.cloudnet2;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.lib.NetworkUtils;
import eu.thesystems.cloud.cloudsystem.cloudnet.CloudNet;
import eu.thesystems.cloud.converter.CloudObjectConverter;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.global.info.ProcessGroup;
import eu.thesystems.cloud.global.info.ProcessInfo;
import eu.thesystems.cloud.global.info.ProxyGroup;
import eu.thesystems.cloud.global.info.ServerInfo;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is the implementation of CloudNet 2.
 */
public abstract class CloudNet2 extends CloudNet {

    public static final String CLOUD_SUPPORT_CHANNEL = "CloudSupport";

    private final CloudObjectConverter cloudObjectConverter = new CloudNet2ObjectConverter();

    public CloudNet2(SupportedCloudSystem componentType, String name) {
        super(componentType, name, NetworkUtils.class.getPackage().getSpecificationVersion() + " #" + NetworkUtils.class.getPackage().getImplementationVersion());
    }

    @Override
    public CloudObjectConverter getConverter() {
        return this.cloudObjectConverter;
    }

    @Override
    public Collection<ProcessInfo> getProcesses() {
        Collection<ProcessInfo> infos = new ArrayList<>();
        infos.addAll(this.getProxies());
        infos.addAll(this.getServers());
        return infos;
    }

    @Override
    public Collection<ProcessInfo> getProcessesByGroup(String group) {
        Collection<ProcessInfo> infos = new ArrayList<>();
        infos.addAll(this.getProxiesByGroup(group));
        infos.addAll(this.getServersByGroup(group));
        return infos;
    }

    @Override
    public Collection<ProcessGroup> getGroups() {
        Collection<ProcessGroup> groups = new ArrayList<>();
        groups.addAll(this.getServerGroups());
        groups.addAll(this.getProxyGroups());
        return groups;
    }

    @Override
    public ProcessGroup getGroup(String name) {
        ProxyGroup proxyGroup = this.getProxyGroup(name);
        return proxyGroup != null ? proxyGroup : this.getServerGroup(name);
    }

    @Override
    public ProcessInfo getProcess(String name) {
        ServerInfo serverInfo = this.getServer(name);
        return serverInfo != null ? serverInfo : this.getProxy(name);
    }
}
