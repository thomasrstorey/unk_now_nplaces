package net.sysreturn.trstorey.knownfiction;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;

public class MainActivity extends Activity {
private GoogleMap map;
private final String mapTag = "mapTag";
private GoogleMapOptions options;
MapFragment mapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//setUpMapIfNeeded();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void launchMapFragment(View view){
		options = new GoogleMapOptions();
		options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
	    .compassEnabled(false)
	    .rotateGesturesEnabled(true)
	    .tiltGesturesEnabled(true);
		mapFragment = MapFragment.newInstance(options);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.add(android.R.id.content, mapFragment, mapTag);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		map = mapFragment.getMap();
	}
	
	private void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (map == null) {
	    	if(((MapFragment) getFragmentManager().findFragmentByTag(mapTag)).getMap() != null){
	    		map = ((MapFragment) getFragmentManager().findFragmentByTag(mapTag)).getMap();
	    	}
	        // Check if we were successful in obtaining the map.
	        if (map != null) {
	            // The Map is verified. It is now safe to manipulate the map.

	        }
	    }
	}

	@Override
	public void onResume(){
		super.onResume();
		//setUpMapIfNeeded();
	}
}
