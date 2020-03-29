package eu.thesystems.cloud.cloudnet2.master;

import com.google.common.base.Preconditions;
import de.dytanic.cloudnet.lib.proxylayout.AutoSlot;
import de.dytanic.cloudnet.lib.proxylayout.Motd;
import de.dytanic.cloudnet.lib.proxylayout.TabList;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnetcore.CloudNet;
import eu.thesystems.cloud.cloudnet2.CloudNet2ProxyManagement;
import eu.thesystems.cloud.proxy.ProxyLoginConfig;
import eu.thesystems.cloud.proxy.ProxyMOTD;
import eu.thesystems.cloud.proxy.ProxyTabListConfig;

import java.util.stream.Collectors;

public class CloudNet2MasterProxyManagement extends CloudNet2ProxyManagement {

    private CloudNet cloudNet;

    public CloudNet2MasterProxyManagement(CloudNet cloudNet) {
        this.cloudNet = cloudNet;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public ProxyLoginConfig[] getLoginConfigs() {
        return this.cloudNet.getProxyGroups().values().stream()
                .map(super::convertFromCloudNetLoginConfig)
                .toArray(ProxyLoginConfig[]::new);
    }

    @Override
    public ProxyLoginConfig getLoginConfig(String targetProxyGroup) {
        Preconditions.checkNotNull(targetProxyGroup, "targetProxyGroup");

        return super.convertFromCloudNetLoginConfig(this.cloudNet.getProxyGroup(targetProxyGroup));
    }

    @Override
    public void addLoginConfig(ProxyLoginConfig config) {
        Preconditions.checkNotNull(config, "config");

        ProxyGroup proxyGroup = CloudNet.getInstance().getProxyGroup(config.getTargetGroup());
        if (proxyGroup == null) {
            proxyGroup = super.createDefaultProxyGroup(config.getTargetGroup());
        }

        this.updateLoginConfig(proxyGroup, config);
    }

    @Override
    public void updateLoginConfig(ProxyLoginConfig config) {
        Preconditions.checkNotNull(config, "config");

        ProxyGroup proxyGroup = CloudNet.getInstance().getProxyGroup(config.getTargetGroup());
        if (proxyGroup != null) {
            this.updateLoginConfig(proxyGroup, config);
        }
    }

    private void updateLoginConfig(ProxyGroup proxyGroup, ProxyLoginConfig config) {
        proxyGroup.getProxyConfig().setEnabled(true);
        proxyGroup.getProxyConfig().setMaintenance(config.isMaintenance());
        proxyGroup.getProxyConfig().setMotdsLayouts(config.getMotds().stream()
                .map(motd -> new Motd(motd.getFirstLine(), motd.getSecondLine()))
                .collect(Collectors.toList())
        );

        if (!config.getMotds().isEmpty()) {
            ProxyMOTD motd = config.getMotds().get(0);
            proxyGroup.getProxyConfig().setPlayerInfo(motd.getPlayerInfo());
            proxyGroup.getProxyConfig().setAutoSlot(new AutoSlot(motd.getAutoSlotDistance(), motd.isAutoSlot()));
        }

        if (config.getMaintenanceMotds().isEmpty()) {
            proxyGroup.getProxyConfig().setMaintenanceMotdLayout(new Motd("", ""));
            proxyGroup.getProxyConfig().setMaintenaceProtocol("§cMaintenance");
        } else {
            ProxyMOTD motd = config.getMaintenanceMotds().get(0);
            proxyGroup.getProxyConfig().setMaintenanceMotdLayout(new Motd(motd.getFirstLine(), motd.getSecondLine()));
            proxyGroup.getProxyConfig().setMaintenaceProtocol(motd.getProtocolText());
            if (config.getMotds().isEmpty()) {
                proxyGroup.getProxyConfig().setPlayerInfo(motd.getPlayerInfo());
            }
        }

        this.cloudNet.getConfig().createGroup(proxyGroup);
    }

    @Override
    public void updateLoginConfigs(ProxyLoginConfig[] configs) {
        Preconditions.checkNotNull(configs, "configs");

        for (ProxyLoginConfig config : configs) {
            this.updateLoginConfig(config);
        }
    }

    @Override
    public ProxyTabListConfig[] getTabListConfigs() {
        return this.cloudNet.getProxyGroups().values().stream()
                .map(proxyGroup -> super.convertFromCloudNetTabList(proxyGroup.getName(), proxyGroup.getProxyConfig().getTabList()))
                .toArray(ProxyTabListConfig[]::new);
    }

    @Override
    public ProxyTabListConfig getTabListConfig(String targetProxyGroup) {
        Preconditions.checkNotNull(targetProxyGroup, "targetProxyGroup");

        ProxyGroup proxyGroup = this.cloudNet.getProxyGroup(targetProxyGroup);
        return proxyGroup != null && proxyGroup.getProxyConfig().isEnabled() ? super.convertFromCloudNetTabList(proxyGroup.getName(), proxyGroup.getProxyConfig().getTabList()) : null;
    }

    @Override
    public void addTabListConfig(ProxyTabListConfig config) {
        Preconditions.checkNotNull(config, "config");

        ProxyGroup proxyGroup = this.cloudNet.getProxyGroup(config.getTargetGroup());
        if (proxyGroup == null) {
            proxyGroup = super.createDefaultProxyGroup(config.getTargetGroup());
        }

        this.updateTabListConfig(proxyGroup, config);
    }

    @Override
    public void updateTabListConfig(ProxyTabListConfig config) {
        Preconditions.checkNotNull(config, "config");

        ProxyGroup proxyGroup = this.cloudNet.getProxyGroup(config.getTargetGroup());
        if (proxyGroup != null) {
            this.updateTabListConfig(proxyGroup, config);
        }
    }

    private void updateTabListConfig(ProxyGroup proxyGroup, ProxyTabListConfig config) {
        proxyGroup.getProxyConfig().setEnabled(true);
        proxyGroup.getProxyConfig().setTabList(config.getTabLists().length == 0 ?
                new TabList(false, "", "") :
                new TabList(true, config.getTabLists()[0].getHeader(), config.getTabLists()[0].getFooter())
        );

        this.cloudNet.getConfig().createGroup(proxyGroup);
    }

}
