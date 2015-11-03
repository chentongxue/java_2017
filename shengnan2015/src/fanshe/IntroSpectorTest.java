package fanshe;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Properties;

public class IntroSpectorTest 
{
	public static void main(String[] args) throws Exception 
	{
		ReflectPoint pt1 = new ReflectPoint(3, 5);
		
		String propertyName = "x";
		
		// Ù–‘√Ë ˆ∑˚
		PropertyDescriptor pd = new PropertyDescriptor(propertyName, pt1.getClass());
	    Method methodGetX = pd.getReadMethod();	    
	    Object retValue = methodGetX.invoke(pt1);
	    
	    System.out.println(retValue);

  
        Method methodSetX = pd.getWriteMethod();
	    methodSetX.invoke(pt1,7);

	}
}
