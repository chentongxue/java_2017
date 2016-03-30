package sacred.alliance.magic.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.vo.RoleInstance;

import com.alibaba.fastjson.JSON;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.skill.vo.SkillFormula;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
public class Util {
	private static Logger logger = LoggerFactory.getLogger(Util.class);
	private static MessageDigest shaDigest = null;
	private final static String SPLIT_CHARS = "\\||,| |，|、|\r|\n|\t|:";
	public final static double percentRate = (double)1/ProbabilityMachine.RATE_CALCULATE_PERCENT_MODULUS ;
	private static final float TEN_THOUSAND_F = SkillFormula.TEN_THOUSAND;

	public static <K,T> T  fromMap(Map<K,T> map,K key){
		if(null == map || null == key){
			return null ;
		}
		return map.get(key);
	}

	public static String doubleFormat(double dou){
		java.text.DecimalFormat df=new java.text.DecimalFormat("##########0.######");
		return df.format(Util.scale(dou, 2)); 
	}
	
	
	private static double scale(double dou, int median) {
		BigDecimal b = new BigDecimal(dou);
		return b.setScale(median, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	
	public static String[] splitString(String str) {
		return splitString(str, SPLIT_CHARS);
	}

	public static String[] splitString(String str, String delimiters) {
		if (str == null) {
			return null;
		} else {
			String[] splited = str.split(delimiters);
			int num = 0;
			for (String s : splited) {
				if (s.trim().length() > 0) {
					++num;
				}
			}
			String[] result = new String[num];
			int idx = 0;
			for (String s : splited) {
				if (s.trim().length() > 0) {
					result[idx++] = s.trim();
				}
			}

			return result;
		}
	}

	/**
	 * 把指定的字符串首字母转换成大写字母
	 * 
	 * @param str
	 *            指定的字符串
	 * @return 首字母转换成大写字母的结果字符串
	 */
	public static String upperFirstLetter(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}


	/**
	 * 判断两个线段是否有相交区域
	 * 
	 * @param s1
	 * @param l1
	 * @param s2
	 * @param l2
	 * @return
	 */
	public static boolean segmentIntersect(int s1, int l1, int s2, int l2) {
		int e1 = s1 + l1;
		int e2 = s2 + l2;
		return ((s1 >= s2 && s1 < e2) || (e1 >= s2 && e1 < e2));
	}
	
	public static int size(Collection<?> collection){
		if(null == collection){
			return 0 ;
		}
		return collection.size() ;
	}
	
	public static int size(Map<?, ?> map){
		if(null == map){
			return 0 ;
		}
		return map.size() ;
	}
	
	public static boolean isEmpty(Collection<?> collection) {
		return null == collection || 0 == collection.size() ;
	}
	
	public static boolean isEmpty(Object os[]) {
		return null == os || 0 == os.length ;
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return null == map || 0 == map.size() ;
	}

	public static boolean isEmpty(Multimap<?, ?> map) {
		return null == map || 0 == map.size() ;
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
	

	public static boolean isEmpty(String str) {
		return null == str || 0 == str.length() ;
	}

	public static int randomInRange(int begin, int length) {
		if (length <= 0) {
			return begin;
		} else {
			return begin + ProbabilityMachine.absRandomInt(length);
		}
	}

	public static String align(String str, int size) {
		if (str.length() >= size) {
			return str;
		} else {
			StringBuffer sb = new StringBuffer(str);
			for (int i = 0; i < size - str.length(); ++i) {
				sb.append(' ');
			}
			return sb.toString();
		}
	}


	public static String getShaDigest(String contents) {
		if (shaDigest == null) {
			try {
				shaDigest = MessageDigest.getInstance("SHA");
			} catch (Exception e) {
				throw new RuntimeException("不支持SHA算法", e);
			}
		}

		try {
			byte[] bytes = shaDigest.digest(contents.getBytes("UTF-8"));
			return toHex(bytes);
		} catch (Exception e) {
			throw new RuntimeException("SHA摘要失败", e);
		}
	}

	private static String CR = "\r";
	private static String LF = "\n";
	private static String BLANK = " ";

	public static String deleteCrLf(String src) {
		StringBuffer sb = new StringBuffer(src);
		int idx = 0;
		while ((idx = sb.indexOf(CR)) != -1) {
			sb.replace(idx, idx + 1, BLANK);
		}
		while ((idx = sb.indexOf(LF)) != -1) {
			sb.replace(idx, idx + 1, BLANK);
		}
		while ((idx = sb.indexOf("<")) >= 0) {
			sb.replace(idx, idx + 1, "＜");
		}
		while ((idx = sb.indexOf(">")) >= 0) {
			sb.replace(idx, idx + 1, "＞");
		}

		return sb.toString().trim();
	}


	private static String toHex(byte[] data) {
		if (data == null) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; ++i) {
			sb.append(getHex(data[i] >> 4)).append(getHex(data[i] & 0x0f));

		}

		return sb.toString();
	}

	static char[] hexChar = new char[] { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static char getHex(int b) {
		return hexChar[b & 0x0f];
	}

	/**
	 * 判断点2是否在以点1为圆心radius为半径的圆内
	 * @param originX
	 * @param originY
	 * @param x
	 * @param y
	 * @param radius
	 * @return
	 */
	public static boolean inCircle(int originX,int originY,int x,int y ,int radius){
		if(radius <=0){
			return true;
		}
		//return Math.pow(radius, 2) >= Math.pow(originX-x, 2) + Math.pow(originY-y, 2);
		return radius >= distance(originX,originY,x,y) ;
	}
	
	public static int distance(int originX,int originY,int x,int y){
		int dx = originX -x ;
		int dy = originY -y ;
		if (dx < 0) {
			dx = -dx;
		}
		if (dy < 0) {
			dy = -dy;
		}

		int min, max;
		if (dx < dy) {
			min = dx;
			max = dy;
		} else {
			min = dy;
			max = dx;
		}
		return ((max << 8) - (max << 3) - (max << 1) + (min << 6) + (min << 5) + (min << 2) + (min << 1)) >> 8;
	}
	
	public static boolean inRectangle(int originX,int originY,int x,int y,int rectangleWidth, int rectangleHeight) {
		if(x < originX || x > originX + rectangleWidth) {
			return false;
		}
		if(y < originY || y > originY + rectangleHeight) {
			return false;
		}
		return true;
	}
	
	public static int getDegrees(int x,int y,int nextX,int nextY){
		//double tan = (nextY - y)/(nextX -x);
		//return (int)Math.toDegrees(Math.atan(tan));
	    return (int)(Math.atan2(nextY-y,nextX-x)*180/Math.PI);
	}
	
	/**
	 * 过滤src,返回dest不存在的
	 */
	public static <T> Set<T> filterSet(Set<T> src,Set<T> filter,boolean include){
		if(null == src){
			return new HashSet<T>(); 
		}
		if(null == filter){
			return include ? src:new HashSet<T>() ;
		}
		
		Set<T> ret = new HashSet<T>();
		for(T iter : src){
			if(include){
				if(filter.contains(iter)){
					continue ;
				}
			}else {
				if(!filter.contains(iter)){
					continue ;
				}
			}
			ret.add(iter);
		}
		return ret ;
	}
	
	
	public static <T> void mergerMap(Map<T,Integer> map,T key,int value){
		if(map.containsKey(key)){
			map.put(key, map.get(key) + value);
			return ;
		}
		map.put(key, value);
	}
	/**
	 * 合并Map
	 * @param srcMap
	 * @param destMap
	 * @return
	 */
	public static <T> Map<T,Integer> mergerMap(Map<T,Integer> srcMap,Map<T,Integer> destMap ){
		Map<T,Integer> valueMap = new HashMap<T,Integer>();
		if(null == srcMap || 0 == srcMap.size()){
			if(null != destMap){
				valueMap.putAll(destMap);
			}
			return valueMap ;
		}
		if(null == destMap || 0 == destMap.size()){
			valueMap.putAll(srcMap);
			return valueMap ;
		}
		
		valueMap.putAll(srcMap);
		Iterator<T> it = destMap.keySet().iterator();
		while(it.hasNext()){
			T key = it.next();
			int value = destMap.get(key);
			if(valueMap.containsKey(key)){
				valueMap.put(key, valueMap.get(key)+ value);
			}else{
				valueMap.put(key, value);
			}
		}
		return valueMap;
	}
	
	/**
	 * 缩小100000倍 用于计算
	 * @param str
	 * @return
	 *//*
	public static String convertPercentToStr(String str) {
		if(!str.endsWith("%"))return str;
		int bf = str.indexOf("%");
		double value = 0;
		if( bf > -1){
			str = str.replace("%", "");
			str = str.substring(0, bf);
			value = Double.parseDouble(str)/100;
			value = value/ProbabilityMachine.RATE_MODULUS;
		}
		return String.valueOf(value);
	}*/
	
	/**
	 * 获取范围内的随机值
	 * @param min 最小值
	 * @param max 最大值
	 * @return
	 */
	public static int randomInt(int min,int max)   {   
		  return (int)(Math.random()*(max-min+1))+min;
	}

    public static <K,V> K getFistElement(Map<K,V> map){
        if(Util.isEmpty(map)){
            return null ;
        }
        for(Iterator<K> it = map.keySet().iterator();it.hasNext();){
            return it.next();
        }
        return null ;
    }
    
    public static boolean isNumeric(String str){   
        Pattern pattern = Pattern.compile("[0-9]*");   
        return pattern.matcher(str).matches();      
    }
    
    /*//TODO:此方法结果不正确
    public static int getChinaniesNum(String str) {        
        int count = 0;        
        String regEx = "[\\u4e00-\\u9fa5]";        
        Pattern p = Pattern.compile(regEx);        
        Matcher m = p.matcher(str);        
        while (m.find()) {        
        	for (int i = 0; i <= m.groupCount(); i++) {        
        		count = count + 1;        
            }        
        }        
//          System.out.println("共有 " + count + "个 ");    
        return count;
      }  */   
    
    
	/**
	 * 小盒取球，权重即为不同小球的个数，每次取完放回盒内
	 * 抽取必有小球，小球可重复
	 * @param count 抽取次数次数
	 * @param weightMap
	 * @return
	 */
	public static <T> List<T> getLuckyDraw(int count,Map<T,Integer> weightMap){
		List<T> list = Lists.newArrayList();
		Map<T,Integer> factorMap = Maps.newHashMap();
		factorMap.putAll(weightMap);
		if(null == factorMap || 0 == factorMap.size()){
			return list;
		}
		int sum = 0 ;
		for(Integer value : factorMap.values()){
			sum += value;
		}
		if(0 == sum){ 
			return list;
		}
		for(int i =0; i<count ;i++){
			int random = RandomUtil.randomIntWithoutZero(sum);
			int overlapCount = 0; 
			for(Iterator<Map.Entry<T, Integer>> it = factorMap.entrySet().iterator();it.hasNext();){
				Map.Entry<T, Integer> entry = it.next();
				T key = entry.getKey() ;
				int value = entry.getValue() ;
				if((overlapCount < random) && (random <= (overlapCount+ value))){
					list.add(key);
					break;
				}
				overlapCount += value;
			}//for
		}
		return list;
	}
	/**
	 * 随机，权重即为随到不同样品的概率，样品不重复
	 * 抽取必有样品
	 * @param count 抽取次数
	 * @param weightMap
	 * @return
	 */
	public static <T> List<T> getLuckyDrawUnique(int count,Map<T,Integer> weightMap){
		List<T> list = Lists.newArrayList();
		Map<T,Integer> factorMap = Maps.newHashMap();
		factorMap.putAll(weightMap);
		if(null == factorMap || 0 == factorMap.size()){
			return list;
		}
		int sum = 0 ;
		for(Integer value : factorMap.values()){
			sum += value;
		}
		if(0 == sum){ 
			return list;
		}
		for(int i =0; i<count ;i++){
			int random = RandomUtil.randomIntWithoutZero(sum);
			int overlapCount = 0; 
			for(Iterator<Map.Entry<T, Integer>> it = factorMap.entrySet().iterator();it.hasNext();){
				Map.Entry<T, Integer> entry = it.next();
				T key = entry.getKey() ;
				int value = entry.getValue() ;
				if((overlapCount < random) && (random <= (overlapCount+ value))){
					list.add(key);
					it.remove();
					sum -= value;
					break;
				}
				overlapCount += value;
			}//for
		}
		return list;
	}
    /**
	 * 权重算法
	 * @param count 遍历次数
	 * @param weightMap <主键id,数量>
	 * @return 
	 */
	public static Set<Integer> getWeightCalct(int count,Map<Integer,Integer> weightMap){
		Set<Integer> set = new HashSet<Integer>();
		Map<Integer,Integer> factorMap = new HashMap<Integer,Integer>();
		factorMap.putAll(weightMap);
		if(null == factorMap || 0 == factorMap.size()){
			return set;
		}
		int sumGon = 0 ;
		for(Integer gon : factorMap.values()){
			sumGon += gon;
		}
		if(0 == sumGon){
			return set;
		}
		for(int i =0; i<count ;i++){
			int random = RandomUtil.randomIntWithoutZero(sumGon);
			int overlapCount = 0; 
			for(Iterator<Map.Entry<Integer, Integer>> it = factorMap.entrySet().iterator();it.hasNext();){
				Map.Entry<Integer, Integer> entry = it.next();
				int key = entry.getKey() ;
				int gonNum = entry.getValue() ;
				if((overlapCount < random) && (random <= (overlapCount+ gonNum))){
					if(key != 0){
						set.add(key);
						sumGon -= gonNum;
						it.remove();
					}
					break;
				}
				overlapCount += gonNum;
			}
		}
		return set;
	}
	
	
	/**
	 * 权重算法
	 * @param factorMap <主键id,数量>
	 * @return <主键id>
	 */
	public static Integer getWeightCalct(final Map<Integer,Integer> factorMap){
		int sumGon = 0;
		//获得属性总值，用作产生随机属性
		for(Integer gon : factorMap.values()){
			sumGon += gon ;
		}
		if(0 == sumGon){
			return null;
		}
		int random = RandomUtil.randomIntWithoutZero(sumGon);
		int overlapCount = 0; 
		for(Iterator<Integer> it = factorMap.keySet().iterator();it.hasNext();){
			int key = it.next();
			int gonNum = factorMap.get(key);
			if((overlapCount< random) && (random<= (overlapCount+ gonNum))){
				return key;
			}
			overlapCount += gonNum;
		}
		return null;
	}
	
	/**
	 * 权重算法
	 * @param factorMap
	 * @param sumGon
	 * @return
	 */
	public static Integer getWeightCalct(final Map<Integer,Integer> factorMap, int sumGon){
		if(0 == sumGon){
			return null;
		}
		int random = RandomUtil.randomIntWithoutZero(sumGon);
		int overlapCount = 0; 
		for(Iterator<Integer> it = factorMap.keySet().iterator();it.hasNext();){
			int key = it.next();
			int gonNum = factorMap.get(key);
			if((overlapCount< random) && (random<= (overlapCount+ gonNum))){
				return key;
			}
			overlapCount += gonNum;
		}
		return null;
	}
	/**
	 * 将字符串转变为字符数组
	 * @param str
	 * @return
	 */
	public static String[] stringToArray(String str){
		if(null == str || str.length() == 0){
			return null;
		}
		String s[] = new String[str.length()];
		for(int i=0;i<str.length();i++){
			s[i] = str.substring(i,i+1);
		}
		return s;
	}
	
	
	
	/**
	 * 解析npc parms参数eg:status=0&questId=1
	 * @param param
	 * @return
	 */
	public static Map<String,String> urlParamParser(String param){
		Map<String,String> map = new HashMap<String,String>();
		if(null == param || 0 == param.trim().length()){
			return map ;
		}
		String[] arr1 = param.split(Cat.and);
		if(null == arr1){
			return map ;
		}
		for(String str : arr1){
			String[] kv = str.split(Cat.equ);
			if(null == kv || kv.length !=2){
				continue ;
			}
			map.put(kv[0], kv[1]);
		}
		return map ;
	}
	
	/**
	 * 根据指定的颜色和字符串拼出最总结果字符串
	 * @param color
	 * @return
	 */
	public static String getColorString(String color, String str){
		return ("[\\C]" + color + "[C]" + str);
	}
	
	/**
	 * 获取颜色
	 * @param color
	 * @return
	 */
	public static String getColor(String color){
		return ("[\\C]" + color + "[C]");
	}
	
	/**
	 * 给字符串拼上制定颜色，并恢复默认颜色
	 * @param color
	 * @param defColor
	 * @param str
	 * @return
	 */
	public static String getColorString(String str, String color, String defColor) {
		return Util.getColorString(color, str) + Util.getColor(defColor);
	}
	
	/**
	 * 返回阵营颜色的角色名
	 * @param role
	 * @return
	 */
	public static String getColorRoleName(RoleInstance role, ChannelType channelType) {
		return role.getRoleName() + Util.getColor(channelType.getColor());
	}
	
	/**
	 * 根据概率列表和概率总和来得到list下标
	 * @param probsList
	 * @param probsTotal
	 * @return
	 */
	public static int getProbsIndex(List<Integer> probsList, int probsTotal){
		int index = -1;
		int rand = (int)(Math.random() * probsTotal);
		if(rand < probsList.get(0)){
			index = 0;
		}
		else if(rand >= probsList.get(probsList.size()-2)){
			index = probsList.size() - 1;
		}
		else{
			for(int i=0; i < probsList.size() -1; i++){
				if(rand >= probsList.get(i) && rand < probsList.get(i+1)){
					index = i + 1;
					break;
				}
			}
		}
		return index;
	}
	
	/**
	 * 字符串折分
	 * 
	 * @param str
	 * @param cat
	 * @return
	 */
	public static String[] splitStr(String str, String cat) {
		String[] strArr = new String[0];
		if (isEmpty(str)) {
			return strArr;
		}
		strArr = str.split(cat);
		return strArr;
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
	
	/**
	* Map 按值（value）排序
	* @param map Map<String, Integer>
	* @return List<Map.Entry<String, Integer>>
	*/
	public static List<Map.Entry<String, Integer>> getSortedMapEntryListByValue(Map<String, Integer> map) {
		List<Map.Entry<String,Integer>> entryList = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
	        public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
	            return entry2.getValue() - entry1.getValue();
	         }
		});
		return entryList;
	 }
	
	/**
	* Map 按值（value）排序
	* @param map Map<String, Integer>
	* @return List<Map.Entry<String, Integer>>
	*/
	public static List<Map.Entry<String, AtomicLong>> getSortedMapEntryListByLongValue(Map<String, AtomicLong> map) {
		List<Map.Entry<String,AtomicLong>> entryList = new ArrayList<Map.Entry<String,AtomicLong>>(map.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<String, AtomicLong>>() {
	        public int compare(Map.Entry<String, AtomicLong> entry1, Map.Entry<String, AtomicLong> entry2) {
	        	long v1 = entry1.getValue().longValue();
	        	long v2 = entry2.getValue().longValue();
	            if(v1 > v2){
	            	return -1;
	            }
	            if(v1 < v2){
	            	return 1;
	            }
	            return 0;
	         }
		});
		return entryList;
	 }
	
	/**
	 * 将英文逗号转换成中文逗号
	 * @param str
	 * @return
	 */
	public static String replaceComma(String str){
		if(-1 != str.indexOf("")){
			str = str.replaceAll(",", "，");
		}
		return str;
	}

	public static int getSubListSize(int listSize, int subListSize){
		return subListSize > listSize ? listSize : subListSize;
	}
	
	
	public static void sortSkillCdDesc(List<RoleSkillStat> skillList){
		Collections.sort(skillList, new Comparator<RoleSkillStat>() {
			public int compare(RoleSkillStat stat1, RoleSkillStat stat2) {
				Skill skill1 = GameContext.getSkillApp().getSkill(stat1.getSkillId());
				Skill skill2= GameContext.getSkillApp().getSkill(stat2.getSkillId());
				
				SkillDetail sd1 = skill1.getSkillDetail(stat1.getSkillLevel());
				SkillDetail sd2 = skill2.getSkillDetail(stat2.getSkillLevel());
				
				if(sd1 == null || sd2 == null){
					return 0 ;
				}
				if(sd1.getCd() > sd2.getCd()) {
					return -1;
				}
				if(sd1.getCd() < sd2.getCd()) {
					return 1;
				}
				return 0;
			}
		});
	}
	
	public static boolean isNumber(String content) {
		if (null == content || content.trim().length() == 0)
			return false;
		content = content.trim();
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if (c > '9' || c < '0') {
				return false;
			}
		}
		return true;
	}
	
