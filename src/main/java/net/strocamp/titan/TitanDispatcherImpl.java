package net.strocamp.titan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.DecimalFormat;

@Component
@Profile("raspberry")
public class TitanDispatcherImpl implements TitanDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(TitanDispatcherImpl.class);

    private String baseUrl;
    private RestTemplate restTemplate;

    @Value("${titan.wepapi.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        restTemplate = new RestTemplate();
    }

    @Override
    public String getVersion() {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/titan/get/System/SoftwareVersion");

        return restTemplate
                .getForObject(builder.build().encode().toString(), String.class);
    }

    @Override
    public String getShowName() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/titan/get/Show/ShowName");

        return restTemplate
                .getForObject(builder.build().encode().toString(), String.class);

    }

    @Override
    @Async
    public void firePlayback(int userNumber, float level, boolean alwaysRefire) {
        if (level < 0 || level > 1) {
            throw new IllegalArgumentException("Invalid value for level");
        }
        DecimalFormat formatter = new DecimalFormat("#.###");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/titan/script/Playbacks/FirePlaybackAtLevel")
                .queryParam("userNumber", userNumber)
                .queryParam("level", formatter.format(level))
                .queryParam("bool", alwaysRefire);
        try {
            restTemplate.exchange(builder.build().encode().toString(), HttpMethod.GET, null, Void.class);
            logger.info("Fired playback {} at level {}", userNumber, level);
        } catch (RestClientException e) {
            logger.error("Failed to fire playback on the titan", e);
        }
    }
}
