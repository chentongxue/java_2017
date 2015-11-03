package single_ton;

import java.util.HashMap;
import java.util.Map;
/**
 * 记录式单例,构造函数略有差异
 * 
 */
public class SingleTon3_2 {
	private static Map<String, SingleTon3_2> map = new HashMap<String, SingleTon3_2>();
	private String nameStr;
	private String data1;//都得是有默认值的参数
//	static {
//		SingleTon3_2 single = new SingleTon3_2();
//		map.put(single.getClass().getName(), single);
//	}

	private SingleTon3_2(String nameStr) {
		this.nameStr = nameStr;
		this.data1 = "moren";//通过配置文件等找
	
		System.out.println("构造method"+nameStr);
	}

	public static SingleTon3_2 getInstance(String name) {
		if (name == null) {
			name = SingleTon3_2.class.getName();
		}
		if (map.get(name) == null) {
			System.out.println("得到" + name);
			try {
				map.put(name,new SingleTon3_2(name));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map.get(name);
	}

	public static void main(String[] args) {
		System.out.println("Main方法");
		SingleTon3_2 single = SingleTon3_2.getInstance(null);
		SingleTon3_2 single2 = SingleTon3_2.getInstance("1");
		SingleTon3_2 single3 = SingleTon3_2.getInstance("b");
		SingleTon3_2 single4 = SingleTon3_2.getInstance("b");
		SingleTon3_2 single5 = SingleTon3_2.getInstance("b");
		SingleTon3_2 single6 = SingleTon3_2.getInstance("b");
	}

}
