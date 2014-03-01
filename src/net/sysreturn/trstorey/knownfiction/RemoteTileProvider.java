package net.sysreturn.trstorey.knownfiction;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.UrlTileProvider;

public class RemoteTileProvider extends UrlTileProvider {
Context context;	
	public RemoteTileProvider(Context _context){
		super(256,256);
		context = _context;
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_MULTI_PROCESS);
		Log.w("TCP", "url settings: " + settings.getString("urlSettings", "DNE"));
	}
	public static final String PREFS = "unkn_own-prefsFile";
	

	@Override
	public URL getTileUrl(int x, int y, int zoom) {
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_MULTI_PROCESS);
		String root = settings.getString("urlSettings", null); // http://art-tech.arts.ufl.edu/~tstorey/unkn_own/tiles
		String s = String.format(Locale.US, root + "/%d/%d/%d/%d_%d.png", zoom, x, y, x, y);
	    try {
	      URL url = new URL(s);
	      return url;
	    } catch (MalformedURLException e) {
	        throw new AssertionError(e);
	    }
	}

}
