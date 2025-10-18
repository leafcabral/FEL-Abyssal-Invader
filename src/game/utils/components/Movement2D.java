package game.utils.components;

import game.utils.Node2D;
import game.utils.Vec2D;
import java.awt.Graphics2D;

public class Movement2D extends Node2D {
	public Vec2D direction = new Vec2D();
	public float speed = 0;
	
	@Override
	public void _ready() {}
	
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
