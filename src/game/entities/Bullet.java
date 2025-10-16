package game.entities;

import game.utils.Vec2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet extends GameObject{
	public Bullet(Vec2D pos, Vec2D size,
	              Vec2D direction, int speed,
	              BufferedImage sprite, Color fallback_color) {
		super(pos, size, direction, speed, sprite, fallback_color);
	}

	@Override
	public void update(float delta) {
		super.move(delta);
	}
	
	@Override
	public void draw(Graphics2D g2) {
		if (sprite == null) {
			draw_fallback(g2);
		} else {
			// Função que pega Vec2D e acha seu angulo
			double angle = Math.atan2(direction.y, direction.x);
			
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
	public static Bullet newDefaultBullet(Vec2D pos, BufferedImage img) {
		return new Bullet(
			pos, new Vec2D(30, 30),
			new Vec2D(0, -1), 1000,
			img, Color.YELLOW
		);
	}
	
	public static Bullet[] newShotgunBullets(Vec2D pos, BufferedImage img) {
		Bullet bullets[] = new Bullet[3];
		Vec2D directions[] = {
			new Vec2D(-0.5f, -1),
			new Vec2D(0, -1),
			new Vec2D(0.5f, -1)
		};
		
		for (int i = 0; i < bullets.length; i++) {
			bullets[i] = new Bullet(
				pos, new Vec2D(30, 30),
				directions[i], 1000,
				img, Color.GREEN
			);
		}
		
		return bullets;
	}
	
	public static Bullet newBlastBullet(Vec2D pos, BufferedImage img) {
		return new Bullet(
			pos.add(-50), new Vec2D(90, 90),
			new Vec2D(0, -1), 700,
			img, Color.BLUE
		);
	}
}