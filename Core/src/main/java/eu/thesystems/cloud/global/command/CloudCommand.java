package eu.thesystems.cloud.global.command;
/*
 * Created by derrop on 16.11.2019
 */

import java.util.Collections;
import java.util.List;

public abstract class CloudCommand {

    private String name;
    private String[] aliases;
    private String permission;

    public CloudCommand(String name, String[] aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public CloudCommand(String name) {
        this.name = name;
    }

    public CloudCommand(String name, String[] aliases, String permission) {
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public abstract void execute(CloudCommandSender sender, String[] args);

    public List<String> tabComplete(String[] args) {
        return Collections.emptyList();
    }

}
