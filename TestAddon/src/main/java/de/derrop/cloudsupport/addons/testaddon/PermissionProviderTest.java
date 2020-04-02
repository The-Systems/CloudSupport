package de.derrop.cloudsupport.addons.testaddon;

import eu.thesystems.cloud.CloudSystem;
import eu.thesystems.cloud.permission.Permissible;
import eu.thesystems.cloud.permission.PermissionUser;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PermissionProviderTest {

    public static void test(CloudSystem cloud) {
        System.out.println("Available groups: " + cloud.getPermissionProvider().getGroups().stream().map(Permissible::getName).collect(Collectors.joining(", ")));
        String group = "Test-Group-" + UUID.randomUUID();
        System.out.println("Add group: " + group);
        cloud.getPermissionProvider().addGroup(cloud.getPermissionProvider().createGroup(group));
        System.out.println("Available groups: " + cloud.getPermissionProvider().getGroups().stream().map(Permissible::getName).collect(Collectors.joining(", ")));

        System.out.println("All Users: " + cloud.getPermissionProvider().getUsers().stream().map(Permissible::getName).collect(Collectors.joining(", ")));
        System.out.println("Users in group default: " + cloud.getPermissionProvider().getUsers("default").stream().map(Permissible::getName).collect(Collectors.joining(", ")));

        PermissionUser user = cloud.getPermissionProvider().getUsers().iterator().next();
        System.out.println("Add group default for 1 day to user " + user.getName());
        user.addGroup("default", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
        user.update();
        System.out.println("Groups of " + user.getName() + ": " + cloud.getPermissionProvider().getUser(user.getUniqueId()).getGroups());
    }

}
