package baoutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

public class ReadLine {

	public static void main(String[] args) {

		System.err.println("start");
		
		System.out.println("start");
		String csn = Charset.defaultCharset().name();
		System.out.println(csn);
		BufferedReader br;
		try {
			File file = new File("catalina.out");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
			   System.out.println(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	

	}

}
