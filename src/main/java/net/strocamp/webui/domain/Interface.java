package net.strocamp.webui.domain;

public class Interface {
    private String interfaceName;
    private String interfaceAddress;

    public Interface() {
    }

    public Interface(String interfaceName, String interfaceAddress) {
        this.interfaceName = interfaceName;
        this.interfaceAddress = interfaceAddress;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceAddress() {
        return interfaceAddress;
    }

    public void setInterfaceAddress(String interfaceAddress) {
        this.interfaceAddress = interfaceAddress;
    }
}
