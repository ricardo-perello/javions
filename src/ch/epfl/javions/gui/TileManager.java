package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;



public final class TileManager {

    public record TileId(int zoom, int x, int y){
        public TileId{
            Preconditions.checkArgument(isValid(zoom(),x(),y()));
        }
        public static boolean isValid(int zoom, int x, int y) {

            if (zoom >= 6 && zoom <= 19 &&
                    x >= 0 && x < Math.scalb(4, zoom) &&
                    y >= 0 && y < Math.scalb(4, zoom)){
                return true;
            }
            return false;
        }
    }
    private static final int MAX_ENTRIES = 100;
    private final Path path;
    private final String serverName;
    private final LinkedHashMap<TileId, Image> memoryCache =
            new LinkedHashMap<TileId, Image>(100, 1, true){
                @Override
                protected boolean removeEldestEntry(Map.Entry<TileId, Image> eldest) {
                    return this.size() > MAX_ENTRIES;
                }


    };


    public TileManager(Path path, String serverName){
        this.path = path;
        this.serverName = serverName;
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        String directoryString = "/" + tileId.zoom() + "/" + tileId.x();
        String tileString = directoryString + "/" + tileId.y() + ".png";
        Path tilePath = Path.of(path.toString(), tileString);
        if (memoryCache.containsKey(tileId)){
            System.out.println("Image found in memory cache!");
            return memoryCache.get(tileId);
        }
        else if (Files.exists(tilePath) ){
            byte[] bytes = Files.readAllBytes(tilePath);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            Image image = new Image(inputStream);
            memoryCache.put(tileId, image);
            System.out.println("Image found in disk cache!");
            return image;
        }
        else{
            try {
                URL u  =  new  URL (serverName + tileString );
                URLConnection c  = u.openConnection();
                c.setRequestProperty( "User-Agent" , "I had" );
                InputStream  i  = c.getInputStream();
                byte[] bytes = i.readAllBytes();


                Image image = new Image(new ByteArrayInputStream(bytes));
                memoryCache.put(tileId, image);
                System.out.println("Image retrieved from server!");
                Files.createDirectories(Path.of(path + directoryString));
                Files.createFile(tilePath);
                Files.write(tilePath, bytes);
                return image;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
