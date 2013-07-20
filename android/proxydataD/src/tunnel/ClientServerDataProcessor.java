package tunnel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.net.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ClientServerDataProcessor extends Thread {

    boolean running = false;
    static int serverClientDataPort;
    static int serverClientRTPPort;
    //static int bindPort = -1;
    //static String bindIP = null;
    static int bindPort = 60;
    static String bindIP = "192.168.0.110";
    //public static String CONFIG = "tunnel.conf";
    
    byte data[] = new byte[1500];
    DatagramSocket clientServerSocket;
    DatagramSocket serverClientDataSocket;
    DatagramSocket serverClientRTPSocket;
    DatagramPacket clientServerDataPacket = new DatagramPacket(data, 1500);
    public static Hashtable addressMapByCallID = new Hashtable();
    public static Hashtable addressMapByUserName = new Hashtable();
    public static Hashtable addressMapByClientRTPInfo = new Hashtable();
    public static Hashtable addressMapByServerRTPInfo = new Hashtable();
    static java.util.Timer timer = new java.util.Timer();
    static ServerClientDataProcessor serverClientDataProcessor = null;
    static ServerClientRTPProcessor serverClientRTPProcessor = null;
    static ClientServerDataProcessor clientServerDataProcessor = null;
    static ClientServerRTPProcessor clientServerRTPProcessor = null;
    static ShutdownDetection shutdownDetection = null;
    static Logger logger = Logger.getLogger(ClientServerDataProcessor.class.getName());

    public ClientServerDataProcessor() {
        try {
            clientServerSocket = new DatagramSocket(bindPort, InetAddress.getByName(bindIP));
            serverClientDataSocket = new DatagramSocket(0, InetAddress.getByName(bindIP));
            serverClientDataPort = serverClientDataSocket.getLocalPort();
            serverClientRTPSocket = new DatagramSocket(0, InetAddress.getByName(bindIP));
            serverClientRTPPort = serverClientRTPSocket.getLocalPort();
            clientServerRTPProcessor = new ClientServerRTPProcessor(serverClientRTPSocket);
            serverClientDataProcessor = new ServerClientDataProcessor(clientServerSocket, serverClientDataSocket);
            serverClientRTPProcessor = new ServerClientRTPProcessor(clientServerSocket, serverClientRTPSocket);
            timer.schedule(new RemoveOldTunnels(), 25 * 60 * 1000, 25 * 60 * 1000);
            this.running = true;
            start();
            logger.debug("STARTED SUCCESSFULLY");
        } catch (Exception ex) {
            System.err.println(ex);
            System.exit(1);
        }
    }

    public void run() {
        int length = 0;
        String message = null;
        String callID = null;

        while (running) {
            try {
                clientServerSocket.receive(clientServerDataPacket);
                length = clientServerDataPacket.getLength();

                int dataType = (int) data[2];
                if (dataType >= 6 && dataType <= 8) {
                    clientServerRTPProcessor.run(clientServerDataPacket, data, dataType);
                    continue;
                }
                if (dataType != 1) {
                    //logger.debug("Undefined Packet From Client.....");
                    continue;
                }
                //logger.debug("Packet From Client.....");
                int serverPort = byteArrayToInt(data[5], data[6]);
                InetAddress server = InetAddress.getByAddress(new byte[]{data[0], data[1], data[3], data[4]});//;ggetByName("192.168.0.116");
                SocketAddress serverAddress = new InetSocketAddress(server, serverPort);
//                if (SwitchInfoLoader.getInstance().getSwitchInfo(server.getHostAddress()) == null) {
//                    continue;
//                }
                message = new String(decodeSIP(data, length));
                int index = message.indexOf("User-Agent: ");
                String reqTime = message.substring(index + 12, message.indexOf(" ", index + 13));
                message = message.replaceAll("User-Agent: " + reqTime + " ", "User-Agent: IPVision/1.1.0");
                message = message.replaceAll(reqTime, "\r\n");
                //logger.debug("Packet From Client: " + t);
                callID = getCallID(message);
                if (callID == null) {
                    continue;
                }
                addressMapByCallID.put(callID, new Object[]{clientServerDataPacket.getSocketAddress(), new Date().getTime() / 1000});
                addressMapByUserName.put(getUsername(message, "From: "), clientServerDataPacket.getSocketAddress());
                message = message.replaceAll("m=audio \\$m_port\\$ RTP", "m=audio " + serverClientRTPPort + " RTP").replaceAll("IP4 \\$m_ip\\$", "IP4 " + bindIP).replaceAll("\\$rp_add\\$", server.getHostAddress() + ":" + serverPort).replaceAll("\\$lp_add\\$", bindIP + ":" + serverClientDataPort).replaceAll("\\$l_add\\$", bindIP);
                //logger.debug("Packet sent to server: " + t);
                serverClientDataSocket.send(new DatagramPacket(message.getBytes(), message.length(), serverAddress));
            } catch (Exception e) {
                logger.fatal("ClientServerDataProcessor Exception:", e);
            }
        }
    }

    String getMessage(String x, String find) {
        int i = 0, j = 0;
        if (x == null || (i = x.indexOf(find)) < 0 || (j = x.indexOf("\n", i)) < 0) {
            return null;
        }
        return x.substring(i, j).trim();
    }

    boolean isRegister(String x) {
        return x.startsWith("REGISTER ");
    }

    String getCallID(String x) {
        int i = 0, j = 0;
        if (x == null || (i = x.indexOf("Call-ID:")) < 0 || (j = x.indexOf("\n", i)) < 0) {
            return null;
        }
        return x.substring(i + 8, j).trim();
    }

    String getUsername(String x, String y) {
        int i = 0, j = 0;
        if (x == null || (i = x.indexOf(y)) < 0 || (j = x.indexOf("<", i)) < 0 || (i = x.indexOf(">", j)) < 0) {
            return null;
        }
        x = x.substring(j + 1, i).trim();
        if (x.contains("sip:")) {
            x = x.replace("sip:", "");
        }
        j = x.indexOf("@");
        if (j < 0) {
            return x;
        } else {
            return x.substring(0, j);
        }
    }

    public byte[] decodeSIP(byte data[], int length) {
        int l = length;
        byte[] d = new byte[length - 8];
        for (int i = 0; i < length - 8; i++) {
            l--;
            d[i] = (byte) (~data[l]);
        }
        return d;
    }

    public static int byteArrayToInt(byte a, byte b) {
        int iRetVal = -1;
        int iLowest;
        int iLow;
        int iMid;
        int iHigh;

        iLowest = b;
        iLow = a;
        iMid = 0;
        iHigh = 0;
        iRetVal = ((0xFF & iHigh) << 24) | ((0xFF & iMid) << 16) | ((0xFF & iLow) << 8) | (0xFF & iLowest);
        return iRetVal;
    }

    public void stopService() {
        running = false;
        try {
            sleep(10);
            if (clientServerSocket != null) {
                clientServerSocket.close();
            }
            if (serverClientDataSocket != null) {
                serverClientDataSocket.close();
            }
            if (serverClientRTPSocket != null) {
                serverClientRTPSocket.close();
            }
        } catch (Exception e) {
            logger.fatal("Tunnel Exception:", e);
        }

    }

    public static void main(String args[]) {
        try {
            PropertyConfigurator.configure("log4j.properties");
            logger.debug("------------ STARTING UP ---------------");
            shutdownDetection = new ShutdownDetection();
            shutdownDetection.start();
            /*
            try {
                InputStream input = null;
                File file = new File(CONFIG);
                if (file.exists()) {
                    input = new FileInputStream(file);
                }
                Properties prop = new Properties();
                prop.load(input);
                input.close();
                if (prop.containsKey("BIND_IP")) {
                    bindIP = prop.getProperty("BIND_IP");
                } else {
                    logger.debug("No BIND_IP is found in config.txt");
                }
                if (prop.containsKey("BIND_PORT")) {
                    bindPort = Integer.parseInt(prop.getProperty("BIND_PORT"));
                } else {
                    logger.debug("No BIND_PORT is found in config.txt");
                }
            } catch (Exception e) {
                logger.fatal("Exception in reading config file:" + e);
            }
             */
            clientServerDataProcessor = new ClientServerDataProcessor();
        } catch (Exception e) {
            logger.fatal("Tunnel Exception:", e);
            System.exit(1);
        }
    }
}