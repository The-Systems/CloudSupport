package eu.thesystems.cloud.cloudnet2.master;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.permissions.PermissionModule;
import de.dytanic.cloudnetcore.permissions.config.ConfigPermissions;
import eu.thesystems.cloud.GsonUtil;
import eu.thesystems.cloud.cloudnet2.permission.CloudNet2PermissionProvider;
import eu.thesystems.cloud.event.EventHandler;
import eu.thesystems.cloud.events.channel.ChannelMessageReceiveEvent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

public class CloudNet2MasterPermissionProvider extends CloudNet2PermissionProvider { // todo not tested

    private static Field PATH_FIELD, CACHE_FIELD;

    private boolean available;

    {
        try {
            Class.forName("de.dytanic.cloudnetcore.permissions.PermissionModule");
            this.available = true;
        } catch (ClassNotFoundException ignored) {
            this.available = false;
        }
    }

    private CloudNet cloudNet;

    public CloudNet2MasterPermissionProvider(CloudNet2Master master) {
        this.cloudNet = master.getCloudNet();

        master.getEventManager().registerListener(this);
    }

    @EventHandler
    public void handleChannelMessage(ChannelMessageReceiveEvent event) {
        if (!event.getChannel().equals("CloudSupport-Internal-PermissionProvider-Channel")) {
            return;
        }

        if (event.isQuery()) {

            if (event.getMessage().equals("getUser")) {
                User user = null;
                if (event.getData().has("name")) {
                    user = this.getCloudNetUser(event.getData().get("name").getAsString());
                } else if (event.getData().has("uniqueId")) {
                    user = this.getCloudNetUser(UUID.fromString(event.getData().get("uniqueId").getAsString()));
                }
                event.setQueryResult(GsonUtil.GSON.toJsonTree(user).getAsJsonObject());
            } else if (event.getMessage().equals("getUsers")) {
                JsonObject result = new JsonObject();
                result.add("users", GsonUtil.GSON.toJsonTree(this.getCloudNetUsers()));
                event.setQueryResult(result);
            }

            return;
        }

        if (event.getMessage().equals("deleteGroup")) {
            this.deletePermissionGroup(event.getData().get("name").getAsString());
        } else if (event.getMessage().equals("addUser")) {
            this.addCloudNetUser(GsonUtil.GSON.fromJson(event.getData(), User.class));
        } else if (event.getMessage().equals("updateUser")) {
            this.updateCloudNetUser(GsonUtil.GSON.fromJson(event.getData(), User.class));
        }
    }

    @Override
    public boolean isAvailable() {
        return this.available && super.isAvailable();
    }

    @Override
    protected void deletePermissionGroup(String name) {
        ConfigPermissions config = PermissionModule.getInstance().getConfigPermission();

        Path path = null;
        Configuration cache = null;

        try {
            if (PATH_FIELD == null) {
                PATH_FIELD = ConfigPermissions.class.getDeclaredField("path");
            }
            if (CACHE_FIELD == null) {
                CACHE_FIELD = ConfigPermissions.class.getDeclaredField("cache");
            }

            path = (Path) PATH_FIELD.get(config);
            cache = (Configuration) CACHE_FIELD.get(config);
        } catch (IllegalAccessException | NoSuchFieldException exception) {
            exception.printStackTrace();
        }

        Preconditions.checkNotNull(path, "no path for the config set");

        if (cache == null) {
            try (InputStream inputStream = Files.newInputStream(path); InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8)) {
                cache = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Preconditions.checkNotNull(cache, "failed to load the permissions file");

        cache.getSection("groups").set(name, null);

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cache, outputStreamWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updatePermissionGroup(PermissionGroup permissionGroup) {
        PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
    }

    @Override
    protected void updatePlayer(OfflinePlayer offlinePlayer) {
        this.cloudNet.getDbHandlers().getPlayerDatabase().updatePlayer(offlinePlayer);
    }

    @Override
    protected OfflinePlayer getPlayer(UUID uniqueId) {
        return this.cloudNet.getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
    }

    @Override
    protected OfflinePlayer getPlayer(String name) {
        UUID uniqueId = this.cloudNet.getDbHandlers().getNameToUUIDDatabase().get(name);
        return uniqueId != null ? this.getPlayer(uniqueId) : null;
    }

    @Override
    protected void addCloudNetUser(User user) {
        this.cloudNet.getUsers().add(user);
        this.cloudNet.getConfig().save(this.cloudNet.getUsers());
    }

    @Override
    protected void updateCloudNetUser(User user) {
        if (this.cloudNet.getUsers().removeIf(existingUser -> existingUser.getUniqueId().equals(user.getUniqueId()))) {
            this.cloudNet.getUsers().add(user);
            this.cloudNet.getConfig().save(this.cloudNet.getUsers());
        }
    }

    @Override
    protected User getCloudNetUser(UUID uniqueId) {
        return this.cloudNet.getUsers().stream().filter(user -> user.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    protected User getCloudNetUser(String name) {
        return this.cloudNet.getUsers().stream().filter(user -> user.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    protected void deleteCloudNetUser(String name) {
        if (this.cloudNet.getUsers().removeIf(user -> user.getName().equalsIgnoreCase(name))) {
            this.cloudNet.getConfig().save(this.cloudNet.getUsers());
        }
    }

    @Override
    protected void deleteCloudNetUser(UUID uniqueId) {
        if (this.cloudNet.getUsers().removeIf(user -> user.getUniqueId().equals(uniqueId))) {
            this.cloudNet.getConfig().save(this.cloudNet.getUsers());
        }
    }

    @Override
    protected Collection<User> getCloudNetUsers() {
        return this.cloudNet.getUsers();
    }

    @Override
    protected PermissionPool getPermissionPool() {
        return PermissionModule.getInstance().getPermissionPool();
    }

    @Override
    protected Database getPlayerDatabase() {
        return this.cloudNet.getDbHandlers().getPlayerDatabase().getDatabase();
    }
}
