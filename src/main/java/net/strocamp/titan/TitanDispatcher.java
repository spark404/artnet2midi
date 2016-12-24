package net.strocamp.titan;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.text.DecimalFormat;

@Component
public class TitanDispatcher {

    public String getVersion() {
        WebClient restClient = WebClient.create("http://192.168.168.107:4430");
        String apiVersion = restClient
                .path("/titan/get/System/SoftwareVersion")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(String.class);
        return apiVersion;
    }

    public String getShowName() {
        WebClient restClient = WebClient.create("http://192.168.168.107:4430");
        String apiVersion = restClient
                .path("/titan/get/Show/ShowName")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(String.class);
        return apiVersion;

    }

    public void firePlayback(int userNumber, float level, boolean alwaysRefire) throws Exception {
        if (level < 0 || level > 1) {
            throw new Exception("Invalid value for level");
        }
        DecimalFormat formatter = new DecimalFormat("#.###");

        WebClient restClient = WebClient.create("http://192.168.168.107:4430");
        restClient
                .path("/titan/script/Playbacks/FirePlaybackAtLevel")
                .query("userNumber",userNumber)
                .query("level", formatter.format(level))
                .query("bool",alwaysRefire)
                .get();
    }
}
