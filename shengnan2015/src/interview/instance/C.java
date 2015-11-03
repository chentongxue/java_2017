package interview.instance;

import java.lang.String;
import java.lang.System;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class C extends B{
	public static void main(String[] args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		System.out.println("hello");
		A a = new C();
		B b = new B();
		C c = new C();
		
		System.out.println("b instanceof C   "+(b instanceof C));
		System.out.println("c instanceof B   "+(c instanceof B));
		System.out.println("c.getClass().isInstance(b))   "+(c.getClass().isInstance(b)));
		System.out.println("b.getClass().isInstance(c))   "+(b.getClass().isInstance(c)));
		System.out.println("b.getClass().isInstance(c))   "+(a.getClass().isInstance(c)));
		System.out.println(String.class==new String().getClass());//true
		
		
		Set s = new HashSet();
        s.add("foo");
        Iterator it = s.iterator();
        Class[] argsClass = new Class[0];
        Method m = Iterator.class.getMethod("hasNext",argsClass);//这个方法是静态的，所以不能用getMethod（）
        System.out.println(m.invoke(it,argsClass));                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
        
        
        test();
	}
	public static void test(){
		System.out.println("test()--------------");
		Set s = new HashSet();
        s.add("foo");
        Iterator it = s.iterator();
        Class[] argsClass = new Class[0];
        System.out.println("Set.class-----    "+Set.class);
        System.out.println("Iterator.class-----    "+Iterator.class);
        System.out.println("s.getClass()-----    "+s.getClass());
        System.out.println("it.getClass()-----    "+it.getClass());
        B a=new B();
        C aa=new C();
        B aaa=new C();
        System.out.println("A.class-----    "+B.class);
        System.out.println("a.getClass()-----    "+a.getClass());
        System.out.println("AA.class-----    "+C.class);
        System.out.println("aa.getClass()-----    "+aa.getClass());
        System.out.println("aaa.getClass()-----    "+aaa.getClass());
	}
}
