package sacred.alliance.magic.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class SortedValueMap {

	/**
	 * Sort in ascending order?
	 */
	private boolean flagAscending = false; //倒序排列

	/**
	 * Ascending or descending?
	 */
	private static boolean default_flagAscending = false; //倒序排列

	/**
	 * Use this BEFORE you stick in items.
	 * 
	 * @param flag
	 *            is true if ascending order, false if descending.
	 */
	public void setAscending(boolean flag) {
		flagAscending = flag;
	}

	/**
	 * Set the default value for ascending or descending order. This is the
	 * sorting regime that new SortedNumericalMaps will use.
	 * 
	 * @param flag
	 *            is true if new SortedNumericalMaps should sort in
	 *            ascending order, false if in descending order.
	 */
	public static void setDefaultAscending(boolean flag) {
		default_flagAscending = flag;
	}

	// // hashtable maps name->value
	Hashtable table;

	// // linkedlist maps value->name, value being sorted asc or descending
	// // soft-state, call updateList() to make it consistent
	LinkedList list;

	// // true if list is inconsistent and needs to be updated, false
	// otherwise.
	boolean flagDirty = false;

	// // the comparator used to sort the values in this map
	Comparator comp;

	// // the comparator that knows about our internal data structs,
	// // and uses comp to sort the values
	Comparator internalComp = new InternalComparator();

	class InternalComparator implements Comparator {

		public int compare(Object key1, Object key2) {
			// // 1. Compare the values.
			// // Each of these are Map.Entry objects.
			Map.Entry m1 = (Map.Entry) key1;
			Map.Entry m2 = (Map.Entry) key2;
			int returnVal = comp.compare(m1.getValue(), m2.getValue());

			// // 2. Allow multiple instances, so arbitrarily break ties.
			if (returnVal == 0) {
				if (m1.getKey().hashCode() > m2.getKey().hashCode()) {
					returnVal = 1;
				} else {
					returnVal = -1;
				}
			}

			// // 3. Flip the values if we are in descending order.
			if (flagAscending == true) {
				return (returnVal);
			} else {
				return (-1 * returnVal);
			}
		}

	}

	public SortedValueMap(Comparator newComp) {
		// // 1. Create the data struct to hold the values.
		table = new Hashtable();
		comp = newComp;
	}

	/**
	 * Make the sorted list consistent.
	 */
	private void updateList() {
		// // 1. If we are inconsistent with the table...
		if (flagDirty == true) {
			// // 1.1. Create a new list and then sort it.
			list = new LinkedList(table.entrySet());
			Collections.sort(list, internalComp);
			flagDirty = false;
		}
	}

	public void clear() {
		flagDirty = true;
		table.clear();
	}

	public boolean containsKey(Object key) {
		return (table.containsKey(key));
	}

	public boolean containsValue(Object value) {
		return (table.containsValue(value));
	}

	public Set entrySet() {
		return (table.entrySet());
	}

	public boolean equals(Object obj) {
		return (obj.hashCode() == this.hashCode());
	}

	public Object get(Object obj) {
		return (table.get(obj));
	}

	public boolean isEmpty() {
		return (table.isEmpty());
	}

	public Set keySet() {
		return (table.keySet());
	}

	public Object put(Object key, Object value) {
		flagDirty = true;
		return (table.put(key, value));
	}

	public void putAll(Map newMap) {
		Iterator it = newMap.keySet().iterator();
		Object key;

		while (it.hasNext()) {
			key = it.next();
			this.put(key, newMap.get(key));
		}
	}

	public Object remove(Object key) {
		flagDirty = true;
		return (table.remove(key));
	}

	public int size() {
		return (table.size());
	}

	public Collection values() {
		return (table.values());
	}

	/**
	 * Get the key at the beginning of the map.
	 * 
	 * @return whatever key is at the beginning. Will have a value that is
	 *         the largest or smallest value, depending on how the map is
	 *         sorted.
	 */
	public Object getFirstKey() {
		updateList();
		if (list.size() > 0) {
			Map.Entry m = (Map.Entry) list.getFirst();
			return (m.getKey());
		}
		return (null);
	}

	/**
	 * Get the key at the end of the map.
	 * 
	 * @return whatever key is at the end. Will have a value that is the
	 *         largest or smallest value, depending on how the map is
	 *         sorted.
	 */
	public Object getLastKey() {
		updateList();
		if (list.size() > 0) {
			Map.Entry m = (Map.Entry) list.getLast();
			return (m.getKey());
		}
		return (null);
	}

	public Object getRandomKey(){
		int size = list.size() ;
		if (size > 0) {
			Map.Entry m = (Map.Entry) list.get(RandomUtil.absRandomInt(size));
			return (m.getKey());
		}
		return (null);
	}
	/**
	 * Get the value at the beginning of the map.
	 * 
	 * @return whatever value is at the beginning. Either the largest or
	 *         smallest value, depending on how the map is sorted.
	 */
	public Object getFirstValue() {
		return (get(getFirstKey()));
	}

	/**
	 * Get the value at the end of the map.
	 * 
	 * @return whatever value is at the end. Either the largest or smallest
	 *         value, depending on how the map is sorted.
	 */
	public Object getLastValue() {
		return (get(getLastKey()));
	}

	/**
	 * Get the key with the largest value.
	 * 
	 * @return whatever key has the largest value.
	 */
	public Object getLargestKey() {
		if (flagAscending == true) {
			return (getLastKey());
		} else {
			return (getFirstKey());
		}
	}

	/**
	 * Get the key with the smallest value.
	 * 
	 * @return whatever key has the smallest value.
	 */
	public Object getSmallestKey() {
		if (flagAscending == true) {
			return (getFirstKey());
		} else {
			return (getLastKey());
		}
	}

	/**
	 * Get the largest value stored.
	 * 
	 * @return whatever the largest value is.
	 */
	public Object getLargestValue() {
		if (flagAscending == true) {
			return (getLastValue());
		} else {
			return (getFirstValue());
		}
	}

	/**
	 * Get the smallest value stored.
	 * 
	 * @return whatever the smallest value is.
	 */
	public Object getSmallestValue() {
		if (flagAscending == true) {
			return (getFirstValue());
		} else {
			return (getLastValue());
		}
	}

	public String toString() {
		updateList();
		return (list.toString());
	}

	public static void main(String[] argv) {
		SortedValueMap c = new SortedValueMap(new NumericalComparator());

		c.setAscending(false);
		c.put("kuang", 10);
		c.put("zhang", 100);
		c.put("tie", 100);
//		System.out.println(c);
//		System.out.println("first key=" + c.getFirstKey());
//		System.out.println("first value =" + c.getFirstValue());

		c.put("kuang", 110);
//		System.out.println(c);
//		System.out.println("first key=" + c.getFirstKey());
//		System.out.println("first value =" + c.getFirstValue());

		if (true) {
			return;
		}
		c.put("lemur", new Float(0.11f));
		c.put("wallaby", new Float(0.11f));
//		System.out.println(c);

		c.put("kiwi", new Float(0.11f));
		c.put("mandrill", new Float(0.11f));
//		System.out.println(c);

		c.put("emu", new Float(0.11f));
//		System.out.println(c);

		c.put("exceedsum", new Float(0.55f));
//		System.out.println(c);

		c.put("exceedval", new Float(1.05f));
//		System.out.println(c);

//		System.out.println(c.getFirstKey());
//		System.out.println(c.getFirstValue());
//		System.out.println(c.getLastKey());
//		System.out.println(c.getLastValue());
//		System.out.println(c.getSmallestKey());
//		System.out.println(c.getSmallestValue());
//		System.out.println(c.getLargestKey());
//		System.out.println(c.getLargestValue());

//		System.out.println();
//		System.out.println(c.get("exceedsum"));

	}
}