	public static int maxZero(int a){
		return a >0 ? a :0 ;
	}
	
	public static int safeIntAdd(int a,int b){
		long newValue = (long)a + (long)b;
		if(newValue > Integer.MAX_VALUE){
			newValue = Integer.MAX_VALUE;
		}
		return (int)newValue ;
	}
	
	public static Map<String,Integer> parseStringIntMap(String s){
		Map<String,Integer> value = Maps.newHashMap() ;
		if(isEmpty(s)){
			return value ;
		}
		String[] arr = s.split(",");
		for(String str : arr){
			String[] kv = str.split(":");
			Integer v = Integer.parseInt(kv[1]);
			value.put(kv[0], v);
		}
		return value ;
	}
	/**
	 * @param s
	 * @return
	 * @date 2014-4-11 上午10:38:50
	 */
	public static Map<String,Integer> parseStringIntLinkedMap(String s){
		Map<String,Integer> value = Maps.newLinkedHashMap();
		if(isEmpty(s)){
			return value ;
		}
		String[] arr = s.split(",");
		for(String str : arr){
			String[] kv = str.split(":");
			Integer v = Integer.parseInt(kv[1]);
			value.put(kv[0], v);
		}
		return value ;
	}
	
	
	public static String strIntMapToString(Map<String,Integer> map){
		if(isEmpty(map)){
			return "" ;
		}
		String cat = "" ;
		StringBuffer buffer = new StringBuffer();
		for(Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();it.hasNext();){
			Map.Entry<String, Integer> entry = it.next() ;
			buffer.append(cat);
			buffer.append(entry.getKey());
			buffer.append(":");
			buffer.append(entry.getValue());
			cat = "," ;
		}
		return buffer.toString() ;
	}
	
