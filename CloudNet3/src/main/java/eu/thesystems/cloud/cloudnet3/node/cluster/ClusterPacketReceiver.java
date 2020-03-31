package eu.thesystems.cloud.cloudnet3.node.cluster;

import de.dytanic.cloudnet.driver.DriverEnvironment;

public class ClusterPacketReceiver {

    private DriverEnvironment targetType;
    private String targetIdentifier;

    public ClusterPacketReceiver(DriverEnvironment targetType, String targetIdentifier) {
        this.targetType = targetType;
        this.targetIdentifier = targetIdentifier;
    }

    public DriverEnvironment getTargetType() {
        return this.targetType;
    }

    public String getTargetIdentifier() {
        return this.targetIdentifier;
    }
}
