package tunnel;

import java.net.*;
import org.apache.log4j.Logger;

public class ClientServerRTPProcessor extends Thread {

    boolean running = false;
    DatagramSocket serverClientRTPSocket;
    static Logger logger = Logger.getLogger(ClientServerRTPProcessor.class.getName());

    public ClientServerRTPProcessor(DatagramSocket serverClientRTPSocket) {
        this.serverClientRTPSocket = serverClientRTPSocket;
        this.running = true;
        start();
    }

    public void run() {} 
    public void run(DatagramPacket ClientServerRTPPacket,byte[] data, int offset) {
        if (running) {
            try {
                String clientIP = ClientServerRTPPacket.getAddress().getHostAddress();
                int clientPort = ClientServerRTPPacket.getPort();
                int length = ClientServerRTPPacket.getLength();
                InetSocketAddress socketAddress = (InetSocketAddress) ClientServerDataProcessor.addressMapByClientRTPInfo.get(clientIP + ":" + clientPort);
                if (socketAddress != null) {
                    serverClientRTPSocket.send(new DatagramPacket(decodeRTP(data, length, offset), length - offset, socketAddress));
                }
            } catch (Exception e) {
                logger.fatal("ClientServerRTPProcessor Exception:", e);
            }
        }
    }

    public byte[] decodeRTP(byte data[], int length, int offset) {
        byte[] d = new byte[length - offset];
        for (int i = 0, j = offset; i < length-offset; i+=2, j+=2) {
            d[i] = (byte) (~data[j]);
            d[i+1] = data[j+1];
        }
        return d;
    }

    public void stopService() {
        running = false;
    }
}
