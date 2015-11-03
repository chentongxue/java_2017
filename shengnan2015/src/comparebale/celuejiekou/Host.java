package comparebale.celuejiekou;

import java.io.Serializable;
import java.util.Comparator;

public class Host {
	private static class StrLenCmp implements Comparator<String>, Serializable{
		@Override
		public int compare(String o1, String o2) {
			return o1.length() - o2.length();
		}
	}
	public static final Comparator<String> STRING_LENGTH_COMPARATOR = new StrLenCmp();
	public static void main(String[] args) {

	}

}
/**
 嵌套类（nested class）有四种
 
静态成员类 static member class
内部类（3）
非静态成员类 nonstatic member class
匿名类 anonymous class
局部类 local class
 */
