package sort;

import java.util.Arrays;

public class APIsort {

	/**
	 * ��������,���Ԫ���ٵĻ���byte�����������ò���������ϸ��API
	 */
	public static void main(String[] args) {
		byte[] a = {2,1,3,6,5,4,9,8,7};
		sort(a,0,8);
		Arrays.sort(a);
		System.out.println(Arrays.toString(a));

	}
	public static void sort(byte[] a,int left,int right){
		System.out.println("��ʼ���� :\n"+Arrays.toString(a));
		for(int i = left,j = i;i<right;j=++i){
			byte ai = a[i+1];
			System.out.println("ai="+ai+"  a[j] = "+a[j]);
			while(ai<a[j]){
//				System.out.println("ai<a[j]  "+ai+" <"+a[j]);
				a[j+1]= a[j];
				if(j-- == left){
					break;
				}
			}//while��
			a[j+1] = ai;
			System.out.println(a[j+1]+"�����Ϊ :\n"+Arrays.toString(a));
		}
	}
}
