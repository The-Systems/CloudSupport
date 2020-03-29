package eu.thesystems.cloud.loader;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.wrapper.Wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CloudNet3WrapperLoader extends CloudNet3Loader {

    @Override
    public void onEnable() {
        ServiceEnvironmentType environmentType = Wrapper.getInstance().getCurrentServiceInfoSnapshot().getConfiguration().getProcessConfig().getEnvironment();
        if (environmentType == ServiceEnvironmentType.MINECRAFT_SERVER || environmentType == ServiceEnvironmentType.BUNGEECORD) {
            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("CloudNet2-Emulator.jar")) {
                if (inputStream != null) {
                    Files.copy(inputStream, Paths.get("plugins", "CloudNetAPI.jar"));
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        try (InputStream inputStream = this.getClass().getProtectionDomain().getCodeSource().getLocation().openStream()) {
            Files.copy(inputStream, Paths.get("plugins", "CloudSupport.jar"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
    }
}
