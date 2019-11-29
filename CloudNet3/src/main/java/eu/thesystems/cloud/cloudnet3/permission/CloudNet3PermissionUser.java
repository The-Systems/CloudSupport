package eu.thesystems.cloud.cloudnet3.permission;
/*
 * Created by derrop on 26.10.2019
 */

import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.ext.cloudperms.CloudPermissionsPermissionManagement;
import eu.thesystems.cloud.global.permission.PermissionUser;

import java.util.Collection;
import java.util.UUID;

public class CloudNet3PermissionUser implements PermissionUser {
    private IPermissionUser permissionUser;

    public CloudNet3PermissionUser(IPermissionUser permissionUser) {
        this.permissionUser = permissionUser;
    }

    @Override
    public boolean hasPermission(String permission) {
        return CloudPermissionsPermissionManagement.getInstance().hasPermission(this.permissionUser, permission);
    }

    @Override
    public UUID getUniqueId() {
        return this.permissionUser.getUniqueId();
    }

    @Override
    public String getName() {
        return this.permissionUser.getName();
    }

    @Override
    public String getPassword() {
        return this.permissionUser.getHashedPassword();
    }

    @Override
    public Collection<String> getPermissions() {
        return this.permissionUser.getPermissionNames();
    }
}
