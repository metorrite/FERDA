package com.avl;

import java.util.HashMap;
import java.util.Map;

import com.avl.constants.Angle;
import com.avl.constants.Compartment;
import com.avl.constants.Constants;
import com.avl.constants.PlaneType;
import com.avl.constants.ShipSpeed;
import com.avl.constants.ShipStatus;

/*
 * 
 * PAGE 107 7A - Neutral Stern Plane Angles!
 */


public class Ship {

	public final Map<PlaneType, Plane> planeMap = new HashMap<>();
	public ShipStatus status = ShipStatus.BALANCED;
	public Compartment compartment;
	public ShipSpeed speed = ShipSpeed.ONE_THIRD;
	
	public ShipSpeed getSpeed() {
		return speed;
	}

	public void setSpeed(ShipSpeed speed) {
		this.speed = speed;
	}

	public double sternCurrent, shipCurrent, bowCurrent;
	public double totalWeight;
	public boolean needsCorrection = true;
	public double bowCorrectionValue;
	public int neutralSternAngle;
	
	public boolean isNeedsCorrection() {
		return needsCorrection;
	}

	public void setNeedsCorrection(boolean needsCorrection) {
		this.needsCorrection = needsCorrection;
	}

	public double getBowCorrectionValue() {
		return bowCorrectionValue;
	}

	public void setBowCorrectionValue(double bowCorrectionValue) {
		this.bowCorrectionValue = bowCorrectionValue;
	}

	public int getNeutralSternAngle() {
		return neutralSternAngle;
	}

	public void setNeutralSternAngle(int neutralSternAngle) {
		this.neutralSternAngle = neutralSternAngle;
	}

	public ShipStatus getStatus() {
		return status;
	}

	public void setStatus(ShipStatus status) {
		this.status = status;
	}
	
	public Compartment getCompartment() {
		return compartment;
	}

	public void setCompartment(Compartment compartment) {
		this.compartment = compartment;
	}

	public Plane getPlane(PlaneType type) {
		return planeMap.get(type);
	}
	
	public double getSternCurrent() {
		return sternCurrent;
	}

	public void setSternCurrent(double sternCurrent) {
		this.sternCurrent = sternCurrent;
	}

	public double getShipCurrent() {
		return shipCurrent;
	}

	public void setShipCurrent(double shipCurrent) {
		this.shipCurrent = shipCurrent;
	}

	public double getBowCurrent() {
		return bowCurrent;
	}

	public void setBowCurrent(double bowCurrent) {
		this.bowCurrent = bowCurrent;
	}

	public double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public Map<PlaneType, Plane> getPlaneMap() {
		return planeMap;
	}

	public double calculatePlaneWeight(PlaneType type) {
		return calculatePlaneWeight(type, true);
	}
	
	public double getNeutralSternPlaneAngle() {
		switch(getStatus()) {
		case HEAVY:
			if(speed.equals(ShipSpeed.ONE_THIRD))
				return Math.abs(totalWeight) <= 19999 ? -2 : -3;
			else if(speed.equals(ShipSpeed.TWO_THIRDS))
				return Math.abs(totalWeight) <= 19999 ? -2 : Math.abs(totalWeight) >= 20000 && Math.abs(totalWeight) <= 29999 ? -3 : 
					Math.abs(totalWeight) >= 30000 && Math.abs(totalWeight) <= 49999 ? -3.5 : -4.5;
			else
				return Math.abs(totalWeight) <= 19999 ? -1.5 : Math.abs(totalWeight) >= 20000 && Math.abs(totalWeight) <= 29999 ? -2 : 
					Math.abs(totalWeight) >= 30000 && Math.abs(totalWeight) <= 49999 ? -2.5 : -3.5;
		case LIGHT:
			if(speed.equals(ShipSpeed.ONE_THIRD))
				return Math.abs(totalWeight) <= 19999 ? 0 : 1;
			else if(speed.equals(ShipSpeed.TWO_THIRDS))
				return Math.abs(totalWeight) <= 19999 ? 0 : 
					Math.abs(totalWeight) >= 20000 && Math.abs(totalWeight) <= 29999 ? 1 : 
						Math.abs(totalWeight) >= 30000 && Math.abs(totalWeight) <= 49999 ? 1.5 : 3;
			else
				return Math.abs(totalWeight) <= 19999 ? -.5 : 
					Math.abs(totalWeight) >= 20000 && Math.abs(totalWeight) <= 29999 ? 0 : 
						Math.abs(totalWeight) >= 30000 && Math.abs(totalWeight) <= 49999 ? .5 : 1.5;
		default:
			return -1;
		}
	}
	
