package sort;

// ð������
public class BubbleSort {
	// ��С����
	public static void sort(Comparable[] data) {
		// ���鳤��
		int len = data.length;
		for (int i = 0; i < len - 1; i++) {
			// ��ʱ����
			Comparable temp = null;
			// ������־��false ��ʾδ����
			boolean isExchanged = false;
			for (int j = len - 1; j > i; j--) {
				// ���data[j]С��data[j-1],����
				if (data[j].compareTo(data[j - 1]) < 0) {
					temp = data[j];
					data[j] = data[j - 1];
					data[j - 1] = temp;
					// �����˽������ʽ�������־��Ϊ��
					isExchanged = true;
				}// end if
			}// end for
		}// end for
	}// end sort
	// �ҵ���� ��С����
	public static void sort2(Comparable[] data) {
		// ���鳤��
		int len = data.length;
		for (int i = 0; i < len; i++) {
			// ��ʱ����
			Comparable temp = null;
			// ������־��false ��ʾδ����
			for (int j = 0; j < len - i-1; j++) {
				// ���data[j]С��data[j-1],����
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
		// ��JDK1.5���ϰ汾��                                                                                                                                                                                                                                                                                                                                      ʵ����Comparable�ӿ�
		Comparable[] c = { 1, 4, 7, 2, 5, 8, 3, 6, 9 };
		sort2(c);
		for (Comparable data : c) {
			System.out.println(data);
		}
	}
}
