package eu.thesystems.cloud.global.network;
/*
 * Created by derrop on 25.10.2019
 */

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class NetworkAddress {

    private String host;
    private int port;

}
