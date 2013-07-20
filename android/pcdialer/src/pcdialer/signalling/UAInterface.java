package pcdialer.signalling;

public interface UAInterface {
  public void setRegistered();  
  public void setUnauthorized();  
  public void setTrying(String callid);  
  public void setRinging(String callid,String remotertphost, int remotertpport, int codecs[]); 
  public void setAccepted(String callid, String remotertphost, int remotertpport, int codecs[]); 
  public void setBadCodec(String callid);  
  public void setBye(String callid);  
  public void setFailed(String callid);  
  public int setInvited(String callid, String fromid, String fromnumber, String remotertphost, int remotertpport, int codecs[]);  
  public void setCancelled(String callid, int code); 
  public void setCommandReceived(String callid,String[] commands);
}
