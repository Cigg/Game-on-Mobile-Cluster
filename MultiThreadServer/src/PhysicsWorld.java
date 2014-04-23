import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import java.util.HashMap;
//import java.util.Hashtable;
//import java.util.Map;

public class PhysicsWorld {
	//public volatile static Hashtable<Integer, Body> bodies = new Hashtable<Integer, Body>();		

private class Vertex {
	float x;
	float y;
	float normalX;
	float normalY;
	
		Vertex(float x, float y){
			this.x = (float) x;
			this.y = (float) y;
		}
	}

	
	private class Polygon {
		public ArrayList<Vertex> vertecies = new ArrayList<Vertex>();
	}
	public ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	private float originX;
	private float originY;
	
	public volatile static ArrayList<PhysicsBody> bodies = new ArrayList<PhysicsBody>();

	private World world;
	

	public void create(Vec2 gravity) {
		boolean doSleep = false;
		world = new World(gravity, doSleep);		
		loadVertices();
		calculateNormals();
		System.out.println("PhysicsWorld created");
	}

	public void addBall(float xPos, float yPos, float xVel, float yVel, int id,
			float density, float radius, float bounce, float friction) {
		// Create Shape with Properties
		CircleShape circleShape = new CircleShape();

		// Two centimeters
		circleShape.m_radius = 0.01f*39.37f;

		// MultiThreds.getPhysicsWorld().addBall(xPos, yPos, xVel, yVel, id,
		// 0.03f, 0.75f, 0.8f, 0.3f);
		bounce = 0.9f;
		density = 1.0f;
		friction = 0.1f;

		addItem(xPos, yPos, xVel, yVel, circleShape, bounce, id, density,
				friction);
	}

	public RevoluteJoint addTarget(float xPos, float yPos, float bounce) {
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(xPos, yPos);
		
		// TODO: make dynamic bodytype and add pivot
		bodyDef.type = BodyType.DYNAMIC;
		Body body = null;

		while (body == null) {
			body = world.createBody(bodyDef);
			System.out.println("NULLL BODYYY LOOP");
		}

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0;
		fixtureDef.friction = 1; 
		
		System.out.println("ADDING TARGET");
		System.out.println("Target pos: " + xPos + " " + yPos);

		PolygonShape polyDef = new PolygonShape();
		CircleShape pivot = new CircleShape();
		pivot.m_radius = 0.1f;
		
		fixtureDef.shape = pivot;
		//loadVertices();
		//calculateNormals();
		
		//---------- DEFENITION OF VERTECIES FROM FILE ---------
		for(int i=0; i < polygons.size(); i++){
			Polygon polygon = polygons.get(i);
			polyDef.m_vertexCount = polygon.vertecies.size();
			for(int j=0; j < polygon.vertecies.size(); j++) {
				Vertex vertex = polygon.vertecies.get(j);
				polyDef.m_vertices[j].set((originX) + 1.0f*vertex.x, (originY) + 1.0f*vertex.y);
				polyDef.m_normals[j].set(vertex.normalX,vertex.normalY);
			}
			body.createFixture(polyDef,1.0f);
		}
		bodyDef.type = BodyType.STATIC;
		bodyDef.position.set(xPos,yPos);
		Body body2 = null;
		while (body2 == null) {
			body2 = world.createBody(bodyDef);
			System.out.println("NULLL BODYYY LOOP");
		}
		body2.createFixture(fixtureDef);
				
		RevoluteJointDef joint = new RevoluteJointDef();
		joint.bodyA = body;
		joint.bodyB = body2;
		joint.collideConnected = true;
		joint.localAnchorA.set(0,0);
		joint.localAnchorB.set(originX,originY);
		//joint.initialize(body2, body, new Vec2(originX,originY));
		RevoluteJoint the_joint = (RevoluteJoint) world.createJoint(joint);
		return the_joint;
		//the_joint.getJointAngle();
				
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

		bodies.add(new PhysicsBody(id - 1, body));

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
		return bodies.get(id - 1).getPosition();

	}

	public Vec2 getVelocityFromId(int id) {
		return bodies.get(id - 1).getLinearVelocity();
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

	private void loadVertices(){
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(new FileReader("src\\frog.json"));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray jsonArray = (JSONArray) jsonObject.get("rigidBodies");
			jsonObject = (JSONObject) jsonArray.get(0);
			
			JSONObject origin = (JSONObject) jsonObject.get("origin");
			originX = convertToFloat(origin.get("x"));
			originY = convertToFloat(origin.get("y"));
			
			jsonArray = (JSONArray) jsonObject.get("polygons");
			
			Iterator i = jsonArray.iterator();
			while(i.hasNext()) {
				JSONArray polygonArray = (JSONArray) i.next();
				Polygon polygon = new Polygon();
				Iterator j = polygonArray.iterator();
				while(j.hasNext()) {
					JSONObject pair = (JSONObject) j.next();
					Object x =  pair.get("x");
					Object y =  pair.get("y");
					System.out.println("x = " + x + ", y = " + y);
					
					polygon.vertecies.add(new Vertex(convertToFloat(x)-originX,convertToFloat(y)-originY));
				}
				System.out.println();
				polygons.add(polygon);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void calculateNormals(){
		System.out.println(polygons.size());
		if(!polygons.isEmpty()){
			for(int j=0; j < polygons.size(); j++){
				Vertex vertex1;
				Vertex vertex2;
				for(int i=0; i < polygons.get(j).vertecies.size(); i++){
					if(i+1 < polygons.get(j).vertecies.size()) {
						vertex1 = polygons.get(j).vertecies.get(i);
						vertex2 = polygons.get(j).vertecies.get(i+1);
					} else {
						vertex1 = polygons.get(j).vertecies.get(i);
						vertex2 = polygons.get(j).vertecies.get(0);
					}
					
					polygons.get(j).vertecies.get(i).normalX = calculateNormalX(vertex1.x,vertex1.y,vertex2.x,vertex2.y);
					polygons.get(j).vertecies.get(i).normalY = calculateNormalY(vertex1.x,vertex1.y,vertex2.x,vertex2.y);
					//vertecies.get(i).normalX = calculateNormalX(vertex1.x,vertex1.y,vertex2.x,vertex2.y);
					//vertecies.get(i).normalY = calculateNormalX(vertex1.x,vertex1.y,vertex2.x,vertex2.y);
					System.out.println("Normalx = " + polygons.get(j).vertecies.get(i).normalX + ", Normaly = " + polygons.get(j).vertecies.get(i).normalY);
				}
			}
		}else{
			System.out.println("No polygons");
		}
	}
	
	private float convertToFloat(Object x){
		if(x.getClass() == Double.class){
			return ((Double)x).floatValue();
		}else if (x.getClass() == Long.class) {
			return ((Long)x).floatValue();
		} else {
			return (float) x;
		}
	}
	
	public void drawDebug(){
		world.drawDebugData();
	}
	
}