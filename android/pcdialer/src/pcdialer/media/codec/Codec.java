package pcdialer.media.codec;

/** Base interface for all codecs. */

public interface Codec {
  public byte[] encode(short src16[], int off, int len);
  public short[] decode(byte src8[], int off, int len);
}
