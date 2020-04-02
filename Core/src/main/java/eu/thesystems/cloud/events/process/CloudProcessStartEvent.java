package eu.thesystems.cloud.events.process;
/*
 * Created by derrop on 26.10.2019
 */

import eu.thesystems.cloud.info.ProcessInfo;

/**
 * This event is called when a process is started in the network.
 * <p>
 * This event is called on every cloud.
 */
public class CloudProcessStartEvent extends CloudProcessEvent {
    public CloudProcessStartEvent(ProcessInfo processInfo) {
        super(processInfo);
    }
}
