package eu.thesystems.cloud.global.command;
/*
 * Created by derrop on 16.11.2019
 */

public interface CloudCommandSender {

    boolean isPlayer();

    void sendMessage(String message);

    void sendMessage(String... messages);

    String getName();

    boolean hasPermission(String permission);

}