	public static Map<Short,Integer> parseShortIntMap(String s){
		Map<Short,Integer> value = Maps.newHashMap() ;
		if(isEmpty(s)){
			return value ;
		}
		String[] arr = s.split(",");
		for(String str : arr){
			String[] kv = str.split(":");
			Short k = Short.parseShort(kv[0]);
			Integer v = Integer.parseInt(kv[1]);
			value.put(k, v);
		}
		return value ;
	}
	
	/**
	 * @date 2014-4-8 下午06:04:01
	 */
	public static Map<Byte,Integer> parseByteIntMap(String s){
		Map<Byte,Integer> value = Maps.newHashMap() ;
		if(isEmpty(s)){
			return value ;
		}
		String[] arr = s.split(",");
		for(String str : arr){
			String[] kv = str.split(":");
			Byte k = Byte.parseByte(kv[0]);
			Integer v = Integer.parseInt(kv[1]);
			value.put(k, v);
		}
		return value ;
	}
	
	public static Map<Integer,Byte> parseIntegerByteMap(String s){
		Map<Integer,Byte> value = Maps.newHashMap() ;
		if(isEmpty(s)){
			return value ;
		}
		String[] arr = s.split(",");
		for(String str : arr){
			String[] kv = str.split(":");
			Integer k = Integer.parseInt(kv[0]);
			Byte v = Byte.parseByte(kv[1]);
			value.put(k, v);
		}
		return value ;
	}
	
	
	public static <K, V> String kvMapToString(Map<K,V> map){
		if(isEmpty(map)){
			return "" ;
		}
		String cat = "" ;
		StringBuffer buffer = new StringBuffer();
		for(Iterator<Map.Entry<K,V>> it = map.entrySet().iterator();it.hasNext();){
			Map.Entry<K,V> entry = it.next() ;
			buffer.append(cat);
			buffer.append(entry.getKey());
			buffer.append(":");
			buffer.append(entry.getValue());
			cat = "," ;
		}
		return buffer.toString() ;
	}
	/**
	 * 
	 * @param map
	 * @return
	 * @date 2014-4-8 下午06:09:10
	 */
	public static String byteIntMapToString(Map<Byte,Integer> map){
		if(isEmpty(map)){
			return "" ;
		}
		String cat = "" ;
		StringBuffer buffer = new StringBuffer();
		for(Iterator<Map.Entry<Byte, Integer>> it = map.entrySet().iterator();it.hasNext();){
			Map.Entry<Byte, Integer> entry = it.next() ;
			buffer.append(cat);
			buffer.append(entry.getKey());
			buffer.append(":");
			buffer.append(entry.getValue());
			cat = "," ;
		}
		return buffer.toString() ;
	}
	
