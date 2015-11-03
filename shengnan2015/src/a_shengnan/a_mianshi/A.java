package a_shengnan.a_mianshi;
/**
 * 内部类和静态内部类的区别
 *
 * Static Nested Class是被声明为静态（static）的内部类，它可以不依赖于外部类实例被实例化。
 * 而通常的内部类需要在外部类实例化后才能实例化。想要理解static应用于内部类时的含义，你就必须记住，
 *	普通的内部类对象隐含地保存了一个引用，指向创建它的外围类对象。然而，当内部类是static的时，就不是这样了。
 */
public class A {
	private int a;
	private static class B{
		int b = 0;
		public void method(){
			int c = 0;
			b = 0;
			a = 0;
		}
	}

}
