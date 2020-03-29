package eu.thesystems.cloud.loader;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.util.DefaultModuleHelper;
import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;
import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class CloudNet3NodeLoader extends CloudNet3Loader {

    private static final ServiceEnvironmentType[] SUPPORTED_ENVIRONMENTS = new ServiceEnvironmentType[]{
            ServiceEnvironmentType.MINECRAFT_SERVER,
            ServiceEnvironmentType.BUNGEECORD
    };

    @Override
    public void onEnable() {
        CloudSupport.getInstance().selectCloudSystem(Objects.requireNonNull(SupportedCloudSystem.CLOUDNET_3_NODE.createCloudSystem()));

        CloudSupport.getInstance().startAddons();

        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        CloudNetDriver.getInstance().getEventManager().unregisterListeners(this.getClass().getClassLoader());

        CloudSupport.getInstance().stopAddons();

        CloudNetDriver.getInstance().getEventManager().unregisterListener(this);
    }

    @EventListener
    public void handleServicePrepared(CloudServicePreStartEvent event) {
        if (!Arrays.asList(SUPPORTED_ENVIRONMENTS).contains(event.getCloudService().getServiceConfiguration().getProcessConfig().getEnvironment())) {
            return;
        }

        File directory = new File(event.getCloudService().getDirectory(), ".wrapper/modules");
        directory.mkdirs();

        DefaultModuleHelper.copyCurrentModuleInstanceFromClass(CloudNet3NodeLoader.class, new File(directory, "CloudSupport-Loader.jar"));
    }

}
