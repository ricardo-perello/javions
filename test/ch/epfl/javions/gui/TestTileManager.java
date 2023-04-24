package ch.epfl.javions.gui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public final class TestTileManager extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TileManager t = new TileManager(Path.of("tile-cache"),
                "tile.openstreetmap.org");
        Image i = t.imageForTileAt(new TileManager.TileId(17, 67927, 46357));
        Image j = t.imageForTileAt(new TileManager.TileId(17, 67927, 46357));
        for (int k = 0; k < 150; k++) {
            t.imageForTileAt(new TileManager.TileId(17, 67927+k, 46357));
        }
        for (int k = 150; k > 0; k--) {
            t.imageForTileAt(new TileManager.TileId(17, 67927+k, 46357));
        }
        Platform.exit();
    }
}