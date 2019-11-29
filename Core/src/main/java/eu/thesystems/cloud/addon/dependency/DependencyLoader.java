package eu.thesystems.cloud.addon.dependency;
/*
 * Created by derrop on 16.11.2019
 */

import java.io.IOException;
import java.net.URL;

public interface DependencyLoader {

    URL loadDependency(MavenDependency dependency) throws IOException;

}
