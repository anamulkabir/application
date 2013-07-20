package com.ennoviabd.generalservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.graphics.Color;
import android.net.ParseException;
import android.text.format.DateFormat;


public class GlobalSettings {
	public static boolean islogin=false;
	// This application status is used to maintain application logical status. 
	//GEN: general; AAC:alert activate;DNH: deadline not handle,DHN: deadline handle;ARC: archive;DEP: depricated;ERR:error;
	public static String []appstatus={"GEN","AAC","DNH","DHN","ARC","DEP","ERR"};
	public static int []colorbefaviour={1,2,3,4,5};//1:Green;2:Red;3:Yellow;4:Gray;5:Blue
	public static String confiAlertDay="";
	public GlobalSettings(){
		
	}
	// This function take parameter from application and return event behaviour as code
	public static int getEventBehaviour(String eventDT,String eventTT,String remindMe,String isRepeat,String eventType,String eventStatus)
	{
		if(eventStatus!=null )
		{
			if(eventStatus.equalsIgnoreCase(appstatus[4]))// if archive
			{
				return colorbefaviour[4]; // return blue color
			}
			else if(eventStatus.equalsIgnoreCase(appstatus[3])) // Deadline Handle
			{
				return colorbefaviour[2];// return Yellow
			}
			else
			{
				Calendar calendar = Calendar.getInstance();
				
				// This condition handle logic for day type event
				if(eventType!=null && eventType.equalsIgnoreCase("day"))
				{
					
					//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm"); HH:MM:SS.SSS
					int eventmm,eventdd;
					int remindmin=0;
					if(remindMe!=null && !remindMe.isEmpty())
						{
						remindmin=hourToMinutesConvert(remindMe);
						}
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					try
					{
					Date dt = dateFormat.parse(eventDT);
					eventmm=dt.getMonth()+1;// getMonth is always 1 month less of current selected month; so 1+
					eventdd=dt.getDate();//calendar.get(Calendar.MONTH)*24*60;
					
					if(eventmm>=calendar.get(Calendar.MONTH)+1)// schedule event date >= current
					{
						//long dtdiff=getDateDiffms(calendar.getTime().toString(),dt.toString());
						calendar.add(Calendar.MINUTE,remindmin );// adding reminder minutes with current date
						if(eventmm-(calendar.get(Calendar.MONTH)+1)<=0)// checking event month - current month+reminder <=0; that means alert activite 
						{
							calendar.add(Calendar.MINUTE,-remindmin );
							if(eventdd>calendar.get(Calendar.DATE))
							{
								calendar.add(Calendar.MINUTE,remindmin );
								if(eventdd<=calendar.get(Calendar.DATE))
									return colorbefaviour[0]; // show color as green as alert activate
							}
							else
							{
								return colorbefaviour[1]; // show color as red that represent as miss schedule
								
							}
						}
						
						
					}
					else
					{
						return colorbefaviour[1];// show color as red that represent as miss schedule
					}
					
					}catch(Exception e)
					{
						return 0;
						
					}
				}
				else if(eventType!=null && eventType.equalsIgnoreCase("datetime"))
				{
					
					int remindmin=0;
					if(remindMe!=null && !remindMe.isEmpty())
						{
						remindmin=hourToMinutesConvert(remindMe);
						}
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
					try
					{
					Date dt = dateFormat.parse(eventDT+" "+ eventTT);
					long dtdiff =dt.getTime()-calendar.getTimeInMillis();
					
					if(dtdiff>=0)
					{
						//long dtdiff=getDateDiffms(calendar.getTime().toString(),dt.toString());
						calendar.add(Calendar.MINUTE,remindmin );
						dtdiff =dt.getTime()-calendar.getTimeInMillis();
						if(dtdiff<=0)
						{
							
							return colorbefaviour[0]; // alert activate as green color return
							
						}
						
						
					}
					else
					{
						return colorbefaviour[1]; // show color as red that represent as miss schedule
					}
					
					}catch(Exception e)
					{
						return 0;
						
					}
				}
				else if(eventType!=null && eventType.equalsIgnoreCase("time"))
				{
					int remindmin=0;
					SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
					
					try
					{
						String currentTime=timeFormat.format(new Date());
						
					
					long dtdiff =getTimeDiffms(currentTime, eventTT);
					
					if(dtdiff>=0)//
					{
						//long dtdiff=getDateDiffms(calendar.getTime().toString(),dt.toString());
						if(dtdiff<1000*30)
							return colorbefaviour[0];
					}
					else
					{
						return colorbefaviour[1];
					}
					
					}catch(Exception e)
					{
						return 0;
						
					}
					
				}
				
			}
			
		}
		
		return 0;
	}
	// this function returns date different between two dates
	public static long getTimeDiffms(String currTM,String diffTM)
	{
		SimpleDateFormat formatter ; 
		    Date currDT ;
		    Date diffDT ; 
		    formatter = new SimpleDateFormat("HH:mm");
		    try
		    {
		    currDT =formatter.parse(currTM);
		    diffDT = formatter.parse(diffTM);
		    Calendar diffTime = Calendar.getInstance();
	        diffTime.setTime(diffDT);
	        Calendar currTime = Calendar.getInstance();
	        currTime.setTime(currDT);
	       
	        long diff = diffTime.getTimeInMillis() - currTime.getTimeInMillis();
	        return diff;
		    
		    }
		    catch(Exception e)
		    {
		    	return -1;
		    	
		    }
		    
		
		//return -1;
	}
	/*
	 * This function conver hour to minutes [i,e: this function is used to calculate remaining time to raise alert comparing minutes format
	 */
	public static int hourToMinutesConvert(String hrtc)
	{
		int tmp=0;
		if(hrtc.contains("hours"))
		{
			tmp=Integer.parseInt(hrtc.substring(0, hrtc.indexOf("hours")-1))*60;// convert hour to minutes
		}
		else if(hrtc.contains("minutes"))
		{
			tmp=Integer.parseInt(hrtc.substring(0, hrtc.indexOf("minutes")-1)); // convert minutes to minutes and exclude space charcter
		}
		else if(hrtc.contains("days"))
		{
			tmp=Integer.parseInt(hrtc.substring(0, hrtc.indexOf("days")-1))*24*60;// convert day to hour to minutes
		}
			
		//Integer.parseInt(hrtc.substring(0, hrtc.indexOf(":")<0?0:hrtc.indexOf(":")))*60+Integer.parseInt(hrtc.substring(hrtc.indexOf(":")+1));
		
		return tmp;
	}
	public static String stringToDateStrPr(String dt)
	{
		String str="";
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		
		if(dt!=null && dt.length()>=6 && dt.length()<=10)
		{
			str=dt;
			str=(str.replace("/", "")).replace("-", "");
			str=str.substring(0, 2)+"-"+str.substring(2, 4)+"-"+str.substring(4);
			try
			{
			Date tmpdt=sdf.parse(str);
			}
			catch(Exception pe)
			{
				return "";
			}
			
		}
		return str;
	}
	public static String stringToDatePrToDb(String dt)
	{
		String str="";
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
		if(dt!=null && dt.length()>=6 && dt.length()<=10)
		{
			str=dt;
			try
			{
			Date tmpdt=sdf.parse(str);
			str=sdf2.format(tmpdt);
			}
			catch(Exception pe)
			{
				return "";
			}
			
		}
		return str;
	}
	
	public static String stringToDateDbToPr(String dt)
	{
		String str="";
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
		if(dt!=null && dt.length()>=6 && dt.length()<=10)
		{
			str=dt;
			try
			{
			Date tmpdt=sdf2.parse(str);
			str=sdf.format(tmpdt);
			}
			catch(Exception pe)
			{
				return "";
			}
			
		}
		return str;
	}
	public static String stringToTimeStr(String dt)
	{
		if(dt!=null)
		{
		String str=String.format("%-4s", dt).replace(' ', '0');
		if(str.length()==4)
		{
			str=str.substring(0,2)+":"+str.substring(2);
			
		}
		
			try
			{
			Date tmpdt=(new SimpleDateFormat("HH:mm")).parse(str);
			}
			catch(Exception pe)
			{
				return "";
			}
			
		
		return str;
		}
		return "";
	}
	public static String LPad(String schar, String spad, int len) {
		String sret = schar;
		for (int i = sret.length(); i < len; i++) {
			sret = spad + sret;
		}
		return new String(sret);
	}

}
