package sacred.alliance.magic.util;

public class LongTool {
	
	private static final long One = 1;
	
	/**
	 * 取long型变量data的第index位置的值
	 * @param data
	 * @param index
	 * @return
	 */
	public static long getIndexValue(long data, int index){
		return (data>>index)&One;
	}
	
	/**
	 * 将long型变量data的第index位置1
	 * @param data
	 * @param index
	 * @return
	 */
	public static long setIndexValueOne(long data, int index){
		return data|(One<<index);
	}
	
	/**
	 * 将long型变量data的第index位清0
	 * @param data
	 * @param index
	 * @return
	 */
	public static long setIndexValueZero(long data, int index){
		return data&~(One<<index);
	}
	
	/*
	 * (1) 判断int型变量a是奇数还是偶数 a&1 = 0 偶数 a&1 = 1 奇数 
	 * (2) 取int型变量a的第k位(k=0,1,2……sizeof(int))，即a>>k&1 
	 * (3) 将int型变量a的第k位清0，即a=a&~(1<<k) 
	 * (4) 将int型变量a的第k位置1， 即a=a|(1<<k) 
	 * (5) int型变量循环左移k次，即a=a<<k|a>>16-k(设sizeof(int)=16) 
	 * (6) int型变量a循环右移k次，即a=a>>k|a<<16-k(设sizeof(int)=16)
	 */
	
}
