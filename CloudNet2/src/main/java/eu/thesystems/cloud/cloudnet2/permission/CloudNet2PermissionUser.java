package eu.thesystems.cloud.cloudnet2.permission;
/*
 * Created by derrop on 26.10.2019
 */

import de.dytanic.cloudnet.lib.user.User;
import eu.thesystems.cloud.permission.PermissionUser;

import java.util.Collection;
import java.util.UUID;

public class CloudNet2PermissionUser implements PermissionUser {
    private User user;

    public CloudNet2PermissionUser(User user) {
        this.user = user;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.user.hasPermission(permission);
    }

    @Override
    public UUID getUniqueId() {
        return this.user.getUniqueId();
    }

    @Override
    public String getName() {
        return this.user.getName();
    }

    @Override
    public String getPassword() {
        return this.user.getHashedPassword();
    }

    @Override
    public Collection<String> getPermissions() {
        return this.user.getPermissions();
    }
}
