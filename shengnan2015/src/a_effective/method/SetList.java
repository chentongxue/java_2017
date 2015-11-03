package a_effective.method;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
/**
 * 慎用重载
 *
 */
public class SetList {
//167
	/**
	 * ①对于Set set.remove(i)调用的选择重载方法remove(E),这里的E是集合（Integer）的元素类型，将i自动装箱到Integer中。
	 * ②对于List，list.remove(i)调用选择重载方法remove(int i);它从列表指定位置上去除元素。要解决这个问题要么传入参数为Integer，要么调用Integer.valueOf(i)
	 * ③正因为List<E>接口有两个重载的remove方法：remove（E），和remove（int），相应的参数类型Object和int，则根本不同。但是自从有了泛型和自动装箱后，这两种参数类型就不在根本不同了。
	 * 因此Java语言添加了泛型和自动装箱后破坏了List接口。
	 * 	int a = Integer.parseInt("1");
		Integer b= Integer.valueOf("2");
		Integer c = Integer.getInteger("3");//注意不要使用这个，不要与前两个相混淆
		
		虽然1.4版本String类已经有一个contentEquals(StringBuffer)方法，在1.5中新增了CharSequence接口，并且String新增了重载的contentEquals方法即contentEquals(CharSequence)
		但只要这两个方法在同样的参数上被调用时，它们执行相同的功能，重载就不会带来危害。
		public boolean contentEquals(StringBuffer sb) {
        synchronized(sb) {
            return contentEquals((CharSequence)sb);
        }
    }
	 */
	public static void main(String[] args) {
		Set<Integer> set = new TreeSet<Integer>();
		List<Integer> list= new ArrayList<Integer>();
		for(int i = -3; i< 3; i++){
			set.add(i);
			list.add(i);
		}
		for(int i = 0; i< 3; i++){
			set.remove(i);
			list.remove(i);
		}
		System.out.println("set = " + set);//set = [-3, -2, -1]
		System.out.println("list = " + list);//list = [-2, 0, 2]
		test1();
		test2();
	}
	public static void test1(){
		List<Integer> list= new ArrayList<Integer>();
		for(int i = -3; i< 3; i++){
			list.add(i);
		}
		for(int i = 0; i< 3; i++){
			list.remove((Integer)i);
		}
		System.out.println("list = " + list);//[-3, -2, -1]
	}
	public static void test2(){
		List<Integer> list= new ArrayList<Integer>();
		for(int i = -3; i< 3; i++){
			list.add(i);
		}
		for(int i = 0; i< 3; i++){
			list.remove((Integer.valueOf(i)));
		}
		System.out.println("list = " + list);//[-3, -2, -1]
	}
	public static void test3(){
		LinkedHashMap<Integer,Integer> map = new LinkedHashMap<>();
		
		List<Integer> list= new ArrayList<Integer>();
		for(int i = -3; i< 3; i++){
			list.add(i);
		}
		for(int i = 0; i< 3; i++){
			list.remove((Integer.valueOf(i)));
		}
		System.out.println("list = " + list);//[-3, -2, -1]
	}
}
