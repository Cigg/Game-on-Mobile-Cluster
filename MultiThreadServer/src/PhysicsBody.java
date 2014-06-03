package src;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class PhysicsBody {

	Body body;
	int id;

	PhysicsBody(int id, Body body) {
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