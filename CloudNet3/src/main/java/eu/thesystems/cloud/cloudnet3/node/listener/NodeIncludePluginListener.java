package eu.thesystems.cloud.cloudnet3.node.listener;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;
import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.addon.CloudAddon;
import eu.thesystems.cloud.cloudnet3.node.CloudNet3Node;
import eu.thesystems.cloud.info.ProcessInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class NodeIncludePluginListener {

    private CloudNet3Node node;

    public NodeIncludePluginListener(CloudNet3Node node) {
        this.node = node;
    }

    @EventListener
    public void handlePreStart(CloudServicePreStartEvent event) throws IOException {
        ProcessInfo processInfo = this.node.getConverter().convertProcessInfo(event.getCloudService().getServiceInfoSnapshot());

        for (CloudAddon addon : CloudSupport.getInstance().getAddonManager().getLoadedAddons()) {
            if (addon.isEnabled() && addon.getAddonInfo().shouldBeCopiedToServer(processInfo.getType())) {
                Path parentDirectory = event.getCloudService().getDirectory().toPath();
                Path addonsDirectory = parentDirectory.resolve("cloudsupport").resolve("addons");
                Files.createDirectories(addonsDirectory);

                try (InputStream inputStream = addon.getAddonInfo().getUrl().openStream()) {
                    Files.copy(inputStream, addonsDirectory.resolve(addon.getAddonInfo().getName() + ".jar"));
                }
            }
        }
    }

}
