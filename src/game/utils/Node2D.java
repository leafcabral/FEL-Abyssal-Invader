package game.utils;

import java.awt.Graphics2D;

public abstract class Node2D {
	public Vec2D position = new Vec2D();
	public float rotation = 0;
	
	public Node2D(Vec2D position, float rotation) {
		this.position = new Vec2D(position);
		this.rotation = rotation;
	}
	public Node2D() {
		this.position = new Vec2D();
		this.rotation = 0;
	}
	//public abstract void _ready();
	public abstract void _process(float delta);
	public abstract void _draw(Graphics2D g2);
}
