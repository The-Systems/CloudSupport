package eu.thesystems.cloud.proxy;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ProxyTabListConfig {

    private String targetGroup;
    private ProxyTabList[] tabLists;
    private double animationsPerSecond;
    private boolean supportsMultipleTabLists;

    public ProxyTabListConfig(String targetGroup, ProxyTabList[] tabLists, double animationsPerSecond, boolean supportsMultipleTabLists) {
        this.targetGroup = targetGroup;
        this.tabLists = tabLists;
        this.animationsPerSecond = animationsPerSecond;
        this.supportsMultipleTabLists = supportsMultipleTabLists;
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

    public boolean supportsMultipleTabLists() {
        return supportsMultipleTabLists;
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
