package eu.thesystems.cloud.addon.dependency;
/*
 * Created by derrop on 16.11.2019
 */

import java.io.IOException;
import java.net.URL;

public class MemoryDependencyLoader implements DependencyLoader {
    @Override
    public URL loadDependency(MavenDependency dependency) throws IOException {
        String repository = dependency.getRepository().endsWith("/") ? dependency.getRepository() : dependency.getRepository() + "/";
        String groupId = dependency.getGroupId().replace('.', '/');
        return new URL(
                repository + groupId + "/" + dependency.getArtifactId() + "/" + dependency.getVersion() + "/" +
                        dependency.getArtifactId() + "-" + dependency.getVersion() + ".jar"
        );
    }
}
