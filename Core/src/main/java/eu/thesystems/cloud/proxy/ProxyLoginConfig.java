package eu.thesystems.cloud.proxy;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode
public class ProxyLoginConfig {

    private String targetGroup;
    private boolean maintenance;
    private int maxPlayers;
    private List<String> whitelist;
    private List<ProxyMOTD> motds;
    private List<ProxyMOTD> maintenanceMotds;
    private boolean supportsMultipleMaintenanceMotds;

    public ProxyLoginConfig(String targetGroup, boolean maintenance, int maxPlayers, List<String> whitelist, List<ProxyMOTD> motds, List<ProxyMOTD> maintenanceMotds, boolean supportsMultipleMaintenanceMotds) {
        this.targetGroup = targetGroup;
        this.maintenance = maintenance;
        this.maxPlayers = maxPlayers;
        this.whitelist = whitelist;
        this.motds = motds;
        this.maintenanceMotds = maintenanceMotds;
        this.supportsMultipleMaintenanceMotds = supportsMultipleMaintenanceMotds;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    public List<ProxyMOTD> getMotds() {
        return motds;
    }

    public void setMotds(List<ProxyMOTD> motds) {
        this.motds = motds;
    }

    public List<ProxyMOTD> getMaintenanceMotds() {
        return maintenanceMotds;
    }

    public void setMaintenanceMotds(List<ProxyMOTD> maintenanceMotds) {
        this.maintenanceMotds = maintenanceMotds;
    }

    public boolean isSupportsMultipleMaintenanceMotds() {
        return supportsMultipleMaintenanceMotds;
    }

}
