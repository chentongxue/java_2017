/**
Author zhong by 2008-4-29
zhongmingyu@msn.com 
**/
package sacred.alliance.magic.base;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public enum CompareType {

	Eq(0),
	Neq(1),
	Ge(2),
	Le(3),
	Gt(4),
	Lt(5),
	;
	
	int type;
	
	CompareType(int type){
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public static String[] getAllType(){
		CompareType[] type = CompareType.values();
		String[] ret = new String[type.length];
		for(int i=0;i<type.length;i++){
			ret[i] = toString(type[i].getType());
		}
		return ret;
	}
	
	public static String toString(int type){
		switch(type){
		case 0:
			return "=";
		case 1:
			return "!=";
		case 2:
			return ">=";
		case 3:
			return "<=";
		case 4:
			return ">";
		case 5:
		default:
			return "<";
		}
	}
	
	public static String toStringCn(int type){
		switch(type){
		case 0:
			return "等于";
		case 1:
			return "不等于";
		case 2:
			return "大于等于";
		case 3:
			return "小于等于";
		case 4:
			return "大于";
		case 5:
		default:
			return "小于";
		}
	}
	
	public static boolean compare(int attributeValue, CompareType compareSign, int compareValue){
	
		if(compareSign==CompareType.Eq){
			if(attributeValue == compareValue)return true;
		}else if(compareSign==CompareType.Neq){
			if(attributeValue != compareValue)return true;
		}else if(compareSign==CompareType.Ge){
			if(attributeValue >= compareValue)return true;
		}else if(compareSign==CompareType.Le){
			if(attributeValue <= compareValue)return true;
		}else if(compareSign==CompareType.Gt){
			if(attributeValue > compareValue)return true;
		}else if(compareSign==CompareType.Lt){
			if(attributeValue < compareValue)return true;
		}
		return false;
	}
	public static boolean compare(Date date, CompareType compareSign, Date compareDate){
		if(compareSign==CompareType.Eq){
			if(date.getTime()==compareDate.getTime())return true;
		}else if(compareSign==CompareType.Neq){
			if(date.getTime()!=compareDate.getTime())return true;
		}else if(compareSign==CompareType.Ge){
			if(date.getTime()>=compareDate.getTime())return true;
		}else if(compareSign==CompareType.Le){
			if(date.getTime()<=compareDate.getTime())return true;
		}else if(compareSign==CompareType.Gt){
			if(date.getTime()>compareDate.getTime())return true;
		}else if(compareSign==CompareType.Lt){
			if(date.getTime()<compareDate.getTime())return true;
		}
		return false;
	}
	
	public static boolean compareDate(String date1, CompareType compareSign, String date2) throws Exception{
		Date now = new Date();
		String nowTime;
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date firstDate;
		Date secondDate;
		try {
			firstDate = df.parse(date1);
			secondDate = df.parse(date2);
			nowTime = df.format(now);
			now = df.parse(nowTime);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new Exception("canFinishTask exception!",e);
		}
		return compare(firstDate, compareSign, secondDate);
	}
}
