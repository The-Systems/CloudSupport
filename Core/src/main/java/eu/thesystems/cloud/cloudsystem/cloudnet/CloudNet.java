package eu.thesystems.cloud.cloudsystem.cloudnet;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.DefaultCloudSystem;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.event.EventManager;
import eu.thesystems.cloud.event.defaults.DefaultEventManager;

public abstract class CloudNet extends DefaultCloudSystem {
    public CloudNet(SupportedCloudSystem componentType, String name, String version) {
        super(componentType, new DefaultEventManager(), name, version);
    }
}
