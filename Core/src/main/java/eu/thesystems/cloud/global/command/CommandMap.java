package eu.thesystems.cloud.global.command;
/*
 * Created by derrop on 16.11.2019
 */

import java.util.function.Consumer;

public interface CommandMap {

    void registerCommand(CloudCommand command);

    void onCommandRegistered(Consumer<CloudCommand> commandConsumer);

}
