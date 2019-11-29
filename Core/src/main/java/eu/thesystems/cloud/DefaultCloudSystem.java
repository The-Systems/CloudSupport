package eu.thesystems.cloud;
/*
 * Created by derrop on 16.11.2019
 */

import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.event.EventManager;
import eu.thesystems.cloud.exception.CloudSupportException;
import eu.thesystems.cloud.global.command.CommandMap;

public abstract class DefaultCloudSystem implements CloudSystem {

    private SupportedCloudSystem componentType;
    private EventManager eventManager;
    private String name;
    private String version;

    public DefaultCloudSystem(SupportedCloudSystem componentType, EventManager eventManager, String name, String version) {
        this.componentType = componentType;
        this.eventManager = eventManager;
        this.name = name;
        this.version = version;
    }

    @Override
    public SupportedCloudSystem getComponentType() {
        return this.componentType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getInstalledVersion() {
        return this.version;
    }

    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public CommandMap getCommandMap() {
        throw new CloudSupportException(this);
    }
}
