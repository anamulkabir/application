package pcdialer.media.codec;

import pcdialer.media.codec.g729.Encoder;
import pcdialer.media.codec.g729.Decoder;
import pcdialer.media.RTPHandler;

/** Encodes/decodes g729a packets. */

public class G729a implements Codec {
  private Encoder encoder = new Encoder();
  private Decoder decoder = new Decoder();
  private RTPHandler rtpHandler;

  public G729a(RTPHandler rtpHandler) {
    this.rtpHandler = rtpHandler;
  }

  public byte[] encode(short samples[], int off, int len) {
    byte[] encoded = new byte[len / 80 * 10 + 12];
    rtpHandler.buildRTPHeader(encoded, 18, rtpHandler.getSeqNumber(), rtpHandler.getTimeStamp(), rtpHandler.getSsrc());
    encoder.encode(encoded, 12, samples, off, len / 80);
    return encoded;
  }

  private int getuint32(byte[] data, int offset) {
    int ret;
    ret  = (int)data[offset+3] & 0xff;
    ret += ((int)data[offset+2] & 0xff) << 8;
    ret += ((int)data[offset+1] & 0xff) << 16;
    ret += ((int)data[offset+0] & 0xff) << 24;
    return ret;
  }

  private int decode_timestamp;

  public short[] decode(byte encoded[], int off, int len) {
    if (len < 12) {
      return null;
    }
    int decode_timestamp = getuint32(encoded, 4);
    if (this.decode_timestamp == 0) {
      this.decode_timestamp = decode_timestamp;
    } else {
      this.decode_timestamp = decode_timestamp;
    }
    short[] samples = new short[(len - 12) / 10 * 80];
    decoder.decode(samples, 0, encoded, 12, (len - 12) / 10);
    return samples;
  }
}
