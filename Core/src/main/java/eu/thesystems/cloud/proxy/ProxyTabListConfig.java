package eu.thesystems.cloud.proxy;

public class ProxyTabListConfig {

    private String targetGroup;
    private ProxyTabList[] tabLists;
    private double animationsPerSecond;

    public ProxyTabListConfig(String targetGroup, ProxyTabList[] tabLists, double animationsPerSecond) {
        this.targetGroup = targetGroup;
        this.tabLists = tabLists;
        this.animationsPerSecond = animationsPerSecond;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public ProxyTabList[] getTabLists() {
        return tabLists;
    }

    public double getAnimationsPerSecond() {
        return animationsPerSecond;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public void setTabLists(ProxyTabList[] tabLists) {
        this.tabLists = tabLists;
    }

    public void setAnimationsPerSecond(double animationsPerSecond) {
        this.animationsPerSecond = animationsPerSecond;
    }
}
