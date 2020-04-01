package eu.thesystems.cloud.cloudnet3.cluster;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.Packet;

public class PacketClusterOutRedirectPacket extends Packet {

    public static final int CHANNEL = 7826323;

    public PacketClusterOutRedirectPacket(ClusterPacketReceiver receiver, IPacket packet) {
        this(new ClusterPacketReceiver[]{receiver}, packet);
    }

    public PacketClusterOutRedirectPacket(ClusterPacketReceiver[] receivers, IPacket packet) {
        super(CHANNEL, new JsonDocument().append("channel", packet.getChannel()).append("header", packet.getHeader()).append("receivers", receivers), packet.getBody());
    }
}
