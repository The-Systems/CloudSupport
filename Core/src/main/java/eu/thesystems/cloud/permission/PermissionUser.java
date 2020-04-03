package eu.thesystems.cloud.permission;
/*
 * Created by derrop on 26.10.2019
 */

import eu.thesystems.cloud.exception.CloudSupportException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PermissionUser extends Permissible {

    /**
     * Gets the uniqueId of this user
     *
     * @return the UUID
     */
    @NotNull
    UUID getUniqueId();

    /**
     * @return {@code true} if this user supports passwords or {@code false} if not
     */
    boolean supportsPasswords();

    /**
     * Gets the password of this user or null, if they don't have any password.
     *
     * @return the password or {@code null}
     * @throws CloudSupportException this is not supported in CloudNet 2
     */
    @Nullable
    String getHashedPassword();

    /**
     * Changes the password of this user.
     * This requires an update using {@link #update()}.
     *
     * @param password the new password or null to remove it
     * @return {@code true} if the password has been successfully changed or {@code false} if this user doesn't support passwords
     */
    boolean setPassword(@Nullable String password);

    /**
     * Checks whether the given password is correct or not.
     *
     * @param password the password to be checked (case-sensitive)
     * @return {@code true} if it is correct or {@code false} if not
     */
    boolean checkPassword(@NotNull String password);

    /**
     * Fetches the highest permission group of this user sorted by their sortId.
     *
     * @return the highest group
     * @throws IllegalStateException if the user is in no group AND no default group exists
     */
    @NotNull
    PermissionGroup getHighestPermissionGroup();

    /**
     * Adds a specific group to this permissible.
     * This requires an update using {@link #update()}.
     *
     * @param group the name of the group to be added (case-sensitive)
     * @param timeout the timeout when this group times out
     * @return {@code true} if the group was successfully added or {@code false} if the permissible already has the group
     */
    boolean addGroup(String group, long timeout);

}
