package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public final class TileManager {

    private record TileId(int zoom, int x, int y){
        private TileId{
            Preconditions.checkArgument(isValid(zoom(),x(),y()));
        }
        public static boolean isValid(int zoom, int x, int y){ // TODO: 23/4/23 use formula
            return false;
        }
    }

    private final Path path;
    private final String serverName;
    private final LinkedHashMap<TileId, Image> memoryCache =
            new LinkedHashMap<>(100, 1, true);

    public TileManager(Path path, String serverName){
        this.path = path;
        this.serverName = serverName;
    }

    public Image imageForTileAt(TileId tileId){
        Path tilePath = Path.of(path.toString(), tileId.zoom(),)

        if (memoryCache.containsKey(tileId)){
            return memoryCache.get(tileId);
        }
        else if (Files.exists() )
        //else search in disk cache, store in memory
        //else search in server, store in disk and memory and then return image
        return null;
    }
}
