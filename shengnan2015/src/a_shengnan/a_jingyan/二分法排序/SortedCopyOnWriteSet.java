package a_shengnan.a_jingyan.¶þ·Ö·¨ÅÅÐò;

import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public class SortedCopyOnWriteSet<T> extends CopyOnWriteArrayList<T> {
	protected Comparator<T> c;
	public SortedCopyOnWriteSet(Comparator<T> c) {
		this.c = c;
	}
	@Override
	public boolean add(T e) {
		remove(e);

		int insertionPoint = Collections.binarySearch(this, e, c);
		super.add((insertionPoint > -1) ? insertionPoint : (-insertionPoint) - 1, e);
		return true;
	}
}
