package eu.thesystems.cloud.cloudnet2.permission;

import com.google.gson.JsonObject;
import eu.thesystems.cloud.GsonUtil;
import eu.thesystems.cloud.permission.PermissionGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CloudNet2PermissionGroup extends CloudNet2Permissible implements PermissionGroup {

    private de.dytanic.cloudnet.lib.player.permission.PermissionGroup permissionGroup;
    private CloudNet2PermissionProvider permissionProvider;

    private JsonObject properties;

    public CloudNet2PermissionGroup(@NotNull de.dytanic.cloudnet.lib.player.permission.PermissionGroup permissionGroup, @NotNull CloudNet2PermissionProvider permissionProvider) {
        this.permissionGroup = permissionGroup;
        this.permissionProvider = permissionProvider;
    }

    public de.dytanic.cloudnet.lib.player.permission.PermissionGroup getWrapped() {
        return this.permissionGroup;
    }

    public void applyProperties(de.dytanic.cloudnet.lib.player.permission.PermissionGroup permissionGroup) {
        if (this.properties != null) {
            permissionGroup.setOptions(super.asMap(this.properties));
        }
    }

    private void forEachGroups(Consumer<de.dytanic.cloudnet.lib.player.permission.PermissionGroup> consumer) {
        this.forEachGroups(null, this.getGroups(), consumer, 0);
    }

    private void forEachGroups(String firstGroup, Collection<String> groups, Consumer<de.dytanic.cloudnet.lib.player.permission.PermissionGroup> consumer, int layer) {
        if (layer >= 20 + this.permissionProvider.getGroupCount()) {
            throw new IllegalArgumentException("Detected recursive group implementations on group " + firstGroup); // todo test if this works
        }
        ++layer;

        for (String group : groups) {
            de.dytanic.cloudnet.lib.player.permission.PermissionGroup permissionGroup = this.permissionProvider.getCloudNetPermissionGroup(group);
            if (permissionGroup == null) {
                continue;
            }

            consumer.accept(permissionGroup);

            if (!permissionGroup.getImplementGroups().isEmpty()) {
                for (String extendedGroup : permissionGroup.getImplementGroups()) {
                    de.dytanic.cloudnet.lib.player.permission.PermissionGroup extendedPermissionGroup =
                            this.permissionProvider.getCloudNetPermissionGroup(extendedGroup);
                    if (extendedPermissionGroup == null) {
                        continue;
                    }

                    this.forEachGroups(extendedGroup, extendedPermissionGroup.getImplementGroups(), consumer, layer);
                }
            }
        }
    }

    @Override
    public int getSortId() {
        return this.permissionGroup.getTagId();
    }

    @Override
    public void setPrefix(@NotNull String prefix) {
        this.permissionGroup.setPrefix(prefix);
    }

    @Override
    public void setSuffix(@NotNull String suffix) {
        this.permissionGroup.setSuffix(suffix);
    }

    @Override
    public boolean isDefaultGroup() {
        return this.permissionGroup.isDefaultGroup();
    }

    @Override
    public void setDefaultGroup(boolean defaultGroup) {
        this.permissionGroup.setDefaultGroup(defaultGroup);
    }

    @Override
    public @NotNull String getName() {
        return this.permissionGroup.getName();
    }

    @Override
    public @Nullable String getPrefix() {
        return this.permissionGroup.getPrefix();
    }

    @Override
    public @Nullable String getSuffix() {
        return this.permissionGroup.getSuffix();
    }

    @Override
    public @NotNull Collection<String> getOwnPermissions() {
        Collection<String> permissions = this.getOwnGlobalPermissions();

        for (List<String> value : this.permissionGroup.getServerGroupPermissions().values()) {
            permissions.addAll(value);
        }

        return permissions;
    }

    @Override
    public @NotNull Collection<String> getAllPermissions() {
        Map<String, Boolean> permissions = new HashMap<>(this.permissionGroup.getPermissions());

        this.forEachGroups(implementedGroup -> permissions.putAll(implementedGroup.getPermissions()));

        return super.mapPermissions(permissions);
    }

    @Override
    public @NotNull Collection<String> getOwnGlobalPermissions() {
        return super.mapPermissions(this.permissionGroup.getPermissions());
    }

    @Override
    public @NotNull Collection<String> getAllGlobalPermissions() {
        return super.mapPermissions(this.getAllGlobalPermissions0());
    }

    private Map<String, Boolean> getAllGlobalPermissions0() {
        Map<String, Boolean> permissions = new HashMap<>(this.permissionGroup.getPermissions());

        this.forEachGroups(implementedGroup -> permissions.putAll(implementedGroup.getPermissions()));

        return permissions;
    }

    @Override
    public @NotNull Collection<String> getOwnPermissions(String processGroup) {
        return this.permissionGroup.getServerGroupPermissions().getOrDefault(processGroup, Collections.emptyList());
    }

    @Override
    public @NotNull Collection<String> getAllPermissions(String processGroup) {
        Collection<String> permissions = new ArrayList<>(this.permissionGroup.getServerGroupPermissions().getOrDefault(processGroup, Collections.emptyList()));

        this.forEachGroups(implementedGroup -> permissions.addAll(implementedGroup.getServerGroupPermissions().getOrDefault(processGroup, Collections.emptyList())));

        return permissions;
    }

    @Override
    public @NotNull Collection<String> getGroups() {
        return this.permissionGroup.getImplementGroups();
    }

    @Override
    public @NotNull Collection<PermissionGroup> getGroupObjects() {
        return this.permissionGroup.getImplementGroups().stream()
                .map(this.permissionProvider::getGroup)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(String permission, @Nullable String processGroup) {
        permission = permission.toLowerCase();

        Map<String, Boolean> permissions = this.getAllGlobalPermissions0();
        if (processGroup != null) {
            for (String givenPermission : this.getAllPermissions(processGroup)) {
                permissions.put(givenPermission, true);
            }
        }

        if (!permissions.containsKey(permission)) {
            return permissions.get("*");
        }

        if (permissions.get("*") || permissions.get(permission)) {
            return true;
        }

        StringBuilder fullPermission = new StringBuilder();
        for (String sequence : permission.split("\\.")) {
            fullPermission.append(sequence).append('.');
            String testPermission = fullPermission + "*";

            if (!permissions.containsKey(testPermission)) {
                continue;
            }

            return permissions.get(testPermission);
        }

        return false;
    }

    @Override
    public @NotNull JsonObject getProperties() {
        if (this.properties != null) {
            return this.properties;
        }

        JsonObject object = new JsonObject();

        this.permissionGroup.getOptions().forEach((key, value) -> object.add(key, GsonUtil.GSON.toJsonTree(value)));

        return this.properties = object;
    }

    @Override
    public boolean addPermission(String processGroup, String permission) {
        return false;
    }

    @Override
    public boolean addPermission(String permission) {
        return false;
    }

    @Override
    public boolean removePermission(String processGroup, String permission) {
        return false;
    }

    @Override
    public boolean removePermission(String permission) {
        return false;
    }

    @Override
    public boolean addGroup(String group) {
        return false;
    }

    @Override
    public boolean removeGroup(String group) {
        return false;
    }

    @Override
    public void update() {
        this.permissionProvider.update(this);
    }
}
