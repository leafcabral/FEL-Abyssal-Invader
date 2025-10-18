package game.entities;

import game.entities.patterns.MovementPattern;
import game.utils.Vec2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Enemy extends GameObject {
	private MovementPattern movementPattern;
	public int life;
	private float iFrameSeconds = 0;

	public Enemy(Vec2D pos, Vec2D size,
	              Vec2D direction, int speed,
	              BufferedImage sprite, Color fallback_color,
		      int life, MovementPattern movement) {
		super(pos, size, direction, speed, sprite, fallback_color);
		this.life = life;
		this.movementPattern = movement;
	}
	public Enemy(Vec2D pos, BufferedImage img, MovementPattern movement) {
		this(
			pos, new Vec2D(75, 75),
			new Vec2D(0, 1), 200,
			img, Color.RED,
			1, movement
		);
	}

	@Override
	public void update(float delta) {
		this.movementDirection = movementPattern.update(
			delta,
			new Vec2D(spriteShape.x, spriteShape.y)
		);
		this.spriteDirection = this.movementDirection;
		super.update(delta);
		
		if (iFrameSeconds > 0) { iFrameSeconds -= delta; }
	}
	
	public void makeIvencible(float seconds) {
		iFrameSeconds = seconds;
	}
}