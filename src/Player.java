import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage; // Importa para trabalhar com imagens.


public class Player extends GameObject {
	public int life;
	private float iFrameSeconds = 0;

	public Player(Vec2D pos, Vec2D size,
	              Vec2D direction, int speed,
	              BufferedImage sprite, Color fallback_color,
		      int life) {
		super(pos, size, direction, speed, sprite, fallback_color);
		this.life = life;
	}

	@Override
	public void update(float delta) {
		if (iFrameSeconds > 0) {
			iFrameSeconds -= delta;
		};
	};
	
	public void makeIvencible(float seconds) {
		iFrameSeconds = seconds;
	}
}