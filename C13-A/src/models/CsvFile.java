package models;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.FileWriter;


public class CsvFile {
	
	private static final char DEFAULT_SEPARATOR = ',';
	List<String> test = new ArrayList<>();
	
	public void writeLine() throws IOException {
		
		FileWriter writer = new FileWriter("blindmode.csv");
		
	    test.add("Word1");
	    test.add("Word2");
	    test.add("Word3");
	    test.add("Word4");

	    String collect = test.stream().collect(Collectors.joining(","));
	    System.out.println(collect);

	    writer.write(collect);
	    writer.close();
    }
	

}
