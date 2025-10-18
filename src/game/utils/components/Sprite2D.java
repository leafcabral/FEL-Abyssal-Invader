package game.utils.components;

import game.utils.Node2D;
import game.utils.Vec2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Sprite2D extends Node2D {
	public BufferedImage texture;
	public Vec2D size;
	public Color color;
	
	public Sprite2D(BufferedImage texture, Vec2D size, Color color) {
		this.texture = texture;
		this.size = new Vec2D(size);
		this.color = color;
	}
	
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
		} else {
			// Função que pega Vec2D e acha seu angulo
			double angle = super.getRotationAngle();
			
			AffineTransform original = g2.getTransform();
			Vec2D center = this.getCenter();
			
			// Move centro da tela para centro da bala
			// Roda a tela (não tem como rodar o sprite)
			// Volta pra posição inicial
			g2.translate((int)center.x, (int)center.y);
			g2.rotate(angle);
			g2.translate(-size.x / 2, -size.y / 2);
			
			
			g2.drawImage(
				texture, 0, 0,
				(int) size.x, (int) size.y, null
			);
			
			g2.setTransform(original);
		}
	}
	
	public Vec2D getCenter() {
		return new Vec2D(
			position.x + size.x / 2,
			position.y + size.y / 2
		);
	}
}
