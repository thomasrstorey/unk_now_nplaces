package net.sysreturn.trstorey.knownfiction;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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
	public static final int UPDATE_INTERVAL_IN_SECONDS = 30;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL =
	        MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 10;
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
public static final String PREFS = "unkn_own-prefsFile";
EditText serverIP, serverPort, tileURL;
private boolean ipSet;
private boolean portSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*updatesRequested = true;
		locationClient = new LocationClient(this, this, this);
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);
		locationClient.connect();
		
		Intent intent = new Intent();
		intent.setAction("net.sysreturn.trstorey.knownfiction.servicebroadcast");
		sendBroadcast(intent);*/
		
		//restore prefs, if they exist
		ipSet = false;
		portSet = false;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		serverIP = (EditText)findViewById(R.id.serverip);
		serverPort = (EditText)findViewById(R.id.serverport);
		tileURL = (EditText)findViewById(R.id.tile_url);
		
		OnEditorActionListener oeal = new OnEditorActionListener(){
			@Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if(actionId == EditorInfo.IME_ACTION_SEND){
					int id = v.getId();
					fillSharedPreferences(id);
					handled = true;
				}
				return handled;
			}
		};
		
		serverIP.setOnEditorActionListener(oeal);
		serverPort.setOnEditorActionListener(oeal);
		tileURL.setOnEditorActionListener(oeal);
		
		if(settings.contains("ipSetting")){
			Log.w("TCP", "used old ipSetting");
			serverIP.setText(settings.getString("ipSetting", getString(R.string.ip_hint)));
			ipSet = true;
		}
		if(settings.contains("portSetting")){
			Log.w("TCP", "used old portSetting");
			serverPort.setText(settings.getInt("portSetting", 0));
			portSet = true;
		}
		if(settings.contains("urlSetting")){
			Log.w("TCP", "used old urlSetting");
			tileURL.setText(settings.getString("urlSetting", getString(R.string.ip_hint)));
		}
		if(settings.contains("updatesSetting")){
			Log.w("TCP", "used old updatesSetting");
			
			updatesRequested = settings.getBoolean("updatesSetting", false);
		}
	}
	
	void fillSharedPreferences(int id){
		Log.w("TCP","setting preferences");
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		if(id == R.id.serverip){
			Log.w("TCP","set ip prefs");
			editor.putString("ipSettings", serverIP.getText().toString());
			ipSet = true;
			if(portSet){
				Button launcher = (Button)findViewById(R.id.connectbutton);
				launcher.setEnabled(true);
			}
		}
		else if(id == R.id.serverport){
			Log.w("TCP","set port prefs");
			editor.putInt("portSettings", Integer.parseInt(serverPort.getText().toString()));
			portSet = true;
			if(ipSet){
				Button launcher = (Button)findViewById(R.id.connectbutton);
				launcher.setEnabled(true);
			}
		}
		else if(id == R.id.tile_url){
			Log.w("TCP","set url prefs");
			editor.putString("urlSettings", tileURL.getText().toString());
			Button connect = (Button)findViewById(R.id.launchbutton);
			connect.setEnabled(true);
		}
		editor.commit();
		Log.w("TCP", settings.getString("ipSettings", "127.0.0.1"));
		int i = settings.getInt("portSettings", 80);
		Log.w("TCP", Integer.toString(i));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onCheckboxClicked(View view){
		boolean checked = ((CheckBox)view).isChecked();
		if(checked){
			//Intent intent = new Intent();
			//intent.setAction("net.sysreturn.trstorey.knownfiction.servicebroadcast");
			//sendBroadcast(intent);
			Log.w("TCP", "checked");
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("updatesSetting", true);
			editor.commit();
		}
		else if(!checked){
			Log.w("TCP", "unchecked");
			ComponentName comp = new ComponentName(getPackageName(), TCPService.class.getName());
			stopService(new Intent().setComponent(comp));
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("updatesSetting", false);
			editor.commit();
		}
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		/*locationClient = new LocationClient(this, this, this);
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);
		updatesRequested = true;
		locationClient.connect();*/
	}
	
	public void launchMapFragment(View view){
		
		if(servicesConnected()){
			currentLocation = locationClient.getLastLocation();
			if(updatesRequested){
			}
		}
			
		mapFragment = new TiledMap();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(android.R.id.content, mapFragment, mapTag);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		map = mapFragment.getMap();
		currentLocation = locationClient.getLastLocation();
		locationClient.requestLocationUpdates(locationRequest, this);
		
	}
	
	public void startTCPService(View view){
		
		
		locationClient = new LocationClient(this, this, this);
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);
		locationClient.connect();
		
		Intent intent = new Intent();
		intent.setAction("net.sysreturn.trstorey.knownfiction.servicebroadcast");
		sendBroadcast(intent);
	}
	
	@Override
	protected void onStop(){
		
		//locationClient.disconnect();
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
		Log.w("TCP", "clear tiles");
        mapFragment.to.clearTileCache();
        
        
    }
	
	void showErrorDialog(int code) {
		  GooglePlayServicesUtil.getErrorDialog(code, this, 
		      REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
		}
	
}
