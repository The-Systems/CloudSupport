package eu.thesystems.cloud.event.defaults;
/*
 * Created by Mc_Ruben on 11.11.2018
 */

import eu.thesystems.cloud.CloudSupport;
import eu.thesystems.cloud.event.CloudEvent;
import eu.thesystems.cloud.exception.CloudSupportException;
import eu.thesystems.cloud.modules.ModuleInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
class ListenerMethod {
    private Object listener;
    private Method method;
    private byte priority;

    void invoke(CloudEvent event) {
        try {
            this.method.invoke(this.listener, event);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public ModuleInfo getModuleInfo() {
        ClassLoader classLoader = this.listener.getClass().getClassLoader();
        try {
            return CloudSupport.getInstance().getSelectedCloudSystem().getModuleManager().getModuleInfoFromClassloader(classLoader);
        } catch (CloudSupportException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String name() {
        ModuleInfo moduleInfo = this.getModuleInfo();
        return moduleInfo != null ? this.listener.getClass().getName() + ":" + this.method.getName() + "@" + moduleInfo.getName() :
                this.listener.getClass().getName() + ":" + this.method.getName();
    }

}
