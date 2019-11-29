package eu.thesystems.cloud.event;
/*
 * Created by derrop on 25.10.2019
 */

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
