package pcdialer.media;

import javax.sound.sampled.*;
import java.util.*;
import pcdialer.Dialer;

public class Media {

    public boolean playing = false;
    public boolean resample441k = false;
    public boolean ringing = false;
    public boolean keepAudioOpen = true;
    public boolean swVolForce = false;
    public boolean swVolPlay;
    public boolean swVolRec;
    public boolean mute = false;
    public int volPlay = 100;
    public int volRec = 100;
    public int ring_440;
    public int ring_480;
    public int ringCycle;
    public int ringCount;
    public int wait_440;
    public int waitCycle;
    public final double ringVol = 9000.0;
    public String audioInput = "<default>", audioOutput = "<default>";
    public short silence[] = new short[160];
    public short mixed[] = new short[160];
    public short data[] = new short[160];
    public DTMF dtmf = new DTMF();
    public Timer timer;
    public AudioFormat audioFormat;
    public SourceDataLine sourceDataLine;
    public TargetDataLine targetDataLine;
    public FloatControl sdlvol, tdlvol;

    public boolean init() {
        audioFormat = new AudioFormat((resample441k ? 44100.0f : 8000.0f), 16, 1, true, true);
        int idx;
        try {
            Mixer.Info mi[] = AudioSystem.getMixerInfo();
            idx = -1;
            for (int a = 0; a < mi.length; a++) {
                if (mi[a].getName().equalsIgnoreCase(audioOutput)) {
                    idx = a;
                    break;
                }
            }
            if ((audioOutput.equalsIgnoreCase("<default>")) || (idx == -1)) {
                sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            } else {
                sourceDataLine = AudioSystem.getSourceDataLine(audioFormat, mi[idx]);
            }
            if (sourceDataLine == null) {
                throw new Exception("unable to get playback device");
            }
            idx = -1;
            for (int a = 0; a < mi.length; a++) {
                if (mi[a].getName().equalsIgnoreCase(audioInput)) {
                    idx = a;
                    break;
                }
            }
            if ((audioInput.equalsIgnoreCase("<default>")) || (idx == -1)) {
                targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
            } else {
                targetDataLine = AudioSystem.getTargetDataLine(audioFormat, mi[idx]);
            }
            if (targetDataLine == null) {
                throw new Exception("unable to get recording device");
            }
            sourceDataLine.open(audioFormat);
            if (keepAudioOpen) {
                sourceDataLine.start();
            }
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                public void run() {
                    processMedia();
                }
            }, 0, 20);
        } catch (Exception e) {
            return false;
        }
        try {
            if (!swVolForce) {
                sdlvol = (FloatControl) sourceDataLine.getControl(FloatControl.Type.VOLUME);
                if (sdlvol == null) {
                    throw new Exception("unable to get playback volume control");
                }
            } else {
                swVolPlay = true;
            }
        } catch (Exception e1) {
            try {
                sdlvol = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                if (sdlvol == null) {
                    throw new Exception("unable to get playback volume control");
                }
            } catch (Exception w1) {
                swVolPlay = true;
            }
        }
        try {
            if (!swVolForce) {
                tdlvol = (FloatControl) targetDataLine.getControl(FloatControl.Type.VOLUME);
                if (tdlvol == null) {
                    throw new Exception("unable to get recording volume control");
                }
            } else {
                swVolRec = true;
            }
        } catch (Exception e2) {
            try {
                tdlvol = (FloatControl) targetDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                if (tdlvol == null) {
                    throw new Exception("unable to get recording volume control");
                }
            } catch (Exception w2) {
                swVolRec = true;
            }
        }
        if (false) {  //test
            Control cs[] = targetDataLine.getControls();
        }
        return true;
    }

    public void stopMedia() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (sourceDataLine != null) {
            try {
                if (keepAudioOpen) {
                    sourceDataLine.stop();
                } else {
                    if (playing) {
                        sourceDataLine.stop();
                        playing = false;
                    }
                }
                sourceDataLine.close();
            } catch (Exception e) {
            }
            sourceDataLine = null;
        }
        if (targetDataLine != null) {
            try {
                targetDataLine.stop();
                targetDataLine.close();
            } catch (Exception e) {
            }
            targetDataLine = null;
        }
    }

    public void scaleBuffer(short buf[], int scale) {
        if (scale == 0) {
            for (int i = 0; i < 160; i++) {
                buf[i] = 0;
            }
        } else {
            for (int i = 0; i < 160; i++) {
                buf[i] = (short) (buf[i] * scale / 100);
            }
        }
    }

    public byte[] short2byte(short buf[]) {
        byte buf8[];
        if (resample441k) {
            buf8 = new byte[882 * 2];
            int pos = 0;
            float x1 = 0.5125f;
            float x2 = 0.0f;
            int x;
            for (int i = 0; i < 160; i++) {
                x2 += x1;
                x = 5 + (int) x2;
                if (x2 >= 1.0) {
                    x2 -= 1.0f;
                }
                for (int j = 0; j < x; j++) {
                    buf8[pos * 2] = (byte) (buf[i] >>> 8);
                    buf8[pos * 2 + 1] = (byte) (buf[i] & 0xff);
                    pos++;
                    if (pos > 881) {
                        pos = 881;
                    }
                }
            }
        } else {
            buf8 = new byte[160 * 2];
            for (int a = 0; a < 160; a++) {
                buf8[a * 2] = (byte) (buf[a] >>> 8);
                buf8[a * 2 + 1] = (byte) (buf[a] & 0xff);
            }
        }
        return buf8;
    }

    public void byte2short(byte buf8[], short buf16[]) {
        if (resample441k) {
            int pos = 0;
            float x1 = 0.5125f;
            float x2 = 0.0f;
            int x;
            for (int i = 0; i < 160; i++) {
                x2 += x1;
                x = 5 + (int) x2;
                if (x2 >= 1.0) {
                    x2 -= 1.0f;
                }
                buf16[i] = (short) ((((short) (buf8[pos * 2])) << 8) + (((short) (buf8[pos * 2 + 1])) & 0xff));
                pos += x;
                if (pos > 881) {
                    pos = 881;
                }
            }
        } else {
            for (int i = 0; i < 160; i++) {
                buf16[i] = (short) ((((short) (buf8[i * 2])) << 8) + (((short) (buf8[i * 2 + 1])) & 0xff));
            }
        }
    }

    public void writeData(short buf[]) {
        if (swVolPlay) {
            scaleBuffer(buf, volPlay);
        }
        sourceDataLine.write(short2byte(buf), 0, (resample441k ? 882 * 2 : 160 * 2));
    }

    public boolean readData(short buf[]) {
        byte buf8[];
        int ret;
        if (resample441k) {
            if (targetDataLine.available() < (882 * 2)) {
                return false;
            }
            buf8 = new byte[882 * 2];
            ret = targetDataLine.read(buf8, 0, 882 * 2);
            if (ret != 882 * 2) {
                return false;
            }
        } else {
            if (targetDataLine.available() < (160 * 2)) {
                return false;  
            }
            buf8 = new byte[160 * 2];
            ret = targetDataLine.read(buf8, 0, 160 * 2);
            if (ret != 160 * 2) {
                return false;
            }
        }
        byte2short(buf8, buf);
        if (swVolPlay) {
            scaleBuffer(buf, volRec);
        }
        return true;
    }

    public void flush() {
        sourceDataLine.flush();
    }

    public void processMedia() {
        try {
            byte encoded[];
            if (!keepAudioOpen) {
                if (!playing) {
                    if ((Dialer.IS_200OK) || (Dialer.IS_RINGING)) {
                        playing = true;
                        sourceDataLine.start();
                    }
                } else {
                    int pc = 0;
                    if ((Dialer.IS_200OK) || (Dialer.IS_RINGING)) {
                        pc++;
                    }
                    if (pc == 0) {
                        playing = false;
                        sourceDataLine.stop();
                    }
                }
            }
            if (Dialer.IS_RINGING || Dialer.IS_INCOMING) {
                if (!ringing) {
                    startRinging();
                }
            }
            System.arraycopy(silence, 0, mixed, 0, 160);
            if (Dialer.IS_200OK && Dialer.rtpHandler.getSamples(data)) {
                if (Dialer.dtmf != 'x') {
                    mix(mixed, dtmf.getSamples(Dialer.dtmf));
                }
                mix(mixed, data);
                if (ringing) {
                    mix(mixed, getCallWaiting());
                }
                writeData(mixed);
            } else {
                if (ringing) {
                    mix(mixed, getRinging());
                }
                if ((playing) || (keepAudioOpen)) {
                    writeData(mixed);
                }
            }
            boolean readstatus = readData(data);
            if ((mute) || (!readstatus)) {
                System.arraycopy(silence, 0, data, 0, 160);
            }
            if ((Dialer.IS_200OK)) {
                encoded = Dialer.rtpHandler.codec.encode(data, 0, 160);
                if (Dialer.dtmfEnd) {
                    Dialer.rtpHandler.sendDTMF(Dialer.dtmf, true);
                    Dialer.dtmfEnd = false;
                    Dialer.dtmf = 'x';
                } else if (Dialer.dtmf != 'x') {
                    Dialer.rtpHandler.sendDTMF(Dialer.dtmf, false);
                } else {
                    Dialer.rtpHandler.sendRTPPacket(encoded, encoded.length);
                }
            }
        } catch (Exception e) {
        }
    }

    public void mix(short out[], short in[]) {
        for (int i = 0; i < 160; i++) {
            out[i] += in[i];
        }
    }

    public void startRinging() {
        ringing = true;
        ring_440 = 0;
        ring_480 = 0;
        ringCycle = 0;
        ringCount = 0;
        wait_440 = 0;
        waitCycle = 0;
    }

    public short[] getRinging() {
        ringCount += 160;
        if (ringCount == 8000) {
            ringCount = 0;
            ringCycle++;
        }
        if (ringCycle == 5) {
            ringCycle = 0;
        }
        if (ringCycle > 1) {
            ring_440 = 0;
            ring_480 = 0;
            return silence;
        }
        short buffer[] = new short[160];
        for (int i = 0; i < 160; i++) {
            buffer[i] = (short) (Math.sin((2.0 * Math.PI / (8000.0 / 440.0)) * (i + ring_440)) * ringVol);
        }
        ring_440 += 160;
        if (ring_440 == 8000) {
            ring_440 = 0;
        }
        for (int i = 0; i < 160; i++) {
            buffer[i] += (short) (Math.sin((2.0 * Math.PI / (8000.0 / 480.0)) * (i + ring_480)) * ringVol);
        }
        ring_480 += 160;
        if (ring_480 == 8000) {
            ring_480 = 0;
        }
        return buffer;
    }

    public void ring() {
        short[] mixedx = new short[160];
        mix(mixedx, getRinging());
        writeData(mixedx);
    }

    public short[] getCallWaiting() {
        waitCycle++;
        if (waitCycle == 206) {
            waitCycle = 0;
        }
        if ((waitCycle > 6) || (waitCycle == 2) || (waitCycle == 3)) {
            wait_440 = 0;
            return silence;
        }
        short buf[] = new short[160];
        for (int i = 0; i < 160; i++) {
            buf[i] = (short) (Math.sin((2.0 * Math.PI / (8000.0 / 440.0)) * (i + wait_440)) * ringVol);
        }
        wait_440 += 160;
        if (wait_440 == 8000) {
            wait_440 = 0;
        }
        return buf;
    }

    public static String[] getMixers() {
        ArrayList<String> mixers = new ArrayList<String>();
        Mixer.Info mi[] = AudioSystem.getMixerInfo();
        mixers.add("<default>");
        for (int a = 0; a < mi.length; a++) {
            mixers.add(mi[a].getName());
        }
        String newMixers[] = new String[mixers.size()];
        for (int i = 0; i < mixers.size(); i++) {
            newMixers[i] = mixers.get(i);
        }
        return newMixers;
    }

    public void writeDTMF(char digit) {
        short a[] = new short[160];
        System.arraycopy(silence, 0, a, 0, 160);
        short b[] = dtmf.getSamples(digit);
        if (b == null) {
            return;
        }
        mix(a, b);
        writeData(a);
    }

    public void startCalling() {
        writeData(dtmf.getSamples('0'));
        writeData(dtmf.getSamples('1'));
        writeData(dtmf.getSamples('2'));
        writeData(dtmf.getSamples('3'));
        writeData(dtmf.getSamples('4'));
        writeData(dtmf.getSamples('6'));
        writeData(dtmf.getSamples('6'));
        writeData(dtmf.getSamples('7'));
        writeData(dtmf.getSamples('8'));
        writeData(dtmf.getSamples('9'));
    }
}
