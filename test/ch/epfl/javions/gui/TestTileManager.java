package ch.epfl.javions.gui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public final class TestTileManager extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TileManager t = new TileManager(Path.of("tile-cache"),
                "https://tile.openstreetmap.org");
        t.imageForTileAt(new TileManager.TileId(17, 67927, 46357));
        for (int i = 0; i < 120; i++) {
            t.imageForTileAt(new TileManager.TileId(17, i, 46357));
        }
        for (int i = 0; i < 120; i++) {
            t.imageForTileAt(new TileManager.TileId(17, i, 46357));
        }
        Platform.exit();
    }
}