package net.strocamp.core;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Component
public class JettyManager {
    private final static Logger logger = LoggerFactory.getLogger(JettyManager.class);
    private Server server;

    private static final String CONTEXT_PATH = "/";

    static {
        System.setProperty("org.apache.jasper.compiler.disablejsr199","false");
    }

    public void startServer(int port) {
        server = new Server(port);
        try {
            Configuration.ClassList classlist = Configuration.ClassList
                    .setServerDefault( server );
            classlist.addBefore(
                    "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                    "org.eclipse.jetty.annotations.AnnotationConfiguration");

            server.setHandler(getContext());

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
        server.setStopTimeout(10000L);
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

    private WebAppContext getContext() throws IOException {
        String resourceDir = "src/main/resources/web";
        WebAppContext context = new WebAppContext();
        context.setResourceBase(resourceDir);
        context.setDescriptor(resourceDir + "/WEB-INF/web.xml");
        context.setAttribute("javax.servlet.context.tempdir", getScratchDir());
        context.setContextPath(CONTEXT_PATH);
        context.setParentLoaderPriority(true);
        context.setErrorHandler(null);

        context.setConfigurations(new Configuration[]
                {
                        new AnnotationConfiguration(),
                        new WebInfConfiguration(),
                        new WebXmlConfiguration(),
                        new MetaInfConfiguration(),
                        new FragmentConfiguration(),
                        new EnvConfiguration(),
                        new PlusConfiguration(),
                        new JettyWebXmlConfiguration()
                });
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/.*javax.faces-[^/]*\\.jar$|.*/.*jsp-api-[^/]*\\.jar$|.*/.*jsp-[^/]*\\.jar$|.*/.*taglibs[^/]*\\.jar$|.*/target/classes/");

        context.setAttribute("javax.servlet.context.tempdir", getScratchDir());
        context.setClassLoader(getUrlClassLoader());

        context.setResourceBase(new ClassPathResource("webapp").getURI().toString());

        context.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        //context.addBean(new ServletContainerInitializersStarter(context), true);

//        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
//        context.setSessionHandler(new SessionHandler(new HashSessionManager()));
//        context.addEventListener(new ContextLoaderListener(context));

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
        return new URLClassLoader(new URL[0], this.getClass().getClassLoader());
    }

    private List<ContainerInitializer> jspInitializers()
    {
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        List<ContainerInitializer> initializers = new ArrayList<>();
        initializers.add(initializer);
        return initializers;
    }
}
