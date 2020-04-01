package eu.thesystems.cloud.cloudnet3.cluster;

import de.dytanic.cloudnet.driver.DriverEnvironment;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
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
