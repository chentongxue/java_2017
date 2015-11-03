package fanshe;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.omg.CORBA.StructMemberHelper;

public class ReflectTest
{
	
	public static void main(String[] args) throws Exception
	{
		String str1 = "abc";
		Class cls1 = str1.getClass();
		Class cls2 = str1.getClass();
		Class cls3 = Class.forName("java.lang.String");
		
		System.out.println(cls1 == cls2);//true
		System.out.println(cls1 == cls3);//true
		
		System.out.println(cls1.isPrimitive());//fasle
		
		System.out.println(Integer.class.isPrimitive());//fasle
		System.out.println(int.class.isPrimitive());//true
		
		System.out.println(int.class == Integer.class);//fasle
		System.out.println(int.class == Integer.TYPE);//true!!
		
		System.out.println(int[].class.isArray());//true!!!
		
		//得到构造函数--------------------constructor
		String str = new String(new StringBuffer("abc"));
		//constructor 对象代表构造方法
		Constructor  constructor = String.class.getConstructor(StringBuffer.class);
	    String str2 = (String) constructor.newInstance(new StringBuffer("abc"));
	    System.out.println("反射  构造方法生成abc的第一个元素"+str2.charAt(0));//5
	    
	    //Field-----------------得到成员变量
	    ReflectPoint pt1 = new ReflectPoint(3, 5);
	    //只有当y为在flectPoint为public的时候才可以
	    Field fieldY = pt1.getClass().getField("y");//.getClass()为得到字节码    
	    System.out.println("反射  Field.get(pt1)的结果"+fieldY.get(pt1));//5
	    
	    //暴力反射
	    Field fieldX = pt1.getClass().getDeclaredField("x");//.getClass()为得到字节码    
	    fieldX.setAccessible(true);
	    System.out.println(fieldX.get(pt1));//3
	   
	    changeStrValue(pt1);
	    System.out.println(pt1);
	    
	    ////用反射的方式得到字节码的方，.invoke是“方法”的方法
	    Method methodCharAt = String.class.getMethod("charAt", int.class);
	    System.out.println(methodCharAt.invoke(str1, 1));//如果第一个参数为null则为静态方法
	    //传参数数组,java1.4的语法
	    System.out.println(methodCharAt.invoke(str1, new Object[]{2}));
	    
	    //调用主函数
	    String startClassName = args[0];
	    Method mainMethod = Class.forName(startClassName).getMethod("main", String[].class);
	   // mainMethod.invoke(null, new String[]{"111","222","333"}); //不行
	    mainMethod.invoke(null, new Object[]{new String[]{"111","222","333"}}); 
	    mainMethod.invoke(null, (Object)new String[]{"111","222","333"}); //也行
	    //
	    //
	    //
	    //维数相同，字节码相同
	    int [] a1 = new int[]{1,2,3};
	    int [] a2 = new int[4];
	    int[][]a3 = new int[2][3];
	    String [] a4 = new String[]{"a","b","c"};
	    System.out.println(a1.getClass() == a2.getClass());//true
	    System.out.println(a1.getClass() == a2.getClass());//true
	    System.out.println(a1.getClass().getName());//[I
	    System.out.println( (a1.getClass().getSuperclass()).getName() );//java.lang.Object
	   
	    Object aobj1 = a1;
	    Object aobj2 = a4;
	    
	    Object[] aobj4 = a3;
	    Object[] aobj5 = a3;
	    //JDK  1.5传的是Object
	    System.out.println(Arrays.asList(a1));
	    System.out.println(Arrays.asList(a4));
	    //
	    //视频5
	    Object obj = null;
	    printObject(a4);
	    printObject("abc");
	    
	    
	}
	private static void printObject(Object obj)
	{
		//对数组反射的类 Array
		Class clazz = obj.getClass();
		if(clazz.isArray())//如果是数组
		{
			int len = Array.getLength(obj);
			for(int i = 0;i<len;i++)
			{
				System.out.println(Array.get(obj, i));
			}
		}
		else
		{
			
		}
	}
	//改变值
	private static void changeStrValue(Object obj) throws Exception
	{
		Field[] fields = obj.getClass().getFields();
		for (Field field : fields)
		{
			if(field.getType()==String.class)//用字节码比较用==比
			{
				String oldValue = (String)field.get(obj);
				String newValue = oldValue.replace('a', 'b');
				field.set(obj, newValue);
			}
		}
	}

}
class TestArgument2
{
	public static void main(String[] args)
	{
		for(String arg:args)
		{
			System.out.println("82行"+arg);
		}
	}
}
