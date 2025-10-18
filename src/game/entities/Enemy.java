package game.entities;

import game.utils.Vec2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Enemy extends GameObject {
	public int life;
	private float iFrameSeconds = 0;

	public Enemy(Vec2D pos, Vec2D size,
	              Vec2D direction, int speed,
	              BufferedImage sprite, Color fallback_color,
		      int life) {
		super(pos, size, direction, speed, sprite, fallback_color);
		this.life = life;
	}
	public Enemy(Vec2D pos, BufferedImage img) {
		this(
			pos, new Vec2D(75, 75),
			new Vec2D(0, 1), 200,
			img, Color.RED,
			1
		);
	}

	@Override
	public void update(float delta) {
		super.move(delta);
		
		if (iFrameSeconds > 0) {
			iFrameSeconds -= delta;
		};
	}
	
	@Override
	public void draw(Graphics2D g2) {
		if (sprite == null) {
			draw_fallback(g2);
		} else {
			// Função que pega Vec2D e acha seu angulo
			double angle = Math.atan2(direction.y, direction.x);
			// sprite, por padrão, está para cima
			angle -= Math.PI / 2;
			
			AffineTransform original = g2.getTransform();
			int centerX = (int)(pos.x + size.x / 2);
			int centerY = (int)(pos.y + size.y / 2);
			
			// Move centro da tela para centro da bala
			g2.translate(centerX, centerY);
			// Roda a tela (não tem como rodar o sprite)
			g2.rotate(angle);
			// Volta pra posição inicial
			g2.translate(-size.x / 2, -size.y / 2);
			
			
			g2.drawImage(
				sprite,
				0, 0,
				(int) size.x, (int) size.y,
				null
			);
			
			g2.setTransform(original);
		}	
	}
	
	public void makeIvencible(float seconds) {
		iFrameSeconds = seconds;
	}
}