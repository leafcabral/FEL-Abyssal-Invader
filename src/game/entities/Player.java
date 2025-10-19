package game.entities;

import game.utils.Vec2D;

import java.awt.Color;
import java.awt.image.BufferedImage; // Importa para trabalhar com imagens.


public class Player extends Entity {
	public enum WeaponType {
		DEFAULT, SHOTGUN, BLAST
	}
	
	private WeaponType currentWeapon;
	private final float weaponDelays[] = {0.8f, 3.2f, 8f};
	private final float weaponTimers[] = {0, 0, 0};

	public Player(Vec2D pos, Vec2D size,
	              Vec2D direction, int speed,
	              BufferedImage sprite, Color fallback_color,
		      int life) {
		super(pos, size, direction, speed, sprite, fallback_color, life);
		this.switchWeapon(WeaponType.DEFAULT);
	}
	public Player(Vec2D pos, BufferedImage img) {
		this(
			pos, new Vec2D(75, 75),
			new Vec2D(0, 0), 500, 
			img, Color.BLUE,
			3
		);
	}

	@Override
	public void update(double delta) {
		super.update(delta);
		
		// Atualiza temporizadores
		for (int i = 0; i < weaponTimers.length; i++) {
			if (weaponTimers[i] > 0) {
				weaponTimers[i] -= delta;
			}
		}
	};
	
	public Boolean canShoot() {
		return getWeaponTimer() <= 0;
	}
	
	public void resetShootTimer() {
		setWeaponTimer(weaponDelays[currentWeapon.ordinal()]);
	}
	public void clearShootTimer() {
		setWeaponTimer(0);
	}
	
	public void setWeaponTimer(float newTime) {
		weaponTimers[currentWeapon.ordinal()] = newTime;
	}
	public float getWeaponTimer() {
		return weaponTimers[currentWeapon.ordinal()];
	}
	
	public void switchWeapon(WeaponType newWeapon) {
		this.currentWeapon = newWeapon;
	}
	
	public WeaponType getCurrentWeapon() {
		return currentWeapon;
	}
	    
	public float getWeaponCooldown(WeaponType weapon) {
		return weaponTimers[weapon.ordinal()];
	}

	public float getWeaponCooldownProgress(WeaponType weapon) {
		int index = weapon.ordinal();
		if (weaponTimers[index] <= 0) return 0f;
		return weaponTimers[index] / weaponDelays[index];
	}
}
