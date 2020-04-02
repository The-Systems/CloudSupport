package eu.thesystems.cloud.permission;

import com.google.gson.JsonObject;
import eu.thesystems.cloud.exception.CloudSupportException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Permissible {

    /**
     * Gets the name of this permission group.
     *
     * @return the name
     */
    @NotNull
    String getName();

    /**
     * @return the prefix
     */
    @Nullable
    String getPrefix();

    /**
     * @return the suffix
     */
    @Nullable
    String getSuffix();

    /**
     * Gets all global permissions which are specifically defined for this permissible ignoring any implemented groups.
     *
     * @return an immutable list with all permissions
     */
    @NotNull
    Collection<String> getOwnPermissions();

    /**
     * Gets all global permissions ({@link #getOwnPermissions()}) and all permissions of every implemented
     * group ({@link #getGroups()})
     *
     * @return an immutable list with all permissions
     */
    @NotNull
    Collection<String> getAllPermissions();

    /**
     * Gets all global permissions and all specific-group-only permissions which are specifically defined
     * for this permissible ignoring any implemented groups.
     *
     * @return an immutable list with all permissions
     */
    @NotNull
    Collection<String> getOwnGlobalPermissions();

    /**
     * Gets all global permissions ({@link #getOwnPermissions()}), all specific-group-only permissions and all
     * permissions of every implemented group ({@link #getGroups()}).
     *
     * @return an immutable list with all permissions
     */
    @NotNull
    Collection<String> getAllGlobalPermissions();

    /**
     * Gets all specific-group-only permissions which are specifically defined for this group ignoring any implemented groups.
     *
     * @param processGroup the name of the group (case-sensitive)
     * @return an immutable list with all permissions
     */
    @NotNull
    Collection<String> getOwnPermissions(String processGroup);

    /**
     * Gets all specific-group-only permissions ({@link #getOwnPermissions(String)}) and all specific-group-only permissions of every implemented
     * group. ({@link #getGroups()})
     *
     * @param processGroup the name of the group (case-sensitive)
     * @return an immutable list with all permissions
     */
    @NotNull
    Collection<String> getAllPermissions(String processGroup);

    /**
     * If this permissible is a group, this gets a list of all implemented groups.
     * If it is a user, this gets a list of all groups for this user.
     *
     * @return a mutable list with all implemented groups
     */
    @NotNull
    Collection<String> getGroups();

    /**
     * Maps {@link #getGroups()} to {@link PermissionGroup}s using {@link PermissionProvider#getGroup(String)}.
     *
     * @return an immutable list with all implemented groups
     */
    @NotNull
    Collection<PermissionGroup> getGroupObjects();

    /**
     * Checks whether this group has the given permission. Uses the given processGroup to
     * determine the specific-group-only permissions by using {@link #getAllPermissions(String)}.
     *
     * @param permission   the case-insensitive permission
     * @param processGroup the name of the group for {@link #getAllPermissions(String)} (case-sensitive)
     * @return {@code true} if this group has the given permission or {@code false} otherwise
     */
    boolean hasPermission(String permission, String processGroup);

    /**
     * Gets the properties of this permissible as a modifiable Json Object.
     * This requires an update using {@link #update()} after modifying.
     *
     * @return the properties of this group
     */
    @NotNull
    JsonObject getProperties();

    /**
     * Adds the given specific-group-only permission to this permissible.
     * This requires an update using {@link #update()}.
     *
     * @param processGroup the processGroup where this permission should be applied
     * @param permission   the permission to be added (case-insensitive)
     * @return {@code true} if the permission has been added successfully or {@code false} if this permissible already has the given permission
     * @throws CloudSupportException this is not supported for permission users (only groups) in CloudNet 2
     */
    boolean addPermission(String processGroup, String permission);

    /**
     * Adds the given global permission to this permissible.
     * This requires an update using {@link #update()}.
     *
     * @param permission   the permission to be added (case-insensitive)
     * @return {@code true} if the permission has been added successfully or {@code false} if this permissible already has the given permission
     * @throws CloudSupportException this is not supported for permission users (only groups) in CloudNet 2
     */
    boolean addPermission(String permission);

    /**
     * Removes the given specific-group-only permission to this permissible.
     * This requires an update using {@link #update()}.
     *
     * @param processGroup the processGroup where this permission should be removed
     * @param permission   the permission to be removed (case-insensitive)
     * @return {@code true} if the permission has been removed successfully or {@code false} if this permissible doesn't have the given permission
     * @throws CloudSupportException this is not supported for permission users (only groups) in CloudNet 2
     */
    boolean removePermission(String processGroup, String permission);

    /**
     * Removes the given global permission to this permissible.
     * This requires an update using {@link #update()}.
     *
     * @param permission   the permission to be removed (case-insensitive)
     * @return {@code true} if the permission has been removed successfully or {@code false} if this permissible doesn't have the given permission
     * @throws CloudSupportException this is not supported for permission users (only groups) in CloudNet 2
     */
    boolean removePermission(String permission);

    /**
     * Adds a specific group to this permissible with no timeout.
     * This requires an update using {@link #update()}.
     *
     * @param group the name of the group to be added (case-sensitive)
     * @return {@code true} if the group was successfully added or {@code false} if the permissible already has the group
     */
    boolean addGroup(String group);

    /**
     * Removes a specific group from this permissible.
     * This requires an update using {@link #update()}.
     *
     * @param group the name of the group to be removed (case-sensitive)
     * @return {@code true} if the group was successfully removed or {@code false} if the permissible doesn't have the group
     */
    boolean removeGroup(String group);

    /**
     * Updates this permissible using {@link PermissionProvider#update(Permissible)}.
     */
    void update();

}
