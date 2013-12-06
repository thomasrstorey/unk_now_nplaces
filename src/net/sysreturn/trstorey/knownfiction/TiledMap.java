package net.sysreturn.trstorey.knownfiction;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

public class TiledMap extends SupportMapFragment {
	private GoogleMap map;
	SupportMapFragment mapFragment;
	private GoogleMapOptions options;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		options = new GoogleMapOptions();
		options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
	    .compassEnabled(false)
	    .rotateGesturesEnabled(true)
	    .tiltGesturesEnabled(true);
		newInstance(options);
		View v = super.onCreateView(inflater, container, savedInstanceState);
		FrameLayout fl = new FrameLayout(inflater.getContext());
		fl.addView(v);
		return fl;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		setUpMapIfNeeded();
	}
	
	private void setUpTileOverlay(){
		TileProvider tileProvider = new UrlTileProvider(256, 256) {
			  @Override
			  public synchronized URL getTileUrl(int x, int y, int zoom) {
			    // Define the URL pattern for the tile images 
			    String s = "http://i.imgur.com/YO3PHdG.png";
			    try {
			      return new URL(s);
			    } catch (MalformedURLException e) {
			        throw new AssertionError(e);
			    }
			  }
			};
			map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
			
	}
	
	private void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (map == null) {
	    	map = this.getMap();
	    }
	        // Check if we were successful in obtaining the map.
	        if (map != null) {
	            // The Map is verified. It is now safe to manipulate the map.
	        	setUpTileOverlay();
	        }
	    
	}

	@Override
	public void onResume(){
		super.onResume();
		this.getMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		setUpMapIfNeeded();
	}
	
}
