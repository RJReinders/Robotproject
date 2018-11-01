package models;

import java.util.ArrayList;
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
			while(scanner.hasNext()){
				csvWaarde.add(scanner.nextInt());
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return csvWaarde;
	}

}
