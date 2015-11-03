package comparebale.celuejiekou;

import java.io.Serializable;
import java.util.Comparator;

public class Host {
	private static class StrLenCmp implements Comparator<String>, Serializable{
		@Override
		public int compare(String o1, String o2) {
			return o1.length() - o2.length();
		}
	}
	public static final Comparator<String> STRING_LENGTH_COMPARATOR = new StrLenCmp();
	public static void main(String[] args) {

	}

}
/**
 Ƕ���ࣨnested class��������
 
��̬��Ա�� static member class
�ڲ��ࣨ3��
�Ǿ�̬��Ա�� nonstatic member class
������ anonymous class
�ֲ��� local class
 */
