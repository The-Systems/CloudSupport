package eu.thesystems.cloud.permission;
/*
 * Created by derrop on 26.10.2019
 */

import java.util.Collection;
import java.util.UUID;

public interface PermissionUser {

    boolean hasPermission(String permission);

    UUID getUniqueId();

    String getName();

    String getPassword();

    Collection<String> getPermissions();

}
