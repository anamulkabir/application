package pcdialer.signalling;

import java.net.*;
import java.util.*;

/** Handles the client end of a SIP link. */
public class UA extends Signalling {

    public boolean IS_REGISTERRED;
    public int expires;
    public int messageCseq = 1;
    public int unauth = 0;
    public int localPort;
    public int remotePort;
    public String remoteHost;
    public String remoteIP;
    public String user;
    public String auth;
    public String pass;
    public String regCallID;
    public String messageCallID = "";
    public String fromTag;
    public String localHost;
    public static String inetIP;
    public static final int tcpPorts[] = {22, 80};
    public Hashtable<String, SignallingDTO> SignallingDTOList;
    public UAInterface uaInterface;
    public UA ua = null;

    public String getUser() {
        return user;
    }

    public boolean isHold(String callid) {
        SignallingDTO cd = getCallDetails(callid);
        return cd.onHold;
    }

    public boolean isRegistered() {
        return IS_REGISTERRED;
    }

    public boolean initUA(String remotehost, int remoteport, int localport, UA ua, UAInterface uaInterface) {        
        this.ua = ua;
        this.uaInterface = uaInterface;
        this.localPort = localport;
        this.remotePort = remoteport;
        SignallingDTOList = new Hashtable<String, SignallingDTO>();
        try {
            super.init(localport, ua);
            this.localPort = super.signallingPort;
            this.remoteHost = remotehost;
            this.remoteIP = InetAddress.getByName(remotehost).getHostAddress();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void uninitUA() {
        super.uninit();
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public boolean registerUA(String user, String auth, String pass) {
        regCallID = getCallID();
        SignallingDTO signallingDTO = getCallDetails(regCallID);  //new CallDetails
        if ((auth == null) || (auth.length() == 0)) {
            this.auth = user;
        } else {
            this.auth = auth;
        }
        this.user = user;
        this.pass = pass;
        expires = 3600;
        signallingDTO.to = new String[]{user, user, remoteHost + ":" + remotePort, ":"};
        signallingDTO.from = new String[]{user, user, remoteHost + ":" + remotePort, ":"};
        signallingDTO.from = replaceTag(signallingDTO.from, generateTag());
        signallingDTO.contact = "<sip:" + user + "@" + getLocalHost() + ":" + localPort + ">";
        signallingDTO.uri = "sip:" + remoteHost;  // + ";rinstance=" + getrinstance();
        signallingDTO.callID = regCallID;
        signallingDTO.branch = getBranch();
        signallingDTO.cSeq++;
        boolean ret = generateCommand(signallingDTO, "REGISTER", null, false);
        return ret;
    }

    public void keepAliveUA() {
        send(getLocalHost(), remoteIP, remotePort, "\r\n");
    }

    private boolean isServerOnPrivateNetwork() {
        if (remoteIP.startsWith("192.168.")) {
            return true;
        }
        if (remoteIP.startsWith("10.")) {
            return true;
        }
        return false;
    }

    public String getLocalHost() {
        if (localHost != null) {
            return localHost;
        }
        findLocalHost();
        return localHost;
    }

    public void findLocalHost() {
        Socket sock;
//        if (!isServerOnPrivateNetwork()) {
//            if (inetIP != null) {
//                localHost = inetIP;
//                return;
//            }
//            try {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(
//                        new URL("http://checkip.dyndns.org").openStream()));
//                String line = reader.readLine().replaceAll(" ", "");
//                int index = line.indexOf(':');
//                line = line.substring(index + 1);
//                index = line.indexOf('<');
//                inetIP = line.substring(0, index);
//                localHost = inetIP;
//                return;
//            } catch (Exception e3) {
//            }
//        }
        for (int i = 0; i < tcpPorts.length; i++) {
            try {
                sock = new Socket();
                sock.connect(new InetSocketAddress(remoteHost, tcpPorts[i]), 1500);
            } catch (Exception e1) {
                continue;
            }
            localHost = sock.getLocalAddress().getHostAddress();
            try {
                sock.close();
            } catch (Exception e2) {
            }
            return;
        }
        try {
            InetAddress local = InetAddress.getLocalHost();
            localHost = local.getHostAddress();
            return;
        } catch (Exception e4) {
        }
        localHost = "127.0.0.1";
    }

    public SignallingDTO getCallDetails(String callid) {
        SignallingDTO cd = SignallingDTOList.get(callid);
        if (cd == null) {
            cd = new SignallingDTO();
            setCallDetails(callid, cd);
        }
        return cd;
    }

    public void setCallDetails(String callid, SignallingDTO cd) {
        if (cd == null) {
            SignallingDTOList.remove(callid);
        } else {
            SignallingDTOList.put(callid, cd);
        }
    }

    private boolean generateCommand(SignallingDTO cd, String cmd, String header, boolean sdp) {
        StringBuffer commandBuffer = new StringBuffer();
        commandBuffer.append(cmd + " " + (cd.uri == null ? "sip:" + remoteHost : cd.uri) + " SIP/2.0\r\n");
        commandBuffer.append("Via: SIP/2.0/UDP " + getLocalHost() + ":" + localPort + ";branch=" + cd.branch + ";rport\r\n");
        commandBuffer.append("Max-Forwards: 70\r\n");
        commandBuffer.append("Contact: " + cd.contact + "\r\n");
        commandBuffer.append("To: " + joinTag(cd.to) + "\r\n");
        commandBuffer.append("From: " + joinTag(cd.from) + "\r\n");
        commandBuffer.append("Call-ID: " + cd.callID + "\r\n");
        commandBuffer.append("Cseq: " + cd.cSeq + " " + cmd + "\r\n");
        if (cmd.equals("REGISTER")) {
            commandBuffer.append("Expires: " + expires + "\r\n");
            messageCallID = cd.callID;
        }
        commandBuffer.append("Allow: INVITE, ACK, CANCEL, BYE, REFER, NOTIFY, OPTIONS\r\n");
        commandBuffer.append("User-Agent: " + userAgentStr + "\r\n");
        if (header != null) {
            commandBuffer.append(header);
        }
        if ((cd.sdp != null) && (sdp)) {
            commandBuffer.append("Content-Type: application/sdp\r\n");
            commandBuffer.append("Content-Length: " + cd.sdp.length() + "\r\n\r\n");
            commandBuffer.append(cd.sdp);
        } else {
            commandBuffer.append("Content-Length: 0\r\n\r\n");
        }
        return send(getLocalHost(), remoteIP, remotePort, commandBuffer.toString());
    }

    private boolean generateResponse(SignallingDTO signallingDTO, String cmd, int code, String msg, boolean sdp) {
        StringBuffer responseBuffer = new StringBuffer();
        responseBuffer.append("SIP/2.0 " + code + " " + msg + "\r\n");
        if (signallingDTO.viaList != null) {
            for (int a = 0; a < signallingDTO.viaList.length; a++) {
                responseBuffer.append(signallingDTO.viaList[a]);
                responseBuffer.append("\r\n");
            }
        }
        responseBuffer.append("Contact: " + signallingDTO.contact + "\r\n");
        responseBuffer.append("To: " + joinTag(signallingDTO.to) + "\r\n");
        responseBuffer.append("From: " + joinTag(signallingDTO.from) + "\r\n");
        responseBuffer.append("Call-ID: " + signallingDTO.callID + "\r\n");
        responseBuffer.append("Cseq: " + signallingDTO.cSeq + " " + cmd + "\r\n");
        responseBuffer.append("Allow: INVITE, ACK, CANCEL, BYE, REFER, NOTIFY, OPTIONS\r\n");
        responseBuffer.append("User-Agent: " + userAgentStr + "\r\n");
        if ((signallingDTO.sdp != null) && (sdp)) {
            responseBuffer.append("Content-Type: application/sdp\r\n");
            responseBuffer.append("Content-Length: " + signallingDTO.sdp.length() + "\r\n\r\n");
            responseBuffer.append(signallingDTO.sdp);
        } else {
            responseBuffer.append("Content-Length: 0\r\n\r\n");
        }
        send(getLocalHost(), remoteIP, remotePort, responseBuffer.toString());
        return true;
    }

    public boolean reRegisterUA() {
        setCallDetails(regCallID, null);
        IS_REGISTERRED = false;
        return registerUA(user, auth, pass);
    }

    public boolean unRegisterUA() {
        expires = 0;
        SignallingDTO signallingDTO = getCallDetails(regCallID);
        generateCommand(signallingDTO, "REGISTER", null, false);
        return true;
    }

    public String sendInvite(String to, int localrtpport, int codecs[]) {
        String callID = getCallID();
        SignallingDTO signallingDTO = getCallDetails(callID);  //new CallDetails
        signallingDTO.to = new String[]{to, to, remoteHost + ":" + remotePort, ":"};
        signallingDTO.from = new String[]{user, user, remoteHost + ":" + remotePort, ":"};
        signallingDTO.contact = "<sip:" + user + "@" + getLocalHost() + ":" + localPort + ">";
        signallingDTO.uri = "sip:" + to + "@" + remoteHost + ":" + remotePort;
        signallingDTO.from = replaceTag(signallingDTO.from, generateTag());
        signallingDTO.branch = getBranch();
        signallingDTO.o1 = 256;
        signallingDTO.o2 = 256;
        signallingDTO = getCallDetails(callID);
        signallingDTO.callID = callID;
        signallingDTO.codecs = codecs;
        signallingDTO.localRTPPort = localrtpport;
        buildSDP(signallingDTO);
        signallingDTO.cSeq++;
        if (!generateCommand(signallingDTO, "INVITE", null, true)) {
            setCallDetails(callID, null);
            return null;
        }
        return callID;
    }

    public boolean sendRefer(String callid, String orgto, String refer) {
        String headers = "Refer-To: <sip:" + refer + "@" + remoteHost + ">\r\nReferred-By: <sip:" + user + "@" + remoteHost + ":" + localPort + ">\r\n";
        SignallingDTO signallingDTO = getCallDetails(callid);
        signallingDTO.cSeq++;
        boolean ret = generateCommand(signallingDTO, "REFER", headers, false);
        return ret;
    }

    public boolean holdUA(String callid, int localrtpport) {
        SignallingDTO signallingDTO = getCallDetails(callid);
        signallingDTO.o2++;
        signallingDTO.holding = true;
        signallingDTO.localRTPPort = localrtpport;
        buildSDP(signallingDTO);
        if (!generateCommand(signallingDTO, "INVITE", null, true)) {
            return false;
        }
        return true;
    }

    public boolean reinvite(String callid, int localrtpport, int codecs[]) {
        SignallingDTO signallingDTO = getCallDetails(callid);
        signallingDTO.o2++;
        signallingDTO.holding = false;
        signallingDTO.codecs = codecs;
        signallingDTO.localRTPPort = localrtpport;
        buildSDP(signallingDTO);
        signallingDTO.cSeq++;
        if (!generateCommand(signallingDTO, "INVITE", null, true)) {
            return false;
        }
        return true;
    }

    public boolean sendCancel(String callid) {
        SignallingDTO signallingDTO = getCallDetails(callid);
        String authPass;
        if (signallingDTO.authStr != null) {
            authPass = getAuthResponse(signallingDTO.authStr, auth, pass, remoteHost, "BYE", "Proxy-Authorization:");
        } else {
            authPass = null;
        }
        boolean ret = generateCommand(signallingDTO, "CANCEL", authPass, false);
        return ret;
    }

    public boolean sendBye(String callID) {
        SignallingDTO signallingDTO = getCallDetails(callID);
        String authPass;
        if (signallingDTO.authStr != null) {
            authPass = getAuthResponse(signallingDTO.authStr, auth, pass, remoteHost, "BYE", "Proxy-Authorization:");
        } else {
            authPass = null;
        }
        signallingDTO.cSeq++;
        boolean ret = generateCommand(signallingDTO, "BYE", authPass, false);
        return ret;
    }

    public boolean sendAccept(String callid, int localrtpport, int codec[]) {
        SignallingDTO signallingDTO = getCallDetails(callid);
        signallingDTO.codecs = codec;
        signallingDTO.localRTPPort = localrtpport;
        buildSDP(signallingDTO);
        generateResponse(signallingDTO, "INVITE", 200, "OK", true);
        String tmp[] = signallingDTO.to;
        signallingDTO.to = signallingDTO.from;
        signallingDTO.from = tmp;
        return true;
    }

    public boolean sendDeny(String callid, String msg, int code) {
        SignallingDTO signallingDTO = getCallDetails(callid);
        generateResponse(signallingDTO, "INVITE", code, msg, false);
        return true;
    }

    public void sipPacketProcessing(String msg[], String remoteip, int remoteport) {
        String tempStr, authPass;
        try {
            if (!remoteip.equals(this.remoteIP)) {
                return;
            }
            String callID = getHeader("Call-ID:", msg);
            if (callID == null) {
                callID = getHeader("i:", msg);
                if (callID == null) {
                    return;
                }
            }
            SignallingDTO signallingDTO = getCallDetails(callID);
            signallingDTO.cSeq = getCSeq(msg);
            if (signallingDTO.callID == null) {
                signallingDTO.callID = callID;
                signallingDTO.branch = getBranch();
                tempStr = getHeader("To:", msg);
                if (tempStr == null) {
                    tempStr = getHeader("t:", msg);
                }
                signallingDTO.to = splitSIPMessage(tempStr);
                tempStr = getHeader("From:", msg);
                if (tempStr == null) {
                    tempStr = getHeader("f:", msg);
                }
                signallingDTO.from = splitSIPMessage(tempStr);
                signallingDTO.viaList = getViaList(msg);
                signallingDTO.contact = "<sip:" + user + "@" + getLocalHost() + ":" + localPort + ">";
                signallingDTO.uri = getHeader("Contact:", msg);
                if (signallingDTO.uri == null) {
                    signallingDTO.uri = getHeader("m:", msg);
                }
                if (signallingDTO.uri != null) {
                    signallingDTO.uri = signallingDTO.uri.substring(1, signallingDTO.uri.length() - 1);  //remove < > brackets
                }
            }
            String cmd = getCSeqCmd(msg);
            int type = getResponseType(msg);
            switch (type) {
                case -1:
                    tempStr = getRequest(msg);
                    if (tempStr.equalsIgnoreCase("INVITE")) {
                        String remotertphost = getRemoteRTPHost(msg);
                        int remotertpport = getRemoteRTPPort(msg);
                        int codecs[] = getCodecs(msg);
                        signallingDTO.to = replaceTag(signallingDTO.to, generateTag());
                        if (signallingDTO.o1 == 0) {
                            signallingDTO.o1 = getO(msg, 1) + 1;
                            signallingDTO.o2 = getO(msg, 2) + 1;
                        } else {
                            signallingDTO.o2++;
                        }
                        signallingDTO.onHold = isWaiting(msg);
                        switch (uaInterface.setInvited(callID, signallingDTO.from[0], signallingDTO.from[1], remotertphost, remotertpport, codecs)) {
                            case 180:
                                generateResponse(signallingDTO, cmd, 180, "RINGING", false);
                                break;
                            case 200:
                                generateResponse(signallingDTO, cmd, 200, "OK", true);
                                break;
                            case 486:
                                generateResponse(signallingDTO, cmd, 486, "BUSY HERE", false);
                                break;
                        }
                        break;
                    }
                    if (tempStr.equalsIgnoreCase("CANCEL")) {
                        uaInterface.setCancelled(callID, 0);
                        generateResponse(signallingDTO, cmd, 200, "OK", false);
                        setCallDetails(callID, null);
                        break;
                    }
                    if (tempStr.equalsIgnoreCase("BYE")) {
                        generateResponse(signallingDTO, cmd, 200, "OK", false);
                        uaInterface.setBye(callID);
                        setCallDetails(callID, null);
                        break;
                    }
                    if (tempStr.equalsIgnoreCase("OPTIONS")) {
                        generateResponse(signallingDTO, cmd, 200, "OK", false);
                        break;
                    }
                    if (tempStr.equalsIgnoreCase("MESSAGE")) {
                        generateResponse(signallingDTO, cmd, 200, "OK", false);
                        String commands[] = getHeaders("SIPLINK_COMMAND :", msg);
                        messageCallID = callID;
                        fromTag = getHeader("From:", msg);
                        uaInterface.setCommandReceived(callID, commands);
                        break;
                    }
                    break;
                case 100:
                    uaInterface.setTrying(callID);
                    break;
                case 180:
                case 183:
                    uaInterface.setRinging(callID, getRemoteRTPHost(msg), getRemoteRTPPort(msg), getCodecs(msg));
                    break;
                case 200:
                    if (callID.equalsIgnoreCase(regCallID)) {
                        if (expires > 0) {
                            IS_REGISTERRED = true;
                            uaInterface.setRegistered();
                        }
                    } else if (cmd.equalsIgnoreCase("INVITE")) {
                        String remotertphost = getRemoteRTPHost(msg);
                        int remotertpport = getRemoteRTPPort(msg);
                        int codecs[] = getCodecs(msg);
                        signallingDTO.uri = getHeader("Contact:", msg);
                        if (signallingDTO.uri == null) {
                            signallingDTO.uri = getHeader("m:", msg);
                        }
                        signallingDTO.uri = signallingDTO.uri.substring(1, signallingDTO.uri.length() - 1);
                        signallingDTO.to = replaceTag(signallingDTO.to, getHeader("To:", msg));
                        signallingDTO.to = replaceTag(signallingDTO.to, getHeader("t:", msg));
                        generateCommand(signallingDTO, "ACK", null, false);
                        uaInterface.setAccepted(callID, remotertphost, remotertpport, codecs);
                    }
                    break;
                case 202:
                    break;
                case 401:
                    if (cmd.equalsIgnoreCase("REGISTER")) {
                        unauth++;
                        if (unauth > 20) {
                            uaInterface.setUnauthorized();
                            break;
                        }
                    }
                    signallingDTO.authStr = getHeader("WWW-Authenticate:", msg);
                    authPass = getAuthResponse(signallingDTO.authStr, auth, pass, remoteHost, cmd, "Authorization:");
                    if (authPass == null) {
                        break;
                    }
                    signallingDTO.cSeq++;
                    generateCommand(signallingDTO, cmd, authPass, cmd.equalsIgnoreCase("INVITE"));
                    break;
                case 403:
                    if (cmd.equalsIgnoreCase("INVITE")) {
                        uaInterface.setCancelled(callID, type);
                    } else {
                        uaInterface.setUnauthorized();
                    }
                    break;
                case 407:
                    generateCommand(signallingDTO, "ACK", null, false);
                    signallingDTO.authStr = getHeader("Proxy-Authenticate:", msg);
                    authPass = getAuthResponse(signallingDTO.authStr, auth, pass, remoteHost, cmd, "Proxy-Authorization:");
                    if (authPass == null) {
                        break;
                    }
                    signallingDTO.cSeq++;
                    generateCommand(signallingDTO, cmd, authPass, cmd.equalsIgnoreCase("INVITE"));
                    break;
                case 415:
                case 488:
                    generateCommand(signallingDTO, "ACK", null, false);
                    setCallDetails(callID, null);
                    uaInterface.setBadCodec(callID);
                    break;
                default:
                    generateCommand(signallingDTO, "ACK", null, false);
                    uaInterface.setCancelled(callID, type);
                    setCallDetails(callID, null);
                    break;
            }
        } catch (Exception e) {
        }
    }
}
