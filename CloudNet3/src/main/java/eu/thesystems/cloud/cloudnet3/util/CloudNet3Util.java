package eu.thesystems.cloud.cloudnet3.util;
/*
 * Created by derrop on 16.11.2019
 */

import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import eu.thesystems.cloud.converter.CloudObjectConverter;
import eu.thesystems.cloud.global.info.ProcessInfo;

public class CloudNet3Util {

    private CloudNet3Util() {
        throw new UnsupportedOperationException();
    }

    public static boolean isServer(ServiceInfoSnapshot serviceInfoSnapshot) {
        return serviceInfoSnapshot.getConfiguration().getProcessConfig().getEnvironment().isMinecraftJavaServer() ||
                serviceInfoSnapshot.getConfiguration().getProcessConfig().getEnvironment().isMinecraftBedrockServer();
    }

    public static boolean isProxy(ServiceInfoSnapshot serviceInfoSnapshot) {
        return serviceInfoSnapshot.getConfiguration().getProcessConfig().getEnvironment().isMinecraftJavaProxy() ||
                serviceInfoSnapshot.getConfiguration().getProcessConfig().getEnvironment().isMinecraftBedrockProxy();
    }

    public static ProcessInfo getProcessInfoFromService(CloudObjectConverter converter, ServiceInfoSnapshot serviceInfoSnapshot) {
        if (serviceInfoSnapshot == null) {
            return null;
        }
        if (isServer(serviceInfoSnapshot)) {
            return converter.convertServerInfo(serviceInfoSnapshot);
        } else if (isProxy(serviceInfoSnapshot)) {
            return converter.convertProxyInfo(serviceInfoSnapshot);
        } else {
            return converter.convertProcessInfo(serviceInfoSnapshot);
        }
    }

}