	/**
	 * 物品概率
	 * @param table
	 * @return
	 */
	public static int getProbabilityIndexByTable(int[] table) {

		// 概率加总
		int sumProb = 0;
		for (int i = 0; i < table.length; i++) {
			sumProb += table[i];
		}

		// 随机概率
		int probability = RandomUtil.randomInt(sumProb);

		for (int i = 0; i < table.length; i++) {

			// 计算概率区间
			int min = 0;
			for (int j = 0; j < i; j++) {
				min += table[j];
			}
			int max = 0;
			for (int j = 0; j <= i; j++) {
				max += table[j];
			}

			if (probability >= min && probability < max) {
				return i;
			}
		}
		return -1;
	}
	
	public static <T> T deserialization(byte[] data, Class<T> clazz) {
		try {
			if (null == data) {
				return null;
			}
			byte[] bytes = lzma.util.Util.lzmaUnZip(data);
			String text = new String(bytes, "UTF-8");
			return JSON.parseObject(text, clazz);
		} catch (Exception ex) {
			logger.error("", ex);
			return null;
		}
	}
	
	public static byte[] serialization(Object obj) {
		try {
			if (null == obj) {
				return null;
			}
			String jsonStr = JSON.toJSONString(obj);
			return lzma.util.Util.lzmaZip(jsonStr.getBytes("UTF-8"));
		} catch (Exception ex) {
			logger.error("",ex);
			return null;
		}
	}
	
