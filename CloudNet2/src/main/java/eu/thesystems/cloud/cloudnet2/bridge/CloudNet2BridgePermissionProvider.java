package eu.thesystems.cloud.cloudnet2.bridge;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.user.User;
import eu.thesystems.cloud.GsonUtil;
import eu.thesystems.cloud.cloudnet2.permission.CloudNet2PermissionProvider;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CloudNet2BridgePermissionProvider extends CloudNet2PermissionProvider { // todo not tested

    private static final String CHANNEL = "CloudSupport-Internal-PermissionProvider-Channel";

    private CloudAPI cloudAPI;
    private CloudNet2Bridge bridge;

    public CloudNet2BridgePermissionProvider(CloudNet2Bridge bridge) {
        this.bridge = bridge;
        this.cloudAPI = bridge.getCloudAPI();
    }

    @Override
    protected void deletePermissionGroup(String name) {
        JsonObject data = new JsonObject();
        data.addProperty("name", name);
        this.bridge.getChannelMessenger().sendChannelMessageToCloud(CHANNEL, "deleteGroup", data);
    }

    @Override
    protected void updatePermissionGroup(PermissionGroup permissionGroup) {
        this.cloudAPI.updatePermissionGroup(permissionGroup);
    }

    @Override
    protected void updatePlayer(OfflinePlayer offlinePlayer) {
        this.cloudAPI.updatePlayer(offlinePlayer);
    }

    @Override
    protected OfflinePlayer getPlayer(UUID uniqueId) {
        return this.cloudAPI.getOfflinePlayer(uniqueId);
    }

    @Override
    protected OfflinePlayer getPlayer(String name) {
        return this.cloudAPI.getOfflinePlayer(name);
    }

    @Override
    protected void addCloudNetUser(User user) {
        this.bridge.getChannelMessenger().sendChannelMessageToCloud(CHANNEL, "addUser", GsonUtil.GSON.toJsonTree(user).getAsJsonObject());
    }

    @Override
    protected void updateCloudNetUser(User user) {
        this.bridge.getChannelMessenger().sendChannelMessageToCloud(CHANNEL, "updateUser", GsonUtil.GSON.toJsonTree(user).getAsJsonObject());
    }

    @Override
    protected User getCloudNetUser(UUID uniqueId) {
        JsonObject data = new JsonObject();
        data.addProperty("uniqueId", uniqueId.toString());
        return this.getCloudNetUser(data);
    }

    @Override
    protected User getCloudNetUser(String name) {
        JsonObject data = new JsonObject();
        data.addProperty("name", name);
        return this.getCloudNetUser(data);
    }

    private User getCloudNetUser(JsonObject data) {
        try {
            return GsonUtil.GSON.fromJson(this.bridge.getChannelMessenger().sendQueryChannelMessageToCloud(CHANNEL, "getUser", data).get(5, TimeUnit.SECONDS), User.class);
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    protected void deleteCloudNetUser(String name) {
        JsonObject data = new JsonObject();
        data.addProperty("name", name);
        this.bridge.getChannelMessenger().sendChannelMessageToCloud(CHANNEL, "deleteUser", data);
    }

    @Override
    protected void deleteCloudNetUser(UUID uniqueId) {
        JsonObject data = new JsonObject();
        data.addProperty("uniqueId", uniqueId.toString());
        this.bridge.getChannelMessenger().sendChannelMessageToCloud(CHANNEL, "deleteUser", data);
    }

    @Override
    protected Collection<User> getCloudNetUsers() {
        try {
            JsonObject result = this.bridge.getChannelMessenger().sendQueryChannelMessageToCloud(CHANNEL, "getUsers", new JsonObject()).get(5, TimeUnit.SECONDS);
            return GsonUtil.GSON.fromJson(result.get("users"), new TypeToken<Collection<User>>() {
            }.getType());
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    protected PermissionPool getPermissionPool() {
        return this.cloudAPI.getPermissionPool();
    }

    @Override
    protected Database getPlayerDatabase() {
        return this.cloudAPI.getDatabaseManager().getDatabase("cloudnet_internal_players");
    }
}
