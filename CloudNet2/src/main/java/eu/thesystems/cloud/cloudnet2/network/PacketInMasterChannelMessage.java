package eu.thesystems.cloud.cloudnet2.network;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.events.channel.ChannelMessageReceiveEvent;

public class PacketInMasterChannelMessage extends PacketInHandler {

    @Override
    public void handleInput(Document document, PacketSender packetSender) {
        boolean query = document.getBoolean("query");

        JsonObject result = CloudSupport.getInstance().getSelectedCloudSystem().getEventManager().callEvent(new ChannelMessageReceiveEvent(
                document.getString("channel"), document.getString("message"),
                document.contains("data") ? document.get("data").getAsJsonObject() : null,
                query, null
        )).getQueryResult();

        if (query) {
            Document resultDoc = new Document().append("data", result);

            packetSender.sendPacket(new Packet(super.packetUniqueId, 0, resultDoc));
        }
    }
}
