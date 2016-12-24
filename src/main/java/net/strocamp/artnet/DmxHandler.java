package net.strocamp.artnet;

public abstract class DmxHandler {
    private String name;
    private int universe;
    private int address;
    private int width;


    public DmxHandler(String name, int universe, int address, int width) {
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
