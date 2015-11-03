package sort;

// 冒泡排序
public class BubbleSort {
	// 从小到大
	public static void sort(Comparable[] data) {
		// 数组长度
		int len = data.length;
		for (int i = 0; i < len - 1; i++) {
			// 临时变量
			Comparable temp = null;
			// 交换标志，false 表示未交换
			boolean isExchanged = false;
			for (int j = len - 1; j > i; j--) {
				// 如果data[j]小于data[j-1],交换
				if (data[j].compareTo(data[j - 1]) < 0) {
					temp = data[j];
					data[j] = data[j - 1];
					data[j - 1] = temp;
					// 发生了交换，故将交换标志置为真
					isExchanged = true;
				}// end if
			}// end for
		}// end for
	}// end sort
	// 我的理解 从小到大
	public static void sort2(Comparable[] data) {
		// 数组长度
		int len = data.length;
		for (int i = 0; i < len; i++) {
			// 临时变量
			Comparable temp = null;
			// 交换标志，false 表示未交换
			for (int j = 0; j < len - i-1; j++) {
				// 如果data[j]小于data[j-1],交换
				if (data[j].compareTo(data[j + 1]) > 0) {
					temp = data[j];
					data[j] = data[j + 1];
					data[j + 1] = temp;
				}// end if
			}// end for
		}// end for
	}// end sort

	public static void main(String args[]) {
		System.out.println("hello sort");
		// 在JDK1.5以上版本，                                                                                                                                                                                                                                                                                                                                      实现了Comparable接口
		Comparable[] c = { 1, 4, 7, 2, 5, 8, 3, 6, 9 };
		sort2(c);
		for (Comparable data : c) {
			System.out.println(data);
		}
	}
}
