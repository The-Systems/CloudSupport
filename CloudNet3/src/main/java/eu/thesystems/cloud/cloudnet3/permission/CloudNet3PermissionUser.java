package eu.thesystems.cloud.cloudnet3.permission;
/*
 * Created by derrop on 26.10.2019
 */

import com.google.common.base.Preconditions;
import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.permission.PermissionUserGroupInfo;
import eu.thesystems.cloud.permission.PermissionGroup;
import eu.thesystems.cloud.permission.PermissionProvider;
import eu.thesystems.cloud.permission.PermissionUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;

public class CloudNet3PermissionUser extends CloudNet3Permissible implements PermissionUser {
    private IPermissionUser permissionUser;

    public CloudNet3PermissionUser(IPermissionUser permissionUser, PermissionProvider permissionProvider, de.dytanic.cloudnet.driver.provider.PermissionProvider cloudNetPermissionProvider) {
        super(permissionUser, permissionProvider, cloudNetPermissionProvider);
        this.permissionUser = permissionUser;
    }

    public IPermissionUser getWrapped() {
        return this.permissionUser;
    }

    @Override
    public @Nullable String getPrefix() {
        return this.getHighestPermissionGroup().getPrefix();
    }

    @Override
    public @Nullable String getSuffix() {
        return this.getHighestPermissionGroup().getSuffix();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.permissionUser.getUniqueId();
    }

    @Override
    public boolean supportsPasswords() {
        return true;
    }

    @Override
    public @Nullable String getHashedPassword() {
        return this.permissionUser.getHashedPassword();
    }

    @Override
    public void setPassword(String password) {
        this.permissionUser.changePassword(password);
    }

    @Override
    public boolean checkPassword(@NotNull String password) {
        return this.permissionUser.checkPassword(password);
    }

    @Override
    public @NotNull PermissionGroup getHighestPermissionGroup() {
        return super.getGroupObjects().stream().min(Comparator.comparingInt(PermissionGroup::getSortId))
                .orElseGet(() -> super.permissionProvider.getDefaultGroup());
    }

    @Override
    public boolean addGroup(String group, long timeout) {
        Preconditions.checkNotNull(group, "group");

        Collection<PermissionUserGroupInfo> groups = this.permissionUser.getGroups();
        if (groups.stream().anyMatch(groupInfo -> groupInfo.getGroup().equals(group))) {
            return false;
        }
        return groups.add(new PermissionUserGroupInfo(group, timeout));
    }
}
