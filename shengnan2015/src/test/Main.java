package test;

import java.lang.reflect.Field;
/**
 *  getFields()��getDeclaredFields()����
 *  getFields()ֻ�ܷ�����������Ϊ���е��ֶ�,˽�е��ֶ����޷����ʣ��ܷ��ʴ�������̳����Ĺ��з���.
 *  getDeclaredFields()�ܷ����������е��ֶ�,��public,private,protect�޹أ����ܷ��ʴ�������̳����ķ��� 
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
		
		
		//�����ȡ���������
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
