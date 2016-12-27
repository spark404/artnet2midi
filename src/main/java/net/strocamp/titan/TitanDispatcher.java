package net.strocamp.titan;

/**
 * Created by htrippaers on 27/12/2016.
 */
public interface TitanDispatcher {
    String getVersion();

    String getShowName();

    void firePlayback(int userNumber, float level, boolean alwaysRefire) throws Exception;
}
