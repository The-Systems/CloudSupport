package eu.thesystems.cloud.cloudnet2.bridge.bungee;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.bridge.CloudProxy;
import eu.thesystems.cloud.cloudnet2.bridge.CloudNet2Bridge;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

public class CloudNet2Bungee extends CloudNet2Bridge {

    private final CloudProxy cloudProxy = CloudProxy.getInstance();

    public CloudNet2Bungee() {
        super(SupportedCloudSystem.CLOUDNET_2_BUNGEE);
    }
}
