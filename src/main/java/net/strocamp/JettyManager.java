package net.strocamp;

import org.apache.jasper.servlet.JasperInitializer;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class JettyManager {
    private final static Logger logger = LoggerFactory.getLogger(JettyManager.class);
    private Server server;

    private static final String CONTEXT_PATH = "/";
    private static final String CONFIG_LOCATION = "net.strocamp.webui.config";
    private static final String MAPPING_URL = "/";
    private static final String DEFAULT_PROFILE = "dev";

    public void startServer(int port) {
        Server server = new Server(port);
        try {
            Configuration.ClassList classlist = Configuration.ClassList
                    .setServerDefault( server );
            classlist.addBefore(
                    "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                    "org.eclipse.jetty.annotations.AnnotationConfiguration");

            server.setHandler(getServletContextHandler(getContext()));

            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        if (server == null) {
            return;
        }
        //server.setStopTimeout(10000L);
        ;
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        //context.stop();
                        server.stop();
                    } catch (Exception ex) {
                        System.out.println("Failed to stop Jetty");
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ServletContextHandler getServletContextHandler(WebApplicationContext context) throws IOException {
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(CONTEXT_PATH);

        contextHandler.setAttribute("javax.servlet.context.tempdir", getScratchDir());
        contextHandler.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/.*taglibs.*\\.jar$");
        contextHandler.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());

        contextHandler.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        contextHandler.setSessionHandler(new SessionHandler(new HashSessionManager()));

        contextHandler.setClassLoader(getUrlClassLoader());

        contextHandler.addServlet(getJspServletHolder(), "*.jsp");
        contextHandler.addServlet(new ServletHolder("dispatcher", new DispatcherServlet(context)), MAPPING_URL);

        contextHandler.addEventListener(new ContextLoaderListener(context));
        contextHandler.setResourceBase(new ClassPathResource("webapp").getURI().toString());

        return contextHandler;
    }

    private static ServletHolder getJspServletHolder() {
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);
        holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
        holderJsp.setInitParameter("fork", "false");
        holderJsp.setInitParameter("xpoweredBy", "false");
        holderJsp.setInitParameter("compilerTargetVM", "1.7");
        holderJsp.setInitParameter("compilerSourceVM", "1.7");
        holderJsp.setInitParameter("keepgenerated", "true");
        return holderJsp;
    }

    private static WebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        return context;
    }

    private static File getScratchDir() throws IOException
    {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File scratchDir = new File(tempDir.toString(), "embedded-jetty-jsp");

        if (!scratchDir.exists())
        {
            if (!scratchDir.mkdirs())
            {
                throw new IOException("Unable to create scratch directory: " + scratchDir);
            }
        }
        return scratchDir;
    }

    private ClassLoader getUrlClassLoader()
    {
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        return jspClassLoader;
    }

    private List<ContainerInitializer> jspInitializers()
    {
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
        initializers.add(initializer);
        return initializers;
    }

}
