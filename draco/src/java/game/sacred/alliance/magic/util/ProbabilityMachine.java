package sacred.alliance.magic.util;

import java.security.SecureRandom;


public class ProbabilityMachine {
	
	public static int EXCHANGE_RATE = 100;
	
	
	public static int BOX_EXIST_MIllIS_TIME = 5000;
	
	/**
	 * 比率系数
	 */
	public static int RATE_MODULUS = 100000;
	
	/**
	 * 100的倍数
	 */
	public static int RATE_MODULUS_HUNDRED_MULTI = Math.max(1,RATE_MODULUS/100);
	
	/**
	 * 计算百分比的乘数
	 */
	public static int RATE_PERCENT_MODULUS = 100;
	
	/**
	 * 比率系数(计算百分比使用)
	 */
	public static int RATE_CALCULATE_PERCENT_MODULUS = 10000;
	
	
	private static SecureRandom random = new SecureRandom();

	
	public static boolean isProbability(int odds){
		if(odds <= 0){
			return false ;
		}
		if(RATE_MODULUS <= odds){
			return true ;
		}
		return Math.abs(random.nextInt(RATE_MODULUS + 1)) <= odds ;
	}

	public static int randomInt(int limit) {
		if (limit <= 0) {
			throw new RuntimeException("随机数上限错误:" + limit);
		}
		return  Math.abs(random.nextInt()) % limit;
	}
	
	
	public static int randomIntWithoutZero(int limit) {
		if (limit <= 0) {
			throw new RuntimeException("随机数上限错误:" + limit);
		}
		int result = Math.abs(random.nextInt()) % limit;
		if( 0  == result ){
			return 1;
		}
		return  result + 1;
	}

	public static int absRandomInt(int limit) {
		return Math.abs(randomInt(limit));
	}
	

	public static int getRandomNum(int min, int max) {
		return (int) Math.round(Math.random() * (max - min) + min);
	}

	
}
