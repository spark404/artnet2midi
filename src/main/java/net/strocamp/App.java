package net.strocamp;

import net.strocamp.core.JettyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App
{
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws Exception
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("config.xml");

        logger.info("Starting embedded Jetty");
        // Embedded jetty
        new Thread() {
            @Override
            public void run() {
                JettyManager jettyManager = applicationContext.getBean(JettyManager.class);
                jettyManager.startServer(8089);
            }
        }.start();
    }
}
