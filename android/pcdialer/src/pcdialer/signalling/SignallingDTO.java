package pcdialer.signalling;

public class SignallingDTO {
  public int cSeq = 0;
  public String callID;
  public String to[], from[]; 
  public String sdp;
  public String uri;
  public String contact;
  public String authStr;
  public String viaList[];  
  public String branch;
  public int o1, o2;
  public boolean holding; 
  public boolean onHold; 
  public int codecs[];
  public int localRTPPort;
}
