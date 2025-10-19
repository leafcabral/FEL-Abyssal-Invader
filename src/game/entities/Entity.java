package game.entities;

import game.utils.Vec2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class Entity {
	public Rectangle collisionShape;
	public Vec2D movementDirection;
	public float speed;

	public int life;
	public int maxLife;
	public float iFrameSeconds = 0;
	public float iFrameSecondsReverse = 0; // TODO: juntar com o outro
	
	public BufferedImage sprite;
	public Rectangle spriteShape;
	public Vec2D spriteDirection;
	private final Color fallback_color;
	public boolean flipped = false;
	
	
	public Entity(Vec2D pos, Vec2D size,
	                  Vec2D direction, int speed,
	                  BufferedImage sprite, Color fallback_color,
			  int life) {
		this.collisionShape = new Rectangle(
			pos.toPoint(),
			size.toDimension()
		);
		this.spriteShape = new Rectangle(
			pos.toPoint(),
			size.toDimension()
		);
		this.movementDirection = new Vec2D(direction);
		this.spriteDirection = new Vec2D(direction);
		this.speed = speed;
		
		this.sprite = sprite;
		this.fallback_color = fallback_color;
		
		this.life = life;
		this.maxLife = life;
	}
	
	
	public void update(double delta) {
		Vec2D velocity = getVelocity().multiply((float)delta);
		move(velocity);
		
		if (iFrameSeconds > 0) {
			iFrameSeconds -= delta;
			iFrameSecondsReverse += delta;
		}
	}
	
	public void move(Vec2D velocity) {
		collisionShape.translate((int)velocity.x, (int)velocity.y);
		spriteShape.translate((int)velocity.x, (int)velocity.y);
	}
	public void moveX(float velocityX) {
		collisionShape.translate((int)velocityX, 0);
		spriteShape.translate((int)velocityX, 0);
	}
	public void moveY(float velocityY) {
		collisionShape.translate(0, (int)velocityY);
		spriteShape.translate(0, (int)velocityY);
	}
	
	public void draw(Graphics2D g2) {
		if (sprite == null) {
			draw_fallback(g2);
			return;
		}
		
		// ex.: 3.24 -> 2
		int firstDecimalDigit = (int)(iFrameSecondsReverse*10) % 10;
		if (firstDecimalDigit % 2 == 0 && isInvincible()) {
			return;
		}
		
		// Função que pega Vec2D e acha seu angulo
		double angle = spriteDirection.angle();

		AffineTransform original = g2.getTransform();
		int centerX = (int)(spriteShape.x + spriteShape.width / 2);
		int centerY = (int)(spriteShape.y + spriteShape.height / 2);

		// Move centro da tela para centro da bala
		// Roda a tela (não tem como rodar o sprite)
		// Volta pra posição inicial
		g2.translate(centerX, centerY);
		g2.rotate(angle);
		g2.translate(-spriteShape.width / 2, -spriteShape.height / 2);

		if (flipped) {
			g2.drawImage(
				sprite,
				0, 0 + (int)spriteShape.height,
				(int) spriteShape.width, (int) -spriteShape.height,
				null
			);
		} else {
			g2.drawImage(
				sprite,
				0, 0,
				(int) spriteShape.width, (int) spriteShape.height,
				null
			);
		}
		
		g2.setTransform(original);
	}
	
	public void draw_fallback(Graphics2D g2) {
		g2.setColor(fallback_color);
		g2.fillRect(
			(int) spriteShape.x, (int) spriteShape.y,
			(int) spriteShape.width, (int) spriteShape.height
		);
	}
	
	public Vec2D getCenter() {
		Vec2D center = new Vec2D();
		center.x = spriteShape.x + (spriteShape.width / 2);
		center.y = spriteShape.y + (spriteShape.height / 2);
		
		return center;
	}
	
	public Vec2D getVelocity() {
		return movementDirection.normalize().multiply(speed);
	}
	
	public float left() {
		return this.spriteShape.x;
	}
	public float right() {
		return this.spriteShape.x + this.spriteShape.width;
	}
	public float top() {
		return this.spriteShape.y;
	}
	public float bottom() {
		return this.spriteShape.y + this.spriteShape.height;
	}
	
	public void makeInvincible(float seconds) {
		this.iFrameSeconds = seconds;
	}
	
	public boolean isInvincible() {
		return iFrameSeconds > 0;
	}

	public boolean takeDamage(int damage) {
		if (!isInvincible()) {
			makeInvincible(1.5f); // Trocar para iFrameDelay
			
			this.life -= damage;
			if ((life) <= 0) {
				return true;
			}
		}
		return false;
	}

	public void heal(int lifeToHeal) {
		this.life += lifeToHeal;
	}

	public void resetLife() {
		this.life = maxLife;
	}
	
	public boolean collidesWith(Entity other) {
		return this.collisionShape.intersects(other.collisionShape);
	}
}
