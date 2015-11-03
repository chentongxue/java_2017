package sort;

import java.util.Arrays;

public class BinarySearch {

	/**
	 * 二分法查找
	 */
	public int binarySearch(int dataset[], int target, int beginIndex,
			int endIndex) {
		// 数组校验
		if (dataset == null || dataset.length == 0) {
			return -1;
		}
		// beginIndex,endIndex校验
		if (beginIndex > endIndex || beginIndex > dataset.length - 1
				|| endIndex > dataset.length - 1 || beginIndex < 0
				|| endIndex < 0) {
			System.out.println("error arguments!");
			return -1;
		}
		// 无效参数处理
		if (target < dataset[beginIndex] || target > dataset[endIndex]
				|| beginIndex > endIndex) {
			return -1;
		}
		int midIndex = (beginIndex + endIndex) / 2;
		// System.out.println(midIndex);
		if (target == dataset[midIndex]) {
			return midIndex;
		} else if (target < dataset[midIndex]) {
			return binarySearch(dataset, target, beginIndex, midIndex - 1);//midIndex - 1
		} else {
			return binarySearch(dataset, target, midIndex + 1, endIndex);// midIndex + 1
		}
	}

	public static void main(String[] args) {
		BinarySearch bs = new BinarySearch();
		int data[] = new int[]{1,3,5,7,9,12};
		for (int i = 0; i < data.length; i++) {
			int index = bs.binarySearch(data, data[i], 0, data.length-1);
			System.out.println( Arrays.toString(data));
			System.out.println("<"+data[i]+">");
			System.out.println(index);
			System.out.println("-------------------");
		}
	}

}
