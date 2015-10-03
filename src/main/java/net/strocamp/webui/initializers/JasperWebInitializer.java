package net.strocamp.webui.initializers;

import org.apache.jasper.servlet.JasperInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;

public class JasperWebInitializer implements WebApplicationInitializer {
    private final static Logger logger = LoggerFactory.getLogger(JasperInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.debug("Initializing {}", this.getClass().getSimpleName());
        JasperInitializer jasperInitializer = new JasperInitializer();
        jasperInitializer.onStartup(Collections.emptySet(), servletContext);
        logger.debug("Finished initializing {}", this.getClass().getSimpleName());
    }
}
