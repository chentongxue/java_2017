package single_ton;

import java.util.HashMap;
import java.util.Map;
/**
 * 记录式单例
 */
public class SingleTon3 {
	private static Map<String, SingleTon3> map = new HashMap<String, SingleTon3>();
	static {
		SingleTon3 single = new SingleTon3();
		map.put(single.getClass().getName(), single);
	}

	private SingleTon3() {
		System.out.println("构造method");
	}

	public static SingleTon3 getInstance(String name) {
		if (name == null) {
			name = SingleTon3.class.getName();
		}
		if (map.get(name) == null) {
			System.out.println("得到" + name);
			try {
				map.put(name, (SingleTon3) Class.forName("single_ton.SingleTon3").newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return map.get(name);
	}

	public static void main(String[] args) {
		System.out.println("Main方法");
		SingleTon3 single = SingleTon3.getInstance(null);
		SingleTon3 single2 = SingleTon3.getInstance("1");
		SingleTon3 single3 = SingleTon3.getInstance("b");
		SingleTon3 single4 = SingleTon3.getInstance("b");
		SingleTon3 single5 = SingleTon3.getInstance("b");
		SingleTon3 single6 = SingleTon3.getInstance("b");
	}

}
