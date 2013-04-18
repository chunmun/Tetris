package blueBlox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ArffBuilder {

	public static void main(String args){
//		try {
//			BufferedReader br = new BufferedReader(new FileReader("input.txt"));
//			BufferedWriter wr = new BufferedWriter(new FileWriter("output.arff",false));
//			
//			String line;
//			while((line = br.readLine()) != null){
		String line = "Trials : [0, 2, -1, -2, -1, -6] => Score : 28";
				String[] tokens = line.split("] => Score : ");
				tokens[0] = tokens[0].substring(10);
				
				System.out.println(tokens[0]);
				System.out.println(tokens[1]);
				
				
//			}
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
