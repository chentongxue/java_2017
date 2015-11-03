package œ∞πﬂ”√∑®.stringbuilder;

import java.util.List;

import com.google.common.collect.Lists;

public class Test {

	public static String join(List<String> strs, String separator){
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(String s : strs){
			if(first)first = false;
			else sb.append(separator);
			sb.append(s);
		}
		return sb.toString();
	}
	public static String join1(List<String> strs, String separator){
		StringBuilder sb = new StringBuilder();
		int len = strs.size();
		for(int i = 0; i < len - 1; i++){
			sb.append(strs.get(i));
			sb.append(separator);
		}
		sb.append(strs.get(len - 1));
		return sb.toString();
	}
	public static void main(String[] args) {
		List<String> strs = Lists.newArrayList("a" , "b" , "c" , "d");
		String s = join1(strs, ",");
		System.out.println(s);
	}
}
