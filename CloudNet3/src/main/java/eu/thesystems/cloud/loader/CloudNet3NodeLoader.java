package eu.thesystems.cloud.loader;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.module.NodeCloudNetModule;
import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

import java.util.Objects;

public class CloudNet3NodeLoader extends NodeCloudNetModule {

    @ModuleTask(event = ModuleLifeCycle.STARTED)
    public void onEnable() {
        CloudSupport.getInstance().selectCloudSystem(Objects.requireNonNull(SupportedCloudSystem.CLOUDNET_3_NODE.createCloudSystem()));

        CloudSupport.getInstance().startAddons();
    }

    @ModuleTask(event = ModuleLifeCycle.STOPPED)
    public void onDisable() {
        this.getCloudNet().getEventManager().unregisterListeners(this.getClassLoader());

        CloudSupport.getInstance().stopAddons();
    }

}
