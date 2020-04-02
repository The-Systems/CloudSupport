package de.derrop.cloudsupport.addons.testaddon;

import eu.thesystems.cloud.CloudSystem;
import eu.thesystems.cloud.proxy.ProxyLoginConfig;
import eu.thesystems.cloud.proxy.ProxyMOTD;
import eu.thesystems.cloud.proxy.ProxyTabList;
import eu.thesystems.cloud.proxy.ProxyTabListConfig;

import java.util.ArrayList;
import java.util.Arrays;

public class ProxyManagementTest {

    public static void test(CloudSystem cloud) {
        cloud.getProxyManagement().addLoginConfig(new ProxyLoginConfig(
                "Test", true, 5, new ArrayList<>(Arrays.asList("Test")),
                Arrays.asList(new ProxyMOTD("a", "b", true, new String[]{"c", "d"}, "test", false, 1)),
                Arrays.asList(new ProxyMOTD("e", "f", true, new String[]{"c", "d"}, "test", false, 1)),
                true
        ));
        cloud.getProxyManagement().addTabListConfig(new ProxyTabListConfig(
                "Test",
                new ProxyTabList[]{new ProxyTabList("a", "b")},
                1D,
                true
        ));

        System.out.println(Arrays.toString(cloud.getProxyManagement().getLoginConfigs()));
        System.out.println(Arrays.toString(cloud.getProxyManagement().getTabListConfigs()));
    }
    
}
