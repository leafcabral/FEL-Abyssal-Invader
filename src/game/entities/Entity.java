package game.entities;

import game.utils.*;
import game.utils.components.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Entity extends Node2D {
	public Movement2D movement;
	public CollisionShape2D collision;
	public Sprite2D sprite;
	
	public float maxLife;
	public float life;
	private float iFrameSeconds = 0;
	public float iFrameDelay = 0;

	public Entity(
			Vec2D position,
			Vec2D direction, float speed,
			Vec2D collisionSize,
			BufferedImage texture, Vec2D imageSize, Color color,
			float life
		){
		super(position, direction);
		
		this.movement = new Movement2D(direction, speed);
		this.collision = new CollisionShape2D(collisionSize);
		this.sprite = new Sprite2D(texture, imageSize, color);
		
		this.maxLife = life;
		this.life = life;
	}
	public Entity(Vec2D position, Vec2D size, BufferedImage texture, Color color, float life) {
		super(position);
		
		this.movement = new Movement2D();
		this.collision = new CollisionShape2D(size);
		
		Vec2D spriteSize = new Vec2D(texture.getWidth(), texture.getHeight());
		this.sprite = new Sprite2D(texture, spriteSize, color);
		
		this.maxLife = life;
		this.life = life;
		
		syncComponents();
	}
	@Override
	public void _process(float delta) { 
		movement._process(delta);
		
		this.position = new Vec2D(movement.position);
		movement.direction = new Vec2D(this.direction);
		
		if (iFrameSeconds > 0) {
			iFrameSeconds -= delta;
		}
		
		syncComponents();
	}

	@Override
	public void _draw(Graphics2D g2) { sprite._draw(g2); }
	
	private void syncComponents() {
		if (movement != null) {
			movement.position = new Vec2D(this.position);
		}
		if (collision != null) {
			collision.position = new Vec2D(this.position);
		}
		if (sprite != null) {
			sprite.position = new Vec2D(this.position);
			sprite.direction = new Vec2D(this.direction);
		}
	}

	public boolean takeDamage(int damage) {
		if (this.isInvincible()) { return false; }

		life -= damage;
		if (this.life < 0) { this.life = 0; }
		setInvincible(iFrameDelay);
		return life <= 0;
	}
	
	public void setInvincible(float seconds) {
		this.iFrameSeconds = seconds;
	}
	public boolean isInvincible() {
		return iFrameSeconds > 0;
	}
	
	public void resetLife() {
		this.life = this.maxLife;
	}
	
	public boolean collidesWith(Entity other) {
		return this.collision.collidesWith(other.collision);
	}
}
