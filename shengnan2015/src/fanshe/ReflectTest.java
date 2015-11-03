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
		
		//�õ����캯��--------------------constructor
		String str = new String(new StringBuffer("abc"));
		//constructor ��������췽��
		Constructor  constructor = String.class.getConstructor(StringBuffer.class);
	    String str2 = (String) constructor.newInstance(new StringBuffer("abc"));
	    System.out.println("����  ���췽������abc�ĵ�һ��Ԫ��"+str2.charAt(0));//5
	    
	    //Field-----------------�õ���Ա����
	    ReflectPoint pt1 = new ReflectPoint(3, 5);
	    //ֻ�е�yΪ��flectPointΪpublic��ʱ��ſ���
	    Field fieldY = pt1.getClass().getField("y");//.getClass()Ϊ�õ��ֽ���    
	    System.out.println("����  Field.get(pt1)�Ľ��"+fieldY.get(pt1));//5
	    
	    //��������
	    Field fieldX = pt1.getClass().getDeclaredField("x");//.getClass()Ϊ�õ��ֽ���    
	    fieldX.setAccessible(true);
	    System.out.println(fieldX.get(pt1));//3
	   
	    changeStrValue(pt1);
	    System.out.println(pt1);
	    
	    ////�÷���ķ�ʽ�õ��ֽ���ķ���.invoke�ǡ��������ķ���
	    Method methodCharAt = String.class.getMethod("charAt", int.class);
	    System.out.println(methodCharAt.invoke(str1, 1));//�����һ������Ϊnull��Ϊ��̬����
	    //����������,java1.4���﷨
	    System.out.println(methodCharAt.invoke(str1, new Object[]{2}));
	    
	    //����������
	    String startClassName = args[0];
	    Method mainMethod = Class.forName(startClassName).getMethod("main", String[].class);
	   // mainMethod.invoke(null, new String[]{"111","222","333"}); //����
	    mainMethod.invoke(null, new Object[]{new String[]{"111","222","333"}}); 
	    mainMethod.invoke(null, (Object)new String[]{"111","222","333"}); //Ҳ��
	    //
	    //
	    //
	    //ά����ͬ���ֽ�����ͬ
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
	    //JDK  1.5������Object
	    System.out.println(Arrays.asList(a1));
	    System.out.println(Arrays.asList(a4));
	    //
	    //��Ƶ5
	    Object obj = null;
	    printObject(a4);
	    printObject("abc");
	    
	    
	}
	private static void printObject(Object obj)
	{
		//�����鷴����� Array
		Class clazz = obj.getClass();
		if(clazz.isArray())//���������
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
	//�ı�ֵ
	private static void changeStrValue(Object obj) throws Exception
	{
		Field[] fields = obj.getClass().getFields();
		for (Field field : fields)
		{
			if(field.getType()==String.class)//���ֽ���Ƚ���==��
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
			System.out.println("82��"+arg);
		}
	}
}
