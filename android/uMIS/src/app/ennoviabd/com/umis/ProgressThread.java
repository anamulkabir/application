package app.ennoviabd.com.umis;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public  class ProgressThread extends Thread{
// Class constants defining state of the thread
    final static int DONE = 0;
    final static int RUNNING = 1;    
    Handler mHandler;
    int mState;
    int total,delay = 40;

    // Constructor with an argument that specifies Handler on main thread
    // to which messages will be sent by this thread.    
    ProgressThread(Handler h,int maxvalue,int pdelay) {
        mHandler = h;
        total=maxvalue;
        delay=pdelay;
    }
    
    // Override the run() method that will be invoked automatically when 
    // the Thread starts.  Do the work required to update the progress bar on this
    // thread but send a message to the Handler on the main UI thread to actually
    // change the visual representation of the progress. In this example we count
    // the index total down to zero, so the horizontal progress bar will start full and
    // count down.
    @Override
    	public void run() {
    		// TODO Auto-generated method stub
    		//super.run();
    	 mState = RUNNING;   
        
         while (mState == RUNNING) {
             // The method Thread.sleep throws an InterruptedException if Thread.interrupt() 
             // were to be issued while thread is sleeping; the exception must be caught.
             try {
                 // Control speed of update (but precision of delay not guaranteed)
                 Thread.sleep(delay);
             } catch (InterruptedException e) {
                 Log.e("ERROR", "Thread was Interrupted");
             }             
            
         }
    	}
 // Set current state of thread (use state=ProgressThread.DONE to stop thread)
    public void setState(int state) {
        mState = state;
    }

}
