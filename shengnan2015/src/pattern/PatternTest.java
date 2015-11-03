package pattern;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

public class PatternTest {

	public static void main(String[] args) {
		List<String> list = Lists.newArrayList("1_2","1_1_","2","3","4","5","6","7","8","9","-1","a");
		for (String string : list) {
			System.out.println(string+":"+isVipLevelmatch(string));
		}
		System.out.println(Integer.MAX_VALUE);
		byte a = 16>>4-1;
		System.out.println(a);
	}
    public static boolean isNumeric(String str){   
        Pattern pattern = Pattern.compile("[0-9]*");   
        return pattern.matcher(str).matches();      
    }
    public static boolean isVipLevelmatch(String str){   
    	Pattern pattern = Pattern.compile("[1_]");   
    	return pattern.matcher(str).matches();      
    }
}
