package eu.thesystems.cloud.loader;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnetcore.api.CoreModule;
import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.cloudnet2.master.CloudNet2Master;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

import java.util.Objects;

public class CloudNet2MasterLoader extends CoreModule {
    @Override
    public void onBootstrap() {
        CloudSupport.getInstance().selectCloudSystem(Objects.requireNonNull(SupportedCloudSystem.CLOUDNET_2_MASTER.createCloudSystem()));

        CloudNet2Master master = ((CloudNet2Master) CloudSupport.getInstance().getSelectedCloudSystem());
        master.init(this);

        CloudSupport.getInstance().startAddons();
    }

    @Override
    public void onShutdown() {
        this.getCloud().getEventManager().unregisterListener(this);

        CloudSupport.getInstance().stopAddons();
    }
}
