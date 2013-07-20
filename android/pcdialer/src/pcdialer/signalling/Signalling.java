package pcdialer.signalling;

import pcdialer.media.RTPHandler;
import util.MD5;
import java.net.*;
import java.util.*;

public abstract class Signalling {

    public boolean running = true;
    public int signallingPort;
    public String tag;
    public static String userAgentStr = "Flamma/1.1.0";
    public static byte[] encode = new byte[7];
    public Random randomNumGenerator = new Random();
    public DatagramSocket udpSocket;
    public SIPProcessor sipProcessor;
    public RTPHandler rtpHandler;
    public UA ua;

    public byte[] encodeSIP(byte d[], int len) {
        int length = len + 8;
        byte[] data = new byte[length];
        String s2 = new String(d);
        s2 = new String(encode);
        System.arraycopy(encode, 0, data, 0, 2);
        data[2] = (byte) 1;
        System.arraycopy(encode, 2, data, 3, 5);
        for (int i = 8; i < data.length; i++) {
            length--;
            data[length] = (byte) (~d[ i - 8]);
        }
        s2 = new String(data);
        return data;
    }

    public byte[] encodeRTP(byte d[], int length, int offset) {
        byte[] data = new byte[length + offset];
        System.arraycopy(encode, 0, data, 0, 2);
        data[2] = (byte) offset;
        System.arraycopy(encode, 2, data, 3, offset - 3);
        for (int i = offset, j = 0; i < length + offset; i += 2, j += 2) {
            data[i] = (byte) (~d[j]);
            data[i + 1] = d[j + 1];
        }
        return data;
    }

    public byte[] decodeSIP(byte data[], int length) {
        int l = length;
        byte[] d = new byte[length - 3];
        for (int i = 0; i < length - 3; i++) {
            l--;
            d[i] = (byte) (~data[l]);
        }
        return d;
    }

    public byte[] decodeRTP(byte data[], int length, int offset) {
        byte[] d = new byte[length - offset];
        for (int i = 0, j = offset; i < length - offset; i += 2, j += 2) {
            d[i] = (byte) (~data[j]);
            d[i + 1] = data[j + 1];
        }
        return d;
    }

    public boolean init(int udpPort, UA ua) {
        this.ua = ua;
        tag = null;
        while (true) {
            try {
                udpSocket = new DatagramSocket(udpPort, InetAddress.getByName(getLocalHost()));
                break;
            } catch (Exception e) {
                try {
                    udpPort++;
                    if (udpSocket != null) {
                        udpSocket.close();
                    }                   
                } catch (Exception ex) {
                }
            }
        }
        this.signallingPort = udpPort;
        sipProcessor = new SIPProcessor();
        sipProcessor.start();
        return true;
    }

    public void uninit() {
        if (udpSocket != null) {
            try {
                udpSocket.close();
                running = false;  
                sipProcessor.join();
            } catch (Exception ex) {
            }
        }      
        udpSocket = null;
        sipProcessor = null;
    }

    /** replaces Audio port. */
    static String replaceAudioPort(String x) {
        int i = 0, j = 0;
        if (x == null || (i = x.indexOf("m=audio ")) < 0 || (j = x.indexOf("RTP", i)) < 0) {
            return x;
        }
        return x.replaceAll("m=audio " + x.substring(i + 8, j) + "RTP", "m=audio \\$m_port\\$ RTP");
    }

    static String replaceFromTag(String x) {
        int i = 0, j = 0, k = 0;
        if (x == null || (i = x.indexOf("From: ")) < 0 || (j = x.indexOf("@", i)) < 0 || (k = x.indexOf(">", j)) < 0) {
            return x;
        }
        return x.substring(0, j + 1) + "SIPLinkUser2_1" + x.substring(k, x.length());
    }

