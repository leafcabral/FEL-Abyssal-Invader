import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class GameObject {
	public Vec2D pos;
	public Vec2D size;
	public Vec2D direction;
	public int speed;
	
	private BufferedImage sprite;
	private Color fallback_color;
	
	
	public GameObject(Vec2D pos, Vec2D size,
	                  Vec2D direction, int speed,
	                  BufferedImage sprite, Color fallback_color) {
		this.pos = new Vec2D(pos);
		this.size = new Vec2D(size);
		this.direction = new Vec2D(direction);
		this.speed = speed;
		
		this.sprite = sprite;
		this.fallback_color = fallback_color;
	}
	
	
	public abstract void update(float delta);
	public abstract void draw(Graphics2D g2);
	
	
	public BufferedImage getSprite() {
		return sprite;
	}
	
	public void draw_fallback(Graphics2D g2) {
		g2.setColor(fallback_color);
		g2.fillRect(pos.x, pos.y, size.x, size.y);
	}
	
	public Vec2D getCenter() {
		Vec2D center = new Vec2D();
		center.x = pos.x + (size.x * 2);
		center.y = pos.y + (size.y * 2);
		
		return center;
	}
	
	public Vec2D getVelocity() {
		return direction.normalize().multiply(speed);
	}	
}
