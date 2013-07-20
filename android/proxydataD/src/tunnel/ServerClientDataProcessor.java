package tunnel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import org.apache.log4j.Logger;

class ServerClientDataProcessor extends Thread {

    boolean running = false;
    byte[] data = new byte[1500];
    DatagramPacket ServerClientDataPacket = new DatagramPacket(data, 1500);
    DatagramSocket clientServerDataSocket;
    DatagramSocket serverClientDataSocket;
    public static byte[] encode = new byte[2];
    static Logger logger = Logger.getLogger(ServerClientDataProcessor.class.getName());

    public ServerClientDataProcessor(DatagramSocket clientServerDataSocket, DatagramSocket serverClientDataSocket) {
        this.clientServerDataSocket = clientServerDataSocket;
        this.serverClientDataSocket = serverClientDataSocket;
        this.running = true;
        start();
    }

    public void run() {
        int length = 0;
        String message = null;
        String callID;
        String ServerRTPIP;
        String ServerRTPPort;

        while (running) {
            try {
                serverClientDataSocket.receive(ServerClientDataPacket);
                length = ServerClientDataPacket.getLength();
                message = new String(data, 0, length).trim();
                //logger.debug("Packet From Server: " + t);
                callID = getCallID(message);
                if (callID == null) {
                    continue;
                }
                SocketAddress address = null;
                Object[] obj = (Object[]) ClientServerDataProcessor.addressMapByCallID.get(callID);
                if (obj != null) {
                    address = (SocketAddress) obj[0];
                }
                if (address == null) {
                    String username = getUsername(message, "To: ");
                    address = (SocketAddress) ClientServerDataProcessor.addressMapByUserName.get(username);
                    if (address != null) {
                        ClientServerDataProcessor.addressMapByCallID.put(callID, new Object[]{address, new Date().getTime() / 1000});
                    }
                }
                if (address == null) {
                    //logger.debug("continueing");
                    continue;
                }
                ServerRTPPort = getRTPPort(message);
                ServerRTPIP = getRTPIP(message);
                if (ServerRTPPort != null && ServerRTPIP != null) {
                    InetSocketAddress socketAddress = new InetSocketAddress(ServerRTPIP, Integer.parseInt(ServerRTPPort));
                    ClientServerDataProcessor.addressMapByClientRTPInfo.put(address.toString().replaceAll("/", ""), socketAddress);
                    ClientServerDataProcessor.addressMapByServerRTPInfo.put(ServerRTPIP + ":" + ServerRTPPort, address);
                }
                if (ServerRTPPort != null) {
                    message = message.replaceAll("m=audio " + ServerRTPPort + " RTP", "m=audio \\$m_port\\$ RTP");
                }
                if (ServerRTPIP != null) {
                    message = message.replaceAll("IP4 " + ServerRTPIP, "IP4 \\$m_ip\\$");
                }
                byte messageBytes[] = message.getBytes();
                byte encodedMessageBytes[] = encodeSIP(messageBytes, messageBytes.length);
                //logger.debug("Packet sent to client: " + address.toString());
                clientServerDataSocket.send(new DatagramPacket(encodedMessageBytes, encodedMessageBytes.length, address));
            } catch (Exception e) {
                logger.fatal("ServerClientDataProcessor Exception:", e);
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

    byte[] copyRTP(byte[] a, byte b[], int l) {
        byte[] c = new byte[a.length + l];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i];
        }
        for (int i = a.length; i < c.length; i++) {
            c[i] = b[i - a.length];
        }
        return c;
    }

    String getFromTag(String x) {
        int i = 0, j = 0;
        if (x == null || (i = x.indexOf("From: ")) < 0 || (j = x.indexOf("\n", i)) < 0) {
            return null;
        }
        x = x.substring(i, j).trim();
        if ((i = x.indexOf("tag=")) < 0) {
            return null;
        }
        j = x.indexOf(";", i);
        if (j < 0) {
            return x.substring(i + 4, x.length());
        } else {
            return x.substring(i + 4, j);
        }
    }

    String getToTag(String x) {
        int i = 0, j = 0;
        if (x == null || (i = x.indexOf("To: ")) < 0 || (j = x.indexOf("\n", i)) < 0) {
            return null;
        }
        x = x.substring(i, j).trim();
        if ((i = x.indexOf("tag=")) < 0) {
            return null;
        }
        j = x.indexOf(";", i);
        if (j < 0) {
            return x.substring(i + 4, x.length());
        } else {
            return x.substring(i + 4, j);
        }
    }

    String getRTPIP(String x) {
        int i = 0, j = 0;
        if (x == null || (i = x.indexOf("c=IN IP4 ")) < 0 || (j = x.indexOf("\n", i)) < 0) {
            return null;
        }
        return x.substring(i + 9, j).trim();
    }

    String getRTPPort(String x) {
        int i = 0, j = 0;
        if (x == null || (i = x.indexOf("m=audio")) < 0 || (j = x.indexOf("RTP/AVP", i)) < 0) {
            return null;
        }
        return x.substring(i + 7, j).trim();
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

    public byte[] encodeSIP(byte d[], int len) {
        int length = len + 3;
        byte[] data = new byte[length];
        data[0] = (byte) 5;
        System.arraycopy(encode, 0, data, 1, 2);     
        for (int i = 3; i < data.length; i++) {
            length--;
            data[length] = (byte) (~d[i - 3]);
        }
        return data;
    }

    public void stopService() {
        running = false;
    }
}
