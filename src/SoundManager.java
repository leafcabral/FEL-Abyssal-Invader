import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.io.File;

public class SoundManager {

	public void playSound(String filePath) {
		try {
			File soundFile = new File("res/sounds/" + filePath); 
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (Exception e) {
			System.err.println("Erro ao carregar ou tocar o som: " + filePath);
			e.printStackTrace();
		}
	}
}