	private ShipStatus getPlaneWeightStatus(PlaneType type) {
		if(type.equals(PlaneType.STERN))
			return getPlane(type).getAngle().equals(Angle.DOWN) ? ShipStatus.HEAVY : getPlane(type).getAngle().equals(Angle.NEUTRAL) ?
					ShipStatus.BALANCED : ShipStatus.LIGHT;
		else
			return getPlane(type).getAngle().equals(Angle.UP) ? ShipStatus.HEAVY : getPlane(type).getAngle().equals(Angle.DOWN) ?
					ShipStatus.LIGHT : ShipStatus.BALANCED;
	}
	
	public double calculatePlaneWeight(PlaneType type, boolean correction) {
		double value = 0;
		getPlane(type).setForceStatus(getPlaneWeightStatus(type));
		if(type.equals(PlaneType.STERN))
			value = getPlane(type).getWeightStatus().equals(ShipStatus.HEAVY) ? Math.abs((sternCurrent - Constants.STERN_STABLE_ANGLE) * Constants.STERN) 
					: -1 * (sternCurrent - Constants.STERN_STABLE_ANGLE) * Constants.STERN;
		else if(type.equals(PlaneType.SHIP))
			value = getPlane(type).getWeightStatus().equals(ShipStatus.HEAVY) ? Math.abs((shipCurrent - Constants.SHIP_STABLE_ANGLE) * Constants.SHIP) 
					: (shipCurrent - Constants.SHIP_STABLE_ANGLE) * Constants.SHIP;
		else
			value = getPlane(type).getWeightStatus().equals(ShipStatus.HEAVY) ? Math.abs(bowCurrent * Constants.BOW)
					: (bowCurrent * Constants.BOW);
		return value;
	}
	
	public void calculateBowCorrectionFactor() {
		if(getStatus().equals(ShipStatus.BALANCED) || bowCurrent == 0)
			needsCorrection = false;
		else
			needsCorrection = true;
			double correctionFactor = .4 * bowCurrent;
			double correctedSternPlaneAngle = sternCurrent + correctionFactor;
			if(correctedSternPlaneAngle == getNeutralSternPlaneAngle() || getCompartment().equals(Compartment.BALANCED)) {
				setCompartment(Compartment.BALANCED);
				setBowCorrectionValue(0);
			} else {
				setBowCorrectionValue(Math.abs(Constants.STERN * (Math.abs(getNeutralSternPlaneAngle() - (needsCorrection ? correctedSternPlaneAngle : sternCurrent)))));
			}
	}
	
	public void calculateTotalWeight() {
		totalWeight = 0;
		planeMap.values().forEach(plane -> totalWeight += plane.getWeight());
		setStatus(totalWeight > 0 ? ShipStatus.HEAVY : totalWeight < 0 ? ShipStatus.LIGHT : ShipStatus.BALANCED);
		Plane bow = getPlane(PlaneType.BOW);
		Plane stern = getPlane(PlaneType.STERN);
		
		if(bow.getWeight() > stern.getWeight()) {
			setCompartment(Compartment.FORWARD);
		} else if(bow.getWeight() < stern.getWeight()) {
			setCompartment(Compartment.AFT);
		} else {
			setCompartment(Compartment.BALANCED);
		}
	}
}
