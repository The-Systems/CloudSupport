package eu.thesystems.cloud.cloudnet2.bridge.includes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.thesystems.cloud.cloudnet2.bridge.CloudNet2Bridge;
import eu.thesystems.cloud.cloudnet2.bridge.bukkit.CloudNet2Bukkit;
import eu.thesystems.cloud.info.ProcessType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BridgeAddonLoader {

    private CloudNet2Bridge bridge;

    public BridgeAddonLoader(CloudNet2Bridge bridge) {
        this.bridge = bridge;
    }

    public void load() {
        try {
            String[] addons = this.loadAddons();
            if (addons.length == 0) {
                return;
            }

            this.downloadAddons(Paths.get("cloudsupport", "addons"), addons);

        } catch (InterruptedException | ExecutionException | TimeoutException | IOException exception) {
            exception.printStackTrace();
        }
    }

    private void downloadAddons(Path directory, String[] addons) throws InterruptedException, ExecutionException, TimeoutException, IOException {
        Files.createDirectories(directory);

        JsonObject result = this.bridge.getChannelMessenger().sendQueryChannelMessageToCloud(
                "CloudSupport-Internal-Addon-Inclusion-Channel",
                "webAccess",
                new JsonObject()
        ).get(5, TimeUnit.SECONDS);

        String url = result.get("url").getAsString();
        String token = result.get("token").getAsString();

        for (String addon : addons) {
            String addonUrl = url.replace("{addon}", addon);

            System.out.println("Downloading addon \"" + addon + "\" from " + addonUrl + "...");

            URLConnection connection = new URL(addonUrl).openConnection();
            connection.setRequestProperty("Token", token);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, directory.resolve(addon + ".jar"));
            }

            System.out.println("Successfully downloaded the addon \"" + addon + "\"");
        }
    }

    private String[] loadAddons() throws InterruptedException, ExecutionException, TimeoutException {
        JsonObject data = new JsonObject();
        data.addProperty("type", (this.bridge instanceof CloudNet2Bukkit ? ProcessType.MINECRAFT_SERVER : ProcessType.BUNGEE_CORD).toString());

        JsonArray array = this.bridge.getChannelMessenger().sendQueryChannelMessageToCloud(
                "CloudSupport-Internal-Addon-Inclusion-Channel",
                "listAddons",
                data
        ).get(5, TimeUnit.SECONDS).getAsJsonArray("addons");

        String[] addons = new String[array.size()];
        int i = 0;
        for (JsonElement element : array) {
            addons[i++] = element.getAsString();
        }

        return addons;
    }

}
