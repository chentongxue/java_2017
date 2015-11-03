package test;

import java.lang.reflect.Field;
/**
 *  getFields()与getDeclaredFields()区别
 *  getFields()只能访问类中声明为公有的字段,私有的字段它无法访问，能访问从其它类继承来的公有方法.
 *  getDeclaredFields()能访问类中所有的字段,与public,private,protect无关，不能访问从其它类继承来的方法 
 */
public class Main {

	public static void main(String[] args) throws ClassNotFoundException {
		new Main().test();

	}
	public void test() throws ClassNotFoundException{
		Student a = new Student();
		System.out.println(a);
		setA(a);
		System.out.println(a);
		System.err.println(a.getClass().getFields().length);
		for(Field f: Student.class.getFields()){
			System.err.println("aa");
		}
//		System.out.println(Integer.TYPE);//int
//		System.out.println(Integer.class);//class java.lang.Integer
		
		
		//反射获取类的所有域
//		Class classType = Class.forName("java.lang.String");

//		Field[] fields = Student.class.getDeclaredFields();
		Field[] fields = a.getClass().getDeclaredFields();
		
		for (Field field : fields) 
		{
//			System.out.println(field);
			System.out.println(field.getName());
		}
	}
	public void setA(Student a){
		a.setAge(12);
		a.setName("haha");
	}

}
