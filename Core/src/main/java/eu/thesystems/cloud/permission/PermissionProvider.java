package eu.thesystems.cloud.permission;

import java.util.Collection;
import java.util.UUID;

public interface PermissionProvider { // todo not tested

    boolean isAvailable();

    void update(Permissible permissible);

    Collection<PermissionUser> getUsers(String name);

    PermissionUser getUser(UUID uniqueId);

    @Deprecated
    Collection<PermissionUser> getUsers();

    @Deprecated
    Collection<PermissionUser> getUsersByGroup(String group);

    int getUserCount();

    PermissionGroup getGroup(String name);

    Collection<PermissionGroup> getGroups();

    int getGroupCount();

    PermissionGroup getDefaultGroup();

    void addGroup(PermissionGroup permissionGroup);

    void deleteGroup(String groupName);

    void addUser(PermissionUser permissionUser);

    void deleteUsers(String userName);

    void deleteUser(UUID uniqueId);


    PermissionGroup createGroup(String name);

    PermissionUser createUser(UUID uniqueId, String name);

}
