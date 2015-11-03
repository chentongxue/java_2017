package a_effective.method;

public class Test {
	public static void main(String args[]){
		long a[] = {1L, 2L, 3L};
		sort(null, 1, 4);
	}
	private static void sort(long a[], int offset, int length){
		assert a != null;
		assert offset >=0 && offset <= a.length;
		assert length >=0 && length <= a.length - offset;
	}
}
