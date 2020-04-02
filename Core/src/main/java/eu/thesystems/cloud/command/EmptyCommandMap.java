package eu.thesystems.cloud.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class EmptyCommandMap implements CommandMap {

    private Collection<Consumer<CloudCommand>> registeredHandlers = new ArrayList<>();

    @Override
    public void registerCommand(CloudCommand command) {
        for (Consumer<CloudCommand> registeredHandler : this.registeredHandlers) {
            registeredHandler.accept(command);
        }
    }

    @Override
    public void onCommandRegistered(Consumer<CloudCommand> commandConsumer) {
        this.registeredHandlers.add(commandConsumer);
    }
}
