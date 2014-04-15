import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;	
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;



public class PhysicsWorld {

	public class Pair {
		
		Body body;
		int id;
		
		Pair(int id, Body body) {
			if(body == null) {
				System.out.println("Added NULL BODYYYY");
		}
			
			this.body = body;
			this.id = id;
		}
		
		public Vec2 getPosition() {
	
			return body.getPosition();
		}
		
		public Vec2 getLinearVelocity() {
			return body.getLinearVelocity();
		}
		
	}
	
	public volatile static Hashtable<Integer, Body> bodies = new Hashtable<Integer, Body>(); 

	public volatile static ArrayList<Pair> bodies2 = new ArrayList<Pair>();
	
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
	
	public void addTarget(float xPos, float yPos, int id, float bounce) {
		// Create Shape with Properties
		CircleShape circleShape = new CircleShape();
		
		
		circleShape.m_radius = 0.24f;
		
		//MultiThreds.getPhysicsWorld().addBall(xPos, yPos, xVel, yVel,  id, 0.03f, 0.75f, 0.8f, 0.3f);
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(xPos, yPos);
		bodyDef.type = BodyType.STATIC;
		Body body = null;
		
		while (body == null) {
			body = world.createBody(bodyDef);
			System.out.println("NULLL BODYYY LOOP");
		}
		
		PolygonShape polyDef = new PolygonShape();
		polyDef.m_vertices[0].set(0,-1);
        polyDef.m_vertices[1].set(1,1);
        polyDef.m_vertices[2].set(-1,1);
        polyDef.setAsBox(0.5f,0.5f);
		body.createFixture(polyDef, 1.0f);
		body.resetMassData();
	}
	
	private void addItem(float xPos, float yPos, float xVel, float yVel, Shape shape, float bounce, int id, float density, float friction) {
		// Create Dynamic Body
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(xPos, yPos);
		bodyDef.linearVelocity.set(xVel, yVel);
		bodyDef.userData = id;
		bodyDef.type = BodyType.DYNAMIC;
		
		Body body = null;
		while (body == null) {
			body = world.createBody(bodyDef);
			System.out.println("NULLL BODYYY LOOP");
		}
		
		//body.setAngularVelocity(5); // la till
		
		//bodies.put(id, body);
		
		bodies2.add(new Pair(id-1, body));
	
		// Assign shape to Body
		FixtureDef fixtureDef = new FixtureDef();	
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = bounce;
		while(true) {
		try{
			body.createFixture(fixtureDef);
			break;
		} catch(Exception e) {
			System.out.println("Erorror fixtureDef");
		}
		}
	}

	public void update(float deltaTime) {
		// Update Physics World
		world.step(deltaTime, 128, 128); // vilka värden bör de 2 sista parametrarna ha?
	}

	public World getWorld() {
		return world;
	}
	
	public Vec2 getPositionFromId(int id) {
		return bodies2.get(id-1).getPosition();
	
	}
	
	public Vec2 getVelocityFromId(int id) {
		return bodies2.get(id-1).getLinearVelocity();
	}
	
	public float getAngularVelocityFromId(int id) {
		return 0;// bodies.get(id).getAngularVelocity();
	}
}