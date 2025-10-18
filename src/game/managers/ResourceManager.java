package game.managers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceManager {
	public final String RES_DIR = "res/";
	public final String IMGS_DIR = RES_DIR + "images/";
	public final String GIFS_DIR = RES_DIR + "gifs/";
	public final String SOUNDS_DIR = RES_DIR + "sounds/";
	
	private HashMap<String, BufferedImage> images;
	private HashMap<String, ImageIcon> gifs;
	private HashMap<String, AudioInputStream> sounds;
	
	public ResourceManager(boolean preload) {
		this.images = new HashMap<>();
		this.gifs = new HashMap<>();
		this.sounds = new HashMap<>();
		
		if (preload) {
			preloadImages();
			preloadGifs();
			preloadSounds();
		}
	}
	
	private void preloadImages() {
		List<Path> imagesFiles;
		try {
			imagesFiles = Files.walk(Paths.get(IMGS_DIR))
				.filter(Files::isRegularFile)
				.collect(Collectors.toList()
			);
		} catch (IOException e) {
			System.err.println("Failed to load images at " + IMGS_DIR);
			e.printStackTrace();
			return;
		}
		
		for (Path imagePath : imagesFiles) {
			try {
				String filename = imagePath.getFileName().toString();
				BufferedImage img = ImageIO.read(imagePath.toFile());
            
				if (img != null) {
					images.put(filename, img);
				} else {
					System.err.println("Failed to load image: " + filename);
				}
			} catch (IOException e) {
				System.err.println("Failed to load: " + imagePath);
				e.printStackTrace();
			}
		}
	}
	private void preloadGifs() {
		List<Path> gifFiles;
		try {
			gifFiles = Files.walk(Paths.get(GIFS_DIR))
				.filter(Files::isRegularFile)
				.collect(Collectors.toList()
			);
		} catch (IOException e) {
			System.err.println("Failed to load gifs at " + GIFS_DIR);
			e.printStackTrace();
			return;
		}
		
		for (Path imagePath : gifFiles) {
			String filename = imagePath.getFileName().toString();
			ImageIcon gif = new ImageIcon(imagePath.toString());

			if (gif != null) {
				gifs.put(filename, gif);
			} else {
				System.err.println("Failed to load gif: " + filename);
			}
		}
	}
	private void preloadSounds() {
		List<Path> soundsFiles;
		try {
			soundsFiles = Files.walk(Paths.get(SOUNDS_DIR))
				.filter(Files::isRegularFile)
				.collect(Collectors.toList()
			);
		} catch (IOException e) {
			System.err.println("Failed to load sounds at " + SOUNDS_DIR);
			e.printStackTrace();
			return;
		}
		
		for (Path soundPath : soundsFiles) {
			try {
				String filename = soundPath.getFileName().toString();
				AudioInputStream audioIn;
				audioIn = AudioSystem.getAudioInputStream(
					new File(soundPath.toString())
				);
            
				if (audioIn != null) {
					sounds.put(filename, audioIn);
				} else {
					System.err.println("Failed to load image: " + filename);
				}
			} catch (Exception e) {
				System.err.println("Failed to load: " + soundPath);
				e.printStackTrace();
			}
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
	
	public ImageIcon getGif(String key) {
		return gifs.get(key);
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