    public boolean send(String localip, String remote, int remoteport, String datastr) {
        if (datastr.length() > 2) {
            System.err.println("Sent: ");
            System.err.println(datastr);
            System.err.println();
            datastr = replaceFromTag(replaceAudioPort(datastr)).replaceAll("IP4 " + localip, "IP4 \\$m_ip\\$").replaceAll(remote + ":" + remoteport, "\\$rp_add\\$").replaceAll(localip + ":" + signallingPort, "\\$lp_add\\$").replaceAll(localip, "\\$l_add\\$");
            String curTime = System.currentTimeMillis() + "";
            datastr = datastr.replaceAll("\r\n", curTime);
            datastr = datastr.replaceAll("User-Agent: Flamma/1.1.0", "User-Agent: " + curTime + " ");
            byte x[] = datastr.getBytes();
            int length =  x.length; 
            try {
                udpSocket.send(new DatagramPacket(encodeSIP(x, length), length+8, InetAddress.getByName(remote), remoteport));
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static String[] splitSIPMessage(String x) {
        ArrayList<String> messageParts = new ArrayList<String>();
        int i1, i2;
        String x1, x2;
        i1 = x.indexOf('<');
        if (i1 == -1) {
            return null;
        }
        if (i1 == 0) {
            messageParts.add("Unknown Name");
        } else {
            messageParts.add(x.substring(0, i1));
        }
        i1++;
        i2 = x.substring(i1).indexOf('>');
        if (i2 == -1) {
            return null;
        }
        x1 = x.substring(i1, i1 + i2);
        x2 = x.substring(i1 + i2 + 1).trim();
        i1 = x1.indexOf(':');
        if (i1 == -1) {
            return null;
        }
        x1 = x1.substring(i1 + 1); 
        i1 = x1.indexOf('@');
        if (i1 == -1) {
            messageParts.add("");  
        } else {
            messageParts.add(x1.substring(0, i1).trim());  
            x1 = x1.substring(i1 + 1).trim();
        }
        if ((x1.length() > 0) && (x1.charAt(0) == ';')) {
            x1 = x1.substring(1);
        }
        do {
            i1 = x1.indexOf(';');
            if (i1 == -1) {
                x1 = x1.trim();
                if (x1.length() > 0) {
                    messageParts.add(x1);
                }
                break;
            }
            messageParts.add(x1.substring(0, i1).trim());
            x1 = x1.substring(i1 + 1).trim();
        } while (true);
        if (messageParts.size() == 2) {
            messageParts.add("");  
        }
        messageParts.add(":");  
        if ((x2.length() > 0) && (x2.charAt(0) == ';')) {
            x2 = x2.substring(1);
        }
        do {
            i1 = x2.indexOf(';');
            if (i1 == -1) {
                x2 = x2.trim();
                if (x2.length() > 0) {
                    messageParts.add(x2);
                }
                break;
            }
            messageParts.add(x2.substring(0, i1).trim());
            x2 = x2.substring(i1 + 1).trim();
        } while (true);
        String strParts[] = new String[messageParts.size()];
        for (int a = 0; a < messageParts.size(); a++) {
            strParts[a] = messageParts.get(a);
        }
        return strParts;
    }

    public String joinTag(String x[]) {
        StringBuffer buf = new StringBuffer();
        buf.append('\"');
        buf.append(x[0]);
        buf.append('\"');
        buf.append("<sip:");
        buf.append(x[1]);
        buf.append('@');
        buf.append(x[2]);
        int i = 3;
        for (; (i < x.length) && (!x[i].equals(":")); i++) {
            buf.append(';');
            buf.append(x[i]);
        }
        i++;  
        buf.append('>');
        for (; i < x.length; i++) {
            buf.append(';');
            buf.append(x[i]);
        }
        return buf.toString();
    }

    public String getBranch() {
        return String.format("z123456-y12345-%x%x-1--d12345-", randomNumGenerator.nextInt(), randomNumGenerator.nextInt());
    }

    public boolean isWaiting(String msg[]) {
        for (int a = 0; a < msg.length; a++) {
            if (msg[a].equalsIgnoreCase("a=sendonly")) {
                return true;
            }
        }
        return false;
    }

    public String[] getViaList(String msg[]) {
        ArrayList<String> viaList = new ArrayList<String>();
        for (int i = 0; i < msg.length; i++) {
            if (msg[i].regionMatches(true, 0, "Via:", 0, 4)) {
                viaList.add(msg[i]);
                continue;
            }
            if (msg[i].regionMatches(true, 0, "v:", 0, 2)) {
                viaList.add(msg[i]);
                continue;
            }
        }
        String ret[] = new String[viaList.size()];
        for (int a = 0; a < viaList.size(); a++) {
            ret[a] = viaList.get(a);
        }
        return ret;
    }

    public static byte[] intToByteArray(int piValueToConvert) {
        byte[] aRetVal = new byte[4];
        byte iLowest;
        byte iLow;
        byte iMid;
        byte iHigh;
        iLowest = (byte) (piValueToConvert & 0xFF);
        iLow = (byte) ((piValueToConvert >> 8) & 0xFF);
        iMid = (byte) ((piValueToConvert >> 16) & 0xFF);
        iHigh = (byte) ((piValueToConvert >> 24) & 0xFF);
        aRetVal[3] = iLowest;
        aRetVal[2] = iLow;
        aRetVal[1] = iMid;
        aRetVal[0] = iHigh;
        return aRetVal;
    }

    public String getURI(String msg[]) {
        int idx1 = msg[0].indexOf(' ');
        if (idx1 == -1) {
            return null;
        }
        int idx2 = msg[0].substring(idx1 + 1).indexOf(' ');
        if (idx2 == -1) {
            return null;
        }
        return msg[0].substring(idx1 + 1).substring(0, idx2);
    }

    public String generateTag() {
        if (tag != null) {
            return tag;
        }
        tag = String.format("null<sip:null@null>;tag=%x%x", randomNumGenerator.nextInt(), randomNumGenerator.nextInt());
        return tag;
    }

    public static String[] replaceTag(String fields[], String newfield) {
        if (newfield == null) {
            return fields;
        }
        String newfields[] = splitSIPMessage(newfield);
        int oldtagidx = -1;
        boolean seperator = false;
        for (int i = 3; i < fields.length; i++) {
            if (!seperator) {
                if (fields[i].equals(":")) {
                    seperator = true;
                }
                continue;
            }
            if (fields[i].startsWith("tag=")) {
                oldtagidx = i;
                break;
            }
        }
        seperator = false;
        for (int i = 3; i < newfields.length; i++) {
            if (!seperator) {
                if (newfields[i].equals(":")) {
                    seperator = true;
                }
                continue;
            }
            if (newfields[i].startsWith("tag=")) {
                if (oldtagidx != -1) {
                    fields[oldtagidx] = newfields[i];
                    return fields;
                } else {
                    String retfields[] = new String[fields.length + 1];
                    for (int j = 0; j < fields.length; j++) {
                        retfields[j] = fields[j];
                    }
                    retfields[fields.length] = newfields[i];
                    return retfields;
                }
            }
        }
        return fields;
    }

    public static String[] removeTag(String fields[]) {
        boolean seperator = false;
        for (int i = 3; i < fields.length; i++) {
            if (!seperator) {
                if (fields[i].equals(":")) {
                    seperator = true;
                }
                continue;
            }
            if (fields[i].startsWith("tag=")) {
                //remove fields[i]
                String newfields[] = new String[fields.length - 1];
                for (int j = 0; j < i; j++) {
                    newfields[j] = fields[j];
                }
                for (int j = i + 1; j < fields.length; j++) {
                    newfields[j - 1] = fields[j];
                }
                return newfields;
            }
        }
        return fields;  
    }

    public String getCallID() {
        return String.format("%x%x", randomNumGenerator.nextInt(), System.currentTimeMillis());
    }

    public long getNow() {
        return System.currentTimeMillis() / 1000;
    }

    public String getNonce() {
        return String.format("%x%x%x%x", randomNumGenerator.nextInt(), randomNumGenerator.nextInt(), System.currentTimeMillis(), randomNumGenerator.nextInt());
    }

    public int[] getCodecs(String msg[]) {
        //m=audio port RTP/AVP 18 0 101
        String m = getHeader("m=", msg);
        if (m == null) {
            return new int[0];
        }
        String tags[] = m.split(" ");
        int cnt = tags.length - 3;
        if (cnt < 0) {
            return null;  //ohoh
        }
        int ret[] = new int[cnt];
        int pos = 0;
        for (int a = 3; a < tags.length; a++) {
            ret[pos++] = Integer.valueOf(tags[a]);
        }
        return ret;
    }

    public static boolean hasCodec(int codecs[], int codec) {
        for (int a = 0; a < codecs.length; a++) {
            if (codecs[a] == codec) {
                return true;
            }
        }
        return false;
    }

    protected String getRequest(String msg[]) {
        int idx = msg[0].indexOf(" ");
        if (idx == -1) {
            return null;
        }
        return msg[0].substring(0, idx);
    }

    protected int getResponseType(String msg[]) {
        if (msg[0].length() < 11) {
            return -1;  
        }
        if (!msg[0].regionMatches(true, 0, "SIP/2.0 ", 0, 8)) {
            return -1;  
        }    
        return Integer.valueOf(msg[0].substring(8, 11));
    }

    public static String getHeader(String header, String msg[]) {
        int sl = header.length();
        for (int a = 0; a < msg.length; a++) {
            if (msg[a].length() < sl) {
                continue;
            }
            if (msg[a].substring(0, sl).equalsIgnoreCase(header)) {
                return msg[a].substring(sl).trim().replaceAll("\"", "");
            }
        }
        return null;
    }

    public static String[] getHeaders(String header, String msg[]) {
        Vector<String> v = new Vector();
        int sl = header.length();
        for (int a = 0; a < msg.length; a++) {
            if (msg[a].length() < sl) {
                continue;
            }
            if (msg[a].substring(0, sl).equalsIgnoreCase(header)) {
                v.add(msg[a].substring(sl).trim().replaceAll("\"", ""));
            }
        }
        return (String[]) v.toArray(new String[0]);
    }

    public int getCSeq(String msg[]) {
        String cseqstr = getHeader("CSeq:", msg);
        if (cseqstr == null) {
            return -1;
        }
        String parts[] = cseqstr.split(" ");
        return Integer.valueOf(parts[0]);
    }

    public String getCSeqCmd(String msg[]) {
        String cseqstr = getHeader("CSeq:", msg);
        if (cseqstr == null) {
            return null;
        }
        String parts[] = cseqstr.split(" ");
        if (parts.length < 2) {
            return null;
        }
        return parts[1];
    }

    public String getResponse(String user, String pass, String realm, String cmd, String uri, String nonce, String qop, String nc, String cnonce) {
        MD5 md5 = new MD5();
        String H1 = user + ":" + realm + ":" + pass;
        md5.init();
        md5.add(H1.getBytes(), 0, H1.length());
        H1 = new String(md5.byte2char(md5.done()));
        String H2 = cmd + ":" + uri;
        md5.init();
        md5.add(H2.getBytes(), 0, H2.length());
        H2 = new String(md5.byte2char(md5.done()));
        String H3 = H1 + ":" + nonce + ":";
        if (qop != null) {
            H3 += nc + ":" + cnonce + ":" + qop + ":";
        }
        H3 += H2;
        md5.init();
        md5.add(H3.getBytes(), 0, H3.length());
        H3 = new String(md5.byte2char(md5.done()));
        return H3;
    }

    public String getAuthResponse(String request, String user, String pass, String remote, String cmd, String header) {
        if (!request.regionMatches(true, 0, "Digest ", 0, 7)) {
            return null;
        }
        String tags[] = request.substring(7).replaceAll(" ", "").split(",");
        String nonce = null, qop = null, nc = null, cnonce = null;
        String realm = null;
        realm = getHeader("realm=", tags);
        nonce = getHeader("nonce=", tags);
        nc = getHeader("nc=", tags);
        qop = getHeader("qop", tags);  //note:no value
        if (nonce == null) {
            return null;
        }  //no nonce found
        if (realm == null) {
            return null;
        }  //no realm found
        String response = getResponse(user, pass, realm, cmd, "sip:" + remote, nonce, qop, nc, cnonce);
        StringBuffer ret = new StringBuffer();
        ret.append(header);
        ret.append(" Digest username=\"" + user + "\", realm=\"" + realm + "\", uri=\"sip:" + remote + "\", nonce=\"" + nonce + "\"");
        ret.append(", response=\"" + response + "\"");
        if (qop != null) {
            ret.append(", qop=\"" + qop + "\"");
        }
        if (nc != null) {
            ret.append(", nc=\"" + nc + "\"");
        }
        if (cnonce != null) {
            ret.append(", cnonce=\"" + cnonce + "\"");
        }
        ret.append(", algorithm=MD5\r\n");
        return ret.toString();
    }

    public String getRemoteRTPHost(String msg[]) {
        String c = getHeader("c=", msg);
        if (c == null) {
            return null;
        }
        int idx = c.indexOf("IP4 ");
        if (idx == -1) {
            return null;
        }
        return c.substring(idx + 4);
    }

    public int getRemoteRTPPort(String msg[]) {
        String m = getHeader("m=", msg);
        if (m == null) {
            return -1;
        }
        int idx = m.indexOf("audio ");
        if (idx == -1) {
            return -1;
        }
        m = m.substring(idx + 6);
        idx = m.indexOf(' ');
        if (idx == -1) {
            return -1;
        }
        return Integer.valueOf(m.substring(0, idx));
    }

    public int getO(String msg[], int idx) {
        String o = getHeader("o=", msg);
        if (o == null) {
            return 0;
        }
        String os[] = o.split(" ");
        return Integer.valueOf(os[idx]);
    }

    public abstract String getLocalHost();

    public void buildSDP(SignallingDTO cd) {
        StringBuffer content = new StringBuffer();
        content.append("v=0\r\n");
        content.append("o=- " + cd.o1 + " " + cd.o2 + " IN IP4 " + getLocalHost() + "\r\n");
        content.append("s=" + userAgentStr + "\r\n");
        content.append("c=IN IP4 " + (cd.holding ? "0.0.0.0" : getLocalHost()) + "\r\n");
        content.append("t=0 0\r\n");
        content.append("m=audio " + cd.localRTPPort + " RTP/AVP ");
        if (hasCodec(cd.codecs, RTPHandler.CODEC_G729a)) {
            content.append("18 ");
        }
        if (hasCodec(cd.codecs, RTPHandler.CODEC_G711)) {
            content.append("0 ");
        }
        content.append("101\r\n");
        if (hasCodec(cd.codecs, RTPHandler.CODEC_G729a)) {
            content.append("a=rtpmap:18 G729/8000\r\n");
            content.append("a=fmtp:18 annexb=no\r\n");
        }
        if (hasCodec(cd.codecs, RTPHandler.CODEC_G711)) {
            content.append("a=rtpmap:0 PCMU/8000\r\n");
        }
        content.append("a=rtpmap:101 telephone-event/8000\r\n");
        content.append("a=fmtp:101 0-15\r\n");
        content.append("a=silenceSupp:off - - - -\r\n");
        content.append("a=ptime:20\r\n");
        if (cd.holding) {
            content.append("a=sendonly\r\n");
        } else if (cd.onHold) {
            content.append("a=recvonly\r\n");
        } else {
            content.append("a=sendrecv\r\n");
        }
        cd.sdp = content.toString();
    }     

    private class SIPProcessor extends Thread {
        public final int mtu = 1460;
        
        public void run() {
            while (running) {
                try {
                    byte data[] = new byte[mtu];
                    DatagramPacket pack = new DatagramPacket(data, mtu);
                    udpSocket.receive(pack);
                    
                    int length = pack.getLength();
                    if (length <= 2) {
                        continue;  
                    }
                    String remote = pack.getAddress().getHostAddress();
                    int offset = (int) data[0];
                    if (offset >= 6 && offset <= 8) {
                        if (rtpHandler.rtpProcessor1 != null) {
                            rtpHandler.rtpProcessor1.run(pack, decodeRTP(data, length, offset));
                        }
                        continue;
                    }
                    if (offset != 5) {
                        continue;
                    }
                    String s = new String(decodeSIP(data, length));
                    s = s.replaceAll("\\$m_ip\\$", remote).replaceAll("\\$m_port\\$", pack.getPort() + "");
                    System.err.println("Received: ");
                    System.err.println(s);
                    System.err.println();
                    String msg[] = s.split("\r\n");
                    ua.sipPacketProcessing(msg, remote, pack.getPort());
                } catch (Exception e) {
                }
            }
        }
    }
}