	public static <T> T decode(byte[] bytes, Class<T> clazz) {
		if (null == bytes || null == clazz) {
			return null;
		}
		try {
			return ProtobufProxy.create(clazz).decode(bytes);
		} catch (Exception ex) {
			logger.error("decode error,class=" + clazz.getName(), ex);
		}
		return null ;
	}
	
	public static <T> byte[] encode(T value){
		if (null == value) {
			return null;
		}
		try {
			Codec<T> codec = (Codec<T>)ProtobufProxy.create(value.getClass());
			return codec.encode(value);
		} catch (Exception ex) {
			logger.error("encode error,class=" + value.getClass().getName(), ex);
		}
		return null ;
	}
	
	
	
	public static int getAbc(int a, int b, int c, int d, int value, boolean prob,boolean isA) {
		float TEN_THOUSAND_F = SkillFormula.TEN_THOUSAND;
		float aF = 0;
		if(isA){
			aF = (a / TEN_THOUSAND_F * value);
		}
		float rate = (aF + ((b / TEN_THOUSAND_F) * (c / TEN_THOUSAND_F)) + (d / TEN_THOUSAND_F));
		if(prob){
			rate *= 10000;
		}
		BigDecimal decimal = new BigDecimal(rate);
		rate = decimal.setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
		return (int)rate;
	}
	
