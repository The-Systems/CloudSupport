package eu.thesystems.cloud.modules;
/*
 * Created by derrop on 25.10.2019
 */

public interface Module {

    ModuleInfo getModuleInfo();

    ClassLoader getClassLoader();

}
