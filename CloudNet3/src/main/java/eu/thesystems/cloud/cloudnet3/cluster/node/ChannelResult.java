package eu.thesystems.cloud.cloudnet3.cluster.node;

import eu.thesystems.cloud.cloudnet3.cluster.PacketSendResult;

public class ChannelResult<T> {

    private PacketSendResult result;
    private T alternative;

    public ChannelResult(PacketSendResult result, T alternative) {
        this.result = result;
        this.alternative = alternative;
    }

    public PacketSendResult getResult() {
        return result;
    }

    public T getAlternative() {
        return this.alternative;
    }
}