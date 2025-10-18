package game.utils;

public class Vec2D {
	public float x, y;
	
	
	public Vec2D() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2D(Vec2D other) {
		this.x = other.x;
		this.y = other.y;
	}
	
	
	public Vec2D add(float value) {
		return new Vec2D(this.x + value, this.y + value);
	}
	public Vec2D add(Vec2D b) {
		return new Vec2D(this.x + b.x, this.y + b.y);
	}
	
	public void addIp(Vec2D b) {
		this.x += b.x;
		this.y += b.y;
	}
	public void addIp(float value) {
		this.x += value;
		this.y += value;
	}
	
	public Vec2D multiply(float value) {
		return new Vec2D(this.x * value, this.y * value);
	}
	
	public void multiplyIp(float value) {
		this.x *= value;
		this.y *= value;
	}
	
	public float distance(Vec2D b) {
		float deltaX = b.x - this.x;
		float deltaY = b.y - this.y;
		
		double d = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
		return (float) d;
	}
	
	public float magnitude() {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public float angle() {
		return (float) Math.atan2(this.y, this.x);
	}
	
	public Vec2D normalize() {
		float magnitude = this.magnitude();
		Vec2D normalized = new Vec2D();
		
		normalized.x = this.x / magnitude;
		normalized.y = this.y / magnitude;
		
		return normalized;
	}
	public void normalizeIP() {
		double magnitude = this.magnitude();
		
		this.x = (int) (((double) this.x) / magnitude);
		this.y = (int) (((double) this.y) / magnitude);
	}
}
