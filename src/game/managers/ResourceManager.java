package game.managers;

import game.utils.Vec2D;
import java.awt.Font;
import java.awt.Graphics2D;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.sound.sampled.LineEvent;

public class ResourceManager {
	public final String RES_DIR = "res/";
	public final String IMGS_DIR = RES_DIR + "images/";
	public final String GIFS_DIR = RES_DIR + "gifs/";
	public final String SOUNDS_DIR = RES_DIR + "sounds/";
	public final String FONTS_DIR = RES_DIR + "fonts/";
	
	private final HashMap<String, BufferedImage> images;
	private final HashMap<String, String> sounds;
	private final HashMap<String, Font> fonts;
	private final HashMap<String, ImageIcon> gifs;
	private final ArrayList<Gif> activeGifs;
	
	private class Gif {
		public final ImageIcon gif;
		public final String fileName;
		public final int x, y;
		public final float targetTime;
		public float runTime = 0;
		
		public Gif(ImageIcon gif, String fileName, int x, int y, float targetTime) {
			this.fileName = fileName;
			this.gif = gif;
			this.x = x;
			this.y = y;
			this.targetTime = targetTime;
		}
		public Gif(String fileName, Vec2D pos, float targetTime) {
			this.fileName = fileName;
			this.gif = gifs.get(fileName);
			this.x = (int)pos.x;
			this.y = (int)pos.y;
			this.targetTime = targetTime;
		}
	}
	
	public ResourceManager(boolean preload) {
		this.images = new HashMap<>();
		this.sounds = new HashMap<>();
		this.fonts = new HashMap<>();
		this.gifs = new HashMap<>();
		this.activeGifs = new ArrayList<>();
		
		if (preload) {
			preloadImages();
			preloadSounds();
			preloadFonts();
			preloadGifs();
		}
	}
	
	private List<Path> grabFilePaths(String dir) {
		try {
			return Files.walk(Paths.get(dir))
				.filter(path -> !path.toString().contains("_deprecated"))
				.filter(Files::isRegularFile)
				.collect(Collectors.toList()
			);
		} catch (IOException e) {
			System.err.println("Failed to load images at " + IMGS_DIR);
			e.printStackTrace();
			return null;
		}
	}
	
	private void preloadImages() {
		List<Path> imagesFiles = grabFilePaths(IMGS_DIR);
		
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
	private void preloadSounds() {
		List<Path> soundsFiles = grabFilePaths(SOUNDS_DIR);
		
		for (Path soundPath : soundsFiles) {
			try {
				String filename = soundPath.getFileName().toString();
				sounds.put(filename, soundPath.toString());
			} catch (Exception e) {
				System.err.println("Failed to load: " + soundPath);
				e.printStackTrace();
			}
		}
	}
	private void preloadFonts() {
		List<Path> fontFiles = grabFilePaths(FONTS_DIR);
		
		for (Path fontPath : fontFiles) {
			try {
				String filename = fontPath.getFileName().toString();
				Font font = Font.createFont(Font.TRUETYPE_FONT, fontPath.toFile());
				
				fonts.put(filename, font);
			} catch (Exception e) {
				System.err.println("Failed to load font at " + fontPath);
				e.printStackTrace();
			}
		}
	}
	private void preloadGifs() {
		List<Path> gifFiles = grabFilePaths(GIFS_DIR);
		
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
	
	public BufferedImage getImage(String key) {
		return images.get(key);
	}
	
	public Font getFont(String key, int style, float size) {
		if (fonts.get(key) == null) { return null; }
		else { return fonts.get(key).deriveFont(style, size); }
	}
	
	public void playSound(String key) {
		String soundPath = sounds.get(key);
		
		if (soundPath == null) {
			System.err.println("Som nao existe: " + key);
			return;
		} 
		
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(new File(soundPath));
			
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			
			clip.addLineListener(event -> {
				if (event.getType() == LineEvent.Type.STOP) {
					clip.close();
					try { audio.close(); }
					catch (IOException e) {}
				}
			});
			
			clip.start();
		} catch (Exception e) {
			System.err.println("Erro ao tocar: " + key);
			e.printStackTrace();

		}
	}
	
	public void startGif(String key, Vec2D pos, float time) {
		ImageIcon gif = gifs.get(key);
		if (gif == null) {
			System.err.println("Som nao existe: " + key);
			return;
		} 
		
		pos = new Vec2D(pos);
		pos.x -= gif.getIconWidth() / 2;
		pos.y -= gif.getIconHeight() / 2;
		
		activeGifs.add(new Gif(gif, key, (int)pos.x, (int)pos.y, time));
	}
	public void startExplosion(Vec2D pos) { startGif("explosion.gif", pos, 0.4f);}
	
	public void updateGifs(double delta) {
		Iterator<Gif> iterator = activeGifs.iterator();

		while (iterator.hasNext()) {
			Gif gif = iterator.next();
			gif.runTime += delta;

			if (gif.runTime >= gif.targetTime) {
				iterator.remove();
			}
		}
	}
	
	public void drawGifs(Graphics2D g2) {
		 for (Gif gif : activeGifs) {
			if (gif.gif != null) {
				gif.gif.paintIcon(null, g2, gif.x, gif.y);
			}
		};
	}
	
	public void clearAllGifs() { activeGifs.clear(); }
}
