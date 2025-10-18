package game.utils.components;

import game.utils.Node2D;
import game.utils.Vec2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite2D extends Node2D {
	public BufferedImage texture;
	public Vec2D size;
	public Color color;
	
	public Sprite2D(BufferedImage texture, Vec2D size) {
		this.texture = texture;
		this.size = new Vec2D(size);
	}
	
	@Override
	public void _ready() {}

	@Override
	public void _process(float delta) {}

	@Override
	public void _draw(Graphics2D g2) {
		if (texture == null) {
			g2.setColor(color);
			g2.fillRect(
				(int) position.x, (int) position.y,
				(int) size.x, (int) size.y
			);
		}
		else {
			g2.drawImage(
				texture,
				(int) position.x, (int) position.y,
				(int) size.x, (int) size.y,
				null
			);
		}
	}
	
	
}
