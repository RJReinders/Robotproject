package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CsvFile {
	
	private static final String csvFileNameA = "motorA.csv";
	private static final String csvFileNameB = "motorB.csv";
	private String csvFileName = "";
	
	public CsvFile() {
		super();
	}
	
	public void check() {
		ArrayList<Integer> motorA = new ArrayList<>();
		ArrayList<Integer> motorB = new ArrayList<>();
		
		for (int teller=0 ; teller < 100;teller++) {
			motorA.add(teller);
			motorB.add(teller*300);
		}
		
		this.createCsvFileMotor(motorA, "A");
		this.createCsvFileMotor(motorB, "B");
		
		System.out.println("=====================");
		
		motorA = this.readCsvFileMotor("A");
		motorB = this.readCsvFileMotor("B");
		
		for (int waarde : motorA) {
			System.out.print(waarde + ",");
		}
		
		for (int waarde : motorB) {
			System.out.print(waarde + ",");
		}
		
		
	}

	public void createCsvFileMotor(ArrayList<Integer> waarde, String motor) {
		
		if (motor.equals("A")) {
			csvFileName = csvFileNameA;
		} else if (motor.equals("B")) {
			csvFileName = csvFileNameB;
		} 

		File csvFile = new File(csvFileName);

		try {
			PrintWriter pw = new PrintWriter(csvFile);
			for (int gemetenWaarde : waarde) {
				pw.print(gemetenWaarde+ "\n");
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(404);
		}
	}


	public ArrayList<Integer> readCsvFileMotor(String motor) {

		ArrayList<Integer> csvWaarde = new ArrayList<>();
		
		if (motor.equals("A")) {
			csvFileName = csvFileNameA;
		} else if (motor.equals("B")) {
			csvFileName = csvFileNameB;
		} 

		File csvFile = new File(csvFileName);

		try {
			Scanner scanner = new Scanner(csvFile);
			//scanner.useDelimiter(",");
			while(scanner.hasNext()){
				csvWaarde.add(scanner.nextInt());
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return csvWaarde;
	}

}
