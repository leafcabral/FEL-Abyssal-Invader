package game.entities;

import game.utils.Vec2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet extends Entity {
	
	public Bullet(
			Vec2D pos,
			Vec2D direction, float speed,
			Vec2D size,
			BufferedImage sprite, Color color,
			int pierceCount) {
		super(pos, direction, speed, size, sprite, size, color, pierceCount);
	}

	public static Bullet newDefaultBullet(Vec2D pos, BufferedImage img) {
		return new Bullet(
			pos,
			new Vec2D(0, -1), 1000,
			new Vec2D(30, 30),
			img, Color.YELLOW,
			1
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
				pos,
				new Vec2D(0, -1), 1000,
				new Vec2D(30, 30),
				img, Color.RED,
				1
			);
		}
		
		return bullets;
	}
	
	public static Bullet newBlastBullet(Vec2D pos, BufferedImage img) {
		return new Bullet(
			pos,
			new Vec2D(0, -1), 700,
			new Vec2D(90, 90),
			img, Color.BLUE,
			3
		);
		
	}
}