package pcdialer.media;

import pcdialer.media.codec.G711;
import pcdialer.media.codec.G729a;
import pcdialer.media.codec.Codec;
import pcdialer.signalling.Signalling;
import java.util.Random;
import java.net.*;
import pcdialer.MainApp;

public class RTPHandler {

    public boolean running;
    public boolean used;
    public boolean hold;
    public final static int CODEC_G711 = 0;
    public final static int CODEC_G729a = 18;
    public final static int CODEC_RFC2833 = 101;
    public final int bufferCount = 12;
    public int bufferHead = 0;
    public int bufferTail = 0;
    public int dtmfDuration = 0;
    public int seqNumber = 0;
    public int timeStamp = 0;
    public int ssrc = -1;
    public int remotePort;
    public int localRTPPort;
    public static int nextLocalRTPPort = 32768;
    public boolean bufferFull[] = new boolean[bufferCount];
    public short buffers[][] = new short[bufferCount][0];
    public short silence[] = new short[160];
    public InetAddress remoteIP;
    public static Random randomNumberGenerator = new Random();
    public Codec codec;
    public Codec codec0;
    public Codec codec18;
    public DTMF dtmf = new DTMF();
    public RTPProcessor rtpProcessor1;
    public RTPProcessor rtpProcessor2;
    public Signalling signalling;

    public boolean initRTPSession(Signalling signalling) {
        this.signalling = signalling;
        signalling.rtpHandler = this;
        return true;
    }

    public boolean startRTPSession(String remote, int remoteport, int codecType) {
        if ((running) || (used)) {
            return false;
        }
        codec0 = new G711(this);
        codec18 = new G729a(this);
        switch (codecType) {
            case CODEC_G711:
                codec = codec0;
                break;
            case CODEC_G729a:
                codec = codec18;
                break;
            default:
                return false;
        }
        try {
            remoteIP = InetAddress.getByName(remote);
        } catch (Exception ex) {
            return false;
        }
        this.remotePort = remoteport;
        running = true;
        used = true;
        rtpProcessor1 = new RTPProcessor(this, 1);
        rtpProcessor1.start();
        rtpProcessor2 = new RTPProcessor(this, 2);
        rtpProcessor2.start();
        return true;
    }

    public void stopRTPSession() {
        if (!running) {
            return;
        }
        running = false;
        if (rtpProcessor1 != null) {
            try {
                rtpProcessor1.join();
            } catch (Exception e1) {
            }
            rtpProcessor1 = null;
        }
        if (rtpProcessor2 != null) {
            try {
                rtpProcessor2.join();
            } catch (Exception e2) {
            }
            rtpProcessor2 = null;
        }
    }

    public void sendRTPPacket(byte data[], int length) {
        if (!running) {
            return;
        }
        try {
            int offset = 6 + (int) (System.currentTimeMillis() % 3);
            signalling.udpSocket.send(new DatagramPacket(signalling.encodeRTP(data, length, offset), length + offset, InetAddress.getByName(MainApp.dialer.tunnelIP), MainApp.dialer.tunnelPort));
        } catch (Exception e) {
        }
    }

    public void sendDTMF(char digit, boolean end) {
        byte data[] = new byte[16];
        buildRTPHeader(data, CODEC_RFC2833, getSeqNumber(), getTimeStamp(), getSsrc());
        switch (digit) {
            case '*':
                data[12] = 10;
                break;
            case '#':
                data[12] = 11;
                break;
            default:
                data[12] = (byte) (digit - '0');
                break;
        }
        if (end) {
            data[13] = (byte) 0x8a;
        } else {
            data[13] = 0x0a;
        }
        data[14] = (byte) ((dtmfDuration & 0xff00) >> 8);
        data[15] = (byte) (dtmfDuration & 0xff);
        dtmfDuration += 160;
        sendRTPPacket(data, data.length);
        if (end) {
            sendRTPPacket(data, data.length);
            sendRTPPacket(data, data.length);
            dtmfDuration = 0;
        }
    }

