package src;

 class OldBall {
		public int threadId;
		public int ballId;
		public float posX;
		public float posY;
		public float speedX;
		public float speedY;
		
		public OldBall(int threadId, int ballId, float posX, float posY, float speedX, float speedY){
			this.threadId = threadId;
			this.ballId = ballId;
			this.posX = posX;
			this.posY = posY;
			this.speedX = speedX;
			this.speedY = speedY;
		}
		
		public String toString(){
			return ballId + " " + posX + " " + posY + " " + speedX + " " + speedY; 
		}
		
		public void updatePos() {
			posX += speedX;
			posY += speedY;
		}
		
		public boolean isOnDevice(GlobalCoords globalCoords){
			if(posX > globalCoords.minX && posX < globalCoords.maxX && posY > globalCoords.minY && posY < globalCoords.maxY) {
				return true;
			}else {
				return false;
			}
		}
	}//END OF BALL