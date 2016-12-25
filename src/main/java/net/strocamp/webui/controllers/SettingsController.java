package net.strocamp.webui.controllers;

import net.strocamp.artnet.ArtNetException;
import net.strocamp.artnet.ArtNetNode;
import net.strocamp.artnet.ArtNetNodeInfo;
import net.strocamp.webui.domain.Interface;
import net.strocamp.webui.domain.NodeConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

@Controller
@RequestMapping("/settings")
public class SettingsController {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SettingsController.class);

    private ArtNetNode artNetNode;

    @Autowired
    public void setArtNetNode(ArtNetNode artNetNode) {
        this.artNetNode = artNetNode;
    }

    @RequestMapping(value = "/interfaces", method = RequestMethod.GET)
    public @ResponseBody List<Interface> interfaceList() throws ArtNetException {
        return getInterfaces();
    }

    @RequestMapping(value = "/nodestart", method = RequestMethod.POST)
    public @ResponseBody Boolean startServer(NodeConfig nodeConfig) throws ArtNetException {
        artNetNode.configureNetworkFromInterfaceName(nodeConfig.getNetworkInterface());
        artNetNode.setNetwork(nodeConfig.getNetwork());
        artNetNode.setSubnetwork(nodeConfig.getSubnet());
        artNetNode.start();

        return true;
    }

    @RequestMapping(value = "/nodestop", method = RequestMethod.POST)
    public @ResponseBody Boolean stopServer() throws ArtNetException {
        artNetNode.stop();

        return true;
    }

    @RequestMapping(value = "/discovery", method = RequestMethod.GET)
    public @ResponseBody Collection<ArtNetNodeInfo> listDiscoveredNodes() {
        return artNetNode.getDiscoveredNodes();
    }

    private List<Interface> getInterfaces() throws ArtNetException {
        List<Interface> interfaces = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> artNetInterfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface networkInterface : Collections.list(artNetInterfaces)) {
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    if (interfaceAddress.getAddress() instanceof Inet4Address) {
                        interfaces.add(new Interface(networkInterface.getDisplayName(), interfaceAddress.getAddress().getHostAddress()));
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            throw new ArtNetException(e);
        }

        return interfaces;
    }

}