	public static int getAbc(int b, int c, int d) {
		float rate = b * c + d;
		BigDecimal decimal = new BigDecimal(rate);
		rate = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		return (int)rate;
	}
	
	public static int getAngle(int x ,int y){
		double atan = Math.atan2(x, y);
		double angle =180*atan/Math.PI;
		if(x >= 0 && y<= 0){
			angle+= -90;
		}else{
			angle+= 270;
		}
        return (int)angle;
	}
	
	
	public static short getDir(byte dir){
    	return (short)(((dir&0xff)*360)>>8);
    }
	
	public static <T> boolean inSet(T key,Set<T> ... sets){
		for(Set<T> set : sets){
			if(null == set){
				continue ;
			}
			if(set.contains(key)){
				return true ;
			}
		}
		return false ;
	}
	
	public static <K,K1,V1> Map<K1,V1> getIfAbsent(K key,ConcurrentMap<K,ConcurrentMap<K1,V1>> map){
		if(null == map || null == key){
			return null ;
		}
		ConcurrentMap<K1,V1> value = map.get(key);
		if(null != value){
			return value ;
		}
		value = Maps.newConcurrentMap() ;
		ConcurrentMap<K1,V1> oldValue = map.putIfAbsent(key, value);
		if(null == oldValue){
			return value ;
		}
		return oldValue ;
	}
	