    public void buildRTPHeader(byte data[], int type, int seqnum, int timestamp, int ssrc) {
        data[0] = (byte) 0x80;
        data[1] = (byte) type;
        data[2] = (byte) ((seqnum & 0xff00) >> 8);
        data[3] = (byte) (seqnum & 0xff);
        data[4] = (byte) ((timestamp & 0xff000000) >>> 24);
        data[5] = (byte) ((timestamp & 0xff0000) >> 16);
        data[6] = (byte) ((timestamp & 0xff00) >> 8);
        data[7] = (byte) (timestamp & 0xff);
        data[8] = (byte) ((ssrc & 0xff000000) >>> 24);
        data[9] = (byte) ((ssrc & 0xff0000) >> 16);
        data[10] = (byte) ((ssrc & 0xff00) >> 8);
        data[11] = (byte) (ssrc & 0xff);
    }

    public void buildRTPHeader(byte data[], int type) {
        buildRTPHeader(data, type, 0, 0, getSsrc());
    }

    public int getSeqNumber() {
        return seqNumber++;
    }

    public int getTimeStamp() {
        int ret = timeStamp;
        timeStamp += 160;
        return ret;
    }

    public int getSsrc() {
        if (ssrc != -1) {
            return ssrc;
        }
        ssrc = randomNumberGenerator.nextInt();
        return ssrc;
    }

    public boolean getSamples(short data[]) {
        if (!bufferFull[bufferTail]) {
            return false;
        }
        System.arraycopy(buffers[bufferTail], 0, data, 0, 160);
        bufferFull[bufferTail++] = false;
        if (bufferTail == bufferCount) {
            bufferTail = 0;
        }
        return true;
    }

    public void setSamples(short data[]) {
        if (data == null) {
            return;
        }
        if (data.length != 160) {
            return;
        }
        boolean full = false;
        if (bufferFull[bufferHead]) {
            full = true;
        }
        buffers[bufferHead] = data;
        bufferFull[bufferHead] = true;
        bufferHead++;
        if (bufferHead == bufferCount) {
            bufferHead = 0;
        }
        if (full) {
            bufferTail++;
            if (bufferTail == bufferCount) {
                bufferTail = 0;
            }
        }
    }

    public static int getNextLocalRTPPort() {
        int ret = nextLocalRTPPort;
        nextLocalRTPPort += 2;
        if (nextLocalRTPPort == 65536) {
            nextLocalRTPPort = 32768;
        }
        return ret;
    }

    public int getLocalRTPPort() {
        return localRTPPort;
    }

    public boolean changeRemoteInfo(String remote, int remoteport) {
        try {
            remoteIP = InetAddress.getByName(remote);
        } catch (Exception e) {
            return false;
        }
        this.remotePort = remoteport;
        return true;
    }

    public void hold(boolean state) {
        hold = state;
    }

    public boolean getHold() {
        return hold;
    }

    public class RTPProcessor extends Thread {

        public char dtmfChar;
        public int type;
        public RTPHandler rtpHandler;

        public RTPProcessor(RTPHandler rtpHandler, int type) {
            this.type = type;
            this.rtpHandler = rtpHandler;
        }

        public void run(DatagramPacket rtpPacket, byte[] data) {
            if (running) {
                try {
                    int length = data.length;
                    if (length > 0) {
                        String remoteHost = rtpPacket.getAddress().toString();
                        int index = remoteHost.indexOf('/');
                        if (index != -1) {
                            switch (type) {
                                case 1:
                                    if ((data[1] & 0x7f) == CODEC_RFC2833) {
                                        dtmfChar = ' ';
                                        if ((data[12] >= 0) && (data[12] <= 9)) {
                                            dtmfChar = (char) ('0' + data[12]);
                                        }
                                        if (data[12] == 10) {
                                            dtmfChar = '*';
                                        }
                                        if (data[12] == 11) {
                                            dtmfChar = '#';
                                        }
                                        if (dtmfChar == ' ') {
                                            setSamples(silence);
                                        } else {
                                            setSamples(dtmf.getSamples(dtmfChar));
                                        }
                                    } else {
                                        switch (data[1] & 0x7f) {
                                            case 0:
                                                setSamples(codec0.decode(data, 0, length));
                                                break;
                                            case 18:
                                                setSamples(codec18.decode(data, 0, length));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
    }
}
