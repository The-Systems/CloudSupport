package de.dytanic.cloudnet.bridge;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.ICloudService;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.ServiceInfoSnapshotUtil;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.wrapper.Wrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;

public class CloudServer implements ICloudService {

    private Plugin plugin;

    public static CloudServer getInstance() {
        return (CloudServer) CloudAPI.getInstance().getCloudService();
    }

    @Override
    public CloudPlayer getCachedPlayer(UUID uniqueId) {
        return CloudAPI.getInstance().getOnlinePlayer(uniqueId);
    }

    @Override
    public CloudPlayer getCachedPlayer(String name) {
        return CloudAPI.getInstance().getOnlinePlayer(name);
    }

    @Override
    public boolean isProxyInstance() {
        return false;
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        throw new UnsupportedOperationException();
    }

    /**
     * Updates the ServerInfo on a asynchronized BukkitScheduler Task
     */
    public void updateAsync() {
        if (this.plugin == null) {
            this.plugin = Bukkit.getPluginManager().getPlugin("CloudNetAPI");
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this::update);
    }

    /**
     * Changed the State to INGAME and Start a gameserver
     */
    public void changeToIngame() {
        BukkitCloudNetHelper.changeToIngame();
    }

    /**
     * Checks if this instance can starting game servers auto
     *
     * @return
     */
    public boolean isAllowAutoStart() {
        return true;
    }

    /**
     * Updates the ServerInfo
     */
    public void update() {
        Wrapper.getInstance().publishServiceInfoUpdate();
    }

    public void setAllowAutoStart(boolean allowAutoStart) {
        CloudAPI.getInstance().warnUnavailableFeature("CloudServer#setAllowAutoStart");
    }

    public void setServerStateAndUpdate(ServerState serverStateAndUpdate) {
        BukkitCloudNetHelper.setState(serverStateAndUpdate.name());
        update();
    }

    public int getPort() {
        return Wrapper.getInstance().getCurrentServiceInfoSnapshot().getConfiguration().getPort();
    }

    public String getHostAdress() {
        return "127.0.0.1";
    }

    /**
     * Returns the serverConfig from this instance
     *
     * @return
     */
    public ServerConfig getServerConfig() {
        return CloudAPI.getInstance().getConverter().convertToServerConfig(Wrapper.getInstance().getCurrentServiceInfoSnapshot());
    }

    /**
     * Sets the serverConfig in a new default style
     *
     * @param serverConfig
     */
    public void setServerConfig(ServerConfig serverConfig) {
        CloudAPI.getInstance().warnUnavailableFeature("CloudServer#setServerConfig");
    }

    /**
     * Returns the ServerState from this instance
     *
     * @return
     */
    public ServerState getServerState() {
        return CloudAPI.getInstance().getConverter().convertServerState(Wrapper.getInstance().getCurrentServiceInfoSnapshot().getProperty(BridgeServiceProperty.STATE).orElse(null));
    }

    /**
     * Set the serverState INGAME, LOBBY, OFFLINE for switching Signs or your API thinks
     *
     * @param serverState
     */
    public void setServerState(ServerState serverState) {
        BukkitCloudNetHelper.setState(serverState.name());
    }

    /**
     * Returns the max players from the acceptings
     *
     * @return
     */
    public int getMaxPlayers() {
        return BukkitCloudNetHelper.getMaxPlayers();
    }

    /**
     * Set the maxPlayers from this instance
     *
     * @param maxPlayers
     */
    public void setMaxPlayers(int maxPlayers) {
        BukkitCloudNetHelper.setMaxPlayers(maxPlayers);
    }

    public void setMaxPlayersAndUpdate(int maxPlayers) {
        this.setMaxPlayers(maxPlayers);
        update();
    }

    public void setMotdAndUpdate(String motd) {
        this.setMotd(motd);
        update();
    }

    /**
     * Returns the motd from the server marks for the cloud
     *
     * @return
     */
    public String getMotd() {
        return BukkitCloudNetHelper.getApiMotd();
    }

    /**
     * Sets the Motd for the ServerInfo
     *
     * @param motd
     */
    public void setMotd(String motd) {
        BukkitCloudNetHelper.setApiMotd(motd);
    }

    /**
     * Returns the Template of the ServerInfo
     */
    public Template getTemplate() {
        return CloudAPI.getInstance().getConverter().convertToTemplate(Wrapper.getInstance().getCurrentServiceInfoSnapshot());
    }

}
