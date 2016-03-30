package sacred.alliance.magic.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil {

	private final static String SPLIT_CHARS = "\\||,| |，|、|\r|\n|\t";

	public static String[] splitString(String str) {
		return splitString(str, SPLIT_CHARS);
	}

	public static String replace(String strSource, String strFrom, String strTo) {
		if (strSource == null) {
			return null;
		}
		int i = 0;
		if ((i = strSource.indexOf(strFrom, i)) >= 0) {
			char[] cSrc = strSource.toCharArray();
			char[] cTo = strTo.toCharArray();
			int len = strFrom.length();
			StringBuffer buf = new StringBuffer(cSrc.length);
			buf.append(cSrc, 0, i).append(cTo);
			i += len;
			int j = i;
			while ((i = strSource.indexOf(strFrom, i)) > 0) {
				buf.append(cSrc, j, i - j).append(cTo);
				i += len;
				j = i;
			}
			buf.append(cSrc, j, cSrc.length - j);
			return buf.toString();
		}
		return strSource;
	} 
	
	public static String replaceNewLine(String str){
		if(null == str){
			return "" ;
		}
		str = str.trim();
		str = replace(str,"\n","");
		str = replace(str,"\r","");
		return str ;
	}
	
	public static String initString(String str){
		return (null == str)?"" : str ;
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

	public static String getCaller() {
		StackTraceElement stack[] = Thread.currentThread().getStackTrace();
		StackTraceElement ste = stack[stack.length - 1];
		return ste.getClassName() + "." + ste.getMethodName() + "()";
	}

	public static String[] split(String original, String regex) {
		return original.split(regex);
	}

	public static int[] strArrayToIntArray(String[] strs) {
		if (strs == null)
			return null;
		int[] in = new int[strs.length];
		for (int i = 0; i < strs.length; i++) {
			in[i] = Integer.parseInt(strs[i]);
		}
		return in;
	}

	public static boolean match(String filter, String param) {
		Pattern p = Pattern.compile(filter);
		Matcher m = p.matcher(param);
		return m.matches();
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

	public static boolean isCharacter(String content) {
		if (null == content || content.trim().length() == 0)
			return false;
		content = content.trim().toLowerCase();
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))) {
				return false;
			}
		}
		return true;
	}


	public static String getNowTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		return dateFormat.format(c.getTime());
	}

	public static String convertNumber(float number) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(number);
	}

	public static String dateFormatTime(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate = formatter.format(date);
		return curDate;
	}

	public static String dateFormatDay(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String curDate = formatter.format(date);
		return curDate;
	}

	
	public static int randomInt(int min, int max) {
		return (int) (Math.random() * (max - min + 1)) + min;
	}

	public static String encrypt(String a1) {
		String str = a1.toLowerCase();
		String str1 = "";
		if (str == null || str == "")
			return null;
		int length = str.length();
		int tlen = 4;
		// for(int i=0;i<tlen;i++){
		// str1 = str1+"A";
		// }
		if (tlen >= 0) {
			for (int j = 0; j < length; j++) {
				// System.out.println(str.substring(j,j+1));
				if (checkInteger(str.substring(j, j + 1))) {
					int integer = Integer.parseInt(str.substring(j, j + 1)) + 7;
					if (integer == 10) {
						str1 = str1 + String.valueOf("X");
					} else if (integer == 11) {
						str1 = str1 + String.valueOf("Y");
					} else if (integer == 12) {
						str1 = str1 + String.valueOf("Z");
					} else if (integer == 13) {
						str1 = str1 + String.valueOf("T");
					} else if (integer == 14) {
						str1 = str1 + String.valueOf("Q");
					} else if (integer == 15) {
						str1 = str1 + String.valueOf("M");
					} else if (integer == 16) {
						str1 = str1 + String.valueOf("N");
					} else {
						str1 = str1 + String.valueOf(integer);
					}
				} else {
					if (str.substring(j, j + 1).equals("X")) {
						str1 = str1 + "0";
					} else if (str.substring(j, j + 1).equals("Y")) {
						str1 = str1 + "1";
					} else if (str.substring(j, j + 1).equals("Z")) {
						str1 = str1 + "2";
					} else if (str.substring(j, j + 1).equals("T")) {
						str1 = str1 + "3";
					} else if (str.substring(j, j + 1).equals("Q")) {
						str1 = str1 + "4";
					} else if (str.substring(j, j + 1).equals("M")) {
						str1 = str1 + "5";
					} else if (str.substring(j, j + 1).equals("N")) {
						str1 = str1 + "6";
					} else {
						int str_integer = (int) str.charAt(j) + 7;
						char c_str = (char) str_integer;
						str1 = str1 + c_str;
					}
				}
			}
		} else {
			return null;
		}
		return str1;
	}

	public static String desecrypt(String str) {
		String str1 = "";
		if (str == null || str == "")
			return null;
		int length = str.length();
		for (int i = 0; i < length; i++) {
			String str2 = str.substring(i, i + 1);
			if (checkInteger(str2)) {
				if (str2.equals("0")) {
					str1 = str1 + "X";
				} else if (str2.equals("1")) {
					str1 = str1 + "Y";
				} else if (str2.equals("2")) {
					str1 = str1 + "Z";
				} else if (str2.equals("3")) {
					str1 = str1 + "T";
				} else if (str2.equals("4")) {
					str1 = str1 + "Q";
				} else if (str2.equals("5")) {
					str1 = str1 + "M";
				} else if (str2.equals("6")) {
					str1 = str1 + "N";
				} else {
					int integer = Integer.parseInt(str2) - 7;
					str1 = str1 + String.valueOf(integer);
				}
			} else {
				if (str2.equals("X")) {
					int integer = 10 - 7;
					str1 = str1 + String.valueOf(integer);
				} else if (str2.equals("Y")) {
					int integer = 11 - 7;
					str1 = str1 + String.valueOf(integer);
				} else if (str2.equals("Z")) {
					int integer = 12 - 7;
					str1 = str1 + String.valueOf(integer);
				} else if (str2.equals("T")) {
					int integer = 13 - 7;
					str1 = str1 + String.valueOf(integer);
				} else if (str2.equals("Q")) {
					int integer = 14 - 7;
					str1 = str1 + String.valueOf(integer);
				} else if (str2.equals("M")) {
					int integer = 15 - 7;
					str1 = str1 + String.valueOf(integer);
				} else if (str2.equals("N")) {
					int integer = 16 - 7;
					str1 = str1 + String.valueOf(integer);
				} else {
					int i_str = (int) str2.charAt(0) - 7;
					char c_str = (char) i_str;
					str1 = str1 + c_str;
				}
			}
		}
		return str1;
	}

	private static boolean checkInteger(String string) {
		boolean mark = false;
		if (string.equals("0") || string.equals("1") || string.equals("2")
				|| string.equals("3") || string.equals("4")
				|| string.equals("5") || string.equals("6")
				|| string.equals("7") || string.equals("8")
				|| string.equals("9"))
			mark = true;
		return mark;
	}

	public boolean isExistInArr(String str, String[] arr) {
		boolean flag = false;
		for (int i = 0; i < arr.length; i++) {
			if (str.equals(arr[i])) {
				flag = true;
				break;
			}
		}
		return flag;
	}


	public static String generateStr(int n) {
		if (0 >= n)
			throw new java.lang.IllegalArgumentException();
		char[] ascii = new char[n];
		Random random = new Random();
		String result = null;
		for (int i = 0; i < n; i++) {
			int asc = 47;
			asc = random.nextInt(10) + 48;
			ascii[i] = (char) asc;
		}
		result = new String(ascii);
		return result;
	}

	public static boolean nullOrEmpty(String text) {
		return null == text || 0 == text.trim().length();
	}

	public static String getYesterday() {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, -1);

			return sdf.format(cal.getTime());
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 判断是否有@,#,|号(此两字符在游戏中有特殊含义)
	 * @return
	 */
	public static boolean haveSpecialChar(String str){
		if(nullOrEmpty(str)){
			return false ;
		}
		if(str.indexOf("@")>=0){
			return true ;
		}
		if(str.indexOf("#")>=0){
			return true ;
		}
		if(str.indexOf("|")>=0){
			return true ;
		}
		if(str.indexOf("--")>=0){
			return true ;
		}
		return false ;
	}
	
	/**
	 * 验证怪异字符
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static boolean stringFilter(String str) throws PatternSyntaxException { 
		String regEx = str.replaceAll("[\\pP|~|$|^|`|￥|<|>|\\||\\+|=]", "*");
		if(regEx.contains("*")){
			return true;
		}
		return false; 
	}


	public  static boolean notEmptyAndSame(String id1,String id2){
		if(Util.isEmpty(id1) || Util.isEmpty(id2)){
			return false ;
		}
		return id1.equals(id2) ;
	}
}
