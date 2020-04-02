package eu.thesystems.cloud.cloudnet3.cluster.packet;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.network.protocol.Packet;
import eu.thesystems.cloud.cloudnet3.cluster.ClusterPacketReceiver;

public class PacketClusterOutRedirectPacket extends Packet {

    public static final int CHANNEL = 7826323;

    public PacketClusterOutRedirectPacket(ClusterPacketReceiver sender, ClusterPacketReceiver receiver, IPacket packet) {
        this(sender, new ClusterPacketReceiver[]{receiver}, packet);
    }

    public PacketClusterOutRedirectPacket(ClusterPacketReceiver sender, ClusterPacketReceiver[] receivers, IPacket packet) {
        super(CHANNEL, new JsonDocument().append("channel", packet.getChannel()).append("header", packet.getHeader()).append("receivers", receivers).append("sender", sender), packet.getBody());
    }
}
