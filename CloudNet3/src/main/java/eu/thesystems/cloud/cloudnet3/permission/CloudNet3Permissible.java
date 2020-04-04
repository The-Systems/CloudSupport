package eu.thesystems.cloud.cloudnet3.permission;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.driver.permission.*;
import de.dytanic.cloudnet.driver.service.GroupConfiguration;
import eu.thesystems.cloud.GsonUtil;
import eu.thesystems.cloud.permission.Permissible;
import eu.thesystems.cloud.permission.PermissionGroup;
import eu.thesystems.cloud.permission.PermissionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class CloudNet3Permissible implements Permissible {

    private IPermissible permissible;
    protected PermissionProvider permissionProvider;
    private de.dytanic.cloudnet.driver.provider.PermissionProvider cloudNetPermissionProvider;

    private JsonObject properties;

    public CloudNet3Permissible(IPermissible permissible, PermissionProvider permissionProvider, de.dytanic.cloudnet.driver.provider.PermissionProvider cloudNetPermissionProvider) {
        this.permissible = permissible;
        this.permissionProvider = permissionProvider;
        this.cloudNetPermissionProvider = cloudNetPermissionProvider;
    }

    @NotNull
    @Override
    public String getName() {
        return this.permissible.getName();
    }

    private void forEachGroups(Consumer<IPermissionGroup> consumer) {
        this.forEachGroups(this.getName(), this.getGroups(), consumer, 0);
    }

    private void forEachGroups(String firstGroup, Collection<String> groups, Consumer<IPermissionGroup> consumer, int layer) {
        if (layer >= 15 + this.permissionProvider.getGroups().size()) {
            throw new IllegalArgumentException("Detected recursive group implementations on group " + firstGroup);
        }
        ++layer;

        for (String group : groups) {
            IPermissionGroup permissionGroup = this.cloudNetPermissionProvider.getGroup(group);
            if (permissionGroup == null) {
                continue;
            }

            consumer.accept(permissionGroup);

            if (!permissionGroup.getGroups().isEmpty()) {
                for (String extendedGroup : permissionGroup.getGroups()) {
                    IPermissionGroup extendedPermissionGroup = this.cloudNetPermissionProvider.getGroup(extendedGroup);
                    if (extendedPermissionGroup == null) {
                        continue;
                    }

                    this.forEachGroups(extendedGroup, extendedPermissionGroup.getGroups(), consumer, layer);
                }
            }
        }
    }

    private Collection<String> mapPermissions(Collection<Permission> permissions) {
        return permissions == null ? null : permissions.stream().map(Permission::getName).distinct().collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Collection<String> getOwnPermissions() {
        return this.permissible.getPermissionNames();
    }

    @NotNull
    @Override
    public Collection<String> getAllPermissions() {
        return this.mapPermissions(this.getAllPermissions0());
    }

    private Collection<Permission> getAllPermissions0() {
        Collection<Permission> permissions = new ArrayList<>(this.permissible.getPermissions());
        for (Collection<Permission> value : this.permissible.getGroupPermissions().values()) {
            permissions.addAll(value);
        }
        this.forEachGroups(group -> {
            permissions.addAll(group.getPermissions());
            for (Collection<Permission> value : group.getGroupPermissions().values()) {
                permissions.addAll(value);
            }
        });
        return permissions;
    }

    @NotNull
    @Override
    public Collection<String> getOwnPermissions(String processGroup) {
        return this.mapPermissions(this.permissible.getGroupPermissions().get(processGroup));
    }

    @NotNull
    @Override
    public Collection<String> getAllPermissions(String processGroup) {
        return this.mapPermissions(this.getAllPermissions0(processGroup));
    }

    private Collection<Permission> getAllPermissions0(String processGroup) {
        Preconditions.checkNotNull(processGroup);

        Collection<Permission> permissions = new ArrayList<>(this.permissible.getGroupPermissions().getOrDefault(processGroup, Collections.emptyList()));
        this.forEachGroups(group -> permissions.addAll(group.getGroupPermissions().getOrDefault(processGroup, Collections.emptyList())));
        return permissions;
    }

    @NotNull
    @Override
    public Collection<String> getOwnGlobalPermissions() {
        return this.permissible.getPermissionNames();
    }

    @NotNull
    @Override
    public Collection<String> getAllGlobalPermissions() {
        Collection<Permission> permissions = new ArrayList<>(this.permissible.getPermissions());
        this.forEachGroups(group -> permissions.addAll(group.getPermissions()));
        return this.mapPermissions(permissions);
    }

    @NotNull
    @Override
    public Collection<String> getGroups() {
        if (this.permissible instanceof IPermissionUser) {
            return ((IPermissionUser) this.permissible).getGroups().stream().map(PermissionUserGroupInfo::getGroup).collect(Collectors.toList());
        } else if (this.permissible instanceof IPermissionGroup) {
            return ((IPermissionGroup) this.permissible).getGroups();
        } else {
            throw new IllegalArgumentException("Cannot get groups of a permissible that is neither a user nor a group");
        }
    }

    @NotNull
    @Override
    public Collection<PermissionGroup> getGroupObjects() {
        return this.getGroups().stream().map(this.permissionProvider::getGroup).collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(String permission, String processGroup) {
        Preconditions.checkNotNull(permission);

        Permission cloudNetPermission = new Permission(permission);

        PermissionCheckResult result = this.permissible.hasPermission(this.getAllPermissions0(), cloudNetPermission);

        if (result != PermissionCheckResult.DENIED) {
            return result.asBoolean();
        }

        if (processGroup != null) {
            result = this.permissible.hasPermission(this.getAllPermissions0(processGroup), cloudNetPermission);
        }

        return result.asBoolean();
    }

    @NotNull
    @Override
    public JsonObject getProperties() {
        return this.properties == null ? (this.properties = GsonUtil.parseStringAsObject(this.permissible.getProperties().toJson())) : this.properties;
    }

    @Override
    public boolean addPermission(String processGroup, String permission) {
        Preconditions.checkNotNull(permission, "permission");
        Preconditions.checkNotNull(processGroup, "processGroup");
        Preconditions.checkArgument(!permission.isEmpty(), "permission empty");

        boolean negative = permission.charAt(0) == '-';
        return this.permissible.addPermission(processGroup, negative ? permission.substring(1) : permission, negative ? -1 : 1);
    }

    @Override
    public boolean addPermission(String permission) {
        Preconditions.checkNotNull(permission, "permission");
        Preconditions.checkArgument(!permission.isEmpty(), "permission empty");

        boolean negative = permission.charAt(0) == '-';
        return this.permissible.addPermission(negative ? permission.substring(1) : permission, negative ? -1 : 1);
    }

    @Override
    public boolean removePermission(String processGroup, String permission) {
        Preconditions.checkNotNull(permission, "permission");
        Preconditions.checkNotNull(processGroup, "processGroup");
        Preconditions.checkArgument(!permission.isEmpty(), "permission empty");

        boolean negative = permission.charAt(0) == '-';
        return this.permissible.removePermission(processGroup, negative ? permission.substring(1) : permission);
    }

    @Override
    public boolean removePermission(String permission) {
        Preconditions.checkNotNull(permission, "permission");
        Preconditions.checkArgument(!permission.isEmpty(), "permission empty");

        boolean negative = permission.charAt(0) == '-';
        return this.permissible.removePermission(negative ? permission.substring(1) : permission);
    }

    @Override
    public void update() {
        this.permissionProvider.update(this);
    }

    @Override
    public boolean addGroup(String group) {
        Preconditions.checkNotNull(group, "group");

        if (this.permissible instanceof IPermissionUser) {
            Collection<PermissionUserGroupInfo> groups = ((IPermissionUser) this.permissible).getGroups();
            if (groups.stream().anyMatch(groupInfo -> groupInfo.getGroup().equals(group))) {
                return false;
            }
            return groups.add(new PermissionUserGroupInfo(group, -1));
        } else if (this.permissible instanceof IPermissionGroup) {
            Collection<String> groups = ((IPermissionGroup) this.permissible).getGroups();
            if (groups.contains(group)) {
                return false;
            }
            return groups.add(group);
        } else {
            throw new IllegalArgumentException("Cannot modify groups of a permissible that is neither a user nor a group");
        }
    }

    @Override
    public boolean removeGroup(String group) {
        Preconditions.checkNotNull(group, "group");

        if (this.permissible instanceof IPermissionUser) {
            return ((IPermissionUser) this.permissible).getGroups().removeIf(groupInfo -> groupInfo.getGroup().equals(group));
        } else if (this.permissible instanceof IPermissionGroup) {
            return ((IPermissionGroup) this.permissible).getGroups().remove(group);
        } else {
            throw new IllegalArgumentException("Cannot modify groups of a permissible that is neither a user nor a group");
        }
    }

}
