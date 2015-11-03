import java.util.Arrays;


public class Test2 {

	public static int counts = 0;
	public static int a[] = new int[100];
	public static void main(String[] args) {
		counts = 0;
		comb(2,1);
		System.out.println(counts);
//		System.out.println(Arrays.toString(a));
		
	}
	public static void comb(int m, int k)
	{
		int i, j;
		for (i = m; i >= k; i--)
		{
			if(k>1)
			System.out.println((i-1)+","+(k-1));
//			a[k] = i;
//			System.out.println(i);
			if (k>1)
				comb(i - 1, k - 1);
			else
			{
				counts++;
				System.out.println("----"+i+","+(k));
			}
		}
	}

}
