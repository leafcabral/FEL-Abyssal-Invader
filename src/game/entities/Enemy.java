package game.entities;

import game.entities.patterns.AttackPattern;
import game.entities.patterns.MovementPattern;
import game.utils.Vec2D;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Enemy extends Entity {
	public MovementPattern movementPattern;
	public AttackPattern attackPattern;
	private EnemyType type;
	
	public enum EnemyType {
		STRAIGHT_NONE(0),
		WAVE_NONE(1),
		STRAIGHT_SHOOT(2),
		SIDE_SHOOT(3);
		
		public final int difficulty;
		EnemyType(int difficulty) { this.difficulty = difficulty; }
	}

	public Enemy(Vec2D pos, Vec2D size,
			Vec2D direction, int speed,
			BufferedImage sprite, Color fallback_color,
			 int life, MovementPattern movement, AttackPattern attack,
			 EnemyType type) {
		super(pos, size, direction, speed, sprite, fallback_color, life);
		this.movementPattern = movement;
		this.attackPattern = attack;
		this.type = type;
		
	}
	public Enemy(Vec2D pos, BufferedImage img, EnemyType type, Vec2D screenSize) {
		this(
			pos, new Vec2D(75, 75),
			new Vec2D(1, 0), 200,
			img, Color.RED,
			getLifeForType(type),
			getMovementPatternForType(type, screenSize, new Vec2D(img.getWidth(), img.getHeight())),
			getAttackPatternForType(type),
			type
		);
	}

	@Override
	public void update(double delta) {
		this.movementDirection = movementPattern.update(
			(float)delta,
			new Vec2D(spriteShape.x, spriteShape.y)
		);
		if (type == EnemyType.WAVE_NONE) {
			this.spriteDirection = this.movementDirection;
		} else {
			this.spriteDirection = new Vec2D(0, 1);
		}
		super.update(delta);
	}
	
	public void makeIvencible(float seconds) {
		iFrameSeconds = seconds;
	}

	private static MovementPattern getMovementPatternForType(EnemyType type, Vec2D screenSize, Vec2D enemySize) {
		switch (type) {
			case STRAIGHT_NONE:
			case STRAIGHT_SHOOT:
				return MovementPattern.newStraight();
			case WAVE_NONE:
				return MovementPattern.newWave(1f, 3f);
			case SIDE_SHOOT:
				return MovementPattern.newSideToSide(screenSize, enemySize);
			default:
				return MovementPattern.newStraight();
		}
	}
	
	private static AttackPattern getAttackPatternForType(EnemyType type) {
		switch (type) {
			case STRAIGHT_SHOOT:
				return AttackPattern.newSimpleShoot(1.2f);
			case SIDE_SHOOT:
				return AttackPattern.newSimpleShoot(0.7f);
			case STRAIGHT_NONE:
			case WAVE_NONE:
			default:
				return AttackPattern.newNoAttack();
		}
	}
	
	private static int getLifeForType(EnemyType type) {
		switch (type) {
			case STRAIGHT_SHOOT:
				return 2;
			case SIDE_SHOOT:
				return 3;
			case STRAIGHT_NONE:
			case WAVE_NONE:
			default:
				return 1;
		}
	}
	
	@Override
	public boolean takeDamage(int damage) {
		if (!isInvincible()) {
			makeInvincible(0.5f); // Trocar para iFrameDelay
			
			this.life -= damage;
			if ((life) <= 0) {
				return true;
			}
		}
		return false;
	}
}