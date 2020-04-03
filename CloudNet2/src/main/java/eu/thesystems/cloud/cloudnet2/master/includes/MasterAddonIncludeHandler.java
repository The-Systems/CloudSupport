package eu.thesystems.cloud.cloudnet2.master.includes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.web.server.WebServer;
import de.dytanic.cloudnet.web.server.handler.WebHandler;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.addon.CloudAddon;
import eu.thesystems.cloud.addon.CloudAddonInfo;
import eu.thesystems.cloud.cloudnet2.master.CloudNet2Master;
import eu.thesystems.cloud.event.EventHandler;
import eu.thesystems.cloud.events.channel.ChannelMessageReceiveEvent;
import eu.thesystems.cloud.info.ProcessType;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.InputStream;
import java.io.OutputStream;

public class MasterAddonIncludeHandler {

    private CloudNet2Master master;

    private final String path;
    private final String token;

    public MasterAddonIncludeHandler(CloudNet2Master master) {
        this.master = master;
        this.path = "/" + NetworkUtils.randomString(32);
        this.token = NetworkUtils.randomString(4096);

        master.getEventManager().registerListener(this);

        this.registerWebHandler(this.master.getCloudNet().getWebServer());
    }

    private void registerWebHandler(WebServer webServer) {
        webServer.getWebServerProvider().registerHandler(new WebHandler(this.path) {
            @Override
            public FullHttpResponse handleRequest(ChannelHandlerContext channelHandlerContext,
                                                  QueryDecoder queryDecoder,
                                                  PathProvider pathProvider,
                                                  HttpRequest httpRequest) throws Exception {
                FullHttpResponse response = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.NOT_FOUND);

                if (!httpRequest.headers().contains("Token") || !httpRequest.headers().get("Token").equals(MasterAddonIncludeHandler.this.token)) {
                    return response;
                }

                if (!queryDecoder.getQueryParams().containsKey("addon")) {
                    response.setStatus(HttpResponseStatus.BAD_REQUEST);
                    return response;
                }

                CloudAddon addon = CloudSupport.getInstance().getAddonManager().getLoadedAddons().stream()
                        .filter(CloudAddon::isEnabled)
                        .filter(cloudAddon -> cloudAddon.getAddonInfo().getName().equals(queryDecoder.getQueryParams().get("addon")))
                        .findFirst()
                        .orElse(null);

                if (addon == null) {
                    response.setStatus(HttpResponseStatus.BAD_REQUEST);
                    return response;
                }

                response.setStatus(HttpResponseStatus.OK);

                try (InputStream inputStream = addon.getAddonInfo().getUrl().openStream();
                     OutputStream outputStream = new ByteBufOutputStream(response.content())) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                }

                return response;
            }
        });
    }

    @EventHandler
    public void handleQueryChannelMessage(ChannelMessageReceiveEvent event) {
        if (!event.isQuery() || !event.getChannel().equals("CloudSupport-Internal-Addon-Inclusion-Channel")) {
            return;
        }

        if (event.getMessage().equals("webAccess")) {
            WebServer webServer = this.master.getCloudNet().getWebServer();
            String url = "http://" + webServer.getAddress() + ":" + webServer.getPort() + path + "?addon={addon}";
            JsonObject result = new JsonObject();
            result.addProperty("url", url);
            result.addProperty("token", token);
            event.setQueryResult(result);
        } else if (event.getMessage().equals("listAddons")) {
            ProcessType type = ProcessType.valueOf(event.getData().get("type").getAsString());

            String[] addons = CloudSupport.getInstance().getAddonManager().getLoadedAddons().stream()
                    .filter(CloudAddon::isEnabled)
                    .map(CloudAddon::getAddonInfo)
                    .filter(addonInfo -> addonInfo.shouldBeCopiedToServer(type))
                    .map(CloudAddonInfo::getName)
                    .toArray(String[]::new);

            JsonObject result = new JsonObject();
            JsonArray array = new JsonArray();
            for (String addon : addons) {
                array.add(new JsonPrimitive(addon));
            }
            result.add("addons", array);
            event.setQueryResult(result);
        }
    }

}
