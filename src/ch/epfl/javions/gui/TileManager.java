package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import static ch.epfl.javions.gui.BaseMapController.MAX_ZOOM_LEVEL;
import static ch.epfl.javions.gui.BaseMapController.MIN_ZOOM_LEVEL;


public final class TileManager {

    private static final int CACHE_MAX_ENTRIES = 100;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private static final String PROTOCOL_STRING = "https://";
    private static final String FILE_ENDING = ".png";
    private static final String PATH_SEPARATOR = "/";

    private final Path path;
    private final String serverName;
    private final LinkedHashMap<TileId, Image> memoryCache;


    public record TileId(int zoom, int x, int y) {
        public TileId {
            Preconditions.checkArgument(isValid(zoom, x, y));
        }// TODO: 24/4/23 check if we need conditions

        public static boolean isValid(int zoom, int x, int y) {

            return zoom >= MIN_ZOOM_LEVEL && zoom <= MAX_ZOOM_LEVEL &&
                    x >= 0 && x < Math.scalb(4, zoom) &&
                    y >= 0 && y < Math.scalb(4, zoom);
        }
    }


    public TileManager(Path path, String serverName) {
        this.path = path;
        this.serverName = PROTOCOL_STRING + serverName;
        memoryCache =
                new LinkedHashMap<>(CACHE_MAX_ENTRIES, DEFAULT_LOAD_FACTOR, true) {

                };
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        String serverString = serverName + PATH_SEPARATOR + tileId.zoom() + PATH_SEPARATOR
                + tileId.x() + PATH_SEPARATOR + tileId.y() + FILE_ENDING;

        String directoryString = path.toString() + PATH_SEPARATOR + tileId.zoom()
                + PATH_SEPARATOR + tileId.x();

        String tileString = directoryString + PATH_SEPARATOR + tileId.y() + FILE_ENDING;
        Path directoryPath = Path.of(directoryString);
        Path tilePath = Path.of(tileString);

        if (memoryCache.containsKey(tileId)) {
            return memoryCache.get(tileId);
        }
        if (Files.exists(tilePath)) {
            byte[] bytes = Files.readAllBytes(tilePath);
            Image image = new Image(new ByteArrayInputStream(bytes));
            updateCache(tileId, image);
            return image;
        }

        URL u = new URL(serverString);
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "I had");
        try (InputStream i = c.getInputStream()) {
            byte[] bytes = i.readAllBytes();
            Image image = new Image(new ByteArrayInputStream(bytes));
            updateCache(tileId, image);
            System.out.println("Image retrieved from server!");
            Files.createDirectories(directoryPath);
            Files.write(tilePath, bytes);
            return image;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateCache(TileId tileId, Image image) {
        if(memoryCache.size() >= CACHE_MAX_ENTRIES){
            memoryCache.remove(memoryCache.keySet().iterator().next());
        }
        memoryCache.put(tileId,image);
    }
}
