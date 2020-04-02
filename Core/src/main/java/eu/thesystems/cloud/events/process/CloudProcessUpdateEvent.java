package eu.thesystems.cloud.events.process;
/*
 * Created by derrop on 26.10.2019
 */

import eu.thesystems.cloud.info.ProcessInfo;

/**
 * This event is called when a process info is updated in the network.
 * <p>
 * This event is called on every cloud.
 */
public class CloudProcessUpdateEvent extends CloudProcessEvent {
    public CloudProcessUpdateEvent(ProcessInfo processInfo) {
        super(processInfo);
    }
}
