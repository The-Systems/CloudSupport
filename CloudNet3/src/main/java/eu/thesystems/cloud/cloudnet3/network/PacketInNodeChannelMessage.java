package eu.thesystems.cloud.cloudnet3.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListener;
import eu.thesystems.cloud.GsonUtil;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.global.events.channel.ChannelMessageReceiveEvent;

import java.util.UUID;

public class PacketInNodeChannelMessage implements IPacketListener {

    private CloudNet3 cloudNet3;

    public PacketInNodeChannelMessage(CloudNet3 cloudNet3) {
        this.cloudNet3 = cloudNet3;
    }

    @Override
    public void handle(INetworkChannel networkChannel, IPacket packet) throws Exception {
        JsonDocument document = packet.getHeader();

        String channel = document.getString("channel");
        String message = document.getString("message");
        JsonObject data = GsonUtil.parseString(document.getString("data")).getAsJsonObject();
        boolean query = document.getBoolean("query");
        UUID queryId = document.get("queryId", UUID.class);

        JsonObject result = this.cloudNet3.getEventManager().callEvent(new ChannelMessageReceiveEvent(channel, message, data, query, null))
                .getQueryResult();

        this.cloudNet3.getChannelMessenger().completeQuery(queryId, result);
    }
}
