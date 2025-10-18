package game.utils.components;

import game.utils.Node2D;
import game.utils.Vec2D;
import java.awt.Graphics2D;

public class CollisionShape2D extends Node2D {
	public Vec2D size;
	
	public CollisionShape2D(Vec2D size) {
		this.size = new Vec2D(size);
	}
	
	@Override
	public void _ready() {}

	@Override
	public void _process(float delta) {}

	@Override
	public void _draw(Graphics2D g2) {}
	
	public float left() { return this.position.x; }
	public float right() { return this.position.x + this.size.x; }
	public float top() { return this.position.y; }
	public float bottom() { return this.position.y + this.size.y; }
	
	public boolean collidesWith(CollisionShape2D other) {
		return (this.left() < other.right() &&
			this.right() > other.left() &&
			this.top() < other.bottom() &&
			this.bottom() > other.top()
		);
	}
}
