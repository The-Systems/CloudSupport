package de.derrop.cloudsupport.addons.testaddon;
/*
 * Created by derrop on 16.11.2019
 */

import eu.thesystems.cloud.addon.CloudAddon;
import eu.thesystems.cloud.proxy.ProxyLoginConfig;
import eu.thesystems.cloud.proxy.ProxyMOTD;
import eu.thesystems.cloud.proxy.ProxyTabList;
import eu.thesystems.cloud.proxy.ProxyTabListConfig;

import java.util.ArrayList;
import java.util.Arrays;

public class TestAddon extends CloudAddon {
    @Override
    public void onEnable() {
        System.out.println(this.getCloud().getProcesses());

        this.getCloud().getProxyManagement().addLoginConfig(new ProxyLoginConfig(
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
        System.out.println(Arrays.toString(this.getCloud().getProxyManagement().getTabListConfigs()));
    }

    @Override
    public void onDisable() {
        System.out.println("disable");
    }
}
