import org.jbox2d.common.Vec2;

public class Ball {
	
	static final int MAX_LIFETIME = 15;
	static final float MAX_POSITION_X = (float) (100 / 2.5);
	static final float MAX_POSITION_Y = MAX_POSITION_X;
	
	float xPos, yPos;
	float xVel, yVel;
	float mass, radious, lifeTime;
	int id;
	boolean isMoved;

	float lx, ly;

	public Ball(int id, float xPos, float yPos, float xVel, float yVel) {

		this.id = id;
		this.xPos = xPos;
		this.yPos = yPos;
		this.xVel = xVel;
		this.yVel = yVel;
		this.lifeTime = 0;
		this.radious = 1;
		this.isMoved = false;

		lx = xPos;
		ly = yPos;

		System.out.println("NEW BALL: " + id);
		System.out.println("Added ballz: " + xPos + ", " + yPos + "    "
				+ xVel + ", " + yVel); // * Math.pow(10, 9) * 2.5

		MultiThreds.getPhysicsWorld().addBall(xPos, yPos, xVel, yVel, id,
				0.03f, 0.75f, 0.8f, 0.3f);

	}

	public boolean shouldUpdate() {
		return this.isMoved;
	}

	public void update(float deltaTime) {
		this.xPos += this.xVel * deltaTime;
		this.yPos += this.yVel * deltaTime;
		this.lifeTime += deltaTime;

		Vec2 position = MultiThreds.getPhysicsWorld().getPositionFromId(
				this.id);
		this.xPos = position.x;
		this.yPos = position.y;
		
		
		Vec2 velocity = MultiThreds.getPhysicsWorld().getVelocityFromId(this.id);

		if( Math.abs(lx - (position.x - velocity.x * deltaTime)) > 0.01 || 
			Math.abs(ly - (position.y - velocity.y * deltaTime)) > 0.01 ) {
			System.out.println("OOOOOOOOOOOOOOOOOOO___BALL_SHOUDL-.UPDATE___OOOOOOOOOOOOOOOOOOO");
			this.isMoved = true;
		} else {
			this.isMoved = false;
		}

		lx = xPos;
		ly = yPos;
	}

	public boolean isDead() {
		if ((this.lifeTime * Math.pow(10, -9)) > MAX_LIFETIME
				|| outOfBounds()) {
			return true;
		}
		return false;
	}

	public boolean outOfBounds() {
		if (Math.abs(this.xPos) < MAX_POSITION_X
				&& Math.abs(this.yPos) < MAX_POSITION_Y) {
			return false;
		}
		return true;
	}

	public void printInfo() {
		System.out.println("Ball: " + this.xPos + ", " + this.yPos + "    "
				+ this.xVel * Math.pow(10, 9) * 2.5 + ", " + this.yVel
				* Math.pow(10, 9) * 2.5);
	}

	public float getXPos() {
		return this.xPos;
	}

	public float getYPos() {
		return this.yPos;
	}

	public float getXVel() {
		return this.xVel;
	}

	public float getYVel() {
		return this.yVel;
	}

	public void setXVel(float xVel) {
		this.xVel = xVel;
	}

	public void setYVel(float yVel) {
		this.yVel = yVel;
	}
}