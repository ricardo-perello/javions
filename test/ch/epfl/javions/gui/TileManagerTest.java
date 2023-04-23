package ch.epfl.javions.gui;
import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class TileManagerTest {
     @Test
     void TileManagerWorks(){
         TileManager tileManager = new TileManager(Path.of("tile_cache"), "https://tile.openstreetmap.org");
         Image image = tileManager.imageForTileAt(new TileManager.TileId(17,67927, 46357));

     }
}
