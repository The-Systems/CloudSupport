package eu.thesystems.cloud.proxy;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ProxyMOTD {

    private String firstLine;
    private String secondLine;

    /**
     * Defines whether this implementation supports the playerInfo, protocolText,
     * autoSlot and autoSlotDistance parameters or not.
     */
    private boolean supportsExtendedMotd;

    private String[] playerInfo;
    private String protocolText;
    private boolean autoSlot;
    private int autoSlotDistance;

    public ProxyMOTD(String firstLine, String secondLine, boolean supportsExtendedMotd, String[] playerInfo, String protocolText, boolean autoSlot, int autoSlotDistance) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.supportsExtendedMotd = supportsExtendedMotd;
        this.playerInfo = playerInfo;
        this.protocolText = protocolText;
        this.autoSlot = autoSlot;
        this.autoSlotDistance = autoSlotDistance;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }

    public boolean isSupportsExtendedMotd() {
        return supportsExtendedMotd;
    }

    public String[] getPlayerInfo() {
        return playerInfo;
    }

    public String getProtocolText() {
        return protocolText;
    }

    public boolean isAutoSlot() {
        return autoSlot;
    }

    public int getAutoSlotDistance() {
        return autoSlotDistance;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public void setSecondLine(String secondLine) {
        this.secondLine = secondLine;
    }

    public void setPlayerInfo(String[] playerInfo) {
        this.playerInfo = playerInfo;
    }

    public void setProtocolText(String protocolText) {
        this.protocolText = protocolText;
    }

    public void setAutoSlot(boolean autoSlot) {
        this.autoSlot = autoSlot;
    }

    public void setAutoSlotDistance(int autoSlotDistance) {
        this.autoSlotDistance = autoSlotDistance;
    }
}
