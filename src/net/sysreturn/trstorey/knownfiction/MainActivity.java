package net.sysreturn.trstorey.knownfiction;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;

public class MainActivity extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener{

	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL =
	        MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL =
	        MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;	
	
	
private GoogleMap map;
private final String mapTag = "mapTag";

TiledMap mapFragment;
LocationClient locationClient;
Location currentLocation;
LocationRequest locationRequest;
boolean updatesRequested;
//TODO send location data to tcp server onlocationchanged
//TODO update tiles on response from tcp server
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		locationClient = new LocationClient(this, this, this);
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);
		updatesRequested = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		locationClient.connect();
	}
	
	public void launchMapFragment(View view){
		
		if(servicesConnected()){
			currentLocation = locationClient.getLastLocation();
			if(updatesRequested){
				locationClient.requestLocationUpdates(locationRequest, this);
			}
		}
			
		mapFragment = new TiledMap();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(android.R.id.content, mapFragment, mapTag);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		map = mapFragment.getMap();
		//map.setMyLocationEnabled(true);
	}
	
	@Override
	protected void onStop(){
		
		locationClient.disconnect();
		super.onStop();
		
	}
	

	@Override
	public void onResume(){
		super.onResume();

	}
	
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
        case REQUEST_CODE_RECOVER_PLAY_SERVICES:
          if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Google Play Services must be installed.",
                Toast.LENGTH_SHORT).show();
            finish();
          }
          return;
      }
      super.onActivityResult(requestCode, resultCode, data);
    }
    
    private boolean servicesConnected() {
        // Check that Google Play services is available
    	int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	  if (status != ConnectionResult.SUCCESS) {
    	    if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
    	      showErrorDialog(status);
    	    } else {
    	      Toast.makeText(this, "This device is not supported.", 
    	          Toast.LENGTH_LONG).show();
    	      finish();
    	    }
    	    return false;
    	  }
    	  return true;
    	} 

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        	showErrorDialog(connectionResult.getErrorCode());
        }
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();	
	}
	
	@Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
	
	void showErrorDialog(int code) {
		  GooglePlayServicesUtil.getErrorDialog(code, this, 
		      REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
		}
	
}
