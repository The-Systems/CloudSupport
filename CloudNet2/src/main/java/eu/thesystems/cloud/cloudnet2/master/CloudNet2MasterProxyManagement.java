package eu.thesystems.cloud.cloudnet2.master;

import eu.thesystems.cloud.proxy.ProxyMOTD;
import eu.thesystems.cloud.proxy.ProxyManagement;
import eu.thesystems.cloud.proxy.ProxyTabListConfig;

import java.util.Map;

public class CloudNet2MasterProxyManagement implements ProxyManagement {
    @Override
    public Map<String, ProxyMOTD[]> getMOTDs() {
        return null;
    }

    @Override
    public ProxyMOTD[] getMOTDs(String targetProxyGroup) {
        return new ProxyMOTD[0];
    }

    @Override
    public void addMOTD(String targetProxyGroup, ProxyMOTD motd) {

    }

    @Override
    public void updateMOTDs(String targetProxyGroup, ProxyMOTD[] motds) {

    }

    @Override
    public ProxyTabListConfig[] getTabListConfigs() {
        return new ProxyTabListConfig[0];
    }

    @Override
    public ProxyTabListConfig getTabListConfig(String targetProxyGroup) {
        return null;
    }

    @Override
    public void addTabListConfig(ProxyTabListConfig config) {

    }

    @Override
    public void updateTabListConfig(ProxyTabListConfig config) {

    }
}
