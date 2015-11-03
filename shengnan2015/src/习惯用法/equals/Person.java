package ϰ���÷�.equals;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
/**
 * ʵ��equals()
 * ����������Object������������Χ��
 * ����equals()ʱ��ҲҪ������Ӧ��hashCode(),��equals(),����һ��
 * ����ע��String��Ĭ��ֵ��null
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
