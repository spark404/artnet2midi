package net.strocamp.webui.domain;

public class NodeConfig {
    private String networkInterface;
    private int network;
    private int subnet;

    public NodeConfig() {
    }

    public String getNetworkInterface() {
        return networkInterface;
    }

    public void setNetworkInterface(String networkInterface) {
        this.networkInterface = networkInterface;
    }

    public int getNetwork() {
        return network;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    public int getSubnet() {
        return subnet;
    }

    public void setSubnet(int subnet) {
        this.subnet = subnet;
    }
}
