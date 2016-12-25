package net.strocamp.titan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.DecimalFormat;

@Component
public class TitanDispatcher {

    private String baseUrl;

    @Value("${titan.wepapi.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getVersion() {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/titan/get/System/SoftwareVersion");

        return restTemplate
                .getForObject(builder.build().encode().toString(), String.class);
    }

    public String getShowName() {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/titan/get/Show/ShowName");

        return restTemplate
                .getForObject(builder.build().encode().toString(), String.class);

    }

    public void firePlayback(int userNumber, float level, boolean alwaysRefire) throws Exception {
        if (level < 0 || level > 1) {
            throw new Exception("Invalid value for level");
        }
        DecimalFormat formatter = new DecimalFormat("#.###");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/titan/script/Playbacks/FirePlaybackAtLevel")
                .queryParam("userNumber", userNumber)
                .queryParam("level", formatter.format(level))
                .queryParam("bool", alwaysRefire);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(builder.build().encode().toString(), String.class);
    }
}
