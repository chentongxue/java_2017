package generics;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
public class GenericTest 
{
	public static void main(String[] args) throws Exception
	{
		//////////////////////////////////////
		ArrayList collection = new ArrayList();
		collection.add(1);
		collection.add(1L);
		collection.add("abc");
		//////////////////////////////////////
		//////////////////////////////////////
		int i = (Integer) collection.get(0);
		
		//
		ArrayList<String> collection2 = new ArrayList<String>();
		collection2.add("abc");	
		collection2.add("bbc");	
		collection2.add("ccb");	
		String element = collection2.get(0);
		
//		Constructor<String>constructor1 = String.class.getConstructor(String);
//		String str2 = constructor1.newInstance("abc");
//		
//		System.out.println(str2.charAt(2));
		
		
		ArrayList<Integer>collection3 = new ArrayList<Integer>();
		System.out.println(collection3.getClass()==collection2.getClass());//true
		collection3.getClass().getMethod("add", Object.class).invoke(collection3, "abc");
		System.out.println(collection3);//[abc]
		
		printCollection(collection2);//[abc, bbc, ccb]
		
//		String.class.asSubclass(Number.class); // error: java.lang.ClassCastException: class java.lang.String
		Class<?> y ;
		Class<String> x = null;
		y = x;
		
		HashMap<String, Integer> maps = new HashMap<String, Integer>();
		
		maps.put("zzz", 28);
		maps.put("bao", 18);
		maps.put("shengnan", 10);
		
		Set<  Map.Entry<String, Integer> > entrySet = maps.entrySet();
		
		for(Map.Entry<String, Integer> entry :entrySet)
		{
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
		//////////////////////
		////////////////////////
		swap(new String[]{"I","miss","lsn"}, 1, 2);
//		swap(new int[]{1,2,3}, 1, 2); // error 必须为引用类型
		
		/////////001
		Object obj = "abc";
		String x3 = autoConvert(obj);
		///////////004
		copy1(new Vector<String>(), new String[10]);
		copy2(new Date[10], new String[10]);
		copy1(new Vector<String>(), new String[10]); //error //传播性
		
		//005用反射得到泛型的实际参数
		//Vector<Date> v1 = new Vector<Date>();
		Method applyMethod = GenericTest.class.getMethod("applyVector", Vector.class);
		Type[] types = applyMethod.getGenericParameterTypes();
		ParameterizedType pType = (ParameterizedType)types[0];
		
		System.out.println(pType.getRawType());//得到原始类型
		System.out.println(pType.getActualTypeArguments()[0]);//实际参数类型
	}
	//005
	public static void applyVector(Vector<Date>v1)
	{
		
	}
	public static void printCollection(Collection<?> collection)
	{
		System.out.println(collection);
		
		for(Object obj:collection)
		{
			System.out.print(obj);
		}
		System.out.println("hello");
	}
	
	private static <T>void swap(T[] a, int i,int j) 
	{
		T tem = a[i];
		a[i] = a[j];
		a[j] = tem;
	}
	////////////001
	private static <T> T autoConvert(Object obj)
	{
		return (T)obj;
	}
	///////////////002
	private static <T> void fillArray(T[] a, T obj)
	{
		for(int i = 0;i<a.length;i++)
		{
			a[i] = obj;
		}
	}
	///////////打印任意类型的集合
	public static <T> void printCollection2(Collection<T> collection,T obj2)
	{
		System.out.println(collection);
		
		for(Object obj:collection)
		{
			System.out.print(obj);
		}
		collection.add(obj2);
		System.out.println("hello");
	}
	//////////////004  复制泛型元素到数组
	public static <T> void copy1(Collection<T> dest, T[] src)
	{
		
	}
	public static <T> void copy2(T[] dest, T[] src)
	{
		
	}
}
