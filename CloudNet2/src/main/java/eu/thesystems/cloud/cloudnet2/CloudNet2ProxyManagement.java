package eu.thesystems.cloud.cloudnet2;

import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.proxylayout.*;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyGroupMode;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import de.dytanic.cloudnetcore.CloudNet;
import eu.thesystems.cloud.proxy.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public abstract class CloudNet2ProxyManagement implements ProxyManagement {

    protected ProxyGroup createDefaultProxyGroup(String name) {
        return new ProxyGroup(
                name,
                new ArrayList<>(CloudNet.getInstance().getWrappers().keySet()),
                new Template("default", TemplateResource.LOCAL, null, new String[0], new ArrayList<>()),
                ProxyVersion.BUNGEECORD,
                25565,
                0,
                256,
                new ProxyConfig(
                        true, true,
                        new ArrayList<>(),
                        new Motd("", ""), "§cMaintenance",
                        -1, false,
                        new AutoSlot(-1, false),
                        new TabList(false, "", ""),
                        new String[0], new ArrayList<>(),
                        new DynamicFallback("Lobby", new ArrayList<>())
                ),
                ProxyGroupMode.DYNAMIC,
                new WrappedMap()
        );
    }

    protected ProxyLoginConfig convertFromCloudNetLoginConfig(ProxyGroup proxyGroup) {
        return proxyGroup == null ? null : new ProxyLoginConfig(
                proxyGroup.getName(),
                proxyGroup.getProxyConfig().isMaintenance(),
                proxyGroup.getProxyConfig().getMaxPlayers(),
                new ArrayList<>(proxyGroup.getProxyConfig().getWhitelist()),
                proxyGroup.getProxyConfig().getMotdsLayouts().stream()
                        .map(motd -> this.convertFromCloudNetMotd(motd,
                                proxyGroup.getProxyConfig().getPlayerInfo(), null,
                                proxyGroup.getProxyConfig().getAutoSlot().isEnabled(), proxyGroup.getProxyConfig().getAutoSlot().getDynamicSlotSize())
                        )
                        .collect(Collectors.toList()),
                Collections.singletonList(this.convertFromCloudNetMotd(
                        proxyGroup.getProxyConfig().getMaintenanceMotdLayout(),
                        proxyGroup.getProxyConfig().getPlayerInfo(),
                        proxyGroup.getProxyConfig().getMaintenaceProtocol(),
                        false, -1
                )),
                false
        );
    }

    protected ProxyMOTD convertFromCloudNetMotd(Motd motd, String[] playerInfo, String protocolText, boolean autoSlot, int autoSlotDistance) {
        return new ProxyMOTD(
                motd.getFirstLine(),
                motd.getSecondLine(),
                true,
                playerInfo, protocolText,
                autoSlot, autoSlotDistance
        );
    }

    protected ProxyTabListConfig convertFromCloudNetTabList(String group, TabList tabList) {
        return new ProxyTabListConfig(
                group,
                tabList != null && tabList.isEnabled() ? new ProxyTabList[]{new ProxyTabList(tabList.getHeader(), tabList.getFooter())} : new ProxyTabList[0],
                0,
                false
        );
    }
}
