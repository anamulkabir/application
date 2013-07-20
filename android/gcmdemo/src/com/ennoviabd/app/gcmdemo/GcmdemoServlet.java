package com.ennoviabd.app.gcmdemo;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.*;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

@SuppressWarnings("serial")
public class GcmdemoServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		 List<String> devices = Datastore.getDevices();
		 String status;
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
		Sender sender = new Sender("AIzaSyCxWjr_tf5x93OItqLsZvopNjMOVJSkvDw");
		Message message = new Message.Builder().addData("message", "Message from demoAK").build();
		 if (devices.isEmpty()) {
		      status = "Message ignored as there is no device registered!";
		      resp.getWriter().println(status);
		    }
		 else
		 {
			 if (devices.size() >= 1) {
			        // send a single message using plain post
			        
			        for(int i=0;i<devices.size();i++)
			        {
			        	String device = devices.get(i);
			        	Result result = sender.send(message,device, 5);
						if (result.getMessageId() != null) 
						{
							 String canonicalRegId = result.getCanonicalRegistrationId();
							 if (canonicalRegId != null) {
							   // same device has more than on registration ID: update database
								 resp.getWriter().println("same device has more than on registration ID: update database");
							 }
							 resp.getWriter().println(result);
						} else {
							 String error = result.getErrorCodeName();
							 resp.getWriter().println(error);
						}
			        }
		 }
		 }
	}
}
