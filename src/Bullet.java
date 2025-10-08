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
}