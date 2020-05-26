package com.avl;

import com.avl.constants.Angle;
import com.avl.constants.PlaneType;
import com.avl.constants.WeightStatus;

public class Plane {

	private PlaneType type;
	private Angle angle;
	private double weight = 0;
	private WeightStatus weightStatus;
	
	public WeightStatus getWeightStatus() {
		return weightStatus;
	}

	public void setForceStatus(WeightStatus weightStatus) {
		this.weightStatus = weightStatus;
	}

	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Plane(PlaneType type, Angle angle) {
		this.type = type;
		this.angle = angle;
	}

	public Angle getAngle() {
		return angle;
	}

	public void setAngle(Angle angle) {
		this.angle = angle;
	}

	public PlaneType getType() {
		return type;
	}
	
	
}
