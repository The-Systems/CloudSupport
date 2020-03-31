package eu.thesystems.cloud.cloudnet3.node.cluster.node;

import eu.thesystems.cloud.cloudnet3.node.cluster.ClusterPacketReceiver;

public class IndexedPacketReceiver {

    private int index;
    private ClusterPacketReceiver receiver;

    public IndexedPacketReceiver(int index, ClusterPacketReceiver receiver) {
        this.index = index;
        this.receiver = receiver;
    }

    public int getIndex() {
        return index;
    }

    public ClusterPacketReceiver getReceiver() {
        return receiver;
    }
}