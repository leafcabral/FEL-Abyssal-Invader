import java.util.HashMap;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageManager {
	private static HashMap<String, BufferedImage> images = new HashMap<>();
	public final Path RES_DIR = Paths.get("res", "images");
	
	public ImageManager() {
		// Player
		loadImage("player", Paths.get("player", "player_1.png"));
		
		// Enemies
		loadImage("alien1", Paths.get("enemies", "alien_1.png"));
		loadImage("alien2", Paths.get("enemies", "alien_2.png"));
		loadImage("alien3", Paths.get("enemies", "alien_3.png"));
		loadImage("alien4", Paths.get("enemies", "alien_4.png"));
		
		// Bullets
		loadImage("bullet1", Paths.get("bullet.png"));
		
		// Background
		loadImage("background1", Paths.get("background.jpg"));
			
		// Effects
		loadImage("explosion", Paths.get("explosion.gif"));
	}

	private void loadImage(String key, Path path) {
		Path fullPath = RES_DIR.resolve(path);
		
		try {
			images.put(key, ImageIO.read(fullPath.toFile()));
		} catch (IOException e) {
			System.err.println("Failed to load: " + fullPath);
			e.printStackTrace();
		}
	}

	public static BufferedImage getImage(String key) {
		return images.get(key);
	}
}
