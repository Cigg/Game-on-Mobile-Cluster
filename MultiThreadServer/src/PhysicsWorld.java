import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class PhysicsWorld {

	public static Hashtable<Integer, Body> bodies = new Hashtable<Integer, Body>(); 

	private World world;

	public void create(Vec2 gravity) {
		boolean doSleep = false;
		world = new World(gravity, doSleep);
		
		System.out.println("PhysicsWorld created");
	}
	
	public void addBall(float xPos, float yPos, float xVel, float yVel, int id, float density, float radius, float bounce, float friction) {
		// Create Shape with Properties
		CircleShape circleShape = new CircleShape();
		circleShape.m_radius = 0.24f;
		
		//MultiThreds.getPhysicsWorld().addBall(xPos, yPos, xVel, yVel,  id, 0.03f, 0.75f, 0.8f, 0.3f);
		bounce = 0;
		density = 1;
		friction = 0.1f;
		
		addItem(xPos, yPos, xVel, yVel, circleShape, bounce, id, density, friction);
	}
	
	private void addItem(float xPos, float yPos, float xVel, float yVel, Shape shape, float bounce, int id, float density, float friction) {
		// Create Dynamic Body
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(xPos, yPos);
		bodyDef.linearVelocity.set(xVel, yVel);
		bodyDef.userData = id;
		bodyDef.type = BodyType.DYNAMIC;
		Body body = world.createBody(bodyDef);
		body.setAngularVelocity(5); // la till
		bodies.put(id, body);
		
		// Assign shape to Body
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = bounce;
		body.createFixture(fixtureDef);
	}

	public void update(float deltaTime) {
		// Update Physics World
		world.step(deltaTime, 128, 128); // vilka v�rden b�r de 2 sista parametrarna ha?
	}

	public World getWorld() {
		return world;
	}
	
	public Vec2 getPositionFromId(int id) {
		return bodies.get(id).getPosition();
	}
	
	public Vec2 getVelocityFromId(int id) {
		return bodies.get(id).getLinearVelocity();
	}
	
	public float getAngularVelocityFromId(int id) {
		return bodies.get(id).getAngularVelocity();
	}
}