	// 替换
	public static String replaceDes(int x, int a, int b, int c, int d, String des) {
		float rate = (b / TEN_THOUSAND_F) * (c / TEN_THOUSAND_F)
				+ (d / TEN_THOUSAND_F);
		BigDecimal decimalRate = new BigDecimal(rate);
		rate = decimalRate.setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
		float af = (a / TEN_THOUSAND_F) * 100;
		BigDecimal decimalAf = new BigDecimal(af);
		af = decimalAf.setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
		des = des.replace("{a" + x + "}",af + "%");
		float cf = rate * 100;
		BigDecimal decimalCf = new BigDecimal(cf);
		cf = decimalCf.setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
		des = des.replace("{c" + x + "}", cf + "%");
		des = des.replace("{d" + x + "}", String.valueOf((int) rate));
		return des;
	}

	public static Object getFieldValue(Object target,String fieldName){
		try {
			if(null == target || Util.isEmpty(fieldName)){
				return null ;
			}
			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(target) ;
		}catch(Exception ex){
			logger.error("",ex);
		}
		return null ;
	}

    public static void setFieldValue(Object target,String fieldName,Object value){
        try {
            if(null == target || Util.isEmpty(fieldName)){
                return  ;
            }
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target,value);
        }catch(Exception ex){
            logger.error("",ex);
        }
    }

    private static <T> int size(Set<T> src){
        return (null == src)?0:src.size() ;
    }

    public static <T> boolean isElementSame(Set<T> src,Set<T> target){
        int srcSize = size(src);
        int targetSize = size(target);
        if(srcSize != targetSize){
            return false ;
        }
        Set<T> all = Sets.newHashSet();
        if(null != src){
            all.addAll(src) ;
        }
        if(null != target){
            all.addAll(target);
        }
        return srcSize == all.size();
    }
}
