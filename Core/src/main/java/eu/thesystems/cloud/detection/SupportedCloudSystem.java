package eu.thesystems.cloud.detection;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.CloudSystem;

import java.util.function.Supplier;

public enum SupportedCloudSystem {
    CLOUDNET_2_BUKKIT(() -> createCloudSystem("eu.thesystems.cloud.cloudnet2.bridge.bukkit.CloudNet2Bukkit"),
            () -> checkClassExists("org.bukkit.Bukkit") &&
                    checkClassExists("de.dytanic.cloudnet.bridge.CloudServer")
    ),
    CLOUDNET_2_BUNGEE(() -> createCloudSystem("eu.thesystems.cloud.cloudnet2.bridge.bungee.CloudNet2Bungee"),
            () -> checkClassExists("net.md_5.bungee.api.ProxyServer") &&
                    checkClassExists("de.dytanic.cloudnet.bridge.CloudProxy")
    ),
    CLOUDNET_2_MASTER(() -> createCloudSystem("eu.thesystems.cloud.cloudnet2.master.CloudNet2Master"), () -> checkClassExists("de.dytanic.cloudnetcore.CloudNet")),
    CLOUDNET_3_BUKKIT(() -> createCloudSystem("eu.thesystems.cloud.cloudnet3.wrapper.bukkit.CloudNet3Bukkit"),
            () -> checkClassExists("org.bukkit.Bukkit") &&
                    checkClassExists("de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetBridgePlugin")
    ),
    CLOUDNET_3_BUNGEE(() -> createCloudSystem("eu.thesystems.cloud.cloudnet3.wrapper.bungee.CloudNet3Bungee"),
            () -> checkClassExists("net.md_5.bungee.api.ProxyServer") &&
                    checkClassExists("de.dytanic.cloudnet.ext.bridge.bungee.BungeeCloudNetBridgePlugin")
    ),
    CLOUDNET_3_VELOCITY(() -> createCloudSystem("eu.thesystems.cloud.cloudnet3.wrapper.velocity.CloudNet3Velocity"),
            () -> checkClassExists("") &&
                    checkClassExists("de.dytanic.cloudnet.ext.bridge.velocity.VelocityCloudNetBridgePlugin")
    ),
    CLOUDNET_3_NODE(() -> createCloudSystem("eu.thesystems.cloud.cloudnet3.node.CloudNet3Node"), () -> checkClassExists("de.dytanic.cloudnet.CloudNet")),
    REFORMCLOUD_1_BUKKIT(() -> createCloudSystem("eu.thesystems.cloud."),
            () -> checkClassExists("org.bukkit.Bukkit") &&
                    checkClassExists("")
    ),
    REFORMCLOUD_1_BUNGEE(() -> createCloudSystem("eu.thesystems.cloud."),
            () -> checkClassExists("net.md_5.bungee.api.ProxyServer") &&
                    checkClassExists("")
    ),
    REFORMCLOUD_1_VELOCITY(() -> createCloudSystem("eu.thesystems.cloud."),
            () -> checkClassExists("") &&
                    checkClassExists("")
    ),
    REFORMCLOUD_1_CONTROLLER(() -> createCloudSystem("eu.thesystems.cloud."), () -> checkClassExists("")),
    REFORMCLOUD_1_CLIENT(() -> createCloudSystem("eu.thesystems.cloud."), () -> checkClassExists("")),
    REFORMCLOUD_2_BUKKIT(() -> createCloudSystem("eu.thesystems.cloud."),
            () -> checkClassExists("org.bukkit.Bukkit") &&
                    checkClassExists("")
    ),
    REFORMCLOUD_2_BUNGEE(() -> createCloudSystem("eu.thesystems.cloud."),
            () -> checkClassExists("net.md_5.bungee.api.ProxyServer") &&
                    checkClassExists("")
    ),
    REFORMCLOUD_2_NODE(() -> createCloudSystem("eu.thesystems.cloud."), () -> checkClassExists("")),
    REFORMCLOUD_2_CLIENT(() -> createCloudSystem("eu.thesystems.cloud."), () -> checkClassExists("")),
    REFORMCLOUD_2_CONTROLLER(() -> createCloudSystem("eu.thesystems.cloud."), () -> checkClassExists(""));

    private Supplier<CloudSystem> cloudSystemSupplier;
    private Supplier<Boolean> tester;

    SupportedCloudSystem(Supplier<CloudSystem> cloudSystemSupplier, Supplier<Boolean> tester) {
        this.cloudSystemSupplier = cloudSystemSupplier;
        this.tester = tester;
    }

    public boolean isUseable() {
        return this.tester != null && this.tester.get();
    }

    public CloudSystem createCloudSystem() {
        return this.cloudSystemSupplier != null ? this.cloudSystemSupplier.get() : null;
    }

    private static boolean checkClassExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static CloudSystem createCloudSystem(String className) {
        try {
            return (CloudSystem) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
