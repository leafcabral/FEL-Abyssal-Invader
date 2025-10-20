package game.entities.patterns;

import game.entities.Bullet;
import game.entities.Enemy;
import game.utils.Vec2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class AttackPattern {
	public float time = 0;

	public abstract ArrayList<Bullet> attack(Enemy enemy, BufferedImage bulletSprite, double delta);
	
	private static class NoAttack extends AttackPattern {
		@Override
		public ArrayList<Bullet> attack(Enemy enemy, BufferedImage bulletSprite, double delta) {
			return new ArrayList<>(); 
		}
	}
	public static AttackPattern newNoAttack() {
		return new NoAttack();
	}
	
	private static class SimpleShoot extends AttackPattern {
		private final float cooldown;
		private float timer = 0;
		
		public SimpleShoot(float cooldown) {
			this.cooldown = cooldown;
			this.timer = cooldown - 1;
		}
		
		@Override
		public ArrayList<Bullet> attack(Enemy enemy, BufferedImage img, double delta) {
			ArrayList<Bullet> bullets = new ArrayList<>();
			
			timer += delta;
			if (timer >= cooldown) {
				timer = 0;
				
				Vec2D pos = new Vec2D(
					enemy.getCenter().x - 20,
					enemy.collisionShape.y + enemy.collisionShape.height
				);
				
				Bullet bullet = Bullet.newDefaultBullet(pos, img);
				bullet.movementDirection.y = 1;
				bullet.spriteDirection.y = 1;
				bullet.speed /= 2;
				bullets.add(bullet);
			}
			
			return bullets;
		}
	}
	public static AttackPattern newSimpleShoot(float cooldown) {
		return new SimpleShoot(cooldown);
	}
}