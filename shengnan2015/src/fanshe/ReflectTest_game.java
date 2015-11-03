package fanshe;
import java.lang.reflect.Method;

public class ReflectTest_game
{
	
	public static void main(String[] args) throws Exception
	{
		String str1 = "abc";
	    
	    ////用反射的方式得到字节码的方，.invoke是“方法”的方法
	    Method methodCharAt = String.class.getMethod("charAt", int.class);
	    System.out.println(methodCharAt.invoke(str1, 1));//如果第一个参数为null则为静态方法
	    //传参数数组,java1.4的语法
	    System.out.println(methodCharAt.invoke(str1, new Object[]{2}));
	    
	    //调用主函数
//	    String startClassName = args[0];
//	    Method mainMethod = Class.forName(startClassName).getMethod("main", String[].class);
//	    mainMethod.invoke(null, new Object[]{new String[]{"111","222","333"}}); 
//	    mainMethod.invoke(null, (Object)new String[]{"111","222","333"}); //也行
	    
	    
	}



}
class TestArgument
{
	public static void main(String[] args)
	{
		for(String arg:args)
		{
			System.out.println("82行"+arg);
		}
	}
}
