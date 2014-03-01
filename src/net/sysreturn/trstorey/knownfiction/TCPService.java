package net.sysreturn.trstorey.knownfiction;



import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class TCPService extends Service implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener{
	private LocationManager lm;
    private LocationListener locationListener;
    
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL =
	        MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 5;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL =
	        MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
	LocationClient locationClient;
	Location currentLocation;
	LocationRequest locationRequest;
	PendingIntent locationIntent;
	private TCPClient tcpClient;
	
	// Flag that indicates if a request is underway.
    private boolean mInProgress;
    
    private Boolean servicesAvailable = false;
	
    private IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
            return mBinder;
    }
    
    /*
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
            TCPService getServerInstance() {
                    return TCPService.this;
            }
    }
    
    @Override
    public void onCreate(){
    	
    	super.onCreate();
    	
    	mInProgress = false;
    	// Create the LocationRequest object
        locationRequest = LocationRequest.create();
        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        locationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        
        servicesAvailable = servicesConnected();
        
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        locationClient = new LocationClient(this, this, this);
    	
    }
    
    public class connectTask extends AsyncTask<TCPService,String,TCPClient> {
		 
        @Override
        protected TCPClient doInBackground(TCPService... contexts) {
        	Log.w("TCP", "make TCPClient");
            //we create a TCPClient object and
            tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                }
            }, contexts[0]);
            tcpClient.run();
 
            return null;
        }
    }
    
private boolean servicesConnected() {
    	
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
 
            return true;
        } else {
 
            return false;
        }
    }

	public int onStartCommand (Intent intent, int flags, int startId)
	{
	    super.onStartCommand(intent, flags, startId);
	    Log.w("TCP", "servicesAvailable??? " + servicesAvailable);
	    Log.w("TCP", "location client is connected??? " + locationClient.isConnected());
	    Log.w("TCP", "in progress??? " + mInProgress);
	    if(!servicesAvailable || locationClient.isConnected() || mInProgress){
	    	Log.w("TCP", "first one and out");
	    	return START_STICKY;
	    }
	    
	    setUpLocationClientIfNeeded();
	    if(!locationClient.isConnected() || !locationClient.isConnecting() && !mInProgress)
	    {
	    	mInProgress = true;
	    	locationClient.connect();
	    }
	    
	    new connectTask().execute(this);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.noticon)
                        .setContentTitle("Unkn_own")
                        .setContentText("Tracking location.");
        int NOTIFICATION_ID = 12345;

        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
	    
	    return START_STICKY;
	}
	
	/*
	 * Create a new location client, using the enclosing class to
	 * handle callbacks.
	 */
	private void setUpLocationClientIfNeeded()
	{
		if(locationClient == null) 
	        locationClient = new LocationClient(this, this, this);
	}
	
	// Define the callback method that receives location updates
    @Override
    public void onLocationChanged(Location location) {
    	Log.w("TCP Client", "try send message");
        // Report to the UI that the location was updated
        String msg = Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        if(tcpClient != null){
        	tcpClient.sendMessage(msg);
        	Log.w("TCP Client", "sent message");
        	//mapFragment.to.clearTileCache();
        } else{
        	Log.w("TCP Client", "tcpclient is null");
        }
        //Log.d("debug", msg);
        // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroy(){
        // Turn off the request flag
        mInProgress = false;
        if(servicesAvailable && locationClient != null) {
	        locationClient.removeLocationUpdates(this);
	        // Destroy the current location client
	        locationClient = null;
        }
        // Display the connection status
        // Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        super.onDestroy();  
    }
    
    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
    	
        // Request location updates using static settings
        locationClient.requestLocationUpdates(locationRequest, this);
    	Intent intent = new Intent(this, LocationReceiver.class);
        locationIntent = PendingIntent.getBroadcast(getApplicationContext(), 14872, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        locationClient.requestLocationUpdates(locationRequest, locationIntent); 
 
    }
    
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Turn off the request flag
        mInProgress = false;
        // Destroy the current location client
        locationClient = null;
        // Display the connection status
        // Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }
    
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    	mInProgress = false;
    	if (connectionResult.hasResolution()) {
            
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        }
    }
    
    public class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
            
            Log.w("TCP Client", "try send message");
            // Report to the UI that the location was updated
            String msg = Double.toString(location.getLatitude()) + "," +
                    Double.toString(location.getLongitude());
            if(tcpClient != null){
            	tcpClient.sendMessage(msg);
            	Log.w("TCP Client", "sent message");
            	//mapFragment.to.clearTileCache();
            } else{
            	Log.w("TCP Client", "tcpclient is null");
            }
            Log.d("debug", msg);
        }
    }
    

}
