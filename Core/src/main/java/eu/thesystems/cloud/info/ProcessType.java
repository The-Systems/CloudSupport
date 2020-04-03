package eu.thesystems.cloud.info;

public enum ProcessType {
    VELOCITY(true),
    WATERDOG(true),
    BUNGEE_CORD(true),
    MINECRAFT_SERVER(false),
    NUKKIT(false),
    GLOWSTONE(false),
    ALL(false);

    private boolean proxy;

    ProcessType(boolean proxy) {
        this.proxy = proxy;
    }

    public boolean isProxy() {
        return this.proxy;
    }

    public boolean isServer() {
        return !this.isProxy();
    }

}
