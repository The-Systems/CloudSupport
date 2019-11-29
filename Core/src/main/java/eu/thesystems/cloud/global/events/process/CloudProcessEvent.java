package eu.thesystems.cloud.global.events.process;
/*
 * Created by derrop on 26.10.2019
 */

import eu.thesystems.cloud.event.CloudEvent;
import eu.thesystems.cloud.global.info.ProcessInfo;

/**
 * This event is called whenever something happens with processes (start/stop/update).
 * <p>
 * This event is called on every cloud.
 */
public class CloudProcessEvent extends CloudEvent {
    private ProcessInfo processInfo;

    public CloudProcessEvent(ProcessInfo processInfo) {
        this.processInfo = processInfo;
    }

    public ProcessInfo getProcessInfo() {
        return this.processInfo;
    }
}
