package game.entities.patterns;

import game.utils.Vec2D;

public abstract class MovementPattern {
	public float time = 0;

	// Retorna nova direção da entidade
	public abstract Vec2D update(float delta, Vec2D currentPos);
	
	private static class StraightMovement extends MovementPattern {
		@Override
		public Vec2D update(float delta, Vec2D currentPos) {
			this.time += delta;
			return new Vec2D(0,1);
		}
	}
	public static MovementPattern newStraight() {
		return new StraightMovement();
	}
	
	private static class WaveMovement extends MovementPattern {
		private float amplitude, frequency;
		
		public WaveMovement(float amplitude, float frequency) {
			this.amplitude = amplitude;
			this.frequency = frequency;
		}
		
		@Override
		public Vec2D update(float delta, Vec2D currentPos) {
			time += delta;
			double x = Math.sin(time * frequency) * amplitude;
			return new Vec2D((float)x, 1);
		}
	}
	public static MovementPattern newWave(float amplitude, float frequency) {
		return new WaveMovement(amplitude, frequency);
	}
	
	private static class SideToSideMovement extends MovementPattern {
		private Vec2D screenSize;
		private Vec2D guySize;
		private boolean goingRight = true;
		
		public SideToSideMovement(Vec2D screenSize, Vec2D guySize) {
			this.screenSize = screenSize;
			this.guySize = guySize;
		}
		
		@Override
		public Vec2D update(float delta, Vec2D currentPos) {
			time += delta;
			float x;
			if (goingRight) { x = 1; }
			else { x = -1; }
			
			if (currentPos.x + guySize.x >= screenSize.x && goingRight) {
				goingRight = false;
			} else if (currentPos.x <= 0 && !goingRight) {
				goingRight = true;
			}
			
			return new Vec2D(x, 0);
		}
	}
	public static MovementPattern newSideToSide(Vec2D screenSize, Vec2D guySize) {
		return new SideToSideMovement(screenSize, guySize);
	}
}
