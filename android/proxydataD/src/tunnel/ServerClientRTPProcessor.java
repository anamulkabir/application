package tunnel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import org.apache.log4j.Logger;

class ServerClientRTPProcessor extends Thread {

    boolean running = false;
    byte[] data = new byte[400];
    DatagramPacket ServerClientRTPPacket = new DatagramPacket(data, 400);
    DatagramSocket clientServerDataSocket;
    DatagramSocket serverClientRTPSocket;
    public static byte[] encode = new byte[7];
    static Logger logger = Logger.getLogger(ServerClientRTPProcessor.class.getName());

    public ServerClientRTPProcessor(DatagramSocket clientServerDataSocket, DatagramSocket serverClientRTPSocket) {
        this.clientServerDataSocket = clientServerDataSocket;
        this.serverClientRTPSocket = serverClientRTPSocket;
        this.running = true;
        start();
    }

    public void run() {
        while (running) {
            try {
                serverClientRTPSocket.receive(ServerClientRTPPacket);
                String serverRTPIP = ServerClientRTPPacket.getAddress().getHostAddress();
                int serverRTPPort = ServerClientRTPPacket.getPort();
                int length = ServerClientRTPPacket.getLength();
                int offset = 6 + (int) (System.currentTimeMillis() % 3);
                //logger.debug("RTP Packet From Server");
                SocketAddress address = (SocketAddress) ClientServerDataProcessor.addressMapByServerRTPInfo.get(serverRTPIP + ":" + serverRTPPort);
                if (address != null) {                  
                    clientServerDataSocket.send(new DatagramPacket(encodeRTP(data, length, offset), length + offset, address));
                    //logger.debug("RTP Packet sent to Client.");
                }
            } catch (Exception e) {
                logger.fatal("ServerClientRTPProcessor Exception:", e);
            }
        }
    }

    public byte[] encodeRTP(byte d[], int length, int offset) {       
        byte[] data = new byte[length + offset];
        data[0] = (byte) offset;
        System.arraycopy(encode, 0, data, 1, offset-1);      
        for (int i = offset, j = 0; i < length + offset; i+=2, j+=2) {
            data[i] = (byte) (~d[j]);
            data[i+1] = d[j+1];
        }
        return data;
    }

    public void stopService() {
        running = false;
    }
}
