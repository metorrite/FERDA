package com.avl.controllers;

import java.io.File;
import java.io.FileWriter;
import java.text.NumberFormat;

import com.avl.Plane;
import com.avl.Ship;
import com.avl.constants.Compartment;
import com.avl.constants.Constants;
import com.avl.constants.PlaneType;
import com.avl.constants.ShipSpeed;
import com.avl.constants.WeightStatus;

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

	public static final Ship SHIP = new Ship();
	private static NumberFormat format = NumberFormat.getInstance();

	@FXML
	private void startCalculation() {
		System.out.println("Starting calcs.");
		try {
			if (Constants.SIMULATE)
				simulate();
			else
				calculateTrim();

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
		for (int s = -4; s <= 4; s++) {
			SHIP.setSternCurrent(s);
			for (int a = -4; a <= 4; a++) {
				SHIP.setShipCurrent(a);
				for (int b = -4; b <= 4; b++) {
					SHIP.setBowCurrent(b);
					calculateTrim();
					simValue++;
					Plane stern = SHIP.getPlane(PlaneType.STERN);
					Plane shipP = SHIP.getPlane(PlaneType.SHIP);
					Plane bow = SHIP.getPlane(PlaneType.BOW);
					fw.write(
							"------------------------------------------------------------------------------------------------------------");
					fw.write("\nSimulation Number - " + simValue);
					fw.write("\n\nStern Current: " + format.format(SHIP.getSternCurrent()));
					fw.write("\nShip Current: " + format.format(SHIP.getShipCurrent()));
					fw.write("\nBow Current: " + format.format(SHIP.getBowCurrent()));
					fw.write("\n\nStern Weight: " + format.format(stern.getWeight()) + " "
							+ stern.getWeightStatus().toString());
					fw.write("\nShip Weight: " + format.format(shipP.getWeight()) + " "
							+ shipP.getWeightStatus().toString());
					fw.write(
							"\nBow Weight: " + format.format(bow.getWeight()) + " " + bow.getWeightStatus().toString());
					fw.write("\n\nShip total weight: " + format.format(SHIP.getTotalWeight()) + " "
							+ SHIP.getShipWeightStatus().toString());
					fw.write("\nRequires bow plane correction: " + SHIP.isNeedsCorrection());
					fw.write("\n\nPump Value (fwd/aft): " + format.format(SHIP.getBowCorrectionValue()));
					fw.write(
							"\nFWD/AFT TO FWD/AFT: " + (SHIP.getCompartment().equals(Compartment.FORWARD) ? "FWD to AFT"
									: SHIP.getCompartment().equals(Compartment.AFT) ? "AFT to FWD" : "Balanced"));
					fw.write("\n\nPump Value (Flood/Pump): " + format.format(SHIP.getTotalWeight()));
					fw.write("\nFlood / Pump : " + (SHIP.getShipWeightStatus().equals(WeightStatus.HEAVY) ? "Pump"
							: SHIP.getShipWeightStatus().equals(WeightStatus.LIGHT) ? "Flood" : "None"));
					fw.write(
							"\n\n------------------------------------------------------------------------------------------------------------");
					fw.flush();
				}
			}
		}
		fw.close();
	}

	
	/**
	 * This function is the primary function that calculates the overall trim status of the ship.
	 * It incorporates the functions provided in the Ship class to put together the pieces in a
	 * logical order to finalize the assessment of ships current trim.
	 */
	private void calculateTrim() {
		if (!Constants.SIMULATE) {
			DiveController.SHIP.setSternCurrent(Double.parseDouble(sternInput.getText()));
			DiveController.SHIP.setShipCurrent(Double.parseDouble(shipInput.getText()));
			DiveController.SHIP.setBowCurrent(Double.parseDouble(bowInput.getText()));
		}

		DiveController.SHIP.getPlaneMap().clear();

		DiveController.SHIP.getPlaneMap().put(PlaneType.STERN, Ship.createNewPlane(SHIP, PlaneType.STERN));
		DiveController.SHIP.getPlaneMap().put(PlaneType.SHIP,Ship.createNewPlane(SHIP, PlaneType.SHIP));
		DiveController.SHIP.getPlaneMap().put(PlaneType.BOW, Ship.createNewPlane(SHIP,PlaneType.BOW));
		DiveController.SHIP.calculateTotalWeight();
		DiveController.SHIP.calculateBowCorrectionFactor();
		setTrimPumpValues();
		setDepthControlValues();


	}

	/**
	 * Sets the values in the interface to display the FWD and AFT trim values.
	 */
	private void setTrimPumpValues() {
		if (DiveController.SHIP.getCompartment().equals(Compartment.FORWARD)) {
			fwdAftBox.setSelected(true);
			aftFwdBox.setSelected(false);
		} else if (DiveController.SHIP.getCompartment().equals(Compartment.AFT)) {
			aftFwdBox.setSelected(true);
			fwdAftBox.setSelected(false);
		} else {
			aftFwdBox.setSelected(false);
			fwdAftBox.setSelected(false);
		}
		trimPumpValue.setText(format.format(SHIP.getBowCorrectionValue()) + " LBS");
	}
	
	/**
	 * Sets the values in the interface to display the Depth Control values.
	 */
	private void setDepthControlValues() {
		if (DiveController.SHIP.getShipWeightStatus().equals(WeightStatus.HEAVY)) {
			pumpBox.setSelected(true);
			floodBox.setSelected(false);
		} else if (DiveController.SHIP.getShipWeightStatus().equals(WeightStatus.LIGHT)) {
			floodBox.setSelected(true);
			pumpBox.setSelected(false);
		} else {
			pumpBox.setSelected(false);
			floodBox.setSelected(false);
		}
		depthControlValue.setText(format.format(Math.abs(SHIP.getTotalWeight())) + " LBS");
	}
	
	@FXML
	private void setOneThird() {
		SHIP.setSpeed(ShipSpeed.ONE_THIRD);
		Constants.STERN = 1000; // 1300?
		Constants.SHIP = 4000;
		Constants.BOW = 500;
	}

	@FXML
	private void setTwoThird() {
		SHIP.setSpeed(ShipSpeed.TWO_THIRDS);
		Constants.STERN = 3500;
		Constants.SHIP = 11000;
		Constants.BOW = 1300;
	}

	@FXML
	private void setStandard() {
		SHIP.setSpeed(ShipSpeed.STANDARD);
		Constants.STERN = 8000;
		Constants.SHIP = 25000;
		Constants.BOW = 3000;
	}
}
