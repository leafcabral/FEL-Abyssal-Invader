package game.entities.patterns;

import game.utils.Vec2D;

public abstract class MovementPattern {
	public float time = 0;
	private float speed;
	
	public MovementPattern(float speed) { this.speed = speed; }
	
	// Retorna nova direção da entidade
	public abstract Vec2D update(float delta, Vec2D currentPos);
	
	public void updateTime(float delta) { this.time += delta; }
	public void reset() { this.time = 0; }
	public float getSpeed() { return this.speed; }
	
	
	public static MovementPattern newStraight(float speed) {
		return new StraightMovement(speed);
	}

	public static MovementPattern newWave(float speed, float amplitude, float frequency) {
		return new WaveMovement(speed, amplitude, frequency);
	}
	
	
	private static class StraightMovement extends MovementPattern {
		public StraightMovement(float speed) { super(speed); }
		
		@Override
		public Vec2D update(float delta, Vec2D currentPos) {
			this.time += delta;
			return new Vec2D(0,1);
		}
	}
	private static class WaveMovement extends MovementPattern {
		private float amplitude, frequency;
		
		public WaveMovement(float speed, float amplitude, float frequency) {
			super(speed);
			this.amplitude = amplitude;
			this.frequency = frequency;
		}
		
		@Override
		public Vec2D update(float delta, Vec2D currentPos) {
			time += delta;
			float x = (float)Math.sin(time * frequency) * amplitude;
			return new Vec2D(x, 1);
		}
	}
}
