package sort;

import java.util.Arrays;

public class APIsort {

	/**
	 * 插入排序,如果元素少的话，byte数组的排序采用插入排序，详细见API
	 */
	public static void main(String[] args) {
		byte[] a = {2,1,3,6,5,4,9,8,7};
		sort(a,0,8);
		Arrays.sort(a);
		System.out.println(Arrays.toString(a));

	}
	public static void sort(byte[] a,int left,int right){
		System.out.println("初始数组 :\n"+Arrays.toString(a));
		for(int i = left,j = i;i<right;j=++i){
			byte ai = a[i+1];
			System.out.println("ai="+ai+"  a[j] = "+a[j]);
			while(ai<a[j]){
//				System.out.println("ai<a[j]  "+ai+" <"+a[j]);
				a[j+1]= a[j];
				if(j-- == left){
					break;
				}
			}//while就
			a[j+1] = ai;
			System.out.println(a[j+1]+"数组变为 :\n"+Arrays.toString(a));
		}
	}
}
