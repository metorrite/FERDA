package com.avl;

import java.util.HashMap;
import java.util.Map;

import com.avl.constants.Angle;
import com.avl.constants.Compartment;
import com.avl.constants.Constants;
import com.avl.constants.PlaneType;
import com.avl.constants.ShipSpeed;
import com.avl.constants.WeightStatus;

/*
 * 
 * PAGE 107 7A - Neutral Stern Plane Angles!
 */


public class Ship {

	private final Map<PlaneType, Plane> planeMap = new HashMap<>();
	private WeightStatus shipWeightStatus = WeightStatus.BALANCED;
	private Compartment compartment;
	private ShipSpeed speed = ShipSpeed.ONE_THIRD;

	private double sternCurrent, shipCurrent, bowCurrent;
	private double totalWeight = 0;
	private boolean needsCorrection = true;
	private double bowCorrectionValue;
	
	private ShipSpeed getSpeed() {
		return speed;
	}

	public void setSpeed(ShipSpeed speed) {
		this.speed = speed;
	}
	
	public boolean isNeedsCorrection() {
		return needsCorrection;
	}

	private void setNeedsCorrection(boolean needsCorrection) {
		this.needsCorrection = needsCorrection;
	}

	public double getBowCorrectionValue() {
		return bowCorrectionValue;
	}

	private void setBowCorrectionValue(double bowCorrectionValue) {
		this.bowCorrectionValue = bowCorrectionValue;
	}

	public WeightStatus getShipWeightStatus() {
		return shipWeightStatus;
	}

	private void setShipWeightStatus(WeightStatus status) {
		this.shipWeightStatus = status;
	}
	
	public Compartment getCompartment() {
		return compartment;
	}

