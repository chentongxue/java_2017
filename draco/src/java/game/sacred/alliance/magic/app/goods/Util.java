package sacred.alliance.magic.app.goods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
public class Util {
	public static String TARGET = "ENTER";
	public static String BACKSLASH = "\n";
	

	public static String replace(String src){
		if(null == src || 0 == src.trim().length()){
			return "";
		}
		return src.trim().replace(Util.TARGET,Util.BACKSLASH);
	}

	

	/**
	 * 判断数据类型是否为百分比
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isPerc(String value) {
		if (isEmpty(value)) {
			return false;
		}
		if (value.indexOf("%") == -1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 转换为数值
	 * 
	 * @param value
	 * @return
	 */
	public static float toValue(String value) {
		float fValue = 0;
		if (isEmpty(value)) {
			return fValue;
		}
		if (isPerc(value)) {
			fValue = perToValue(value);
		} else {
			fValue = toFloat(value);
		}
		return fValue;
	}

	/**
	 * 数值转换为百分比
	 * 
	 * @param value
	 * @return
	 */
	public static float perToValue(String value) {
		if (isEmpty(value)) {
			return 0;
		}
		if (isPerc(value)) {
			value = value.substring(0, value.indexOf("%"));
		}
		return Float.parseFloat(value) / 100;
	}

	
	

	/**
	 * String to float
	 * 
	 * @param value
	 * @return
	 */
	public static float toFloat(String value) {
		if (isEmpty(value)) {
			return 0;
		}
		return Float.parseFloat(value);
	}

	

	/**
	 * 字符串折分
	 * 
	 * @param str
	 * @param cat
	 * @return
	 */
	public static String[] splitStr(String str, String cat) {
		if (isEmpty(str)) {
			return new String[0];
		}
		return str.split(cat);
	}
	
	/**
	 * 字符串多维折分
	 * 
	 * @param str
	 * @param cat1
	 * @param cat2
	 * @return
	 */
	public static List<String[]> splitMultStr(String str, String cat1, String cat2) {
		if (isEmpty(str)) {
			return null ;
		}
		List<String[]> list = new ArrayList<String[]>();
		String[] strArr1 = splitStr(str, cat1);
		for (String str1 : strArr1) {
			String[] strArr2 = splitStr(str1, cat2);
			list.add(strArr2);
		}
		return list;
	}



	/**
	 * 判断对象是否为空
	 * 
	 * @param <T>
	 * @param obj
	 * @return
	 */
	public static <T> boolean isEmpty(T obj) {
		if (obj == null) {
			return true;
		}
		return false;
	}
	
	public static boolean isEmpty(String str) {
		return (null == str) || 0 == str.trim().length() ;
	}
	
	public static boolean isEmpty(Collection<?> collection) {
		if (collection == null) {
			return true;
		} else if (collection.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEmpty(Map<?, ?> map) {
		if (map == null) {
			return true;
		} else if (map.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	
	
	

	public static int[] strArrayToInt(String[] src){
		if(null == src || src.length == 0){
			return null;
		}
		int len = src.length;
		int[] result = new int[len];
		for(int i = 0; i <  len; i++){
			result[i] = Integer.valueOf(src[i]);
		}
		return result;
	}
	
	public static int[] listToInt(List<Integer> list){
		if(isEmpty(list)){
			return null;
		}
		int len = list.size();
		int[] result = new int[len];
		for(int i = 0; i < len; i++){
			result[i] = list.get(i);
		}
		
		return result;
	}
	
	public static int[] setToIntArray(Set<Integer> set) {
		if(isEmpty(set)){
			return null;
		}
		Integer[] temp = set.toArray(new Integer[set.size()]);
		int len = temp.length;
		int[] result = new int[len];
		for(int i = 0; i < len; i++){
			result[i] = temp[i].intValue();
		}
		return result;
	}
	
	public static byte[] toArray(List<Byte> list){
		if(isEmpty(list)){
			return null;
		}
		int len = list.size();
		byte[] result = new byte [len];
		for(int i = 0; i < len; i++){
			result[i] = list.get(i);
		}
		return result;
	}
	
	
	public static String getString(String[] cols,int index){
		if(null == cols || index >= cols.length || index <0){
			return "" ;
		}
		return cols[index] ;
	}

	public static <K, V> Map.Entry<K, V> removeFirst(Map<K, V> map) {
		if (Util.isEmpty(map)) {
			return null;
		}
		for (java.util.Iterator<Map.Entry<K, V>> it = map.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			it.remove();
			return entry;
		}
		return null;
	}
	
	public static <V> String toString(Set<V> set){
		if(Util.isEmpty(set)){
			return "" ;
		}
		String cat = "" ;
		StringBuffer buffer = new StringBuffer("");
		for(V v : set){
			buffer.append(cat).append(v);
			cat = "," ;
		}
		return buffer.toString() ;
	}
	
	public static Set<Integer> toIntegerSet(String str){
		Set<Integer> set = Sets.newLinkedHashSet();
		if(Util.isEmpty(str)){
			return set ;
		}
		String[] ss = Util.splitStr(str, ",");
		for(String s : ss){
			set.add(Integer.parseInt(s));
		}
		return set ;
	}
	
	
}
