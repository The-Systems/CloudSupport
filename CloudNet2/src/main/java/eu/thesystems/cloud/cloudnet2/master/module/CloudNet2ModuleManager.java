package eu.thesystems.cloud.cloudnet2.master.module;
/*
 * Created by derrop on 25.10.2019
 */

import de.dytanic.cloudnet.modules.ModuleClassLoader;
import de.dytanic.cloudnetcore.CloudNet;
import eu.thesystems.cloud.cloudnet2.CloudNet2;
import eu.thesystems.cloud.exception.CloudSupportException;
import eu.thesystems.cloud.modules.Module;
import eu.thesystems.cloud.modules.ModuleInfo;
import eu.thesystems.cloud.modules.ModuleManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class CloudNet2ModuleManager implements ModuleManager {

    private CloudNet cloudNet;
    private CloudNet2 cloudNet2;

    public CloudNet2ModuleManager(CloudNet cloudNet, CloudNet2 cloudNet2) {
        this.cloudNet = cloudNet;
        this.cloudNet2 = cloudNet2;
    }

    @Override
    public ModuleInfo loadModule(Path file) {
        throw new CloudSupportException(this.cloudNet2);
    }

    @Override
    public void unloadModule(ModuleInfo moduleInfo) {
        throw new CloudSupportException(this.cloudNet2);
    }

    @Override
    public void enableModule(Module module) {
        throw new CloudSupportException(this.cloudNet2);
    }

    @Override
    public void disableModule(ModuleInfo moduleInfo) {
        this.getCloudNetModule(moduleInfo).ifPresent(module -> this.cloudNet.getModuleManager().disableModule(module));
    }

    @Override
    public void deleteModule(ModuleInfo moduleInfo) {
        this.disableModule(moduleInfo);
        try {
            Files.deleteIfExists(moduleInfo.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ModuleInfo getModuleInfoFromClassloader(ClassLoader classLoader) {
        return classLoader instanceof ModuleClassLoader ? this.cloudNet2.getConverter().convertModuleInfo(((ModuleClassLoader) classLoader).getConfig()) : null;
    }

    @Override
    public ModuleInfo installModule(String url, Path path) {
        throw new CloudSupportException(this.cloudNet2);
    }

    private Optional<de.dytanic.cloudnet.modules.Module> getCloudNetModule(ModuleInfo moduleInfo) {
        return this.cloudNet.getModuleManager().getModules().stream()
                .filter(module -> module.getName().equals(moduleInfo.getName()))
                .filter(module -> module.getAuthor().equals(moduleInfo.getAuthors()[0]))
                .filter(module -> module.getVersion().equals(moduleInfo.getVersion()))
                .findFirst();
    }
}
