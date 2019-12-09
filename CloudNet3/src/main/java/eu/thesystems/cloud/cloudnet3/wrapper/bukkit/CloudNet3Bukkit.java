package eu.thesystems.cloud.cloudnet3.wrapper.bukkit;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.api.CloudAPI;
import eu.thesystems.cloud.cloudnet3.wrapper.CloudNet3Wrapper;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

public class CloudNet3Bukkit extends CloudNet3Wrapper {
    public CloudNet3Bukkit() {
        super(SupportedCloudSystem.CLOUDNET_3_BUKKIT);
        new CloudAPI().bootstrap();
    }
}
