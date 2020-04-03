package eu.thesystems.cloud.cloudnet2.master.includes;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnet.lib.service.plugin.PluginResourceType;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutStartProxy;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutStartServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.lang.reflect.Field;

public class MasterServerTemplateModifier extends ChannelOutboundHandlerAdapter {

    private static final Field PACKET_DATA_FIELD;

    static {
        try {
            PACKET_DATA_FIELD = Packet.class.getDeclaredField("data");

            PACKET_DATA_FIELD.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            throw new Error("Failed load fields for template modifiers", exception);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Packet packet = (Packet) msg;

        if (packet instanceof PacketOutStartServer) {

            Document data = (Document) PACKET_DATA_FIELD.get(packet);
            ServerProcessMeta serverProcess = data.getObject("serverProcess", ServerProcessMeta.class);

            serverProcess.getDownloadablePlugins().add(this.getPlugin());

            PACKET_DATA_FIELD.set(packet, new Document("serverProcess", serverProcess));

        } else if (packet instanceof PacketOutStartProxy) {

            Document data = (Document) PACKET_DATA_FIELD.get(packet);
            ProxyProcessMeta proxyProcess = data.getObject("proxyProcess", ProxyProcessMeta.class);

            proxyProcess.getDownloadablePlugins().add(this.getPlugin());

            PACKET_DATA_FIELD.set(packet, new Document("proxyProcess", proxyProcess));
        }

        super.write(ctx, msg, promise);
    }

    private ServerInstallablePlugin getPlugin() {
        return new ServerInstallablePlugin("CloudSupport", PluginResourceType.MASTER, null);
    }

}
