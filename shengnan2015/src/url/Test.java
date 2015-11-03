package url;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
/**
 * b≤ª∫√ π
 * @author pc
 *
 */
public class Test {
	public static void main(String args[]){
		BufferedReader br;
		try {
			File file = new File("online.txt");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				callURL(line);
				System.out.println(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void callURL(String path){
		URL url;
		try {
			url = new URL(path);
			URLConnection urlcon = url.openConnection();
	        InputStream is = urlcon.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	}
}
