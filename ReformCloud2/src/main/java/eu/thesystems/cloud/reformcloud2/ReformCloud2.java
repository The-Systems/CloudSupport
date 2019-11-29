package eu.thesystems.cloud.reformcloud2;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.cloudsystem.reformcloud.ReformCloud;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

public abstract class ReformCloud2 extends ReformCloud {
    public ReformCloud2(SupportedCloudSystem componentType, String name) {
        super(componentType, name, System.getProperty("reformcloud.runner.version"));
    }
}
