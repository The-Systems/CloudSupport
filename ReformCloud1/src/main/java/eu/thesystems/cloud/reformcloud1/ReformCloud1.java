package eu.thesystems.cloud.reformcloud1;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.cloudsystem.reformcloud.ReformCloud;
import eu.thesystems.cloud.converter.CloudObjectConverter;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.global.info.ProcessGroup;
import eu.thesystems.cloud.global.info.ProcessInfo;
import systems.reformcloud.utility.StringUtil;

import java.util.Collection;

public abstract class ReformCloud1 extends ReformCloud {

    private final CloudObjectConverter cloudObjectConverter = null; //todo

    public ReformCloud1(SupportedCloudSystem componentType, String name) {
        super(componentType, name, StringUtil.REFORM_VERSION);
    }

    @Override
    public boolean distinguishesProxiesAndServers() {
        return true;
    }

    @Override
    public CloudObjectConverter getConverter() {
        return this.cloudObjectConverter;
    }

    @Override
    public Collection<ProcessGroup> getGroups() {
        return null;
    }

    @Override
    public Collection<ProcessInfo> getProcesses() {
        return null;
    }

    @Override
    public Collection<ProcessInfo> getProcessesByGroup(String group) {
        return null;
    }

    @Override
    public ProcessGroup getGroup(String name) {
        return null;
    }

}
