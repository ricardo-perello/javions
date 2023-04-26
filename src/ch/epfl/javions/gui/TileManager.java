package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import static ch.epfl.javions.gui.BaseMapController.MAX_ZOOM_LEVEL;
import static ch.epfl.javions.gui.BaseMapController.MIN_ZOOM_LEVEL;


public final class TileManager {
    // TODO: 26/4/23 comments
    private static final int CACHE_MAX_ENTRIES = 100;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private static final String PROTOCOL_STRING = "https://";
    private static final String FILE_ENDING = ".png";
    private static final String PATH_SEPARATOR = "/";

    private final Path path;
    private final String serverName;
    private final LinkedHashMap<TileId, Image> memoryCache;

    /**
     * TileId record is an identification for a specific tile of the map
     *
     * @param zoom zoom level
     * @param x    x position of tile
     * @param y    y position of tile
     */
    public record TileId(int zoom, int x, int y) {

        /**
         * Compact constructor of TileId that calls isValid method.
         *
         * @param zoom zoom level
         * @param x    x position of tile
         * @param y    y position of tile
         */
        public TileId {
            Preconditions.checkArgument(isValid(zoom, x, y));
        }

        /**
         * isValid method checks if the parameters of the tile can exist.
         *
         * @param zoom zoom level
         * @param x    x position of tile
         * @param y    y position of tile
         * @return true when tile is valid, false if tile is not valid (tile doesn't exist).
         */
        public static boolean isValid(int zoom, int x, int y) {

            return zoom >= MIN_ZOOM_LEVEL && zoom <= MAX_ZOOM_LEVEL &&
                    x >= 0 && x < Math.scalb(1, zoom) &&
                    y >= 0 && y < Math.scalb(1, zoom);
        }
    }

    /**
     * Constructor for TileManager
     *
     * @param path       path of repository where the tiles will be stored in disk.
     * @param serverName name of the server where you will request the tiles from. (tile.openstreetmap.org)
     */
    public TileManager(Path path, String serverName) {
        this.path = path;
        this.serverName = PROTOCOL_STRING + serverName;
        memoryCache =
                new LinkedHashMap<>(CACHE_MAX_ENTRIES, DEFAULT_LOAD_FACTOR, true) {

                };
    }

    /**
     * ImageForTileAt method is in charge of finding the image of a tile. It looks for the image in
     * your memory cache first, if it doesn't find it there, it looks in the disk cache, if it
     * doesn't find it there it will request it using HTTP from the server and store it in the caches.
     *
     * @param tileId is TileId of the tile we want the image of.
     * @return Image of tile.
     * @throws IOException when stream has a problem during the reading or writing of the image
     *                     from the caches.
     */
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
            Files.createDirectories(directoryPath);
            Files.write(tilePath, bytes);
            return image;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * UpdateCache ensures that the memory cache doesn't surpass its maximum capacity when you
     * add a new tile to the cache.
     *
     * @param tileId tileId of the tile you want to put in the cache.
     * @param image image of the tile you want to put in the cache.
     */
    private void updateCache(TileId tileId, Image image) {
        if (memoryCache.size() >= CACHE_MAX_ENTRIES) {
            memoryCache.remove(memoryCache.keySet().iterator().next());
        }
        memoryCache.put(tileId, image);
    }
}
