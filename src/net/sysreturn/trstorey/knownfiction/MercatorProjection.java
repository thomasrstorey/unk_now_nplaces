package net.sysreturn.trstorey.knownfiction;


import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MercatorProjection {
	private GoogleMap map;
	double TILE_SIZE;
	LatLng here;
	double pointX;
	double pointY;
	double originX;
	double originY;
	double pixelsPerLonDegree;
	double pixelsPerLonRadian;
	
	MercatorProjection(GoogleMap _map, int _tileSize, LatLng _ll){
		map = _map;
		TILE_SIZE = _tileSize;
		here = _ll;
		originX = TILE_SIZE/2; 
	    originY = TILE_SIZE/2;
		pixelsPerLonDegree = TILE_SIZE / 360;
		pixelsPerLonRadian = TILE_SIZE / (2*Math.PI);
	}
	
	void fromLatLngToPoint(){
		pointX = originX +here.longitude * pixelsPerLonDegree;
		double siny = bound(Math.sin(Math.toRadians(here.latitude)), -0.9999, 0.9999);
		pointY = originY + 0.5 * Math.log((1+siny)/(1-siny))+ -pixelsPerLonRadian;
	}
	
	double bound(double value, double min, double max){
		if(min!=0) value = Math.max(value, min);
		if(max!=0) value = Math.min(value, max);
		return value;
	}
	
	void printPixelLocation(){
		int numTiles = 1 << (int)map.getCameraPosition().zoom;
		Log.w("lonlat", "x location in pixels: " + Math.floor(pointX*numTiles));
		Log.w("lonlat", "y location in pixels: " + Math.floor(pointY*numTiles));
	}
}
