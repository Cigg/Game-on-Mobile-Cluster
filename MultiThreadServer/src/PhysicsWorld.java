import java.util.HashMap;
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

	private Map<Integer, Body> bodies = new HashMap<Integer, Body>();

	private World world;

	public void create(Vec2 gravity) {
		boolean doSleep = false;
		world = new World(gravity, doSleep);
		
		System.out.println("PhysicsWorld created");
	}
	
	public void addBall(float xPos, float yPos, float xVel, float yVel, int id, float density, float radius, float bounce, float friction) {
		// Create Shape with Properties
		CircleShape circleShape = new CircleShape();
		circleShape.m_radius = radius;
		
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
		world.step(deltaTime, 10, 10); // vilka värden bör de 2 sista parametrarna ha?
	}

	public World getWorld() {
		return world;
	}
	
	public Body getBodyFromId(int id) {
		return bodies.get(id);
	}
}