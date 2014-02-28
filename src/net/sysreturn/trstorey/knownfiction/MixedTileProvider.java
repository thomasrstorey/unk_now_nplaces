package net.sysreturn.trstorey.knownfiction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

public class MixedTileProvider implements TileProvider {
    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int BUFFER_SIZE = 16 * 1024;
    
    private final RemoteTileProvider remoteProvider;
    
    
    private AssetManager mAssets;
    public Context context;

    public MixedTileProvider(AssetManager assets, Context _context) {
    	context = _context;
    	remoteProvider = new RemoteTileProvider(context);
        mAssets = assets;
        
    }
    
	@Override
	public Tile getTile(int x, int y, int zoom) {
		byte[] image = readTileImage();
		final URL url = remoteProvider.getTileUrl(x, y, zoom);
		
		try{
		      HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		      int responseCode = huc.getResponseCode();
		      if(responseCode == 200){
		    	  return remoteProvider.getTile(x, y, zoom);  
		      } else{
		    	  return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
		      }
	      } catch(IOException ioe){
	    	  Log.e("Error", ioe.toString());
	    	  return null;
	      }
	}
	
	private byte[] readTileImage(){
		InputStream in = null;
		ByteArrayOutputStream buffer = null;
		 try{
			 in = mAssets.open("images/default.png");
			 buffer = new ByteArrayOutputStream();
			 int nRead;
			 byte[] data = new byte[BUFFER_SIZE];
			 
			 while((nRead = in.read(data, 0, BUFFER_SIZE)) != -1){
				 buffer.write(data, 0, nRead);
			 }
			 buffer.flush();
			 return buffer.toByteArray();
		 } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	     } catch (OutOfMemoryError e) {
	            e.printStackTrace();
	            return null;
	     } finally {
	            if (in != null) try { in.close(); } catch (Exception ignored) {}
	            if (buffer != null) try { buffer.close(); } catch (Exception ignored) {}
	     }
	}

}
