package eu.thesystems.cloud.loader;
/*
 * Created by derrop on 09.12.2019
 */

import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;

public class CloudNet3Module extends DriverModule {

    private CloudNet3Loader loader;

    @ModuleTask(event = ModuleLifeCycle.STARTED)
    public void onEnable() {

        try {
            Class<?> clazz = super.getDriver().getDriverEnvironment() == DriverEnvironment.CLOUDNET ?
                    Class.forName(this.getClass().getPackage().getName() + ".CloudNet3NodeLoader") :
                    Class.forName(this.getClass().getPackage().getName() + ".CloudNet3WrapperLoader");

            this.loader = (CloudNet3Loader) clazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
        }

        if (this.loader != null) {
            this.loader.onEnable();
        }
    }

    @ModuleTask(event = ModuleLifeCycle.STOPPED)
    public void onDisable() {
        if (this.loader != null) {
            this.loader.onDisable();
        }
    }

}
