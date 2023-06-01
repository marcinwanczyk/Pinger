package com.example.ping;

import org.apache.commons.net.util.SubnetUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


@Service
public class PingService {

    private boolean isPinging = false;

    public String startPinging() {
        if (isPinging) return "Pinging already in progress!";

        isPinging = true;
        pingDevice();
        return "Pinging started!";
    }

    public String stopPinging(){
        if(!isPinging) return "Pinging is not working right now!";
        isPinging = false;
        return "Pinging stopped!";
    }

    public String getPingStatus() {
        if (isPinging) {
            return "Pinging is in progress.";
        } else {
            return "Pinging is not active.";
        }

    }

    public String pingDevice() {

        String networkRange = getNetworkRange();

        SubnetUtils subnetUtils = new SubnetUtils(networkRange);
        SubnetUtils.SubnetInfo subnetInfo = subnetUtils.getInfo();


        for (String ip : subnetInfo.getAllAddresses()) {
            boolean isReachable = ping(ip);
//            if (!isReachable) triggerError(ip);
        }

        return "Pinging completed";
    }

    private void triggerError(String ipAddress) {
        System.out.println("No response from IP: " + ipAddress);
    }


    private boolean ping(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            int timeout = 1000;

            if (address.isReachable(timeout)) {
                System.out.println("Ping successful for IP: " + ipAddress);
                return true;
            }
            else {
//                System.out.println("Ping failed for IP: " + ipAddress);
                return false;
            }
        } catch (IOException e) {
            System.err.println("Exception occured while pinging IP: " + ipAddress);
            e.printStackTrace();
            return false;
        }
    }

    private String getNetworkRange() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                            String hostAddress = inetAddress.getHostAddress();
                            int subnetPrefixLength = networkInterface.getInterfaceAddresses().stream()
                                    .filter(interfaceAddress -> interfaceAddress.getAddress().equals(inetAddress))
                                    .findFirst().map(InterfaceAddress::getNetworkPrefixLength)
                                    .orElse((short) 0);
                            return hostAddress + "/" + subnetPrefixLength;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
