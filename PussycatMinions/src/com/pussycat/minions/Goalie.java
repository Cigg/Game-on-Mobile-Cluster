package com.pussycat.minions;

import android.R.bool;

public class Goalie {
	
	private float direction;
	private float radius;
	private float position;
	private float goalOpeningWidth; //the distance between the two corners that creates the opening
	private bool goal;
	
	private void generateFence() {
		
	}
	
	public static void addGoalie() {
		// TODO Auto-generated method stub
		
	}


	public float getDirection() {
		return direction;
	}


	public void setDirection(float direction) {
		this.direction = direction;
	}


	public float getPosition() {
		return position;
	}


	public void setPosition(float position) {
		this.position = position;
	}


	public float getRadius() {
		return radius;
	}


	public void setRadius(float radius) {
		this.radius = radius;
	}


	public bool getGoal() {
		return goal;
	}


	public void setGoal(bool goal) {
		this.goal = goal;
	}


	public float getGoalOpeningWidth() {
		return goalOpeningWidth;
	}


	public void setGoalOpeningWidth(float goalOpeningWidth) {
		this.goalOpeningWidth = goalOpeningWidth;
	}
	
}
