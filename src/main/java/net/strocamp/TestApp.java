package net.strocamp;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestApp {
    public static void main(String args[]) throws InterruptedException {
        System.out.println("<--Pi4J--> GPIO Listen Example ... started.");

        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalInput resetButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.OFF);
        final GpioPinDigitalInput aButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.OFF);
        final GpioPinDigitalInput bButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.OFF);

        final GpioPinDigitalOutput statusLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14, PinState.LOW);

        System.out.println("<--Pi4J--> GPIO Listen Example ... pins configured.");
        statusLed.blink(500l, 3000l, PinState.HIGH);


        final AtomicBoolean buttonPressed = new AtomicBoolean(false);


        resetButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
                if (PinEdge.RISING.equals(gpioPinDigitalStateChangeEvent.getEdge())) {
                    System.out.println("resetButton hit resetting...");
                    //quit.set(true);
                    buttonPressed.set(false);
                    statusLed.low();
                }
            }
        });

        aButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
                if (PinEdge.FALLING.equals(gpioPinDigitalStateChangeEvent.getEdge())) {
                    if (buttonPressed.compareAndSet(false, true)) {
                        System.out.println("aButton hit...");
                        statusLed.high();
                    }
                }
            }
        });

        bButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
                if (PinEdge.FALLING.equals(gpioPinDigitalStateChangeEvent.getEdge())) {
                    if (buttonPressed.compareAndSet(false, true)) {
                        System.out.println("bButton hit...");
                        statusLed.high();
                    }
                }
            }
        });

        while (true) {
            Thread.sleep(250l);
        }
    }
}
