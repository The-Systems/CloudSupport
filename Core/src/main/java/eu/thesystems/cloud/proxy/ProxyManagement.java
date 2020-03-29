package eu.thesystems.cloud.proxy;

public interface ProxyManagement {

    boolean isAvailable();

    ProxyLoginConfig[] getLoginConfigs();

    ProxyLoginConfig getLoginConfig(String targetProxyGroup);

    void addLoginConfig(ProxyLoginConfig config);

    void updateLoginConfig(ProxyLoginConfig config);

    void updateLoginConfigs(ProxyLoginConfig[] configs);

    ProxyTabListConfig[] getTabListConfigs();

    ProxyTabListConfig getTabListConfig(String targetProxyGroup);

    void addTabListConfig(ProxyTabListConfig config);

    void updateTabListConfig(ProxyTabListConfig config);

}
