package com.avl;

import com.avl.constants.Angle;
import com.avl.constants.PlaneType;
import com.avl.constants.ShipStatus;

public class Plane {

	private PlaneType type;
	private Angle angle;
	private double weight = 0;
	private ShipStatus weightStatus;
	
	public ShipStatus getWeightStatus() {
		return weightStatus;
	}

	public void setForceStatus(ShipStatus weightStatus) {
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
