package eu.thesystems.cloud.addon.dependency;
/*
 * Created by derrop on 16.11.2019
 */

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MavenDependency {
    private String groupId;
    private String artifactId;
    private String version;
    private String repository;
}
