package eu.thesystems.cloud.cloudnet3;

import com.google.common.base.Preconditions;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.ext.syncproxy.*;
import de.dytanic.cloudnet.ext.syncproxy.node.CloudNetSyncProxyModule;
import eu.thesystems.cloud.proxy.*;
import jdk.dynalink.NamedOperation;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CloudNet3ProxyManagement implements ProxyManagement {

    private boolean available;

    {
        try {
            Class.forName("de.dytanic.cloudnet.ext.syncproxy.SyncProxyConfigurationProvider");
            this.available = true;
        } catch (ClassNotFoundException ignored) {
            this.available = false;
        }
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    private void validateAvailable() {
        Preconditions.checkArgument(this.available, "The SyncProxyModule by CloudNet 3 is not loaded");
    }

    private SyncProxyProxyLoginConfiguration convertToCloudNetLoginConfig(ProxyLoginConfig config) {
        return config == null ? null : new SyncProxyProxyLoginConfiguration(
                config.getTargetGroup(),
                config.isMaintenance(),
                config.getMaxPlayers(),
                config.getWhitelist(),
                config.getMotds().stream().map(this::convertToCloudNetMOTD).collect(Collectors.toList()),
                config.getMaintenanceMotds().stream().map(this::convertToCloudNetMOTD).collect(Collectors.toList())
        );
    }

    private ProxyLoginConfig convertFromCloudNetLoginConfig(SyncProxyProxyLoginConfiguration configuration) {
        return configuration == null ? null : new ProxyLoginConfig(
                configuration.getTargetGroup(),
                configuration.isMaintenance(),
                configuration.getMaxPlayers(),
                configuration.getWhitelist(),
                configuration.getMotds().stream().map(this::convertFromCloudNetMOTD).collect(Collectors.toList()),
                configuration.getMaintenanceMotds().stream().map(this::convertFromCloudNetMOTD).collect(Collectors.toList()),
                true
        );
    }

    private SyncProxyMotd convertToCloudNetMOTD(ProxyMOTD motd) {
        return motd == null ? null : new SyncProxyMotd(
                motd.getFirstLine(), motd.getSecondLine(),
                motd.isAutoSlot(), motd.getAutoSlotDistance(),
                motd.getPlayerInfo(), motd.getProtocolText()
        );
    }

    private ProxyMOTD convertFromCloudNetMOTD(SyncProxyMotd motd) {
        return motd == null ? null : new ProxyMOTD(
                motd.getFirstLine(), motd.getSecondLine(),
                true,
                motd.getPlayerInfo(), motd.getProtocolText(),
                motd.isAutoSlot(), motd.getAutoSlotMaxPlayersDistance()
        );
    }

    private SyncProxyTabListConfiguration convertToCloudNetTabListConfig(ProxyTabListConfig config) {
        return config == null ? null : new SyncProxyTabListConfiguration(
                config.getTargetGroup(),
                Arrays.stream(config.getTabLists())
                        .map(proxyTabList -> new SyncProxyTabList(proxyTabList.getHeader(), proxyTabList.getFooter()))
                        .collect(Collectors.toList()),
                config.getAnimationsPerSecond()
        );
    }

    private ProxyTabListConfig convertFromCloudNetTabListConfig(SyncProxyTabListConfiguration configuration) {
        return configuration == null ? null : new ProxyTabListConfig(
                configuration.getTargetGroup(),
                configuration.getEntries().stream()
                        .map(syncProxyTabList -> new ProxyTabList(syncProxyTabList.getHeader(), syncProxyTabList.getFooter()))
                        .toArray(ProxyTabList[]::new),
                configuration.getAnimationsPerSecond()
        );
    }

    private SyncProxyConfiguration loadConfig() {
        return CloudNetDriver.getInstance().getDriverEnvironment() == DriverEnvironment.CLOUDNET ?
                CloudNetSyncProxyModule.getInstance().getSyncProxyConfiguration() :
                SyncProxyConfigurationProvider.load();
    }

    @Override
    public ProxyLoginConfig[] getLoginConfigs() {
        this.validateAvailable();

        return this.loadConfig().getLoginConfigurations().stream()
                .map(this::convertFromCloudNetLoginConfig)
                .filter(Objects::nonNull)
                .toArray(ProxyLoginConfig[]::new);
    }

    @Override
    public ProxyLoginConfig getLoginConfig(String targetProxyGroup) {
        this.validateAvailable();
        Preconditions.checkNotNull(targetProxyGroup, "targetProxyGroup");

        return this.loadConfig().getLoginConfigurations().stream()
                .filter(configuration -> configuration.getTargetGroup().equalsIgnoreCase(targetProxyGroup))
                .findFirst()
                .map(this::convertFromCloudNetLoginConfig)
                .orElse(null);
    }

    @Override
    public void addLoginConfig(ProxyLoginConfig config) {
        this.validateAvailable();
        Preconditions.checkNotNull(config, "config");

        this.updateConfig(syncProxyConfiguration -> syncProxyConfiguration.getLoginConfigurations().add(this.convertToCloudNetLoginConfig(config)));
    }

    @Override
    public void updateLoginConfig(ProxyLoginConfig config) {
        this.validateAvailable();
        Preconditions.checkNotNull(config, "config");

        this.updateConfig(syncProxyConfiguration -> {
            syncProxyConfiguration.getLoginConfigurations().removeIf(configuration -> configuration.getTargetGroup().equalsIgnoreCase(config.getTargetGroup()));
            syncProxyConfiguration.getLoginConfigurations().add(this.convertToCloudNetLoginConfig(config));
        });
    }

    @Override
    public void updateLoginConfigs(ProxyLoginConfig[] configs) {
        this.validateAvailable();
        Preconditions.checkNotNull(configs, "configs");

        this.updateConfig(syncProxyConfiguration -> syncProxyConfiguration.setLoginConfigurations(
                Arrays.stream(configs)
                        .map(this::convertToCloudNetLoginConfig)
                        .collect(Collectors.toList())
        ));
    }

    @Override
    public ProxyTabListConfig[] getTabListConfigs() {
        this.validateAvailable();

        return this.loadConfig().getTabListConfigurations()
                .stream()
                .map(this::convertFromCloudNetTabListConfig)
                .toArray(ProxyTabListConfig[]::new);
    }

    @Override
    public ProxyTabListConfig getTabListConfig(String targetProxyGroup) {
        this.validateAvailable();
        Preconditions.checkNotNull(targetProxyGroup, "targetProxyGroup");

        return this.loadConfig().getTabListConfigurations()
                .stream()
                .filter(configuration -> configuration.getTargetGroup().equalsIgnoreCase(targetProxyGroup))
                .findFirst()
                .map(this::convertFromCloudNetTabListConfig)
                .orElse(null);
    }

    @Override
    public void addTabListConfig(ProxyTabListConfig config) {
        this.validateAvailable();
        Preconditions.checkNotNull(config, "config");

        this.updateConfig(syncProxyConfiguration -> syncProxyConfiguration.getTabListConfigurations().add(this.convertToCloudNetTabListConfig(config)));
    }

    @Override
    public void updateTabListConfig(ProxyTabListConfig config) {
        this.validateAvailable();
        Preconditions.checkNotNull(config, "config");

        this.updateConfig(syncProxyConfiguration -> {
            syncProxyConfiguration.getTabListConfigurations().removeIf(configuration -> configuration.getTargetGroup().equalsIgnoreCase(config.getTargetGroup()));
            syncProxyConfiguration.getTabListConfigurations().add(this.convertToCloudNetTabListConfig(config));
        });
    }

    private void updateConfig(Consumer<SyncProxyConfiguration> modifier) {
        SyncProxyConfiguration configuration = this.loadConfig();

        modifier.accept(configuration);

        SyncProxyConfigurationWriterAndReader.write(configuration, CloudNetSyncProxyModule.getInstance().getConfigurationFile());

        CloudNetSyncProxyModule.getInstance().setSyncProxyConfiguration(configuration);
        CloudNetDriver.getInstance().getMessenger().sendChannelMessage(
                SyncProxyConstants.SYNC_PROXY_CHANNEL_NAME,
                SyncProxyConstants.SYNC_PROXY_UPDATE_CONFIGURATION,
                new JsonDocument("syncProxyConfiguration", configuration)
        );
    }

}
