package a_shengnan.a_jingyan;

import java.util.ArrayList;
import java.util.List;

public class TestLongArr {

	public static void main(String[] args) {
		List<Long> list = new ArrayList<Long>();
		list.add(null);
		list.add(2L);
		list.add(3L);
		System.out.println(list);
		long [] array = list.stream().mapToLong(t->t.longValue()).toArray();
	}
}
