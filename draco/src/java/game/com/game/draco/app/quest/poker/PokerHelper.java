package com.game.draco.app.quest.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;

public class PokerHelper {
	
	public static final int Fifty_Two = 52;
	public static final byte One_Hundred = 100; 
	public static final byte Four = 4;
	public static final byte Twelve = 12;
	public static final byte Thirteen = 13;
	public static final int[] Poker_Colors = new int[]{0, 1, 2, 3};//花色依次为：黑桃/红桃/梅花/方片
	public static final int[] Poker_Numbers = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};//扑克数字：(0/1/2...10/11/12)表示(2/3/4...Q/K/A)
	
	public static int randomFirstPoker(){
		return RandomUtil.randomInt(Fifty_Two);
	}
	
	public static int randomSecondPoker(int firstPoker){
		Map<Integer,Integer> weightMap = GameContext.getQuestPokerApp().getPokerSecondWeightMap();
		Integer key = Util.getWeightCalct(weightMap);
		PokerTwoType twoType = PokerTwoType.Common;
		if(null != key){
			twoType = PokerTwoType.get(key);
		}
		if(null == twoType){
			twoType = PokerTwoType.Common;
		}
		return PokerHelper.makeSecondPoker(firstPoker, twoType);
	}
	
	public static int randomThirdPoker(int firstPoker, int secondPoker){
		PokerTwoType twoType = PokerHelper.getPokerTwoType(firstPoker, secondPoker);
		Map<Integer,Integer> weightMap = GameContext.getQuestPokerApp().getPokerThirdWeightMap(twoType);
		Integer key = Util.getWeightCalct(weightMap);
		PokerThreeType threeType = PokerThreeType.Common;
		if(null != key){
			threeType = PokerThreeType.get(key);
		}
		if(null == threeType){
			threeType = PokerThreeType.Common;
		}
		return PokerHelper.makeThirdPoker(firstPoker, secondPoker, twoType, threeType);
	}
	
	/**
	 * 生成第二张扑克牌
	 * @param firstPoker 第一张牌
	 * @param twoType 前两张牌型
	 * @return
	 */
	private static int makeSecondPoker(int firstPoker, PokerTwoType twoType){
		int firstNum = firstPoker / Four;
		int firstColor = firstPoker % Four;
		int color = -1;
		int num = -1;
		switch(twoType){
		case DuiZi:
			color = PokerHelper.getOtherColor(firstColor);//花色不同
			num = firstNum;//数字相同
			break;
		case Link:
			color = PokerHelper.getOtherColor(firstColor);//花色不同
			num = PokerHelper.getLinkSecondNum(firstNum);//数字相连
			break;
		case Gap:
			color = PokerHelper.getOtherColor(firstColor);//花色不同
			num = PokerHelper.getGapSecondNum(firstNum);//数字间连
			break;
		case TongHua:
			color = firstColor;//花色相同
			num = PokerHelper.getCommonNum(firstNum);//数字相差很远
			break;
		case TongHua_Link:
			color = firstColor;//花色相同
			num = PokerHelper.getLinkSecondNum(firstNum);//数字相连
			break;
		case TongHua_Gap:
			color = firstColor;//花色相同
			num = PokerHelper.getGapSecondNum(firstNum);//数字间连
			break;
		}
		if(color < 0 || num < 0){
			color = PokerHelper.getOtherColor(firstColor);//花色不同
			num = PokerHelper.getCommonNum(firstNum);//数字相差很远
		}
		return num * Four + color;
	}
	
	/**
	 * 生成第三张扑克牌
	 * @param firstPoker 第一张牌
	 * @param secondPoker 第二张牌
	 * @param twoType 前两张牌的牌型
	 * @param threeType 三张牌的牌型
	 * @return
	 */
	private static int makeThirdPoker(int firstPoker, int secondPoker, PokerTwoType twoType, PokerThreeType threeType){
		int firstNum = firstPoker / Four;
		int firstColor = firstPoker % Four;
		int secondNum = secondPoker / Four;
		int secondColor = secondPoker % Four;
		int num = -1;
		int color = -1;
		switch(twoType){
		case Common:
			if(PokerThreeType.DuiZi == threeType){
				return PokerHelper.getDuiZiThirdPoker(firstColor, firstNum, secondNum, secondColor);
			}
			break;
		case DuiZi:
			if(PokerThreeType.BaoZi == threeType){
				color = PokerHelper.getOtherColor(firstColor, secondColor);//花色不同
				num = firstNum;//数字相同
			}
			break;
		case Link:
			if(PokerThreeType.DuiZi == threeType){
				return PokerHelper.getDuiZiThirdPoker(firstColor, firstNum, secondNum, secondColor);
			}else if(PokerThreeType.ShunZi == threeType){
				color = PokerHelper.randomColor();//随机花色
				num = PokerHelper.getLinkThirdNum(firstNum, secondNum);//两边的数字
			}
			break;
		case Gap:
			if(PokerThreeType.DuiZi == threeType){
				return PokerHelper.getDuiZiThirdPoker(firstColor, firstNum, secondNum, secondColor);
			}else if(PokerThreeType.ShunZi == threeType){
				color = PokerHelper.randomColor();//随机花色
				num = PokerHelper.getGapThirdNum(firstNum, secondNum);//两个数字中间的数字
			}
			break;
		case TongHua:
			if(PokerThreeType.DuiZi == threeType){
				return PokerHelper.getDuiZiThirdPoker(firstColor, firstNum, secondNum, secondColor);
			}else if(PokerThreeType.TongHua == threeType){
				color = firstColor;//花色相同
				num = PokerHelper.getOtherNum(firstNum, secondNum);//数字都不同
			}
			break;
		case TongHua_Link:
			if(PokerThreeType.DuiZi == threeType){
				return PokerHelper.getDuiZiThirdPoker(firstColor, firstNum, secondNum, secondColor);
			}else if(PokerThreeType.TongHua == threeType){
				color = firstColor;//花色相同
				num = PokerHelper.getOtherNum(firstNum, secondNum);//数字都不同
			}else if(PokerThreeType.ShunZi == threeType){
				color = PokerHelper.getOtherColor(firstColor, secondColor);//花色不同
				num = PokerHelper.getLinkThirdNum(firstNum, secondNum);//两边的数字
			}else if(PokerThreeType.TongHuaShun == threeType){
				color = firstColor;//花色相同
				num = PokerHelper.getLinkThirdNum(firstNum, secondNum);//两边的数字
			}
			break;
		case TongHua_Gap:
			if(PokerThreeType.DuiZi == threeType){
				return PokerHelper.getDuiZiThirdPoker(firstColor, firstNum, secondNum, secondColor);
			}else if(PokerThreeType.TongHua == threeType){
				color = firstColor;//花色相同
				num = PokerHelper.getOtherNum(firstNum, secondNum);//数字都不同
			}else if(PokerThreeType.ShunZi == threeType){
				color = PokerHelper.getOtherColor(firstColor, secondColor);//花色不同
				num = PokerHelper.getGapThirdNum(firstNum, secondNum);//两个数字中间的数字
			}else if(PokerThreeType.TongHuaShun == threeType){
				color = firstColor;//花色相同
				num = PokerHelper.getGapThirdNum(firstNum, secondNum);//两个数字中间的数字
			}
			break;
		}
		if(color < 0 || num < 0){
			color = PokerHelper.getOtherColor(firstColor, secondColor);//花色不同
			num = PokerHelper.getOtherNum(firstNum, secondNum);//数字都不同
		}
		return num * Four + color;
	}
	
	/** 获取对子牌型的第三张牌 **/
	private static int getDuiZiThirdPoker(int firstColor, int firstNum, int secondNum, int secondColor){
		int num = firstNum;
		int color = PokerHelper.getOtherColor(firstColor);
		int val = RandomUtil.randomInt(2);
		if(1 == val){
			num = secondNum;
			color = PokerHelper.getOtherColor(secondColor);
		}
		return num * Four + color;
	}
	
	/** 获取前两张牌的牌型 **/
	private static PokerTwoType getPokerTwoType(int firstPoker, int secondPoker){
		int firstNum = firstPoker / Four;
		int firstColor = firstPoker % Four;
		int secondNum = secondPoker / Four;
		int secondColor = secondPoker % Four;
		if(firstNum == secondNum){
			return PokerTwoType.DuiZi;
		}
		if(firstColor == secondColor){
			if(PokerHelper.isLinkNum(firstNum, secondNum)){
				return PokerTwoType.TongHua_Link;
			}
			if(PokerHelper.isGapNum(firstNum, secondNum)){
				return PokerTwoType.TongHua_Link;
			}
			return PokerTwoType.TongHua;
		}
		if(PokerHelper.isLinkNum(firstNum, secondNum)){
			return PokerTwoType.Link;
		}
		if(PokerHelper.isGapNum(firstNum, secondNum)){
			return PokerTwoType.Gap;
		}
		return PokerTwoType.Common;
	}
	
	/** 随机一种花色 **/
	private static int randomColor(){
		return RandomUtil.randomInt(Four);
	}
	
	/** 随机一个数字 **/
	private static int randomNumber(){
		return RandomUtil.randomInt(Thirteen);
	}
	
	/** 随机不同的其他花色 **/
	private static int getOtherColor(int color){
		int index = RandomUtil.randomInt(Four-1);
		if(index < color){
			return Poker_Colors[index];
		}
		return Poker_Colors[index + 1]; 
	}
	
	
	/** 随机不同的其他花色 **/
	private static int getOtherColor(int color1, int color2){
		int[] colors = new int[Four];//长度为最大，避免数组越界
		int i = 0;
		for(int val : Poker_Colors){
			if(val == color1 || val == color2){
				continue;
			}
			colors[i] = val;
			i++;
		}
		int maxSize = Four - 2;
		if(color1 == color2){
			maxSize = Four - 1;
		}
		int index = RandomUtil.randomInt(maxSize);
		return colors[index];
	}
	
	/** 获取没关联的数字 **/
	private static int getCommonNum(int num){
		int min = num + 3;
		int max = num + 10;
		return RandomUtil.randomInt(min, max) % Thirteen;
	}
	
	private static int getOtherNum(int num){
		int index = RandomUtil.randomInt(Thirteen-1);
		if(index < num){
			return Poker_Numbers[index];
		}
		return Poker_Numbers[index + 1]; 
	}

	/** 获取没关联的数字 **/
	private static int getOtherNum(int num1, int num2){
		int[] numbers = new int[Thirteen];//长度为最大，避免数组越界
		Set<Integer> filterSet = new HashSet<Integer>();
		filterSet.add(num1);
		filterSet.add(num2);
		//如果两个数字相连，不能随机两边相邻的数字；如果两个数字间连，不能随机中间的数字
		if(Math.abs(num1 - num2) == 1){
			int preNum = (Math.min(num1, num2) -1 + Thirteen) % Thirteen;
			int nextNum = (Math.max(num1, num2) + 1) % Thirteen;
			filterSet.add(preNum);
			filterSet.add(nextNum);
		}else if(Math.abs(num1 - num2) == 2){
			filterSet.add((num1+num2)/2);
		}
		int i = 0;
		for(int val : Poker_Numbers){
			if(filterSet.contains(val)){
				continue;
			}
			numbers[i] = val;
			i++;
		}
		int maxSize = Thirteen - filterSet.size();
		int index = RandomUtil.randomInt(maxSize);
		return numbers[index];
	}
	
	/** 获取相连的数字 **/
	private static int getLinkSecondNum(int num){
		int[] values = new int[2];
		values[0] = (num - 1 + Thirteen) % Thirteen;
		values[1] = (num + 1) % Thirteen;
		int index = RandomUtil.randomInt(2);
		return values[index];
	}
	
	/** 获取间隔的数字 **/
	private static int getGapSecondNum(int num){
		int[] values = new int[2];
		values[0] = (num - 2 + Thirteen) % Thirteen;
		values[1] = (num + 2) % Thirteen;
		int index = RandomUtil.randomInt(2);
		return values[index];
	}
	
	/** 获取相连的第三个顺子数字 **/
	private static int getLinkThirdNum(int firstNum, int secondNum){
		int[] values = new int[2];
		values[0] = (Math.min(firstNum, secondNum) - 1 + Thirteen) % Thirteen;
		values[1] = (Math.max(firstNum, secondNum) + 1) % Thirteen;
		int index = RandomUtil.randomInt(2);
		return values[index];
	}
	
	/** 获取间隔的第三个顺子数字 **/
	private static int getGapThirdNum(int firstNum, int secondNum){
		int value = (firstNum + secondNum) / 2;
		return value % Thirteen;
	}
	
	private static boolean isLinkNum(int firstNum, int secondNum){
		return 1 == Math.abs(secondNum - firstNum);
	}
	
	private static boolean isGapNum(int firstNum, int secondNum){
		return 2 == Math.abs(secondNum - firstNum);
	}
	
	/**
	 * 判断3张牌的牌型
	 * @param x 第一张牌
	 * @param y 第二张牌
	 * @param z 第三张牌
	 * @return
	 */
	public static PokerThreeType getPokerThreeType(int x, int y, int z){
		if(x < 0 || y < 0 || z <0){
			return PokerThreeType.Common;
		}
		//按照最终牌型的由大到小顺序判断
		//判断豹子
		if(PokerHelper.isBaoZi(x, y, z)){
			return PokerThreeType.BaoZi;
		}
		//判断同花顺
		if(PokerHelper.isTongHuaShun(x, y, z)){
			return PokerThreeType.TongHuaShun;
		}
		//判断顺子
		if(PokerHelper.isShunZi(x, y, z)){
			return PokerThreeType.ShunZi;
		}
		//判断同花
		if(PokerHelper.isTongHua(x, y, z)){
			return PokerThreeType.TongHua;
		}
		//判断对子
		if(PokerHelper.isDuiZi(x, y, z)){
			return PokerThreeType.DuiZi;
		}
		return PokerThreeType.Common;
	}
	
	/** 豹子：3张牌数字相同 **/
	public static boolean isBaoZi(int x, int y, int z){
		return PokerHelper.sameNumber(x, y, z);
	}
	
	/** 同花顺：花色相同、数字相连 **/
	private static boolean isTongHuaShun(int x, int y, int z){
		return PokerHelper.sameColor(x, y, z) && PokerHelper.isShunZi(x, y, z);
	}
	
	/** 顺子：数字相连 **/
	private static boolean isShunZi(int x, int y, int z){
		List<Integer> list = new ArrayList<Integer>();
		list.add(x/4);
		list.add(y/4);
		list.add(z/4);
		Collections.sort(list);//按数字从小到大排序
		int num1 = list.get(0);
		int num2 = list.get(1);
		int num3 = list.get(2);
		//三张牌数字必须相连
		boolean numLink = num1 + 1 == num2 && num2 + 1 == num3;
		if(!numLink){
			return false;
		}
		//K12不算顺子
		if(Twelve-1 == num1 && Twelve == num2 && 0 == num3){
			return false;
		}
		return true;
	}
	
	/** 同花：花色相同 **/
	private static boolean isTongHua(int x, int y, int z){
		return PokerHelper.sameColor(x, y, z);
	}
	
	/** 对子：有2张牌的数字相同 **/
	private static boolean isDuiZi(int x, int y, int z){
		return PokerHelper.sameNumber(x, y) || PokerHelper.sameNumber(y, z) || PokerHelper.sameNumber(z, x);
	}
	
	private static boolean sameNumber(int x, int y, int z) {
		return x / 4 == y / 4 && y / 4 == z / 4;
	}
	
	private static boolean sameNumber(int x, int y) {
		return x / 4 == y / 4;
	}
	
	private static boolean sameColor(int x, int y, int z) {
		return x % 4 == y % 4 && y % 4 == z % 4;
	}
	
	public static int refreshSecondPoker(int firstPoker){
		int firstNum = firstPoker / Four;
		int firstColor = firstPoker % Four;
		int color = PokerHelper.randomColor();
		int num;
		//若花色相同，则数字就不能相同
		if(color == firstColor){
			num = PokerHelper.getOtherNum(firstNum);
		}else{
			num = PokerHelper.randomNumber();
		}
		return num * Four + color;
	}
	
	public static int refreshThirdPoker(int firstPoker, int secondPoker){
		int firstNum = firstPoker / Four;
		int firstColor = firstPoker % Four;
		int secondNum = secondPoker / Four;
		int secondColor = secondPoker % Four;
		int color = PokerHelper.randomColor();
		int num;
		//前两张牌花色相同
		if(firstColor == secondColor){
			//若三张牌花色都相同，则数字就不能相同
			if(color == firstColor){
				num = PokerHelper.getOtherNum(firstNum, secondNum);
			}else{
				num = PokerHelper.randomNumber();
			}
		}else{
			//若与第一张牌花色相同，则不能与第一张牌数字相同; 若与第二张牌花色相同，则不能与第二张牌数字相同。
			if(color == firstColor){
				num = PokerHelper.getOtherNum(firstNum);
			}else if(color == secondColor){
				num = PokerHelper.getOtherNum(secondNum);
			}else{
				num = PokerHelper.randomNumber();
			}
		}
		return num * Four + color;
	}
	
}
