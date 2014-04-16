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
			if (body == null) {
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

	public void addBall(float xPos, float yPos, float xVel, float yVel, int id,
			float density, float radius, float bounce, float friction) {
		// Create Shape with Properties
		CircleShape circleShape = new CircleShape();

		circleShape.m_radius = 0.24f;

		// MultiThreds.getPhysicsWorld().addBall(xPos, yPos, xVel, yVel, id,
		// 0.03f, 0.75f, 0.8f, 0.3f);
		bounce = 0.9f;
		density = 1.0f;
		friction = 0.1f;

		addItem(xPos, yPos, xVel, yVel, circleShape, bounce, id, density,
				friction);
	}

	public void addTarget(float xPos, float yPos, float bounce) {
		// Create Shape with Properties
		CircleShape circleShape = new CircleShape();

		circleShape.m_radius = 0.24f;

		// MultiThreds.getPhysicsWorld().addBall(xPos, yPos, xVel, yVel, id,
		// 0.03f, 0.75f, 0.8f, 0.3f);
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(xPos, yPos);
		bodyDef.type = BodyType.STATIC;
		Body body = null;

		while (body == null) {
			body = world.createBody(bodyDef);
			System.out.println("NULLL BODYYY LOOP");
		}

		System.out.println("ADDING TARGET");
		System.out.println("Target pos: " + xPos + " " + yPos);

		PolygonShape polyDef = new PolygonShape();
		// polyDef.m_vertices[0].set(-3,0);
		// polyDef.m_vertices[1].set(-1,3);
		// polyDef.m_vertices[2].set(4,4);

		// Define vertices
		float vert1X = -0.015f;
		float vert1Y = 0.0f;
		float vert2X = 0.01f;
		float vert2Y = -0.015f;
		float vert3X = 0.01f;
		float vert3Y = 0.015f;
		polyDef.m_vertexCount = 3;
		polyDef.m_vertices[0].set(vert1X * 39.37f, vert1Y * 39.37f);
		polyDef.m_vertices[1].set(vert2X * 39.37f, vert2Y * 39.37f);
		polyDef.m_vertices[2].set(vert3X * 39.37f, vert3Y * 39.37f);
		polyDef.m_normals[0].set(calculateNormalX(vert1X, vert1Y, vert2X, vert2Y), calculateNormalY(vert1X, vert1Y, vert2X, vert2Y));
		polyDef.m_normals[1].set(calculateNormalX(vert2X, vert2Y, vert3X, vert3Y), calculateNormalY(vert2X, vert2Y, vert3X, vert3Y));
		polyDef.m_normals[2].set(calculateNormalX(vert3X, vert3Y, vert1X, vert1Y), calculateNormalY(vert3X, vert3Y, vert1X, vert1Y));
		body.createFixture(polyDef, 1.0f);

		vert1X = -0.015f;
		vert1Y = 0.0f;
		vert2X = 0.02f;
		vert2Y = 0.02f;
		vert3X = -0.005f;
		vert3Y = 0.015f;
		polyDef.m_vertexCount = 3;
		polyDef.m_vertices[0].set(vert1X * 39.37f, vert1Y * 39.37f);
		polyDef.m_vertices[1].set(vert2X * 39.37f, vert2Y * 39.37f);
		polyDef.m_vertices[2].set(vert3X * 39.37f, vert3Y * 39.37f);
		polyDef.m_normals[0].set(calculateNormalX(vert1X, vert1Y, vert2X, vert2Y), calculateNormalY(vert1X, vert1Y, vert2X, vert2Y));
		polyDef.m_normals[1].set(calculateNormalX(vert2X, vert2Y, vert3X, vert3Y), calculateNormalY(vert2X, vert2Y, vert3X, vert3Y));
		polyDef.m_normals[2].set(calculateNormalX(vert3X, vert3Y, vert1X, vert1Y), calculateNormalY(vert3X, vert3Y, vert1X, vert1Y));
		body.createFixture(polyDef, 1.0f);
		
		vert1X = -0.015f;
		vert1Y = 0.0f;
		vert2X = -0.005f;
		vert2Y = -0.015f;
		vert3X = 0.02f;
		vert3Y = -0.02f;
		polyDef.m_vertexCount = 3;
		polyDef.m_vertices[0].set(vert1X * 39.37f, vert1Y * 39.37f);
		polyDef.m_vertices[1].set(vert2X * 39.37f, vert2Y * 39.37f);
		polyDef.m_vertices[2].set(vert3X * 39.37f, vert3Y * 39.37f);
		polyDef.m_normals[0].set(calculateNormalX(vert1X, vert1Y, vert2X, vert2Y), calculateNormalY(vert1X, vert1Y, vert2X, vert2Y));
		polyDef.m_normals[1].set(calculateNormalX(vert2X, vert2Y, vert3X, vert3Y), calculateNormalY(vert2X, vert2Y, vert3X, vert3Y));
		polyDef.m_normals[2].set(calculateNormalX(vert3X, vert3Y, vert1X, vert1Y), calculateNormalY(vert3X, vert3Y, vert1X, vert1Y));
		body.createFixture(polyDef, 1.0f);

		// polyDef.friction = 0.3;
		// polyDef.restitution = 0.1;
		// polyDef.m_vertices[5].set(-4,-4);
		// polyDef.m_vertices[6].set(-1,-3);
		// polyDef.m_vertices[7].set(-3,0);
		// Fyra centimeter kvadrat
		// polyDef.setAsBox(0.02f*39.37f,0.02f*39.37f);
		// FixtureDef fixtureDef = new FixtureDef();
		// fixtureDef.shape = polyDef;
		// fixtureDef.density = 1.0f;
		// fixtureDef.friction = 0.1f;
		// fixtureDef.restitution = 0.9f;
		// body.createFixture(fixtureDef);
		// body.resetMassData();
	}

	private void addItem(float xPos, float yPos, float xVel, float yVel,
			Shape shape, float bounce, int id, float density, float friction) {
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

		// body.setAngularVelocity(5); // la till

		// bodies.put(id, body);

		bodies2.add(new Pair(id - 1, body));

		// Assign shape to Body
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = bounce;
		while (true) {
			try {
				body.createFixture(fixtureDef);
				break;
			} catch (Exception e) {
				System.out.println("Erorror fixtureDef");
			}
		}

		// body.resetMassData();
	}

	public void update(float deltaTime) {
		// Update Physics World
		world.step(deltaTime, 128, 128); // vilka värden bör de 2 sista
											// parametrarna ha?
	}

	public World getWorld() {
		return world;
	}

	public Vec2 getPositionFromId(int id) {
		return bodies2.get(id - 1).getPosition();

	}

	public Vec2 getVelocityFromId(int id) {
		return bodies2.get(id - 1).getLinearVelocity();
	}

	public float getAngularVelocityFromId(int id) {
		return 0;// bodies.get(id).getAngularVelocity();
	}

	private float calculateNormalX(float x1, float y1, float x2, float y2) {
		float dy = y2 - y1;
		float dx = x2 - x1;
		return (float) (dy / (Math.sqrt(dx * dx + dy * dy)));
	}

	private float calculateNormalY(float x1, float y1, float x2, float y2) {
		float dy = y2 - y1;
		float dx = x2 - x1;
		return (float) (-dx / (Math.sqrt(dx * dx + dy * dy)));
	}
}