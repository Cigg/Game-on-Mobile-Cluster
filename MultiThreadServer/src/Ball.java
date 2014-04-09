import org.jbox2d.common.Vec2;

	public class Ball {
				
		float xPos, yPos;
		float xVel, yVel;
		float  mass, radious, lifeTime;
		int id;
		
		public Ball(int id,float xPos, float yPos, float xVel, float yVel) {
			MultiThreds.incrementBallCount();
			this.id = id++;
			this.xPos = xPos;
			this.yPos = yPos;
			this.xVel = xVel;
			this.yVel = yVel;
			this.lifeTime = 0;
			
			// old
			// addBall(float x, float y, Object data, float density, float radius, float bounce, float friction)
			
			MultiThreds.physicsWorld.addBall(xPos, yPos, xVel, yVel,  this.id, 0.03f, 0.2f, 0.8f, 0.3f);
			
			System.out.println("Added ballz: " + xPos + ", " + yPos + "    " + xVel + ", " + yVel); // * Math.pow(10, 9) * 2.5
		}
		
		public void update(float deltaTime) {
			// Get pos from PhysicsWorld. Physics handles position and velocity
			Vec2 position = MultiThreds.physicsWorld.getPositionFromId(id);
			this.xPos = position.x;
			this.yPos = position.y;
			
			// Old way
			//this.xPos += this.xVel * deltaTime;
			//this.yPos += this.yVel * deltaTime;
			
			this.lifeTime += deltaTime;
			//System.out.println("Life: " + this.lifeTime * Math.pow(10, -9));
		}
		
		public boolean isDead() {
			if((this.lifeTime * Math.pow(10, -9)) > clientThread.MAX_LIFETIME || outOfBounds()) {
				return true;
			}
			return false;
		}
		
		public boolean outOfBounds() {
			if(Math.abs(this.xPos) < clientThread.MAX_POSITION_X &&  Math.abs(this.yPos) < clientThread.MAX_POSITION_Y) {
				return false;
			}
			return true;
		}
		
		
		public void printInfo() {
			System.out.println("Ball: " + this.xPos + ", " + this.yPos + "    " + this.xVel* Math.pow(10, 9) * 2.5 + ", " + this.yVel* Math.pow(10, 9) * 2.5);
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
	}