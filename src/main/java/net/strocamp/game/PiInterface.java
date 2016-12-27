package net.strocamp.game;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class PiInterface {
    private final static Logger logger = LoggerFactory.getLogger(PiInterface.class);

    private final GpioController gpio = GpioFactory.getInstance();
    private GpioPinDigitalInput resetButton;
    private GpioPinDigitalInput aButton;
    private GpioPinDigitalInput bButton;
    private GpioPinDigitalOutput statusLed;

    public void blink() {
        statusLed.blink(100l, 1500l, PinState.HIGH);
    }

    public void ledOn() {
        statusLed.high();
    }

    public void ledOff() {
        statusLed.low();
    }

    public void onAButton(ButtonEventHandler handler) {
        aButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
                if (PinEdge.FALLING.equals(gpioPinDigitalStateChangeEvent.getEdge())) {
                    handler.handle();
                }
            }
        });
    }

    public void onBButton(ButtonEventHandler handler) {
        bButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
                if (PinEdge.FALLING.equals(gpioPinDigitalStateChangeEvent.getEdge())) {
                    handler.handle();
                }
            }
        });

    }

    public void onResetButton(ButtonEventHandler handler) {
        resetButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
                if (PinEdge.RISING.equals(gpioPinDigitalStateChangeEvent.getEdge())) {
                    handler.handle();
                }
            }
        });
    }

    @PostConstruct
    public void initialize() {
        logger.debug("Initializing Pins on the Raspberry Pi");

        resetButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.OFF);
        aButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.OFF);
        bButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.OFF);

        statusLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, PinState.LOW);
    }

    @PreDestroy
    public void destroy() {
        if (!gpio.isShutdown()) {
            gpio.shutdown();
        }
    }

    @FunctionalInterface
    public interface ButtonEventHandler {
        void handle();
    }
}
