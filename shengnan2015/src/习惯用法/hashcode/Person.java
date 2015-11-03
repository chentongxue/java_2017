package 习惯用法.hashcode;

import java.util.Arrays;
import java.util.HashMap;

import com.google.common.collect.Maps;
/**
 * 
	hashCode()最简单的合法实现就是简单地return 0；虽然这个实现是正确的，但是这会导致HashMap这些数据结构运行得很慢。
	注意如果如果a或b未赋值会产生java.lang.NullPointerException
 *
 */
public class Person {
	private String a;
	private Object b;
	private byte c;
	int [] d;
	
	@Override
	public int hashCode(){
		return a.hashCode() + b.hashCode() + c + Arrays.hashCode(d);
	}
	public static void main(String[] args) {
		Person p = new Person();
		Person p1 = new Person();
		HashMap map = Maps.newHashMap();
		map.put(p,"hi");
		map.put(p1,"hi2");
		System.out.println(map);
		System.out.println(map.get(p));
		System.out.println(map.get(p1));
//		System.out.println(p.hashCode());
	}
}
