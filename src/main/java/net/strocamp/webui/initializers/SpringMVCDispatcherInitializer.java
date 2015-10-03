package net.strocamp.webui.initializers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class SpringMVCDispatcherInitializer implements WebApplicationInitializer {
    private final static Logger logger = LoggerFactory.getLogger(SpringMVCDispatcherInitializer.class);
    private static final String CONFIG_LOCATION = "net.strocamp.webui.config";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.debug("Initializing {}", this.getClass().getSimpleName());
        ServletRegistration.Dynamic registration =
                servletContext.addServlet("dispatcher", new DispatcherServlet(getSpringContext()));
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
        logger.debug("Finished initializing {}", this.getClass().getSimpleName());
    }

    private static WebApplicationContext getSpringContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        return context;
    }

}
