package eu.thesystems.cloud.cloudnet2.permission;

import com.google.common.base.Preconditions;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.hash.DyHash;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.DefaultPermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.thesystems.cloud.permission.Permissible;
import eu.thesystems.cloud.permission.PermissionGroup;
import eu.thesystems.cloud.permission.PermissionProvider;
import eu.thesystems.cloud.permission.PermissionUser;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class CloudNet2PermissionProvider implements PermissionProvider { // todo not tested

    protected abstract void deletePermissionGroup(String name);

    protected abstract void updatePermissionGroup(de.dytanic.cloudnet.lib.player.permission.PermissionGroup permissionGroup);

    protected abstract void updateCloudNetUser(User user);

    protected abstract void updatePlayer(OfflinePlayer offlinePlayer);

    protected abstract OfflinePlayer getPlayer(UUID uniqueId);

    protected abstract OfflinePlayer getPlayer(String name);

    protected abstract void addCloudNetUser(User user);

    protected abstract User getCloudNetUser(UUID uniqueId);

    protected abstract User getCloudNetUser(String name);

    protected abstract void deleteCloudNetUser(String name);

    protected abstract void deleteCloudNetUser(UUID uniqueId);

    protected abstract Collection<User> getCloudNetUsers();

    protected abstract PermissionPool getPermissionPool();

    protected abstract Database getPlayerDatabase();

    protected de.dytanic.cloudnet.lib.player.permission.PermissionGroup getCloudNetPermissionGroup(String name) {
        return this.getPermissionPool().getGroups().get(name);
    }

    @Override
    public boolean isAvailable() {
        PermissionPool pool = this.getPermissionPool();
        return pool != null && pool.isAvailable();
    }

    @Override
    public void update(Permissible permissible) {
        this.validateAvailable();

        if (permissible instanceof CloudNet2PermissionGroup) {
            de.dytanic.cloudnet.lib.player.permission.PermissionGroup permissionGroup = ((CloudNet2PermissionGroup) permissible).getWrapped();
            ((CloudNet2PermissionGroup) permissible).applyProperties(permissionGroup);
            this.updatePermissionGroup(permissionGroup);
        } else if (permissible instanceof CloudNet2PermissionUser) {
            OfflinePlayer offlinePlayer = ((CloudNet2PermissionUser) permissible).getWrappedOfflinePlayer();
            User user = ((CloudNet2PermissionUser) permissible).getWrappedUser();

            if (user != null) {
                ((CloudNet2PermissionUser) permissible).applyProperties(user);

                this.updateCloudNetUser(user);
            } else if (offlinePlayer != null) {
                this.updatePlayer(offlinePlayer);
            }
        } else {
            throw new IllegalArgumentException("No PermissionUser/PermissionGroup given: " + (permissible != null ? permissible.getClass().getName() : "null"));
        }
    }

    @Override
    public Collection<PermissionUser> getUsers(String name) {
        this.validateAvailable();
        Preconditions.checkNotNull(name, "name");

        OfflinePlayer offlinePlayer = this.getPlayer(name);
        if (offlinePlayer == null) {
            User user = this.getCloudNetUser(name);
            if (user == null) {
                return null;
            }
            return Collections.singletonList(new CloudNet2PermissionUser(user, this));
        }

        return Collections.singletonList(new CloudNet2PermissionUser(offlinePlayer, this));
    }

    @Override
    public PermissionUser getUser(UUID uniqueId) {
        this.validateAvailable();
        Preconditions.checkNotNull(uniqueId, "uniqueId");

        OfflinePlayer offlinePlayer = this.getPlayer(uniqueId);
        if (offlinePlayer == null) {
            User user = this.getCloudNetUser(uniqueId);
            if (user == null) {
                return null;
            }
            return new CloudNet2PermissionUser(user, this);
        }

        return new CloudNet2PermissionUser(offlinePlayer, this);
    }

    @Override
    public Collection<PermissionUser> getUsers() {
        this.validateAvailable();

        Collection<PermissionUser> users = new ArrayList<>();

        this.forEachRegisteredPlayers(offlinePlayer -> users.add(new CloudNet2PermissionUser(offlinePlayer, this)));

        for (User cloudNetUser : this.getCloudNetUsers()) {
            users.add(new CloudNet2PermissionUser(cloudNetUser, this));
        }

        return users;
    }

    @Override
    public Collection<PermissionUser> getUsersByGroup(String group) {
        this.validateAvailable();
        Preconditions.checkNotNull(group, "group");

        Collection<PermissionUser> users = new ArrayList<>();

        this.forEachRegisteredPlayers(offlinePlayer -> {
            if (offlinePlayer.getPermissionEntity().getGroups().stream().anyMatch(data -> data.getGroup().equalsIgnoreCase(group))) {
                users.add(new CloudNet2PermissionUser(offlinePlayer, this));
            }
        });

        return users;
    }

    private void forEachRegisteredPlayers(Consumer<OfflinePlayer> consumer) {
        Database database = this.getPlayerDatabase();

        database.loadDocuments();
        for (Document document : database.getDocs()) {
            OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", OfflinePlayer.TYPE);

            if (offlinePlayer != null && offlinePlayer.getPermissionEntity() != null) {
                consumer.accept(offlinePlayer);
            }
        }
    }

    @Override
    public int getUserCount() {
        return this.getPlayerDatabase().size();
    }

    @Override
    public PermissionGroup getGroup(String name) {
        this.validateAvailable();
        Preconditions.checkNotNull(name, "name");

        de.dytanic.cloudnet.lib.player.permission.PermissionGroup permissionGroup = this.getPermissionPool().getGroups().get(name);

        return permissionGroup == null ? null : new CloudNet2PermissionGroup(permissionGroup, this);
    }

    @Override
    public Collection<PermissionGroup> getGroups() {
        this.validateAvailable();

        return this.getPermissionPool().getGroups().values().stream()
                .map(permissionGroup -> new CloudNet2PermissionGroup(permissionGroup, this))
                .collect(Collectors.toList());
    }

    @Override
    public int getGroupCount() {
        return this.getPermissionPool().getGroups().size();
    }

    @Override
    public PermissionGroup getDefaultGroup() {
        return Optional.ofNullable(this.getPermissionPool().getDefaultGroup())
                .map(permissionGroup -> new CloudNet2PermissionGroup(permissionGroup, this))
                .orElseThrow(() -> new IllegalArgumentException("No defaultGroup defined"));
    }

    @Override
    public void addGroup(PermissionGroup permissionGroup) {
        Preconditions.checkNotNull(permissionGroup, "permissionGroup");

        de.dytanic.cloudnet.lib.player.permission.PermissionGroup cloudNetPermissionGroup = ((CloudNet2PermissionGroup) permissionGroup).getWrapped();
        ((CloudNet2PermissionGroup) permissionGroup).applyProperties(cloudNetPermissionGroup);

        this.updatePermissionGroup(cloudNetPermissionGroup);
    }

    @Override
    public void deleteGroup(String groupName) {
        Preconditions.checkNotNull(groupName, "groupName");

        this.deletePermissionGroup(groupName);
    }

    @Override
    public void addUser(PermissionUser permissionUser) {
        Preconditions.checkNotNull(permissionUser, "permissionUser");

        User user = ((CloudNet2PermissionUser) permissionUser).getWrappedUser();
        Preconditions.checkNotNull(user, "Missing wrapped user in the PermissionUser");

        this.addCloudNetUser(user);
    }

    @Override
    public void deleteUsers(String userName) {
        Preconditions.checkNotNull(userName, "userName");

        this.deleteCloudNetUser(userName);
    }

    @Override
    public void deleteUser(UUID uniqueId) {
        Preconditions.checkNotNull(uniqueId, "uniqueId");

        this.deleteCloudNetUser(uniqueId);
    }

    @Override
    public PermissionGroup createGroup(String name) {
        Preconditions.checkNotNull(name, "name");

        return new CloudNet2PermissionGroup(new DefaultPermissionGroup(name), this);
    }

    @Override
    public PermissionUser createUser(UUID uniqueId, String name) {
        Preconditions.checkNotNull(uniqueId, "uniqueId");
        Preconditions.checkNotNull(name, "name");

        return new CloudNet2PermissionUser(new User(
                name, uniqueId, NetworkUtils.randomString(32), DyHash.hashString(""), new ArrayList<>(), new HashMap<>()
        ), this);
    }

    private void validateAvailable() {
        Preconditions.checkArgument(this.isAvailable(), "The PermissionSystem by CloudNet 2 is not enabled");
    }

}
