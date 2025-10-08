public class Vec2D {
	public int x, y;
	
	public Vec2D(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Vec2D(Vec2D other) {
		this.x = other.x;
		this.y = other.y;
	}
	
	public double distance(Vec2D b) {
		int deltaX = b.x - this.x;
		int deltaY = b.y - this.y;
		
		return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
	}
}
