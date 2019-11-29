package eu.thesystems.cloud.event;
/*
 * Created by Mc_Ruben on 06.01.2019
 */

public class EventPriority {

    private EventPriority() {
        throw new UnsupportedOperationException();
    }

    public static final byte LOWEST = -2, LOW = -1, NORMAL = 0, HIGH = 1, HIGHEST = 2;

}
