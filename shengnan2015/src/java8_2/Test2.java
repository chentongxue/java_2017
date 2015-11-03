package java8_2;

import java.util.Arrays;
import java.util.List;

/**
 	2.ʹ����ѭ�������ѭ��
 	��ѭ����������ô�ɣ�������Ƕ��2�����ϵ�forѭ���Ķ��Ƚ��Ѷ�����ֻ��˳����List�е�Ԫ�أ�
	��ѭ��������Ҫ��ʲô����������ô�ɣ���һ����Ҫ˳����List�е�Ԫ��
 */
public class Test2 {
	public static void main(String args[]){
		//Prior Java 8 :
		List<String> features = Arrays.asList("Lambdas", "Default Method", "Stream API", "Date and Time API");
		for(String feature : features) {
		   System.out.println(feature);
		}
		  
		//In Java 8:
		features.forEach(n -> System.out.println(n));
		  
		// Even better use Method reference feature of Java 8
		// method reference is denoted by :: (double colon) operator
		// looks similar to score resolution operator of C++
		features.forEach(System.out::println);
	}

}
