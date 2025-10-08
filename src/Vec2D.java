public class Vec2D {
	public int x, y;
	
	
	public Vec2D() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2D(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2D(Vec2D other) {
		this.x = other.x;
		this.y = other.y;
	}
	
	
	public Vec2D add(int value) {
		return new Vec2D(this.x + value, this.y + value);
	}
	public Vec2D add(Vec2D b) {
		return new Vec2D(this.x + b.x, this.y + b.y);
	}
	
	public void addIp(Vec2D b) {
		this.x += b.x;
		this.y += b.y;
	}
	public void addIp(int value) {
		this.x += value;
		this.y += value;
	}
	
	public Vec2D multiply(int value) {
		return new Vec2D(this.x * value, this.y * value);
	}
	
	public void multiplyIp(int value) {
		this.x *= value;
		this.y *= value;
	}
	
	public double distance(Vec2D b) {
		int deltaX = b.x - this.x;
		int deltaY = b.y - this.y;
		
		return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
	}
	
	public double magnitude() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public Vec2D normalize() {
		double magnitude = this.magnitude();
		Vec2D normalized = new Vec2D();
		
		normalized.x = (int) (((double) this.x) / magnitude);
		normalized.y = (int) (((double) this.y) / magnitude);
		
		return normalized;
	}
	public void normalizeIP() {
		double magnitude = this.magnitude();
		
		this.x = (int) (((double) this.x) / magnitude);
		this.y = (int) (((double) this.y) / magnitude);
	}
}
