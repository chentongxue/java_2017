package ac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileReaderTest {
	public static void main(String[] args) {
		System.out.println("start");
		String csn = Charset.defaultCharset().name();
		System.out.println(csn);
		BufferedReader br;
		long time = System.currentTimeMillis();
		try {
			File file = new File("dirty-word.txt");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(new String(line.getBytes("utf-8"),"utf-8"));
				System.out.println(line);
			   }
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		time = System.currentTimeMillis() -time;
		System.out.println(time);
	}
	public static void init(){
		
	}
}
