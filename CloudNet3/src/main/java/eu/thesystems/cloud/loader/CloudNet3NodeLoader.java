package eu.thesystems.cloud.loader;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

import java.util.Objects;

public class CloudNet3NodeLoader extends CloudNet3Loader {

    @Override
    public void onEnable() {
        CloudSupport.getInstance().selectCloudSystem(Objects.requireNonNull(SupportedCloudSystem.CLOUDNET_3_NODE.createCloudSystem()));

        CloudSupport.getInstance().startAddons();
    }

    @Override
    public void onDisable() {
        CloudNetDriver.getInstance().getEventManager().unregisterListeners(this.getClass().getClassLoader());

        CloudSupport.getInstance().stopAddons();
    }
}
