package java8_2;

import java.util.Arrays;
import java.util.List;


/**
  3.֧�ֺ������
     Ϊ��֧�ֺ�����̣�Java 8������һ���µİ�java.util.function��
     ������һ���ӿ�java.util.function.Predicate��֧��Lambda������̣�
 */
public class Test3 {

	public static void main(String[] args) {
		String str = "abc";
		List<String> languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");
		  
		  System.out.println("Languages which starts with J :");
		  filter(languages, (str)->str.startsWith("J"));
		  
		  System.out.println("Languages which ends with a ");
		  filter(languages, (str)->str.endsWith("a"));
		  
		  System.out.println("Print all languages :");
		  filter(languages, (str)->true);
		  
		   System.out.println("Print no language : ");
		   filter(languages, (str)->false);
		  
		   System.out.println("Print language whose length greater than 4:");
//		   filter(languages, (str)->str. > 4);
		}
		  
		public	static void filter(List names, Predicate condition) {
		    names.stream().filter((name) -> (condition.test(name)))
		        .forEach((name) -> {System.out.println(name + " ");
		    });
		 }
}