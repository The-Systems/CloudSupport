package eu.thesystems.cloud.cloudnet3.module;
/*
 * Created by derrop on 26.10.2019
 */

import de.dytanic.cloudnet.driver.module.IModuleProvider;
import de.dytanic.cloudnet.driver.module.IModuleWrapper;
import eu.thesystems.cloud.cloudnet3.CloudNet3;
import eu.thesystems.cloud.modules.Module;
import eu.thesystems.cloud.modules.ModuleInfo;
import eu.thesystems.cloud.modules.ModuleManager;

import java.nio.file.Path;

public class CloudNet3ModuleManager implements ModuleManager {
    // todo
    private CloudNet3 cloudNet3;
    private IModuleProvider moduleProvider;

    public CloudNet3ModuleManager(CloudNet3 cloudNet3, IModuleProvider moduleProvider) {
        this.cloudNet3 = cloudNet3;
        this.moduleProvider = moduleProvider;
    }

    @Override
    public ModuleInfo loadModule(Path file) {
        return null;
    }

    @Override
    public void unloadModule(ModuleInfo moduleInfo) {

    }

    @Override
    public void enableModule(Module module) {

    }

    @Override
    public void disableModule(ModuleInfo moduleInfo) {

    }

    @Override
    public void deleteModule(ModuleInfo moduleInfo) {

    }

    @Override
    public ModuleInfo getModuleInfoFromClassloader(ClassLoader classLoader) {
        for (IModuleWrapper module : this.moduleProvider.getModules()) {
            if (module.getClassLoader() != null && module.getClassLoader().equals(classLoader)) {
                return this.cloudNet3.getConverter().convertModuleInfo(module.getModuleConfiguration());
            }
        }
        return null;
    }
}
