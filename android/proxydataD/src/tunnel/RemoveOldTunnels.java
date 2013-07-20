package tunnel;

import java.util.Enumeration;
import org.apache.log4j.Logger;

class RemoveOldTunnels extends java.util.TimerTask {

    static Logger logger = Logger.getLogger(RemoveOldTunnels.class.getName());
    String reqString = null;

    public RemoveOldTunnels() {
    }

    public void run() {
        long t = 0;
        long now = new java.util.Date().getTime() / 1000;
        String s;
        String remove = "";
        Object ko;
        Object o[];

        for (Enumeration e = ClientServerDataProcessor.addressMapByCallID.elements(), k = ClientServerDataProcessor.addressMapByCallID.keys(); e.hasMoreElements();) {
            o = (Object[]) e.nextElement();
            ko = k.nextElement();
            s = o[0].toString().replaceAll("/", "");
            t = (Long) o[1];
            if (now - t > 20 * 60) {
                ClientServerDataProcessor.addressMapByCallID.remove(ko);
                ClientServerDataProcessor.addressMapByClientRTPInfo.remove(s);
                remove += "#" + s + "#";
            }
        }
        for (Enumeration e = ClientServerDataProcessor.addressMapByUserName.elements(), k = ClientServerDataProcessor.addressMapByUserName.keys(); e.hasMoreElements();) {
            s = e.nextElement().toString().replaceAll("/", "");
            ko = k.nextElement();
            if (remove.contains("#" + s + "#")) {
                ClientServerDataProcessor.addressMapByUserName.remove(ko);
            }
        }
        for (Enumeration e = ClientServerDataProcessor.addressMapByServerRTPInfo.elements(), k = ClientServerDataProcessor.addressMapByServerRTPInfo.keys(); e.hasMoreElements();) {
            s = e.nextElement().toString().replaceAll("/", "");
            ko = k.nextElement();
            if (remove.contains("#" + s + "#")) {
                ClientServerDataProcessor.addressMapByServerRTPInfo.remove(ko);
            }
        }
    }
}
