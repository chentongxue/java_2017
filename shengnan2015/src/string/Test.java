package string;

public class Test {

	private static String a = "2015-09-18 00:05:12,yyb,common,ky,1,1,457023187535667,1001,LogOn         ,7F8E6307CB30F79E50991E155349384F,";
	public static void main(String[] args) {
		String[] info = a.split(",");
		System.out.println(info.length);
		System.out.println(info[9]);
	}
	private String getS(){
		a = "hello";
		a.replace("o", "a");
		return a;
	}
}
