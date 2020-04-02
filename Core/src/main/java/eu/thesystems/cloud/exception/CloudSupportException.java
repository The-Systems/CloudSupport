package eu.thesystems.cloud.exception;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.CloudSystem;

/**
 * This exception is thrown when you use a method, that is not supported by your cloud
 */
public class CloudSupportException extends RuntimeException {

    private CloudSystem cloudSystem;

    public CloudSupportException(CloudSystem cloudSystem) {
        this(cloudSystem, false);
    }

    public CloudSupportException(CloudSystem cloudSystem, boolean scheduledForImplementation) {
        super("Not supported in " + cloudSystem.getName() + (scheduledForImplementation ? " (Will be implemented soon)" : ""));
        this.cloudSystem = cloudSystem;
    }

    public CloudSystem getCloudSystem() {
        return cloudSystem;
    }
}
