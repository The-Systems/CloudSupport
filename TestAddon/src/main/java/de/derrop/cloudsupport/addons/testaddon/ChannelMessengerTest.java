package de.derrop.cloudsupport.addons.testaddon;

import com.google.gson.JsonObject;
import eu.thesystems.cloud.CloudSystem;
import eu.thesystems.cloud.info.ProcessInfo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class ChannelMessengerTest {
    public static void test(CloudSystem cloud) {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                System.out.println("All processes: " + cloud.getProcesses().stream().map(ProcessInfo::getName).collect(Collectors.toList()));

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }

                if (cloud.getProcesses().isEmpty()) {
                    continue;
                }
                try {
                    System.out.println(cloud.getChannelMessenger().sendQueryChannelMessage("Lobby-1", "test channel from " + cloud.getOwnComponentName(), "msg to " + "Lobby-1 from Master", new JsonObject()).get(5, TimeUnit.SECONDS));
                } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                    exception.printStackTrace();
                }

                /*if (cloud.getComponentType() != SupportedCloudSystem.CLOUDNET_2_MASTER) {
                    try {
                        System.out.println("Response from master: " + cloud.getChannelMessenger().sendQueryChannelMessageToCloud(
                                "test-master-channel", "msg to master from " + cloud.getOwnComponentName(),
                                new JsonObject()).get()
                        );
                    } catch (InterruptedException | ExecutionException exception) {
                        exception.printStackTrace();
                    }

                    cloud.getChannelMessenger().sendChannelMessageToCloud("test master channel without query", "non query msg to master from " + cloud.getOwnComponentName(), new JsonObject());
                } else {
                    for (ProcessInfo process : cloud.getProcesses()) {
                        try {
                            System.out.println("Response from " + process.getName() + ": " + cloud.getChannelMessenger().sendQueryChannelMessage(
                                    process.getName(),
                                    "test-channel", "test-message to " + process.getName() + " from " + cloud.getOwnComponentName(),
                                    new JsonObject()).get()
                            );
                        } catch (InterruptedException | ExecutionException exception) {
                            exception.printStackTrace();
                        }
                    }
                }*/
            }
        }).start();
    }
}
