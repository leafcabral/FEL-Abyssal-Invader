import java.awt.Color;
import java.awt.image.BufferedImage;

public class Bullet extends GameObject{
	public Bullet(Vec2D pos, Vec2D size,
	              Vec2D direction, int speed,
	              BufferedImage sprite, Color fallback_color) {
		super(pos, size, direction, speed, sprite, fallback_color);
	}

	@Override
	public void update(float delta) {
		super.move(delta);
	}
	
	public static Bullet newDefaultBullet(Vec2D pos) {
		return new Bullet(
			pos, new Vec2D(10, 20),
			new Vec2D(0, -1), 1600,
			ImageManager.getImage("bullet1"), Color.YELLOW
		);
	}
}