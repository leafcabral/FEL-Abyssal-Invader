package game.utils;

import java.awt.Graphics2D;

public abstract class Node2D {
	public Vec2D position = new Vec2D();
	public Vec2D direction = new Vec2D(0, -1);
	
	public Node2D(Vec2D position, Vec2D direction) {
		this.position = new Vec2D(position);
		this.direction = new Vec2D(direction);
	}
	public Node2D(Vec2D position) {
		this.position = position;
		this.direction = new Vec2D(0, -1);
	}
	public Node2D() {
		this.position = new Vec2D();
		this.direction = new Vec2D(0, -1);
	}
	//public abstract void _ready();
	public abstract void _process(float delta);
	public abstract void _draw(Graphics2D g2);
	
	public void setDirection(Vec2D newDirection) {
		this.direction = new Vec2D(newDirection).normalize();
	}
	public void setDirection(float x, float y) {
		this.direction = new Vec2D(x, y).normalize();
	}

	public float getRotationAngle() {
		return (float) Math.atan2(direction.y, direction.x);
	}
	
	public void syncDirection(Node2D parent) {
		if (parent != null) {
			this.direction = new Vec2D(parent.direction);
		}
	}
}
