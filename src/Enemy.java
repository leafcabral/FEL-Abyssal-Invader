import java.awt.Color;
import java.awt.image.BufferedImage;

public class Enemy extends GameObject {
	public int life;
	private float iFrameSeconds = 0;
	//private static Random random = new Random();

	public Enemy(Vec2D pos, Vec2D size,
	              Vec2D direction, int speed,
	              BufferedImage sprite, Color fallback_color,
		      int life) {
		super(pos, size, direction, speed, sprite, fallback_color);
		this.life = life;
	}

	@Override
	public void update(float delta) {
		super.move(delta);
		
		if (iFrameSeconds > 0) {
			iFrameSeconds -= delta;
		};
	}
	
	public void makeIvencible(float seconds) {
		iFrameSeconds = seconds;
	}
}