	private void setCompartment(Compartment compartment) {
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

	private void setTotalWeight(double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public Map<PlaneType, Plane> getPlaneMap() {
		return planeMap;
	}
	
	/**
	 * Determines the neutral angle of the stern planes based on total weight and ships speed.
	 * 
	 * @return
	 */
	private double getNeutralSternPlaneAngle() {
		switch(getShipWeightStatus()) {
		case HEAVY:
			if(getSpeed().equals(ShipSpeed.ONE_THIRD))
				return Math.abs(getTotalWeight()) <= 19999 ? -2 : -3;
			else if(getSpeed().equals(ShipSpeed.TWO_THIRDS))
				return Math.abs(getTotalWeight()) <= 19999 ? -2 : Math.abs(getTotalWeight()) >= 20000 && Math.abs(getTotalWeight()) <= 29999 ? -3 : 
					Math.abs(getTotalWeight()) >= 30000 && Math.abs(getTotalWeight()) <= 49999 ? -3.5 : -4.5;
			else
				return Math.abs(getTotalWeight()) <= 19999 ? -1.5 : Math.abs(getTotalWeight()) >= 20000 && Math.abs(getTotalWeight()) <= 29999 ? -2 : 
					Math.abs(getTotalWeight()) >= 30000 && Math.abs(getTotalWeight()) <= 49999 ? -2.5 : -3.5;
		case LIGHT:
			if(getSpeed().equals(ShipSpeed.ONE_THIRD))
				return Math.abs(getTotalWeight()) <= 19999 ? 0 : 1;
			else if(getSpeed().equals(ShipSpeed.TWO_THIRDS))
				return Math.abs(getTotalWeight()) <= 19999 ? 0 : 
					Math.abs(getTotalWeight()) >= 20000 && Math.abs(getTotalWeight()) <= 29999 ? 1 : 
						Math.abs(getTotalWeight()) >= 30000 && Math.abs(getTotalWeight()) <= 49999 ? 1.5 : 3;
			else
				return Math.abs(getTotalWeight()) <= 19999 ? -.5 : 
					Math.abs(getTotalWeight()) >= 20000 && Math.abs(getTotalWeight()) <= 29999 ? 0 : 
						Math.abs(getTotalWeight()) >= 30000 && Math.abs(getTotalWeight()) <= 49999 ? .5 : 1.5;
		default:
			return -1;
		}
	}
	
	/**
	 * Determines the status of the individual weight status for a plane type.
	 * 
	 * @param type The type of plane to determine the weight status of
	 * @return the status of the weight of the plane
	 */
	private WeightStatus getPlaneWeightStatus(PlaneType type) {
		if(type.equals(PlaneType.STERN))
			return getPlane(type).getAngle().equals(Angle.DOWN) ? WeightStatus.HEAVY : getPlane(type).getAngle().equals(Angle.NEUTRAL) ?
					WeightStatus.BALANCED : WeightStatus.LIGHT;
		else
			return getPlane(type).getAngle().equals(Angle.UP) ? WeightStatus.HEAVY : getPlane(type).getAngle().equals(Angle.DOWN) ?
					WeightStatus.LIGHT : WeightStatus.BALANCED;
	}
	
	/**
	 * Calculates 
	 * 
	 * @param type The PlaneType you want to calculate for
	 * @return Returns the value of the weight calculated
	 */
	private double calculatePlaneWeight(PlaneType type) {
		getPlane(type).setForceStatus(getPlaneWeightStatus(type));
		if(type.equals(PlaneType.STERN))
			return getPlane(type).getWeightStatus().equals(WeightStatus.HEAVY) ? Math.abs((sternCurrent - Constants.STERN_STABLE_ANGLE) * Constants.STERN) 
					: -1 * (sternCurrent - Constants.STERN_STABLE_ANGLE) * Constants.STERN;
		else if(type.equals(PlaneType.SHIP))
			return getPlane(type).getWeightStatus().equals(WeightStatus.HEAVY) ? Math.abs((shipCurrent - Constants.SHIP_STABLE_ANGLE) * Constants.SHIP) 
					: (shipCurrent - Constants.SHIP_STABLE_ANGLE) * Constants.SHIP;
		else
			return getPlane(type).getWeightStatus().equals(WeightStatus.HEAVY) ? Math.abs(bowCurrent * Constants.BOW)
					: (bowCurrent * Constants.BOW);
	}
	
	/**
	 * Calculates the bow plane correction factor and determines if it is needed.
	 */
	public void calculateBowCorrectionFactor() {
			setNeedsCorrection((getShipWeightStatus().equals(WeightStatus.BALANCED) || bowCurrent == 0) ? false : true);
			double correctionFactor = .4 * bowCurrent;
			double correctedSternPlaneAngle = sternCurrent + correctionFactor;
			if(correctedSternPlaneAngle == getNeutralSternPlaneAngle() || getCompartment().equals(Compartment.BALANCED)) {
				setCompartment(Compartment.BALANCED);
				setBowCorrectionValue(0);
			} else {
				//setBowCorrectionValue(Math.abs(Constants.STERN * (Math.abs(getNeutralSternPlaneAngle() - (isNeedsCorrection() ? correctedSternPlaneAngle : sternCurrent)))));
				setBowCorrectionValue(Math.abs(Constants.STERN * (Math.abs((isNeedsCorrection() ? correctedSternPlaneAngle : sternCurrent) - getNeutralSternPlaneAngle()))));

			}
	}
	
	/**
	 * Takes all the planes attached to the ship and adds their weight together.
	 */
	public void calculateTotalWeight() {
		setTotalWeight(0);
		planeMap.values().forEach(plane -> {
			plane.setWeight(calculatePlaneWeight(plane.getType()));
			setTotalWeight(getTotalWeight() + plane.getWeight());
		});
		
		setShipWeightStatus(getTotalWeight() > 0 ? WeightStatus.HEAVY : getTotalWeight() < 0 ? WeightStatus.LIGHT : WeightStatus.BALANCED);
		
		if(getPlane(PlaneType.BOW).getWeight() > getPlane(PlaneType.STERN).getWeight()) {
			setCompartment(Compartment.FORWARD);
		} else if(getPlane(PlaneType.BOW).getWeight() < getPlane(PlaneType.STERN).getWeight()) {
			setCompartment(Compartment.AFT);
		} else {
			setCompartment(Compartment.BALANCED);
		}
	}
	
	/**
	 * Creates a new plane to be added to the Ship
	 * 
	 * @param ship
	 * @param plane
	 * @return
	 */
	public static Plane createNewPlane(Ship ship, PlaneType plane) {
		
		switch(plane) {
		
		case STERN:
			return new Plane(plane, ship.getSternCurrent() < -1 ? Angle.DOWN : ship.getSternCurrent() == -1 ? Angle.NEUTRAL : Angle.UP);
		case SHIP:
			return new Plane(plane, ship.getShipCurrent() < 0 ? Angle.DOWN : ship.getShipCurrent() == 0 ? Angle.NEUTRAL : Angle.UP);
		case BOW:
			return new Plane(plane, ship.getBowCurrent() < 0 ? Angle.DOWN : ship.getShipCurrent() == 0 ? Angle.NEUTRAL : Angle.UP);
		
		default:
			return new Plane(PlaneType.SHIP, Angle.NEUTRAL);
		}
	}
}
