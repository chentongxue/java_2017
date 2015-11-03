package alex;

public class StringReplace {
	public static void main(String args[]){
		String s = "abcc";
		s = s.replace("a", "A");
		s = replaceComma(s);
		System.out.println(s);
	}
	public static String replaceComma(String str){
		str = str.replace("b", "mmm");
		str = "nulll";
		return str;
	}

}
