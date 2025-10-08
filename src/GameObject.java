import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class GameObject {
	public Vec2D pos;
	public Vec2D size;
	public Vec2D speed;
	
	private BufferedImage sprite;
	private Color fallback_color;
	
	public GameObject(Vec2D pos, Vec2D size, Vec2D speed,
			  BufferedImage sprite, Color fallback_color) {
		this.pos = new Vec2D(pos);
		this.size = new Vec2D(size);
		this.speed = new Vec2D(speed);
		
		this.sprite = sprite;
		this.fallback_color = fallback_color;
	}
	
	public abstract void update();
	public abstract void draw(Graphics2D g2);
	
	public void draw_fallback(Graphics2D g2) {
		g2.setColor(fallback_color);
		g2.fillRect(pos.x, pos.y, size.x, size.y);
	}
}
