package eu.thesystems.cloud.addon;
/*
 * Created by derrop on 16.11.2019
 */

import eu.thesystems.cloud.addon.dependency.MavenDependency;
import eu.thesystems.cloud.detection.SupportedCloudSystem;
import eu.thesystems.cloud.info.ProcessType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CloudAddonInfo {

    private String name;
    private String version;
    private String main;
    private String[] authors;
    private ProcessType[] copyToProcesses;
    private Collection<MavenDependency> dependencies;
    private SupportedCloudSystem[] availableCloudSystems; // todo should throw an exception if this addon is loaded on a cloud system that is not supported (only if this array is not null)
    private transient URL url;
    private transient URLClassLoader classLoader;

    public void setUrl(URL url) {
        if (this.url != null) {
            throw new IllegalStateException();
        }
        this.url = url;
    }

    public void setClassLoader(URLClassLoader classLoader) {
        if (this.classLoader != null) {
            throw new IllegalStateException();
        }
        this.classLoader = classLoader;
    }

    public boolean shouldBeCopiedToServer(ProcessType type) {
        if (this.copyToProcesses == null) {
            return false;
        }
        Collection<ProcessType> types = Arrays.asList(this.copyToProcesses);
        return types.contains(ProcessType.ALL) || types.contains(type);
    }

}
