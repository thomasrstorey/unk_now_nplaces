package net.sysreturn.trstorey.knownfiction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class TCPClient {
	//public String SERVERIP = "128.227.217.26";
	//public int SERVERPORT = 8124;
	public String SERVERIP;
	public int SERVERPORT;
	
	public Message msg;
	public static final String PREFS = "unkn_own-prefsFile";
	
	private OnMessageReceived mMsgListener;
	private volatile boolean mRun = false;
	
	protected TCPService context;
	
	private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
              if(msg.arg1 == 1)
                    Toast.makeText(context.getApplicationContext(),"Connecting to TCP Server", Toast.LENGTH_LONG).show();
              else if(msg.arg1 == 2)
                  Toast.makeText(context.getApplicationContext(),"Connected to TCP Server", Toast.LENGTH_LONG).show();
              else if(msg.arg1 == 3)
                  Toast.makeText(context.getApplicationContext(),"Disconnected from TCP Server", Toast.LENGTH_LONG).show();
              else if(msg.arg1 == 4)
                  Toast.makeText(context.getApplicationContext(),"Bad TCP Server IP or Port", Toast.LENGTH_LONG).show();
        }
    };
	
	PrintWriter out;
    BufferedReader in;


	public TCPClient(OnMessageReceived listener, TCPService _context){
		Log.e("TCP", "set mMsgListener");
		mMsgListener = listener;
		context = _context;
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_MULTI_PROCESS);
		if(settings.contains("ipSettings")){
			SERVERIP = settings.getString("ipSettings", "127.0.0.1");
		} else{
			Log.w("TCP", "FOR SOME REASON IT DOESN'T EXIST");
		}
		SERVERPORT = settings.getInt("portSettings", 80);
		Log.e("TCP", "ip = " + SERVERIP + " port = " + SERVERPORT);
	}
	
	
	public void sendMessage(String message){
		if(out != null && !out.checkError()){
			out.println(message);
			out.flush();
		}
	}
	
	public void stopClient(){
		Log.e("TCP", "Stopped Client");
		mRun = false;
	}
	
	public void run(){
		mRun = true;
		String serverMsg = null;
		try{
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(SERVERIP, SERVERPORT), 10000);
			Log.e("TCP Client", "Connecting");
			

					 msg = handler.obtainMessage();
				     msg.arg1 = 1;
				     handler.sendMessage(msg);
					

			try{
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				Log.e("TCP Client", "sent");
				Log.e("TCP Client", "done");
				Log.e("TCP", "mRun = " + mRun);
				
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				Log.e("TCP", "mRun = " + mRun);
				
						//Toast.makeText(context, "Connected to TCP Server", Toast.LENGTH_SHORT).show();
				 msg = handler.obtainMessage();
			     msg.arg1 = 2;
			     handler.sendMessage(msg);
				
				while(mRun){
					/*serverMsg = in.readLine();
					Log.w("TCP", "serverMsg = " + serverMsg);
					if(serverMsg != null && mMsgListener != null){
						mMsgListener.messageReceived(serverMsg);
					}
					serverMsg = null;*/
				}
					//Log.e("Response", "Received Message: " + serverMsg);
			} catch (Exception e) {
				 
	            Log.e("TCP", "S: Error", e);
						//Toast.makeText(context, "Could not connect to TCP Server", Toast.LENGTH_SHORT).show();
	
	        } finally {
	            //the socket must be closed. It is not possible to reconnect to this socket
	            // after it is closed, which means a new socket instance has to be created.
	            socket.close();
	            Log.e("TCP", "Disconnected from TCP Server");
						//Toast.makeText(context, "Disconnected from TCP Server", Toast.LENGTH_SHORT).show();
	             msg = handler.obtainMessage();
			     msg.arg1 = 3;
			     handler.sendMessage(msg);
	        }
		} catch (Exception e) {
			 
	        Log.e("TCP", "C: Error", e);
	        Message msg = handler.obtainMessage();
	        msg.arg1 = 4;
	        handler.sendMessage(msg);
	    }
		
	
	}
	public interface OnMessageReceived {
	    public void messageReceived(String message);
	}
}

