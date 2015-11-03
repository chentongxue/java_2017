package a_effective.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.math.BigInteger;
//166
/**
 * �Ƚϱ���Ĳ����ǣ���Զ��Ҫ��������������ͬ��Ŀ�����ط���.
 * ���ʹ�ÿɱ���������ص������Ǹ�������Ҫ������
 */
public class CollectionClassifier2 {
	public static String classify(Collection<?> s){
		return s instanceof Set ? "set":
			   s instanceof List? "List":
				   "unkown collection";
	}
	public static void main(String args[]){
		Collection<?>[] collections = {
				new HashSet<String>(),
				new ArrayList<BigInteger>(),
				new HashMap<String, String>().values()};
		for(Collection<?> c : collections){
			System.out.println(classify(c));
		}
	}
}
/*
set
List
unkown collection
*/
