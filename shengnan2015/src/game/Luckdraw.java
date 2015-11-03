package game;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;
/**
 * HashSet��������
 * TreeSey��˳��
 * @author mofun030601
 *
 */
public class Luckdraw {

	private Luckdraw(){}
	private static Luckdraw instance = null;
	public static synchronized Luckdraw getInstance(){
		if(instance == null){
			instance = new Luckdraw();
		}
		return instance;
	}
//	public void  get
	public static void main(String[] args) {
		
		TreeSet set = new TreeSet<Integer>();
         int n = 100000;
         while(n-->=0){
//        	 int a = (int)(Math.random()*100);
        	 int a = Luckdraw.getInstance().getRandom(10, 10);
        	 set.add(a);
         }
         System.out.println(Arrays.toString(set.toArray()));
         Collections.shuffle(null); 
         System.out.println();
	}
	/**
	 * �����߽�
	 * @param left
	 * @param right
	 * @return
	 */
	public int getRandom(int left ,int right){
		if(left == right){
			return left ;
		}
		if(right < left){
			return (int) (Math.random() * (left - right + 1)) + right;
		}
		return (int) (Math.random() * (right - left + 1)) + left;
	}
	/**
	 * 
	 * @param oddsArrs ��������
	 * @param left ��߽�
	 * @param right �ұ߽�
	 * @return ����������±�
	 */
	public int getRandom(int[] oddsArrs){
		int sum = 0;
		for (int i : oddsArrs) {
			sum+=i;
		}
		return 0;
	}

}
