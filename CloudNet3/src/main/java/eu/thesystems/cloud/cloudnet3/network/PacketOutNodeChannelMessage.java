package eu.thesystems.cloud.cloudnet3.network;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.network.protocol.Packet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

public class PacketOutNodeChannelMessage extends Packet {
    public static final int CHANNEL = 9827346;

    public PacketOutNodeChannelMessage(String channel, String message, JsonObject data, boolean query, UUID queryId) {
        super(CHANNEL, new JsonDocument().append("channel", channel).append("message", message).append("data", data.toString()).append("query", query).append("queryId", queryId).append("result", false));
    }

    public PacketOutNodeChannelMessage(UUID queryId, JsonObject result) {
        super(CHANNEL, new JsonDocument().append("queryId", queryId).append("result", true).append("data", result != null ? result.toString() : null));
    }
}
