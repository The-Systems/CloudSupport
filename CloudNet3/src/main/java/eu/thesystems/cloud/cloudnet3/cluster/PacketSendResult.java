package eu.thesystems.cloud.cloudnet3.cluster;

public enum PacketSendResult {
    SUCCESS, RECEIVER_NOT_FOUND, RECEIVER_OFFLINE, INVALID_TARGET_TYPE;

    public boolean asBoolean() {
        return this == SUCCESS;
    }

}
