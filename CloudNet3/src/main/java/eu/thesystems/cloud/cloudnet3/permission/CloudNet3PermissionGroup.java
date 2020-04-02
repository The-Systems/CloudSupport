package eu.thesystems.cloud.cloudnet3.permission;

import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import eu.thesystems.cloud.permission.PermissionGroup;
import eu.thesystems.cloud.permission.PermissionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CloudNet3PermissionGroup extends CloudNet3Permissible implements PermissionGroup {

    private IPermissionGroup permissionGroup;

    public CloudNet3PermissionGroup(IPermissionGroup permissionGroup, PermissionProvider permissionProvider, de.dytanic.cloudnet.driver.provider.PermissionProvider cloudNetPermissionProvider) {
        super(permissionGroup, permissionProvider, cloudNetPermissionProvider);
        this.permissionGroup = permissionGroup;
    }

    public IPermissionGroup getWrapped() {
        return this.permissionGroup;
    }

    @Override
    public int getSortId() {
        return this.permissionGroup.getSortId();
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
    public @Nullable String getPrefix() {
        return this.permissionGroup.getPrefix();
    }

    @Override
    public @Nullable String getSuffix() {
        return this.permissionGroup.getSuffix();
    }
}
