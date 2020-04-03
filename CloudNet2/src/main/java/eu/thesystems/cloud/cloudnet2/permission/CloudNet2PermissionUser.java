package eu.thesystems.cloud.cloudnet2.permission;
/*
 * Created by derrop on 26.10.2019
 */

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.lib.hash.DyHash;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.user.User;
import eu.thesystems.cloud.GsonUtil;
import eu.thesystems.cloud.permission.PermissionGroup;
import eu.thesystems.cloud.permission.PermissionUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CloudNet2PermissionUser extends CloudNet2Permissible implements PermissionUser {

    private CloudNet2PermissionProvider permissionProvider;

    private OfflinePlayer offlinePlayer;
    private User user;

    private JsonObject properties;

    public CloudNet2PermissionUser(@NotNull OfflinePlayer offlinePlayer, @NotNull CloudNet2PermissionProvider permissionProvider) {
        this.offlinePlayer = offlinePlayer;
        this.permissionProvider = permissionProvider;
    }

    public CloudNet2PermissionUser(@NotNull User user, @NotNull CloudNet2PermissionProvider permissionProvider) {
        this.user = user;
        this.permissionProvider = permissionProvider;
    }

    public OfflinePlayer getWrappedOfflinePlayer() {
        return this.offlinePlayer;
    }

    public User getWrappedUser() {
        return this.user;
    }

    public void applyProperties(User user) {
        if (this.properties != null) {
            user.getMetaData().clear();
            user.getMetaData().putAll(super.asMap(this.properties));
        }
    }

    @NotNull
    @Override
    public UUID getUniqueId() {
        return this.offlinePlayer != null ? this.offlinePlayer.getUniqueId() : this.user.getUniqueId();
    }

    @Override
    public boolean supportsPasswords() {
        return this.user != null;
    }

    @NotNull
    @Override
    public String getName() {
        return this.offlinePlayer != null ? this.offlinePlayer.getName() : this.user.getName();
    }

    @Override
    public @Nullable String getPrefix() {
        return this.offlinePlayer != null ? this.offlinePlayer.getPermissionEntity().getPrefix() : null;
    }

    @Override
    public @Nullable String getSuffix() {
        return this.offlinePlayer != null ? this.offlinePlayer.getPermissionEntity().getSuffix() : null;
    }

    private Stream<String> filterGroups() {
        return this.offlinePlayer.getPermissionEntity().getGroups().stream()
                .filter(groupEntityData -> groupEntityData.getTimeout() <= 0 || groupEntityData.getTimeout() > System.currentTimeMillis())
                .map(GroupEntityData::getGroup);
    }

    private void forEachGroups(Consumer<de.dytanic.cloudnet.lib.player.permission.PermissionGroup> consumer) {
        this.filterGroups()
                .map(group -> this.permissionProvider.getCloudNetPermissionGroup(group))
                .filter(Objects::nonNull)
                .forEach(consumer);
    }

    @Override
    public @NotNull Collection<String> getOwnPermissions() {
        return this.offlinePlayer != null ? super.mapPermissions(this.offlinePlayer.getPermissionEntity().getPermissions()) : this.user.getPermissions();
    }

    @Override
    public @NotNull Collection<String> getAllPermissions() {
        if (this.user != null) {
            return this.user.getPermissions();
        }

        Map<String, Boolean> permissions = new HashMap<>(this.offlinePlayer.getPermissionEntity().getPermissions());
        this.forEachGroups(permissionGroup -> {
            permissions.putAll(permissionGroup.getPermissions());
            for (List<String> value : permissionGroup.getServerGroupPermissions().values()) {
                for (String permission : value) {
                    permissions.put(permission, true);
                }
            }
        });

        return super.mapPermissions(permissions);
    }

    @Override
    public @NotNull Collection<String> getOwnGlobalPermissions() {
        return this.getOwnPermissions();
    }

    @Override
    public @NotNull Collection<String> getAllGlobalPermissions() {
        if (this.user != null) {
            return this.user.getPermissions();
        }

        Map<String, Boolean> permissions = new HashMap<>(this.offlinePlayer.getPermissionEntity().getPermissions());
        this.forEachGroups(permissionGroup -> permissions.putAll(permissionGroup.getPermissions()));

        return super.mapPermissions(permissions);
    }

    @Override
    public @NotNull Collection<String> getOwnPermissions(String processGroup) {
        return Collections.emptyList();
    }

    @Override
    public @NotNull Collection<String> getAllPermissions(String processGroup) {
        if (this.user != null) {
            return Collections.emptyList();
        }

        Collection<String> permissions = new ArrayList<>();
        this.forEachGroups(permissionGroup -> permissions.addAll(permissionGroup.getServerGroupPermissions().getOrDefault(processGroup, Collections.emptyList())));

        return permissions;
    }

    @Override
    public @NotNull Collection<String> getGroups() {
        return this.offlinePlayer != null ? this.filterGroups().collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public @NotNull Collection<PermissionGroup> getGroupObjects() {
        return this.offlinePlayer != null ? this.filterGroups().map(this.permissionProvider::getGroup).collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public boolean hasPermission(String permission, String processGroup) {
        if (this.user != null) {
            return this.user.getPermissions().contains("*") || this.user.getPermissions().stream().anyMatch(s -> s.equalsIgnoreCase(permission));
        }

        return this.offlinePlayer.getPermissionEntity().hasPermission(this.permissionProvider.getPermissionPool(), permission, processGroup);
    }

    @Override
    public @NotNull JsonObject getProperties() {
        if (this.properties != null) {
            return this.properties;
        }

        if (this.user != null) {
            JsonObject object = new JsonObject();
            this.user.getMetaData().forEach((key, value) -> object.add(key, GsonUtil.GSON.toJsonTree(value)));
            return this.properties = object;
        }

        return this.properties = this.offlinePlayer.getMetaData().obj();
    }

    @Override
    public boolean addPermission(String processGroup, String permission) {
        return false; // todo throw CloudSupportException? (only available for permission groups)
    }

    @Override
    public boolean addPermission(String permission) {
        permission = permission.toLowerCase();

        if (this.user != null) {
            if (this.user.getPermissions().contains(permission)) {
                return false;
            }
            this.user.getPermissions().add(permission);
            return true;
        }

        if (this.offlinePlayer != null) {
            boolean negative = permission.startsWith("-");
            if (negative) {
                permission = permission.substring(1);
            }

            if (this.offlinePlayer.getPermissionEntity().getPermissions().containsKey(permission) &&
                    this.offlinePlayer.getPermissionEntity().getPermissions().get(permission) == !negative) {
                return false;
            }

            this.offlinePlayer.getPermissionEntity().getPermissions().put(permission, !negative);
            return true;
        }

        return false;
    }

    @Override
    public boolean removePermission(String processGroup, String permission) {
        return false; // todo throw CloudSupportException? (only available for permission groups)
    }

    @Override
    public boolean removePermission(String permission) {
        permission = permission.toLowerCase();

        if (this.user != null) {
            if (!this.user.getPermissions().contains(permission)) {
                return false;
            }

            this.user.getPermissions().remove(permission);
            return true;
        }

        if (this.offlinePlayer != null) {
            if (permission.startsWith("-")) {
                permission = permission.substring(1);
            }

            if (!this.offlinePlayer.getPermissionEntity().getPermissions().containsKey(permission)) {
                return false;
            }

            this.offlinePlayer.getPermissionEntity().getPermissions().remove(permission);
            return true;
        }

        return false;
    }

    @Override
    public boolean addGroup(String group) {
        return this.addGroup(group, -1);
    }

    @Override
    public boolean addGroup(String group, long timeout) {
        if (this.offlinePlayer != null) { // Users can't have groups
            if (this.filterGroups().anyMatch(s -> s.equalsIgnoreCase(group))) {
                return false;
            }

            this.offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(group, timeout));
            return true;
        }

        return false;
    }

    @Override
    public boolean removeGroup(String group) {
        return this.offlinePlayer != null && // Users can't have groups
                this.offlinePlayer.getPermissionEntity().getGroups().removeIf(groupEntityData -> groupEntityData.getGroup().equalsIgnoreCase(group));
    }

    @Override
    public void update() {
        this.permissionProvider.update(this);
    }

    @Override
    public String getHashedPassword() {
        return this.user.getHashedPassword();
    }

    @Override
    public boolean setPassword(@Nullable String password) {
        if (this.user != null) {
            this.user.setPassword(password);
        }

        return this.user != null;
    }

    @Override
    public boolean checkPassword(@NotNull String password) {
        Preconditions.checkNotNull(password, "password");

        return this.user.getHashedPassword() != null && this.user.getHashedPassword().equals(DyHash.hashString(password));
    }

    @Override
    public @NotNull PermissionGroup getHighestPermissionGroup() {
        if (this.offlinePlayer != null) {
            de.dytanic.cloudnet.lib.player.permission.PermissionGroup cloudNetPermissionGroup =
                    this.offlinePlayer.getPermissionEntity().getHighestPermissionGroup(this.permissionProvider.getPermissionPool());
            if (cloudNetPermissionGroup != null) {
                return new CloudNet2PermissionGroup(cloudNetPermissionGroup, this.permissionProvider);
            }
        }

        return this.permissionProvider.getDefaultGroup();
    }
}
