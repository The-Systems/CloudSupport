package eu.thesystems.cloud.permission;

import org.jetbrains.annotations.NotNull;

public interface PermissionGroup extends Permissible {

    /**
     * Gets the sort id of this group.
     *
     * @return the sortId
     */
    int getSortId();

    /**
     * Sets the prefix of this group.
     * This requires an update using {@link #update()}.
     *
     * @param prefix the new prefix for this group
     */
    void setPrefix(@NotNull String prefix);

    /**
     * Sets the suffix of this group.
     * This requires an update using {@link #update()}.
     *
     * @param suffix the new suffix for this group
     */
    void setSuffix(@NotNull String suffix);

    /**
     * @return whether this group is a default group or not
     */
    boolean isDefaultGroup();

    /**
     * Sets whether this group is a default group or not.
     * This requires an update using {@link #update()}.
     *
     * @param defaultGroup whether this group should be a default group or not
     */
    void setDefaultGroup(boolean defaultGroup);

}
