import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage; // Importa para trabalhar com imagens.


public class Player extends GameObject {
	private int life;
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
	
	@Override
	public void draw(Graphics2D g2) {
		BufferedImage spriteTemp = super.getSprite();
		
		if (spriteTemp == null) {
			super.draw_fallback(g2);
			return;
		}
		
		g2.drawImage(spriteTemp, pos.x, pos.y, size.x, size.y, null);
	}
	
	public void makeIvencible(float seconds) {
		iFrameSeconds = seconds;
	}
}