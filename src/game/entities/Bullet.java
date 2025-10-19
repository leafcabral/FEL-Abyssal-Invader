package game.entities;

import game.utils.Vec2D;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Bullet extends Entity{
	public int damage;
	public int enemiesPierced = 0;

	public Bullet(Vec2D pos, Vec2D size,
	              Vec2D direction, int speed,
	              BufferedImage sprite, Color fallback_color,
		      int life, int damage) {
		super(pos, size, direction, speed, sprite, fallback_color, life);
		this.damage = damage;
	}

	@Override
	public void update(double delta) {
		super.update(delta);
	}
	
	public static Bullet newDefaultBullet(Vec2D pos, BufferedImage img) {
		return new Bullet(
			pos, new Vec2D(60, 30),
			new Vec2D(0, -1), 1000,
			img, Color.YELLOW,
			1, 1
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
				pos, new Vec2D(60, 30),
				directions[i], 1000,
				img, Color.GREEN,
				1, 2
			);
		}
		
		return bullets;
	}
	
	public static Bullet newBlastBullet(Vec2D pos, BufferedImage img) {
		return new Bullet(
			pos.add(-50), new Vec2D(160, 90),
			new Vec2D(0, -1), 700,
			img, Color.BLUE,
			5, 5
		);
	}
}