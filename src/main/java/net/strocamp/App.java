package net.strocamp;

import net.strocamp.game.ButtonDmxHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = { "net.strocamp.webui", "net.strocamp.artnet", "net.strocamp.game" , "net.strocamp.titan"})
@PropertySource(value = {"file:/home/spark/artnet2midi/app.properties"}, ignoreResourceNotFound = true)
public class App
{
    @Value("${gameserver.dmx.address}")
    private int buttonAddress;

    public static void main( String[] args ) throws Exception
    {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public ButtonDmxHandler getButtonDmxHandler() {
        return new ButtonDmxHandler("GameButton", 0, buttonAddress);
    }

    @Bean
    public TaskExecutor getTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(2);
        taskExecutor.setQueueCapacity(5);
        return taskExecutor;
    }

}
