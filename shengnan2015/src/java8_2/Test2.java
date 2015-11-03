package java8_2;

import java.util.Arrays;
import java.util.List;

/**
 	2.使用内循环替代外循环
 	外循环：描述怎么干，代码里嵌套2个以上的for循环的都比较难读懂；只能顺序处理List中的元素；
	内循环：描述要干什么，而不是怎么干；不一定需要顺序处理List中的元素
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
