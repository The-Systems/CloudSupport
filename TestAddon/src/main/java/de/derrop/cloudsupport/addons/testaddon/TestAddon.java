package de.derrop.cloudsupport.addons.testaddon;
/*
 * Created by derrop on 16.11.2019
 */

import com.google.gson.JsonObject;
import eu.thesystems.cloud.addon.CloudAddon;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.event.EventHandler;
import eu.thesystems.cloud.global.events.channel.ChannelMessageReceiveEvent;
import eu.thesystems.cloud.global.info.ProcessInfo;
import eu.thesystems.cloud.proxy.ProxyLoginConfig;
import eu.thesystems.cloud.proxy.ProxyMOTD;
import eu.thesystems.cloud.proxy.ProxyTabList;
import eu.thesystems.cloud.proxy.ProxyTabListConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class TestAddon extends CloudAddon {
    @Override
    public void onEnable() {
        this.getCloud().getEventManager().registerListener(this);

        new Thread(() -> {
            while (!Thread.interrupted()) {
                System.out.println("All processes: " + this.getCloud().getProcesses().stream().map(ProcessInfo::getName).collect(Collectors.toList()));

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }

                if (this.getCloud().getProcesses().isEmpty()) {
                    continue;
                }
                String target = this.getCloud().getProcesses().toArray(new ProcessInfo[0])[0].getName();
                System.out.println(target);
                try {
                    System.out.println(this.getCloud().getChannelMessenger().sendQueryChannelMessage(target, "test channel from " + this.getCloud().getOwnComponentName(), "msg to " + target, new JsonObject()).get(5, TimeUnit.SECONDS));
                } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                    exception.printStackTrace();
                }

                /*if (this.getCloud().getComponentType() != SupportedCloudSystem.CLOUDNET_2_MASTER) {
                    try {
                        System.out.println("Response from master: " + this.getCloud().getChannelMessenger().sendQueryChannelMessageToCloud(
                                "test-master-channel", "msg to master from " + this.getCloud().getOwnComponentName(),
                                new JsonObject()).get()
                        );
                    } catch (InterruptedException | ExecutionException exception) {
                        exception.printStackTrace();
                    }

                    this.getCloud().getChannelMessenger().sendChannelMessageToCloud("test master channel without query", "non query msg to master from " + this.getCloud().getOwnComponentName(), new JsonObject());
                } else {
                    for (ProcessInfo process : this.getCloud().getProcesses()) {
                        try {
                            System.out.println("Response from " + process.getName() + ": " + this.getCloud().getChannelMessenger().sendQueryChannelMessage(
                                    process.getName(),
                                    "test-channel", "test-message to " + process.getName() + " from " + this.getCloud().getOwnComponentName(),
                                    new JsonObject()).get()
                            );
                        } catch (InterruptedException | ExecutionException exception) {
                            exception.printStackTrace();
                        }
                    }
                }*/
            }
        }).start();

        /*this.getCloud().getProxyManagement().addLoginConfig(new ProxyLoginConfig(
                "Test", true, 5, new ArrayList<>(Arrays.asList("Test")),
                Arrays.asList(new ProxyMOTD("a", "b", true, new String[]{"c", "d"}, "test", false, 1)),
                Arrays.asList(new ProxyMOTD("e", "f", true, new String[]{"c", "d"}, "test", false, 1)),
                true
        ));
        this.getCloud().getProxyManagement().addTabListConfig(new ProxyTabListConfig(
                "Test",
                new ProxyTabList[]{new ProxyTabList("a", "b")},
                1D,
                true
        ));

        System.out.println(Arrays.toString(this.getCloud().getProxyManagement().getLoginConfigs()));
        System.out.println(Arrays.toString(this.getCloud().getProxyManagement().getTabListConfigs()));*/
    }

    @EventHandler
    public void handle(ChannelMessageReceiveEvent event) {
        System.out.println("received message: " + event.getChannel() + ": " + event.getMessage() + " -> " + event.getData());
        System.out.println("query: " + event.isQuery());
        if (event.isQuery()) {
            JsonObject result = new JsonObject();
            result.addProperty(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            System.out.println("set result to: " + result);
            event.setQueryResult(result);
        }
    }

    @Override
    public void onDisable() {
        System.out.println("disable");
    }
}
