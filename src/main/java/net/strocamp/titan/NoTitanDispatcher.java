package net.strocamp.titan;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("testing")
public class NoTitanDispatcher implements TitanDispatcher {
    @Override
    public String getVersion() {
        return "Simulator";
    }

    @Override
    public String getShowName() {
        return "Simulator Show";
    }

    @Override
    public void firePlayback(int userNumber, float level, boolean alwaysRefire) throws Exception {

    }
}
