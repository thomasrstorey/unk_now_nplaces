package net.sysreturn.trstorey.knownfiction;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;

public class MainActivity extends FragmentActivity {

private GoogleMap map;
private final String mapTag = "mapTag";
TiledMap mapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void launchMapFragment(View view){
		mapFragment = new TiledMap();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(android.R.id.content, mapFragment, mapTag);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		map = mapFragment.getMap();
	}
	

	@Override
	public void onResume(){
		super.onResume();
	}
}
