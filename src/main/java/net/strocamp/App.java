package net.strocamp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = { "net.strocamp.webui", "net.strocamp.artnet", "net.strocamp.game" , "net.strocamp.titan"})
@PropertySource(value = {"file:/home/spark/artnet2midi/app.properties"}, ignoreResourceNotFound = true)
public class App
{
    public static void main( String[] args ) throws Exception
    {
        SpringApplication.run(App.class, args);
    }
}
