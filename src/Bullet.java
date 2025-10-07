import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bullet {
	public int x, y, width, height, speed;
	public static final String name = "res/bullet.png";
	private BufferedImage bulletImage;

	public Bullet(int x, int y, int width, int height, int speed) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = speed;
		
		try {
			this.bulletImage = ImageIO.read(new File(name));
		} catch (IOException e) {
			System.err.println("Erro ao carregar a imagem do inimigo: " + name);
			e.printStackTrace();
			// Se a imagem não carregar, 'enemyImage' será null.
		}
	}

	public void update() {
		y -= speed;
	}

	public void draw(Graphics2D g2) {
		if (bulletImage != null) {
			g2.drawImage(bulletImage, x, y, width, height, null);
		} else {
			g2.setColor(Color.YELLOW);
			g2.fillRect(x, y, width, height);
		}
	}
}