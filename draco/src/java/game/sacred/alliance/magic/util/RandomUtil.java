package sacred.alliance.magic.util;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Random;

import com.game.draco.app.skill.vo.SkillFormula;

public class RandomUtil {
	private static final int  TEN_THOUSAND = SkillFormula.TEN_THOUSAND ;
	private static SecureRandom random = new SecureRandom();
	//private static Random random = new Random(System.currentTimeMillis());

	public static void main(String[] args){
		
		for(int i= 0 ;i<10 ;i++){
			System.out.println(random.nextInt(10));
		}
	}
	
	public static boolean randomBoolean(Double odds) {
		if (odds.intValue() < 0) {
			return false ;
		}
		if (odds.intValue() >= 1) {
			return true;
		}
		return random.nextDouble() <= odds;
	}

	public static boolean randomBoolean(Float odds) {
		if (odds.intValue() < 0) {
			return false ;
		}
		if (odds.intValue() >= 1) {
			return true;
		}
		return random.nextDouble() <= odds;
	}

	/**
	 * 不包括边界随机
	 */
	public static int randomInt(int limit) {
		if (limit <= 0) {
			return 0 ;
		}
		return Math.abs(random.nextInt()) % limit;
	}

	
	
	/**
	 * 根据给定上限生成随机数
	 * @param limit
	 * @return
	 */
	public static int randomIntWithoutZero(int limit) {
		if (limit <= 0) {
			return 1 ;
		}
		int result = Math.abs(random.nextInt()) % limit;
		if (0 == result) {
			return 1;
		}
		return result + 1;
	}

	public static int absRandomInt(int limit) {
		return Math.abs(randomInt(limit));
	}
	
	
	public static int randomInRange(int begin, int length) {
		if (length <= 0) {
			return begin;
		} else {
			return begin + absRandomInt(length);
		}
	}
	
	public static float randomFloat(float min,float max){
		if(min == max){
			return min ;
		}
		if(max < min){
			float oldMax = max ;
			max = min ;
			min = oldMax ;
		}
		return (float)(Math.random()* (max - min + 1)) + min;
	}
	
	
	public static float randomFloatScope(float min,float max){
		if(min == max){
			return min ;
		}
		if(max < min){
			float oldMax = max ;
			max = min ;
			min = oldMax ;
		}
		return (float)(Math.random()* (max - min )) + min;
	}
	
	/**
	 * 根据给定的数值区间产生随机数
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomInt(int min, int max) {
		if(min == max){
			return min ;
		}
		if(max < min){
			int oldMax = max ;
			max = min ;
			min = oldMax ;
		}
		return (int) (Math.random() * (max - min + 1)) + min;
	}
	
	public static boolean randomBoolean(){
		return random.nextBoolean();
	}
	
	/**四舍五入 [median:小数点的第几位]**/
	public static float scale(float dou, int median) {
		BigDecimal b = new BigDecimal(dou);
		return b.setScale(median, BigDecimal.ROUND_HALF_UP).floatValue();
	}
	
	public static boolean on(int probability){
		if(probability >= TEN_THOUSAND){
			return true ;
		}
		if(probability<=0){
			return false ;
		}
		return (Math.abs(random.nextInt()) % TEN_THOUSAND) < probability ;
	}
}

