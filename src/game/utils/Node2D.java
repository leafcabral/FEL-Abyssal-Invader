package game.utils;

import java.awt.Graphics2D;

public abstract class Node2D {
	// Protected por que tem como os componentes acessarem ele diretamente
	protected Vec2D position = new Vec2D();
	protected float rotation = 0;
	
	public Vec2D getPosition() { return new Vec2D(position); }
	public void setPosition(Vec2D pos) { this.position = new Vec2D(pos); }
	public void setPosition(float x, float y) { this.position = new Vec2D(x, y); }
	
	public float getRotation() { return rotation; }
	public void setRotation(float rotation) { this.rotation = rotation; }
	
	public abstract void _ready();
	public abstract void _process(float delta);
	public abstract void _draw(Graphics2D g2);
}
