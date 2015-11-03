package alex;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
/**
 * ReflectionToStringBuilderµÄÊ¹ÓÃ
 *
 */
public class StudentBoy extends Student{
	private String addr;
	public StudentBoy(){
		super();
	}
	public StudentBoy(String name, int age, String addr){
		super(name, age);
		this.addr = addr;
	}
	
	public static void main(String args[]){
		Student s = new StudentBoy("nan", 16,"chifeng");
		Student s1 = new StudentBoy("bao", 17,"yantai");
		Student s2 = new StudentBoy("lee", 18,"qingdao");
		System.out.println(s);
		System.out.println(s1);
		System.out.println(s2);
		
	}
//	@Override
//	public String toString() {
//		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
//	}

}
