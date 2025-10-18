package game.entities;

import game.utils.Vec2D;
import java.awt.Color;
import java.awt.image.BufferedImage;


public class Player extends Entity {
	public enum WeaponType {
		DEFAULT, SHOTGUN, BLAST
	}
	private WeaponType currentWeapon;
	private float weaponDelays[] = {0.5f, 1.0f, 2.0f};
	private float weaponTimers[] = {0, 0, 0};
	
	private BufferedImage sprites[];
	private int imgIndex = 0;
	private final float changeSpriteDelay = 0.1f;
	private float changeSpriteTimer = changeSpriteDelay;

	public Player(Vec2D pos, BufferedImage img1, BufferedImage img2, BufferedImage img3) {
		super(pos, new Vec2D(75, 75), img1, Color.BLUE, 3);
		this.sprites = new BufferedImage[]{img1, img2, img3};
		this.movement.speed = 500;
		this.switchWeapon(WeaponType.DEFAULT);
	}

	@Override
	public void _process(float delta) {
		super._process(delta);

		for (int i = 0; i < weaponTimers.length; i++) {
			if (weaponTimers[i] > 0) {
				weaponTimers[i] -= delta;
			}
		}
		
		if (direction.x != 0) {
			if (changeSpriteTimer > 0) {
				changeSpriteTimer -= delta;
			} else {
				imgIndex = (imgIndex + 1) % sprites.length;
				sprite.texture = sprites[imgIndex];
				changeSpriteTimer = changeSpriteDelay;
			}
		} else {
			imgIndex = 0;
			sprite.texture = sprites[imgIndex];
		}
	};
	
	public void switchWeapon(WeaponType newWeapon) {
		this.currentWeapon = newWeapon;
	}
	public WeaponType getCurrentWeapon() {
		return currentWeapon;
	}
	
	public void setWeaponTimer(float newTime) {
		weaponTimers[currentWeapon.ordinal()] = newTime;
	}
	public float getWeaponTimer() {
		return weaponTimers[currentWeapon.ordinal()];
	}
	
	public Boolean canShoot() {
		return getWeaponTimer() <= 0;
	}
	
	public void resetShootTimer() {
		setWeaponTimer(weaponDelays[currentWeapon.ordinal()]);
	}
	public void clearShootTimer() {
		setWeaponTimer(0);
	}
	
	public float getWeaponCooldown(WeaponType weapon) {
		return weaponTimers[weapon.ordinal()];
	}

	public float getWeaponCooldownProgress(WeaponType weapon) {
		int index = weapon.ordinal();
		if (weaponTimers[index] <= 0) { return 0f; }
		return weaponTimers[index] / weaponDelays[index];
	}
}
