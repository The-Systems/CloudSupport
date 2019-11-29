package eu.thesystems.cloud.cloudnet2.bridge.bukkit;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.bridge.CloudServer;
import eu.thesystems.cloud.cloudnet2.bridge.CloudNet2Bridge;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

public class CloudNet2Bukkit extends CloudNet2Bridge {

    private final CloudServer cloudServer = CloudServer.getInstance();

    public CloudNet2Bukkit() {
        super(SupportedCloudSystem.CLOUDNET_2_BUKKIT);
    }
}
