package net.strocamp.game;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Profile("raspberry")
public class PiInterfaceImpl implements PiInterface {
    private final static Logger logger = LoggerFactory.getLogger(PiInterfaceImpl.class);

    private final GpioController gpio = GpioFactory.getInstance();
    private GpioPinDigitalInput resetButton;
    private GpioPinDigitalInput aButton;
    private GpioPinDigitalInput bButton;
    private GpioPinDigitalOutput statusLed;

    @Override
    public void blink() {
        statusLed.blink(100l, 1500l, PinState.HIGH);
    }

    @Override
    public void ledOn() {
        statusLed.high();
    }

    @Override
    public void ledOff() {
        statusLed.low();
    }

    @Override
    public void onAButton(ButtonEventHandler handler) {
        aButton.addListener((GpioPinListenerDigital)gpioPinDigitalStateChangeEvent -> {
            if (PinEdge.FALLING.equals(gpioPinDigitalStateChangeEvent.getEdge())) {
                handler.handle();
            }
        });
    }

    @Override
    public void onBButton(ButtonEventHandler handler) {
        bButton.addListener((GpioPinListenerDigital)gpioPinDigitalStateChangeEvent -> {
            if (PinEdge.FALLING.equals(gpioPinDigitalStateChangeEvent.getEdge())) {
                handler.handle();
            }
        });

    }

    @Override
    public void onResetButton(ButtonEventHandler handler) {
        resetButton.addListener((GpioPinListenerDigital)gpioPinDigitalStateChangeEvent -> {
            if (PinEdge.RISING.equals(gpioPinDigitalStateChangeEvent.getEdge())) {
                handler.handle();
            }
        });
    }

    @PostConstruct
    public void initialize() {
        logger.debug("Initializing Pins on the Raspberry Pi");

        resetButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.OFF);
        aButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.OFF);
        bButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.OFF);

        statusLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14, PinState.LOW);
    }

    @PreDestroy
    public void destroy() {
        if (!gpio.isShutdown()) {
            gpio.shutdown();
        }
    }

}
