package com.ennoviabd.app.javaproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Filetest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String content;
		File file = new File("e:\\shared\\hello1.txt");
		try {
		    //FileReader reader = new FileReader(file);
			 InputStream ios = new FileInputStream(file);
		    byte[] chars = new byte[(int) file.length()];
		    byte [] b=new byte[chars.length];
		    ios.read(chars);
		    content = new String(chars);
		    //byte [] b=new byte[chars.length*2];
		    //byte [] b=charToBytesASCII(chars);
		    //byte [] b1=new byte[chars.length*2];
		    //byte [] b2 =new byte[chars.length*2];
		    //byte [] b2=content.getBytes();
		    
		   
		     //ByteBuffer.wrap(b2).asCharBuffer().put(chars);
		    b=decodeMsg(chars,chars.length);
		    //b1=decodeMsg(b1,b1.length);
		    //b2=decodeMsg(b2,b2.length);
		    //char []c1=byteToChar(b);
		    
		    //String example = "This is an example";
            //Convert String to byte[] using .getBytes() function
         //byte[] bytes = example.getBytes();
            //Convert byte[] to String using new String(byte[])      
         String s = new String(b);
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}

	}
	 public static byte[] decodeMsg(byte data[], int length) {
	        byte[] d = new byte[length];
	        for (int i = 0;i < length; i+=1) {
	            d[i] = (byte) (~data[i]);
	            
	        }
	        return d;
	    }

	public static byte[] stringToBytesASCII(String str) {
		 char[] buffer = str.toCharArray();
		 byte[] b = new byte[buffer.length];
		 for (int i = 0; i < b.length; i++) {
		  b[i] = (byte) buffer[i];
		 }
		 return b;
		}
	public static byte[] charToBytesASCII(char[] buffer) {
		 byte[] b = new byte[buffer.length];
		 for (int i = 0; i < b.length; i++) {
		  b[i] = (byte) buffer[i];
		 }
		 return b;
		}
	public static char[] byteToChar(byte[] b) {
		char []c=new char[b.length];
		 //byte[] b = new byte[buffer.length];
		 for (int i = 0; i < b.length; i++) {
		  c[i] = (char) b[i];
		 }
		 return c;
		}

}
