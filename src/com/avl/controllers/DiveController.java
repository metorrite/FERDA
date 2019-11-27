package com.avl.controllers;

import java.io.File;
import java.io.FileWriter;
import java.text.NumberFormat;

import com.avl.Plane;
import com.avl.Ship;
import com.avl.constants.Angle;
import com.avl.constants.Compartment;
import com.avl.constants.Constants;
import com.avl.constants.PlaneType;
import com.avl.constants.ShipSpeed;
import com.avl.constants.ShipStatus;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class DiveController {

	@FXML
	private TextField sternInput, shipInput, bowInput, trimPumpValue, depthControlValue;

	@FXML
	private CheckBox fwdAftBox, aftFwdBox, pumpBox, floodBox;

	
	@FXML 
	private Button oneThirdButton, twoThirdButton, stdButton;

	public static final Ship ship = new Ship();
	private static NumberFormat format = NumberFormat.getInstance();
	
	@FXML
	private void startCalculation() {
		System.out.println("Starting calcs.");
		try {
			if(Constants.SIMULATE)
				simulate();
			else
				simulateCalc();
			
		} catch (Exception e) {
			System.out.println("ERROR PARSING INPUT!");
			e.printStackTrace();
		}
	}
	private void simulate() throws Exception {
		File f = new File("simulation.txt");
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		int simValue = 0;
		for(int s = -4; s<=4; s++) {
			ship.setSternCurrent(s);
			for(int a = -4; a<=4; a++) {
				ship.setShipCurrent(a);
				for(int b = -4; b<=4; b++) {
					ship.setBowCurrent(b);
					simulateCalc();
					simValue++;
					Plane stern = ship.getPlane(PlaneType.STERN);
					Plane shipP = ship.getPlane(PlaneType.SHIP);
					Plane bow = ship.getPlane(PlaneType.BOW);
					fw.write("------------------------------------------------------------------------------------------------------------");
					fw.write("\nSimulation Number - " + simValue);
					fw.write("\n\nStern Current: " + format.format(ship.getSternCurrent()));
					fw.write("\nShip Current: " + format.format(ship.getShipCurrent()));
					fw.write("\nBow Current: " + format.format(ship.getBowCurrent()));
					fw.write("\n\nStern Weight: " + format.format(stern.getWeight()) + " " + stern.getWeightStatus().toString());
					fw.write("\nShip Weight: " + format.format(shipP.getWeight()) + " " + shipP.getWeightStatus().toString());
					fw.write("\nBow Weight: " + format.format(bow.getWeight()) + " " + bow.getWeightStatus().toString());
					fw.write("\n\nShip total weight: " + format.format(ship.getTotalWeight()) + " " + ship.getStatus().toString());
					fw.write("\nRequires bow plane correction: " + ship.isNeedsCorrection());
					fw.write("\n\nPump Value (fwd/aft): " + format.format(ship.getBowCorrectionValue()));
					fw.write("\nFWD/AFT TO FWD/AFT: " + (ship.getCompartment().equals(Compartment.FORWARD) ? "FWD to AFT" : ship.getCompartment().equals(Compartment.AFT) ? "AFT to FWD" : "Balanced"));
					fw.write("\n\nPump Value (Flood/Pump): " + format.format(ship.getTotalWeight()));
					fw.write("\nFlood / Pump : " + (ship.getStatus().equals(ShipStatus.HEAVY) ? "Pump" : ship.getStatus().equals(ShipStatus.LIGHT) ? "Flood" : "None"));
					fw.write("\n\n------------------------------------------------------------------------------------------------------------");
					fw.flush();
				}
			}
		}
		fw.close();
	}
	
	private void simulateCalc() {
		if(!Constants.SIMULATE) {
		  DiveController.ship.setSternCurrent(Double.parseDouble(sternInput.getText()))
		  ;
		  DiveController.ship.setShipCurrent(Double.parseDouble(shipInput.getText()));
		  DiveController.ship.setBowCurrent(Double.parseDouble(bowInput.getText()));
		}
		DiveController.ship.getPlaneMap().clear();

		DiveController.ship.getPlaneMap().put(PlaneType.STERN,
				new Plane(PlaneType.STERN, DiveController.ship.getSternCurrent() < 0 ? Angle.DOWN : Angle.UP));
		DiveController.ship.getPlaneMap().put(PlaneType.SHIP,
				new Plane(PlaneType.SHIP, DiveController.ship.getShipCurrent() < 0 ? Angle.DOWN : Angle.UP));
		DiveController.ship.getPlaneMap().put(PlaneType.BOW,
				new Plane(PlaneType.BOW, DiveController.ship.getBowCurrent() < 0 ? Angle.DOWN : Angle.UP));

		DiveController.ship.getPlaneMap().values().forEach(plane -> plane.setWeight(DiveController.ship.calculatePlaneWeight(plane.getType())));
		DiveController.ship.calculateTotalWeight();
		DiveController.ship.calculateBowCorrectionFactor();
		trimPumpValue.setText(format.format(ship.getBowCorrectionValue()) + " LBS");
		  if(DiveController.ship.getCompartment().equals(Compartment.FORWARD)) {
			 fwdAftBox.setSelected(true);
			 aftFwdBox.setSelected(false); 
		  } else if(DiveController.ship.getCompartment().equals(Compartment.AFT)) {
			 aftFwdBox.setSelected(true);
			 fwdAftBox.setSelected(false); 
		 } else {
			  aftFwdBox.setSelected(false);
			  fwdAftBox.setSelected(false); 
		 }
		 
		if (DiveController.ship.getStatus().equals(ShipStatus.HEAVY)) {
			pumpBox.setSelected(true);
			floodBox.setSelected(false);
		} else if (DiveController.ship.getStatus().equals(ShipStatus.LIGHT)) {
			floodBox.setSelected(true);
			pumpBox.setSelected(false);
		} else {
			pumpBox.setSelected(false);
			floodBox.setSelected(false);
		}
		depthControlValue.setText(format.format(Math.abs(ship.getTotalWeight())) + " LBS");
	}
	
	@FXML
	private void setOneThird() {
		ship.setSpeed(ShipSpeed.ONE_THIRD);
		Constants.STERN = 1000; //1300?
		Constants.SHIP = 4000;
		Constants.BOW = 500;
	}
	
	@FXML
	private void setTwoThird() {
		ship.setSpeed(ShipSpeed.TWO_THIRDS);
		Constants.STERN = 3500;
		Constants.SHIP = 11000;
		Constants.BOW = 1300;
	}
	
	@FXML
	private void setStandard() {
		ship.setSpeed(ShipSpeed.STANDARD);
		Constants.STERN = 8000;
		Constants.SHIP = 25000;
		Constants.BOW = 3000;
	}
}
