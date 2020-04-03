package eu.thesystems.cloud.cloudnet3.permission;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.permission.PermissionAddGroupEvent;
import de.dytanic.cloudnet.driver.event.events.permission.PermissionUpdateGroupEvent;
import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.exception.CloudSupportException;
import eu.thesystems.cloud.permission.Permissible;
import eu.thesystems.cloud.permission.PermissionGroup;
import eu.thesystems.cloud.permission.PermissionProvider;
import eu.thesystems.cloud.permission.PermissionUser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CloudNet3PermissionProvider implements PermissionProvider {

    private CloudNet3 cloudNet3;

    private Map<String, PermissionGroup> cachedGroups = new ConcurrentHashMap<>();

    public CloudNet3PermissionProvider(CloudNet3 cloudNet3) {
        this.cloudNet3 = cloudNet3;
        for (IPermissionGroup group : this.getPermissionProvider().getGroups()) {
            this.cacheGroup(group);
        }
        cloudNet3.getCloudNetDriver().getEventManager().registerListener(this); // Todo unregister?
    }

    @EventListener
    public void handlePermissionGroupUpdate(PermissionUpdateGroupEvent event) {
        this.cacheGroup(event.getPermissionGroup());
    }

    @EventListener
    public void handlePermissionGroupAdd(PermissionAddGroupEvent event) {
        this.cacheGroup(event.getPermissionGroup());
    }

    private void cacheGroup(IPermissionGroup group) {
        this.cachedGroups.put(group.getName(), new CloudNet3PermissionGroup(group, this, this.getPermissionProvider()));
    }

    private de.dytanic.cloudnet.driver.provider.PermissionProvider getPermissionProvider() {
        return this.cloudNet3.getCloudNetDriver().getPermissionProvider();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void update(Permissible permissible) {
        if (permissible instanceof PermissionGroup) {
            this.getPermissionProvider().updateGroup(this.convertPermissionGroupToCloudNet((PermissionGroup) permissible));
        } else if (permissible instanceof PermissionUser) {
            this.getPermissionProvider().updateUser(this.convertPermissionUserToCloudNet((PermissionUser) permissible));
        } else {
            throw new IllegalArgumentException("No PermissionUser/PermissionGroup given: " + (permissible != null ? permissible.getClass().getName() : "null"));
        }
    }

    @Override
    public Collection<PermissionUser> getUsers(String name) {
        List<IPermissionUser> users = this.getPermissionProvider().getUsers(name);
        return users.stream()
                .map(this::convertPermissionUserFromCloudNet)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionUser getUser(UUID uniqueId) {
        return this.convertPermissionUserFromCloudNet(this.getPermissionProvider().getUser(uniqueId));
    }

    @Override
    public Collection<PermissionUser> getUsers() {
        return this.getPermissionProvider().getUsers().stream()
                .map(this::convertPermissionUserFromCloudNet)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<PermissionUser> getUsersByGroup(String group) {
        return this.getPermissionProvider().getUsersByGroup(group).stream()
                .map(this::convertPermissionUserFromCloudNet)
                .collect(Collectors.toList());
    }

    @Override
    public int getUserCount() {
        throw new CloudSupportException(this.cloudNet3, true);
    }

    @Override
    public PermissionGroup getGroup(String name) {
        return this.cachedGroups.get(name);
    }

    @Override
    public Collection<PermissionGroup> getGroups() {
        return Collections.unmodifiableCollection(this.cachedGroups.values());
    }

    @Override
    public int getGroupCount() {
        return this.cachedGroups.size();
    }

    @Override
    public PermissionGroup getDefaultGroup() {
        return this.cachedGroups.values().stream()
                .filter(PermissionGroup::isDefaultGroup)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No defaultGroup defined"));
    }

    @Override
    public void addGroup(PermissionGroup permissionGroup) {
        this.getPermissionProvider().addGroup(this.convertPermissionGroupToCloudNet(permissionGroup));
        this.cachedGroups.put(permissionGroup.getName(), permissionGroup);
    }

    @Override
    public void deleteGroup(String groupName) {
        this.getPermissionProvider().deleteGroup(groupName);
    }

    @Override
    public void addUser(PermissionUser permissionUser) {
        this.getPermissionProvider().addUser(this.convertPermissionUserToCloudNet(permissionUser));
    }

    @Override
    public void deleteUsers(String userName) {
        this.getPermissionProvider().deleteUser(userName);
    }

    @Override
    public void deleteUser(UUID uniqueId) {
        this.getPermissionProvider().deleteUser(new de.dytanic.cloudnet.driver.permission.PermissionUser(uniqueId, "", null, 0));
    }

    @Override
    public PermissionGroup createGroup(String name) {
        return new CloudNet3PermissionGroup(new de.dytanic.cloudnet.driver.permission.PermissionGroup(name, 1), this, this.getPermissionProvider());
    }

    @Override
    public PermissionUser createUser(UUID uniqueId, String name) {
        return new CloudNet3PermissionUser(new de.dytanic.cloudnet.driver.permission.PermissionUser(uniqueId, name, null, 0), this, this.getPermissionProvider());
    }

    private IPermissionGroup convertPermissionGroupToCloudNet(PermissionGroup permissionGroup) {
        if (!(permissionGroup instanceof CloudNet3PermissionGroup)) {
            throw new IllegalArgumentException("permissionGroup has to be a CloudNet 3 PermissionGroup");
        }
        return ((CloudNet3PermissionGroup) permissionGroup).getWrapped();
    }

    private IPermissionUser convertPermissionUserToCloudNet(PermissionUser permissionUser) {
        if (!(permissionUser instanceof CloudNet3PermissionUser)) {
            throw new IllegalArgumentException("permissionUser has to be a CloudNet 3 PermissionUser");
        }
        return ((CloudNet3PermissionUser) permissionUser).getWrapped();
    }

    private PermissionUser convertPermissionUserFromCloudNet(IPermissionUser permissionUser) {
        return new CloudNet3PermissionUser(permissionUser, this, this.getPermissionProvider());
    }

}
