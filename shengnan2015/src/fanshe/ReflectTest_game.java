package fanshe;
import java.lang.reflect.Method;

public class ReflectTest_game
{
	
	public static void main(String[] args) throws Exception
	{
		String str1 = "abc";
	    
	    ////�÷���ķ�ʽ�õ��ֽ���ķ���.invoke�ǡ��������ķ���
	    Method methodCharAt = String.class.getMethod("charAt", int.class);
	    System.out.println(methodCharAt.invoke(str1, 1));//�����һ������Ϊnull��Ϊ��̬����
	    //����������,java1.4���﷨
	    System.out.println(methodCharAt.invoke(str1, new Object[]{2}));
	    
	    //����������
//	    String startClassName = args[0];
//	    Method mainMethod = Class.forName(startClassName).getMethod("main", String[].class);
//	    mainMethod.invoke(null, new Object[]{new String[]{"111","222","333"}}); 
//	    mainMethod.invoke(null, (Object)new String[]{"111","222","333"}); //Ҳ��
	    
	    
	}



}
class TestArgument
{
	public static void main(String[] args)
	{
		for(String arg:args)
		{
			System.out.println("82��"+arg);
		}
	}
}
