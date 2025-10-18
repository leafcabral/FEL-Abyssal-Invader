package game.utils.components;

import game.utils.Node2D;
import game.utils.Vec2D;
import java.awt.Graphics2D;

public class Movement2D extends Node2D {
	public float speed = 0;
	
	public Movement2D(Vec2D direction, float speed) {
		this.direction = new Vec2D(direction);
		this.speed = speed;
	}
	public Movement2D() {
		this.direction = new Vec2D(0,0);
		this.speed = 0;
	}
	
	@Override
	public void _process(float delta) {
		position.addIp(getVelocity().multiply(delta));
	}
	
	@Override
	public void _draw(Graphics2D g2) {}
	
	public Vec2D getVelocity() {
		return direction.normalize().multiply(speed);
	}
}
