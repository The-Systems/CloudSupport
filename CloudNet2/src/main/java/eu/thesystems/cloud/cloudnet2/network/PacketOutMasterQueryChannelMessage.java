package eu.thesystems.cloud.cloudnet2.network;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketOutMasterQueryChannelMessage extends Packet {
    public static final int ID = 8927436;

    public PacketOutMasterQueryChannelMessage(String channel, String message, JsonObject data) {
        super(ID, new Document().append("channel", channel).append("message", message).append("data", data));
    }
}
