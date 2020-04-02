package eu.thesystems.cloud.command;
/*
 * Created by derrop on 16.11.2019
 */

public class DefaultConsoleCommandSender implements CloudCommandSender {
    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void sendMessage(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }
}
