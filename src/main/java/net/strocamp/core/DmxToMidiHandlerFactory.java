package net.strocamp.core;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class DmxToMidiHandlerFactory implements FactoryBean<DmxToMidiHandler> {
    @Override
    public DmxToMidiHandler getObject() throws Exception {
        return new DmxToMidiHandler();
    }

    @Override
    public Class<?> getObjectType() {
        return DmxToMidiHandler.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
