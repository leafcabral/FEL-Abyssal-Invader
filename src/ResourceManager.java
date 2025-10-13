
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class ResourceManager {
	public final String RES_DIR = "res/";
	public final String IMG_DIR = RES_DIR + "images/";
	public final String SOUNDS_DIR = RES_DIR + "sounds/";
	
	private HashMap<String, BufferedImage> images;
	private HashMap<String, AudioInputStream> sounds;
	
	public ResourceManager(boolean preload) {
		this.images = new HashMap<>();
		this.sounds = new HashMap<>();
		
		if (preload) {
			preloadImages();
			preloadSounds();
		}
	}
	
	private void preloadImages() {
		// Player
		loadImage("player", "player/player_1.png");
		// Enemies
		loadImage("alien1", "enemies/alien_1.png");
		loadImage("alien2", "enemies/alien_2.png");
		loadImage("alien3", "enemies/alien_3.png");
		loadImage("alien4", "enemies/alien_4.png");
		// Bullets
		loadImage("bullet1", "bullet.png");
		// Background
		loadImage("background1", "background.jpg");
		// Effects
		loadImage("explosion", "explosion.gif");
		// Life
		loadImage("life", "life.png");
	}
	
	private void preloadSounds() {
		loadSound("explosion", "explosion.wav");
		loadSound("shot", "shot.wav");
	}
	
	private void loadImage(String key, String value) {
		String path = IMG_DIR + value;
		
		try {
			images.put(key, ImageIO.read(new File(path)));
		} catch (IOException e) {
			System.err.println("Failed to load: " + path);
			e.printStackTrace();
		}
	}
	
	private void loadSound(String key, String value) {
		String path = SOUNDS_DIR + value;
		
		try {
			File soundFile = new File(path);
			AudioInputStream audioIn;
			audioIn = AudioSystem.getAudioInputStream(soundFile);
			
			// Se os audios não fosse tocados em cima do outro,
			// seria melhor armazenar Clip ao inves de Audio...
			sounds.put(key, audioIn);
		} catch (Exception e) {
			System.err.println("Erro ao carregar som: " + path);
			e.printStackTrace();
		}
	}
	
	public BufferedImage getImage(String key) {
		return images.get(key);
	}
	
	public void playSound(String key) {
		AudioInputStream audio = sounds.get(key);
		
		if (audio == null) {
			System.err.println("Som não existe: " + key);
		} else {
			try {
				Clip clip = AudioSystem.getClip();
				clip.open(audio);
				clip.start();
			} catch (Exception e) {
				System.err.println("Erro ao tocar: " + key);
				e.printStackTrace();

			}
		}
	}
}
