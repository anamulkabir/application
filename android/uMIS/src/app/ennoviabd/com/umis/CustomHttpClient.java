package app.ennoviabd.com.umis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class CustomHttpClient {
	public static final int HTTP_TIMEOUT=30*1000;// millisecond
	private static HttpClient chttpclient;
	private static HttpClient getHttpclient()
	{
		if(chttpclient==null)
		{
			chttpclient=new DefaultHttpClient();
			final HttpParams params=chttpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
		}
		
		return chttpclient;
	}
	
	public static String executeHttpPost(String url,ArrayList<NameValuePair>postParamenters)throws Exception
	{
		BufferedReader br=null;
		try{
			HttpClient httpcl=getHttpclient();
			HttpPost request=new HttpPost(url);
			UrlEncodedFormEntity formentity=new UrlEncodedFormEntity(postParamenters);
			request.setEntity(formentity);
			HttpResponse response = httpcl.execute(request);
			br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb=new StringBuffer("");
			String line="";
			String nl=System.getProperty("line.separator");
			while((line=br.readLine())!=null)
			{
				sb.append(line+nl);
				break;
			}
			br.close();
			String rs=sb.toString();
			return rs;
		}
		finally
		{
			if(br!=null)
			{
				try{
					br.close();
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		
	}
	

}
