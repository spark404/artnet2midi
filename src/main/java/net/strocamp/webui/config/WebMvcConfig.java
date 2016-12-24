package net.strocamp.webui.config;

import net.strocamp.artnet.ArtNetNode;
import net.strocamp.game.ButtonDmxHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "net.strocamp.webui", "net.strocamp.artnet", "net.strocamp.game" , "net.strocamp.titan"})
@PropertySource({"file:/home/spark/artnet2midi/app.properties"})
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("css/**").addResourceLocations("css/");
        registry.addResourceHandler("fonts/**").addResourceLocations("fonts/");
        registry.addResourceHandler("js/**").addResourceLocations("js/");
    }

    @Bean
    public InternalResourceViewResolver getInternalResourceViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/jsp/");
        internalResourceViewResolver.setSuffix(".jsp");
        return internalResourceViewResolver;
    }

    @Bean
    public MappingJackson2JsonView getJsonViewResolver() {
        return new MappingJackson2JsonView();
    }

    @Bean
    public ButtonDmxHandler getButtonDmxHandler() {
        return new ButtonDmxHandler("GameButton", 0, 25);
    }

}
