import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage; // Importa para trabalhar com imagens.


public class Player extends GameObject {
	public int life;
	private float iFrameSeconds = 0;
	private final float shootDelay = 300;
	private float shootTimer = 0;

	public Player(Vec2D pos, Vec2D size,
	              Vec2D direction, int speed,
	              BufferedImage sprite, Color fallback_color,
		      int life) {
		super(pos, size, direction, speed, sprite, fallback_color);
		this.life = life;
	}
	public Player(Vec2D pos) {
		this(
			pos, new Vec2D(50,50),
			new Vec2D(0, 1), 5,
			ImageManager.getImage("player"), Color.GREEN,
			5
		);
	}

	@Override
	public void update(float delta) {
		if (iFrameSeconds > 0) {
			iFrameSeconds -= delta;
		};
		if (shootTimer > 0) {
			shootTimer -= delta;
		}
	};
	
	public void makeIvencible(float seconds) {
		iFrameSeconds = seconds;
	}
	
	public Bullet shoot() {
		if (shootTimer <= 0) {
			shootTimer = shootDelay;
			return Bullet.newDefaultBullet(pos);
		}
		return null;
	}
}