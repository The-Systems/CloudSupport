package eu.thesystems.cloud;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import eu.thesystems.cloud.addon.CloudAddon;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CloudNet2EmulatorAddon extends CloudAddon {
    @Override
    public void onEnable() {
        Path directory = Paths.get("plugins");
        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Path path = directory.resolve("CloudNet2-Emulator.jar");

        try (InputStream inputStream = super.getAddonInfo().getUrl().openStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if (super.getCloud().getComponentType() == SupportedCloudSystem.CLOUDNET_3_BUKKIT) {
            new CloudAPI().bootstrap();
            CloudAPI.getInstance().setCloudService(new CloudServer());
        } else if (super.getCloud().getComponentType() == SupportedCloudSystem.CLOUDNET_3_BUNGEE) {
            new CloudAPI().bootstrap();
            // todo implement CloudProxy just like the CloudServer
        }
    }

    @Override
    public void onDisable() {

    }
}
