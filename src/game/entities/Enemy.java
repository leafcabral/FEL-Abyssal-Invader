package game.entities;

import game.entities.patterns.MovementPattern;
import game.utils.Vec2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class Enemy extends Entity {
	private MovementPattern movementPattern;

	public Enemy(Vec2D pos, BufferedImage img, MovementPattern movementPattern, int life) {
		super(pos, new Vec2D(75, 75), img, Color.RED, life);
		this.movementPattern = movementPattern;
		this.movement.speed = 200;
	}
	
	@Override
	public void _process(float delta) {
		this.direction = movementPattern.update(delta, this.position);
		super._process(delta);
	}
	
	public boolean takeDamage() {
		if (super.isInvincible()) return false;

		life--;
		setInvincible(0.5f);
		return life <= 0;
	}
}