package game.entities;

import game.utils.Vec2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class GameObject {
	public Vec2D pos;
	public Vec2D size;
	public Vec2D movementDirection;
	public float speed;
	public Vec2D spriteDirection;
	
	public BufferedImage sprite;
	private final Color fallback_color;
	public boolean flipped = false;
	
	
	public GameObject(Vec2D pos, Vec2D size,
	                  Vec2D direction, int speed,
	                  BufferedImage sprite, Color fallback_color) {
		this.pos = new Vec2D(pos);
		this.size = new Vec2D(size);
		this.movementDirection = new Vec2D(direction);
		this.spriteDirection = new Vec2D(direction);
		this.speed = speed;
		
		this.sprite = sprite;
		this.fallback_color = fallback_color;
	}
	
	
	public abstract void update(float delta);
	
	public void draw(Graphics2D g2) {
		if (sprite == null) {
			draw_fallback(g2);
			return;
		}
		
		// Função que pega Vec2D e acha seu angulo
		float angle = spriteDirection.angle();

		AffineTransform original = g2.getTransform();
		int centerX = (int)(pos.x + size.x / 2);
		int centerY = (int)(pos.y + size.y / 2);

		// Move centro da tela para centro da bala
		g2.translate(centerX, centerY);
		// Roda a tela (não tem como rodar o sprite)
		g2.rotate(angle);
		// Volta pra posição inicial
		g2.translate(-size.x / 2, -size.y / 2);

		if (flipped) {
			g2.drawImage(
				sprite,
				0, 0 + (int)size.y,
				(int) size.x, (int) -size.y,
				null
			);
		} else {
			g2.drawImage(
				sprite,
				0, 0,
				(int) size.x, (int) size.y,
				null
			);
		}
		
		g2.setTransform(original);
	}
	
	public void draw_fallback(Graphics2D g2) {
		g2.setColor(fallback_color);
		g2.fillRect(
			(int) pos.x, (int) pos.y,
			(int) size.x, (int) size.y
		);
	}
	
	public Vec2D getCenter() {
		Vec2D center = new Vec2D();
		center.x = pos.x + (size.x / 2);
		center.y = pos.y + (size.y / 2);
		
		return center;
	}
	
	public Vec2D getVelocity() {
		return movementDirection.normalize().multiply(speed);
	}
	
	public void move(float delta) {
		pos.addIp(getVelocity().multiply(delta));
	}
	
	public float left() {
		return this.pos.x;
	}
	public float right() {
		return this.pos.x + this.size.x;
	}
	public float top() {
		return this.pos.y;
	}
	public float bottom() {
		return this.pos.y + this.size.y;
	}
	
	public boolean collides(GameObject other) {
		return (this.left() < other.right() &&
			this.right() > other.left() &&
			this.top() < other.bottom() &&
			this.bottom() > other.top()
		);
	}
}
