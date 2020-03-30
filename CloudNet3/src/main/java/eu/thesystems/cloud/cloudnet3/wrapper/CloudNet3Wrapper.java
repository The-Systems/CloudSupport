package eu.thesystems.cloud.cloudnet3.wrapper;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.cloudnet3.wrapper.database.CloudNet3WrapperDatabaseProvider;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.global.database.DatabaseProvider;

public class CloudNet3Wrapper extends CloudNet3 {

    private final Wrapper wrapper = Wrapper.getInstance();
    private final DatabaseProvider databaseProvider = new CloudNet3WrapperDatabaseProvider(this.wrapper);

    public CloudNet3Wrapper(SupportedCloudSystem supportedCloudSystem) {
        super(
                supportedCloudSystem,
                "CloudNet3-Wrapper",
                Wrapper.class.getPackage().getImplementationTitle() + "-" + Wrapper.class.getPackage().getImplementationVersion(),
                BridgePlayerManager.getInstance()
        );
    }

    @Override
    public String getOwnComponentName() {
        return this.wrapper.getServiceId().getName();
    }

    @Override
    public DatabaseProvider getDatabaseProvider() {
        return this.databaseProvider;
    }

}
