package fanshe;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

public class ReflectTest2
{
	public static void main(String[] args) throws Exception
	{
		InputStream ips = new FileInputStream("config.properties");
		Properties props = new Properties();
		
		props.load(ips);
		ips.close();
		////////////类加载器,找到类，找到加载器
		ReflectTest2.class.getClassLoader().getResourceAsStream("config.properties");
		///////////////
		ReflectTest2.class.getResourceAsStream("config.properties");
		
		
		String className = props.getProperty("className");
		Collection collection = (Collection)Class.forName(className).newInstance();
		
		//Collection collection = new HashSet();
		ReflectPoint pt1 = new ReflectPoint(3, 3);
		ReflectPoint pt2 = new ReflectPoint(5, 5);
		ReflectPoint pt3 = new ReflectPoint(3, 3);
		collection.add(pt1);
		collection.add(pt2);
		collection.add(pt3);
		collection.add(pt1);
		
		System.out.println(collection.size());
		//HashSet最好不要改掉元素的值，否则会查不到元素而造成内存泄露。
		pt1.y = 886;
		collection.remove(pt1);
		System.out.println(collection.size());
	}
}
