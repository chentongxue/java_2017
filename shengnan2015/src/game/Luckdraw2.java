package game;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.RandomAccess;
import java.util.TreeSet;
/**
 * HashSet杂乱无序
 * TreeSey有顺序
 * @author mofun030601
 *
 */
public class Luckdraw2 {

	private Luckdraw2(){}
	private static Luckdraw2 instance = null;
    private static final int SHUFFLE_THRESHOLD        =    5;
	public static synchronized Luckdraw2 getInstance(){
		if(instance == null){
			instance = new Luckdraw2();
		}
		return instance;
	}
//	public void  get
	public static void main(String[] args) {
		
		TreeSet set = new TreeSet<Integer>();
         int n = 100000;
         while(n-->=0){
//        	 int a = (int)(Math.random()*100);
        	 int a = Luckdraw2.getInstance().getRandom(10, 10);
        	 set.add(a);
         }
         System.out.println(Arrays.toString(set.toArray()));
         int [] arrs  = new int[100];
         for (int i = 0; i < arrs.length; i++) {
			arrs[i] = i;
		}
         System.out.println(Arrays.toString(arrs));
//         Luckdraw2.getInstance().shuffle(arrs);
//         Luckdraw2.getInstance().bubbleShuffle(arrs);
         System.out.println(Arrays.toString(arrs));
         Map map = new HashMap<Integer, Integer>();
         System.out.println((map.values()).getClass().getName());
         System.out.println((int)0.6);
	}
	/**
	 * 包含边界
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
	 * @param oddsArrs 几率数组
	 * @param left 左边界
	 * @param right 右边界
	 * @return 返回数组的下标
	 */
	public int getRandom(int[] oddsArrs){
		int sum = 0;
		for (int i : oddsArrs) {
			sum+=i;
		}
		return 0;
	}

   /** 
    * 打乱数组，去重
    * anthor:bao
    */
   public  void CleanShuffle(int[] arr) {
	   int len = arr.length;
	   for (int i = 0; i < len - 3; i++) {
		   int index  = (int)(Math.random()*(len - 2 - i));
		   System.out.println("随机index = ["+index+"]len - 2 - i ="+(len - 2 - i));
		   if(arr[index]!=arr[len - 1 - i])
			   swap(arr,len - 1 - i,index);
		   }
	   swap(arr, 0, 1);
   }
   private void swap(int[] arr, int i, int j) {
       int tmp = arr[i];
       arr[i] = arr[j];
       arr[j] = tmp;
   }

}
