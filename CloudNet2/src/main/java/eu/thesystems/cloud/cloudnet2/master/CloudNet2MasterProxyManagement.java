package eu.thesystems.cloud.cloudnet2.master;

import eu.thesystems.cloud.proxy.ProxyLoginConfig;
import eu.thesystems.cloud.proxy.ProxyManagement;
import eu.thesystems.cloud.proxy.ProxyTabListConfig;

public class CloudNet2MasterProxyManagement implements ProxyManagement {
    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public ProxyLoginConfig[] getLoginConfigs() {
        return new ProxyLoginConfig[0];
    }

    @Override
    public ProxyLoginConfig getLoginConfig(String targetProxyGroup) {
        return null;
    }

    @Override
    public void addLoginConfig(ProxyLoginConfig config) {

    }

    @Override
    public void updateLoginConfig(ProxyLoginConfig config) {

    }

    @Override
    public void updateLoginConfigs(ProxyLoginConfig[] configs) {

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
