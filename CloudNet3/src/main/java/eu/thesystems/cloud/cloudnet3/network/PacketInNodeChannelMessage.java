package eu.thesystems.cloud.cloudnet3.network;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.IPacketListener;
import eu.thesystems.cloud.GsonUtil;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.events.channel.ChannelMessageReceiveEvent;

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
        UUID queryId = document.get("queryId", UUID.class);
        boolean query = document.getBoolean("query") && queryId != null;
        boolean isResult = document.getBoolean("result");

        if (isResult && queryId != null) {
            this.cloudNet3.getChannelMessenger().completeQuery(queryId, data);
            return;
        }
        JsonObject result = this.cloudNet3.getEventManager().callEvent(new ChannelMessageReceiveEvent(channel, message, data, query, null))
                .getQueryResult();

        if (query) {
            networkChannel.sendPacket(new PacketOutNodeChannelMessage(queryId, result));
        }
    }
}
