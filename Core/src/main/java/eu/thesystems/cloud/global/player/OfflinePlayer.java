package eu.thesystems.cloud.global.player;
/*
 * Created by derrop on 25.10.2019
 */

import eu.thesystems.cloud.global.network.NetworkAddress;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class OfflinePlayer {
    private UUID uniqueId;
    private String name;
    private NetworkAddress lastAddress;
    private long firstLogin;
    private long lastLogin;
}
