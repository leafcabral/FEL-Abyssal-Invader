package game.entities;

import game.entities.patterns.MovementPattern;
import game.utils.Vec2D;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Enemy extends Entity {
	private MovementPattern movementPattern;
	private float iFrameSeconds = 0;

	public Enemy(Vec2D pos, Vec2D size,
	              Vec2D direction, int speed,
	              BufferedImage sprite, Color fallback_color,
		      int life, MovementPattern movement) {
		super(pos, size, direction, speed, sprite, fallback_color, life);
		this.movementPattern = movement;
	}
	public Enemy(Vec2D pos, BufferedImage img, MovementPattern movement) {
		this(
			pos, new Vec2D(75, 75),
			new Vec2D(1, 0), 200,
			img, Color.RED,
			1, movement
		);
	}

	@Override
	public void update(double delta) {
		this.movementDirection = movementPattern.update(
			(float)delta,
			new Vec2D(spriteShape.x, spriteShape.y)
		);
		this.spriteDirection = this.movementDirection;
		super.update(delta);
	}
	
	public void makeIvencible(float seconds) {
		iFrameSeconds = seconds;
	}
}