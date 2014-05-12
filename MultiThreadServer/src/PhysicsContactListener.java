package src;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

public class PhysicsContactListener implements ContactListener{
	
	public void beginContact(Contact c) {
		// TODO Auto-generated method stub
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();
		
		if(fa.getUserData() != null && fa.getUserData().equals("Pivot")) {			
			System.out.println("-------------------Touched!-------------------");
			if(fb.getUserData() instanceof Integer) {
				int id = (Integer) fb.getUserData();
				ClientThread.removeAndScore(id);
				PhysicsWorld.removeBall(id);
			}
		}
		
		if(fb.getUserData() != null && fb.getUserData().equals("Pivot")) {
			System.out.println("-------------------Touched!-------------------");
			if(fa.getUserData() instanceof Integer) {
				int id = (Integer) fa.getUserData();
				ClientThread.removeAndScore(id);
				PhysicsWorld.removeBall(id);
			}
		}
	}

	public void endContact(Contact c) {
		// TODO Auto-generated method stub
		
	}

	public void postSolve(Contact c, ContactImpulse i) {
		// TODO Auto-generated method stub
		
	}

	public void preSolve(Contact c, Manifold m) {
		// TODO Auto-generated method stub
		
	}

}
