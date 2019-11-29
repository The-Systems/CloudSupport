package eu.thesystems.cloud;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.addon.loader.CloudAddonManager;
import eu.thesystems.cloud.addon.loader.DefaultCloudAddonManager;
import eu.thesystems.cloud.detection.SupportedCloudSystem;

public class CloudSupport {

    private static CloudSupport instance;

    public static CloudSupport getInstance() {
        return instance != null ? instance : (instance = new CloudSupport());
    }

    private CloudSystem selectedCloudSystem;
    private CloudAddonManager addonManager = new DefaultCloudAddonManager();

    public boolean selectCloudSystem() {
        System.out.println("[CloudSupport] Searching for cloud system...");
        for (SupportedCloudSystem value : SupportedCloudSystem.values()) {
            if (value.isUseable()) {
                CloudSystem cloudSystem = value.createCloudSystem();
                if (cloudSystem != null) {
                    this.selectCloudSystem(cloudSystem);
                    return true;
                }
            }
        }
        System.out.println("[CloudSupport] Cannot find cloud system");
        return false;
    }

    public void selectCloudSystem(CloudSystem cloudSystem) {
        this.selectedCloudSystem = cloudSystem;
        System.out.println("[CloudSupport] Selected cloud system: " + cloudSystem.getName());
    }

    public void debug(String message) {
        if (this.isDebugging()) {
            System.err.println("[CloudSupport DEBUG] " + message);
        }
    }

    public boolean isDebugging() {//todo
        return true;
    }

    public CloudAddonManager getAddonManager() {
        return this.addonManager;
    }

    public CloudSystem getSelectedCloudSystem() {
        return this.selectedCloudSystem;
    }

    public void startAddons() {
        System.out.println("[CloudSupport] Loading addons...");
        this.addonManager.loadAddons();
        this.addonManager.enableAddons();
        System.out.println("[CloudSupport] Successfully loaded and enabled all addons");
    }

    public void stopAddons() {
        System.out.println("[CloudSupport] Disabling addons...");
        this.addonManager.unloadAddons();
        System.out.println("[CloudSupport] Successfully disabled and unloaded all addons");
    }
}
