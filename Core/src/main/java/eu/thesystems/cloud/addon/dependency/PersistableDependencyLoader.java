package eu.thesystems.cloud.addon.dependency;
/*
 * Created by derrop on 16.11.2019
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class PersistableDependencyLoader extends MemoryDependencyLoader {

    private Path baseDirectory;

    public PersistableDependencyLoader(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public Path getBaseDirectory() {
        return baseDirectory;
    }

    @Override
    public URL loadDependency(MavenDependency dependency) throws IOException {
        Path dependencyPath = this.getBaseDirectory()
                .resolve(dependency.getGroupId().replace('.', File.separatorChar))
                .resolve(dependency.getArtifactId())
                .resolve(dependency.getVersion())
                .resolve(dependency.getArtifactId() + "-" + dependency.getVersion() + ".jar");

        if (!Files.exists(dependencyPath)) {
            Files.createDirectories(dependencyPath.getParent());
            URL url = super.loadDependency(dependency);
            try (InputStream inputStream = url.openStream()) {
                Files.copy(inputStream, dependencyPath);
            }
        }
        return dependencyPath.toUri().toURL();
    }

}
