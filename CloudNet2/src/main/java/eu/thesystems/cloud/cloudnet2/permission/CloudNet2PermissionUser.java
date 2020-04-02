package eu.thesystems.cloud.cloudnet2.permission;
/*
 * Created by derrop on 26.10.2019
 */

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.lib.user.User;
import eu.thesystems.cloud.permission.PermissionGroup;
import eu.thesystems.cloud.permission.PermissionUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class CloudNet2PermissionUser implements PermissionUser {
    private User user;

    public CloudNet2PermissionUser(User user) {
        this.user = user;
    }

    @Override
    public UUID getUniqueId() {
        return this.user.getUniqueId();
    }

    @Override
    public boolean supportsPasswords() {
        return false;
    }

    @NotNull
    @Override
    public String getName() {
        return this.user.getName();
    }

    @Override
    public @Nullable String getPrefix() {
        return null;
    }

    @Override
    public @Nullable String getSuffix() {
        return null;
    }

    @Override
    public @NotNull Collection<String> getOwnPermissions() {
        return null;
    }

    @Override
    public @NotNull Collection<String> getAllPermissions() {
        return null;
    }

    @Override
    public @NotNull Collection<String> getOwnGlobalPermissions() {
        return null;
    }

    @Override
    public @NotNull Collection<String> getAllGlobalPermissions() {
        return null;
    }

    @Override
    public @NotNull Collection<String> getOwnPermissions(String processGroup) {
        return null;
    }

    @Override
    public @NotNull Collection<String> getAllPermissions(String processGroup) {
        return null;
    }

    @Override
    public @NotNull Collection<String> getGroups() {
        return null;
    }

    @Override
    public @NotNull Collection<PermissionGroup> getGroupObjects() {
        return null;
    }

    @Override
    public boolean hasPermission(String permission, String processGroup) {
        return false;
    }

    @Override
    public @NotNull JsonObject getProperties() {
        return null;
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

    }

    @Override
    public String getHashedPassword() {
        return this.user.getHashedPassword();
    }

    @Override
    public void setPassword(@Nullable String password) {

    }

    @Override
    public boolean checkPassword(@NotNull String password) {
        return false;
    }

    @Override
    public @NotNull PermissionGroup getHighestPermissionGroup() {
        return null;
    }

    @Override
    public boolean addGroup(String group, long timeout) {
        return false;
    }
}
