package net.sysreturn.trstorey.knownfiction;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

public class TiledMap extends SupportMapFragment {
	private GoogleMap map;
	SupportMapFragment mapFragment;
	private GoogleMapOptions options;
	private MercatorProjection mp;
	private LatLng test;
	public TileOverlay to;
    private AssetManager mAssets;
	
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
		test = new LatLng(41.850033,-87.6500523);
		mAssets = this.getActivity().getAssets();
		return fl;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		setUpMapIfNeeded();
	}
	
	private void setUpTileOverlay(){
		TileProvider tp = new MixedTileProvider(mAssets, getActivity());
			TileOverlayOptions too = new TileOverlayOptions().tileProvider(tp);
			to = map.addTileOverlay(too);
			mp = new MercatorProjection(this.getMap(), 256, test);
			mp.fromLatLngToPoint();
			mp.printPixelLocation();
	}
	
	private void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (map == null) {
	    	map = this.getMap();
	    }
	        // Check if we were successful in obtaining the map.
	        if (map != null) {
	        	map.setMyLocationEnabled(true);
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
