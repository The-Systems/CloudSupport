package eu.thesystems.cloud.proxy;

import java.util.Map;

public interface ProxyManagement {

    Map<String, ProxyMOTD[]> getMOTDs();

    ProxyMOTD[] getMOTDs(String targetProxyGroup);

    void addMOTD(String targetProxyGroup, ProxyMOTD motd);

    void updateMOTDs(String targetProxyGroup, ProxyMOTD[] motds);

    ProxyTabListConfig[] getTabListConfigs();

    ProxyTabListConfig getTabListConfig(String targetProxyGroup);

    void addTabListConfig(ProxyTabListConfig config);

    void updateTabListConfig(ProxyTabListConfig config);

}
