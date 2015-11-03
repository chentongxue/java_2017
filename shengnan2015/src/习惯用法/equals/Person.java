package 习惯用法.equals;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
/**
 * 实现equals()
 * 参数必须是Object，而不能是外围类
 * 覆盖equals()时，也要覆盖相应的hashCode(),与equals(),保持一致
 * 另外注意String的默认值是null
 */
public class Person {
	private String name;
	private int birthYear;
	byte[] raw;
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Person)){
			return false;
		}
		Person other = (Person)o;
		return StringUtils.equals(name, other.name)
				&& birthYear == other.birthYear
				&& Arrays.equals(raw, other.raw);
	}
	public static void main(String args[]){
		Person p = new Person();
		Person p2 = new Person();
		System.out.println(p.equals(p2));
	}
}
