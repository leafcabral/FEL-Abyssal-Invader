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

	public Entity(
			Vec2D position, float rotation,
			Vec2D direction, float speed,
			Vec2D collisionSize,
			BufferedImage texture, Vec2D imageSize, Color color
		){
		super(position, rotation);
		this.movement = new Movement2D(direction, speed);
		this.collision = new CollisionShape2D(collisionSize);
		this.sprite = new Sprite2D(texture, imageSize, color);
	}
	public Entity(Vec2D position, Vec2D size, BufferedImage texture, Color color) {
		super(position, 0);
		// default movement 
		this.collision = new CollisionShape2D(size);
		Vec2D spriteSize = new Vec2D(texture.getWidth(), texture.getHeight());
		this.sprite = new Sprite2D(texture, spriteSize, color);
	}
	
	@Override
	public void _process(float delta) { movement._process(delta); }

	@Override
	public void _draw(Graphics2D g2) { sprite._draw(g2); }
}
