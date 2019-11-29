package eu.thesystems.cloud.addon.loader;
/*
 * Created by derrop on 16.11.2019
 */

import java.net.URL;

public class InvalidAddonInfoException extends RuntimeException {

    private URL url;

    public InvalidAddonInfoException(URL url, String message) {
        super(message);
        this.url = url;
    }

    public URL getUrl() {
        return this.url;
    }
}
