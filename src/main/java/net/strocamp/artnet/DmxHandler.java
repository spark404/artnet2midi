package net.strocamp.artnet;

public abstract class DmxHandler {
    private String name;
    private int universe;
    private int address;
    private int width;


    public DmxHandler(String name, int universe, int address, int width) {
        if (address < 1 || address > 512) {
            throw new IllegalArgumentException("Address should be a valid DMX address between 1 and 512");
        }

        if (width < 1 || (width + address) > 513) {
            throw new IllegalArgumentException("Width should be valid for the combination of address and width");
        }

        this.name = name;
        this.universe = universe;
        this.address = address;
        this.width = width;
    }


    public String getName() {
        return name;
    }

    public int getUniverse() {
        return universe;
    }

    public int getAddress() {
        return address;
    }

    public int getWidth() {
        return width;
    }

    public abstract void onDmx(byte[] data);
}
