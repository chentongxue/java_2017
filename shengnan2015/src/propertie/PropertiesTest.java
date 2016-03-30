package propertie;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesTest {

	public static void main(String[] args) {
		PropertiesTest test = new PropertiesTest();
        Map<String, String> map = test.loadProperties("item.properties");
        System.out.println(map);
	}
	/**
	 * 
	 * @param map
	 * @param fileName
	 * @return
	 */
	public Map<String, String> loadProperties(String fileName){
		Map<String, String> emtyMap = new HashMap<String, String>();
        try {
//        	FileInputStream fis = new FileInputStream(fileName);//属性文件流 
//        	InputStream in = PropertiesTest.class.getClassLoader().getResourceAsStream(fileName);
        	InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
            Properties ps = new Properties();
			ps.load(in);
			return new HashMap<String, String>((Map)ps);
		} catch (IOException e) {
			e.printStackTrace();
			return emtyMap;
		}
	}

}
