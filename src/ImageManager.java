import java.util.HashMap;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageManager {
	public String dir = "res/images/";
	private HashMap<String, BufferedImage> images = new HashMap<>();
	
	public ImageManager() {
		// Player
		loadImage("player", "player/player_1");
		
		// Enemies
		loadImage("alien1", "enemies/alien_1.png");
		loadImage("alien2", "enemies/alien_2.png");
		loadImage("alien3", "enemies/alien_3.png");
		loadImage("alien4", "enemies/alien_4.png");
		
		// Bullets
		loadImage("bullet1", "bullet.png");
		
		// Background
		loadImage("background1", "background.png");
			
		// Effects
		loadImage("explosion", "explosion.gif");
	}

	private void loadImage(String name, String path) {
		try {
			images.put(name, ImageIO.read(new File(path)));
		} catch (IOException e) {
			System.err.println("Failed to load: " + path);
			e.printStackTrace();
		}
	}

	public BufferedImage getImage(String key) {
		return images.get(key);
	}
}
