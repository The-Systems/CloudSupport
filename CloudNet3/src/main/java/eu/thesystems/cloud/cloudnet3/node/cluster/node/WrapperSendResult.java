package eu.thesystems.cloud.cloudnet3.node.cluster.node;

import de.dytanic.cloudnet.cluster.IClusterNodeServer;
import eu.thesystems.cloud.cloudnet3.node.cluster.PacketSendResult;

public class WrapperSendResult {

    private PacketSendResult result;
    private IClusterNodeServer server;

    public WrapperSendResult(PacketSendResult result, IClusterNodeServer server) {
        this.result = result;
        this.server = server;
    }

    public PacketSendResult getResult() {
        return result;
    }

    public IClusterNodeServer getServer() {
        return server;
    }
}