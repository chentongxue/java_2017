package google_test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.*;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = CharMatcher.DIGIT.retainFrom("some text   899  83  and more");
		System.err.println(s);
		
		
		String s2 = CharMatcher.DIGIT.removeFrom("some text 89983 and more");
		System.err.println(s2);
		
		
		int[] numbers = { 1, 2, 3, 4, 5 };
		String cc = Joiner.on(";").join(Ints.asList(numbers));

		System.err.println(cc);
		
		
		Map<String, Integer> user = new HashMap<String, Integer>();  
        user.put("张三", 20);  
        user.put("李四", 22);  
        user.put("王五", 25);  
        // 所有年龄大于20岁的人员  
        Map<String, Integer> filtedMap = Maps.filterValues(user,  
                new Predicate<Integer>() {  
                    public boolean apply(Integer value) {  
                        return value > 20;  
                    }  
                });  
        System.out.println(filtedMap);  
        
        //----------------------
        Multimap<String,String> phonebook=ArrayListMultimap.create(); 
        phonebook.put("a","43434"); 
        phonebook.put("a","3434434"); 
      System.out.println(phonebook.get("a")); 

		test();
	}
	public static void test(){
		String s = "I love you";
		char[] chars = s.toCharArray();
		for (char c : chars) {
			System.err.println(c);
		}
	}

}
