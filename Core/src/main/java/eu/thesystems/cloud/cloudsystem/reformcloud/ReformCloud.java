package eu.thesystems.cloud.cloudsystem.reformcloud;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.DefaultCloudSystem;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.event.defaults.DefaultEventManager;

public abstract class ReformCloud extends DefaultCloudSystem {
    public ReformCloud(SupportedCloudSystem componentType, String name, String version) {
        super(componentType, new DefaultEventManager(), name, version);
    }
}
