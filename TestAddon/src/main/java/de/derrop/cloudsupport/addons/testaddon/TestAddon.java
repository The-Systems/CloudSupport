package de.derrop.cloudsupport.addons.testaddon;
/*
 * Created by derrop on 16.11.2019
 */

import com.google.gson.JsonObject;
import eu.thesystems.cloud.addon.CloudAddon;
import eu.thesystems.cloud.event.EventHandler;
import eu.thesystems.cloud.events.channel.ChannelMessageReceiveEvent;

import java.util.UUID;

public class TestAddon extends CloudAddon {
    @Override
    public void onEnable() {
        this.getCloud().getEventManager().registerListener(this);

        PermissionProviderTest.test(super.getCloud());
    }

    @EventHandler
    public void handle(ChannelMessageReceiveEvent event) {
        System.out.println("received message: " + event.getChannel() + ": " + event.getMessage() + " -> " + event.getData() + " (Query: " + event.isQuery() + ")");
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
