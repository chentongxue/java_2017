package sacred.alliance.magic.condition;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.CondCompareType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class Condition implements KeySupport<Integer>{
	public final static String REPLACE_SIGN = "${0}";
	private final static String CAT = ",";
	private int id;
	private String name;
	private byte type;
	//比较类型eg: 0:小于, 1:大于
	private byte compareType;
	private String paramId1; //对于某些条件直接其id,比如:指定id的通天塔通关多少次
	//比较的上下限值，如果只有一个参数用第一个
	private int minValue;
	private int maxValue;
	private boolean display;
	private String condOrValue;
	
	//根据条件的类型生成相关属性类
	private CondLogic conditionLogic;
	private CondCompareType conditionCompareType;
	//是否替换名字中的相应属性值
	private boolean strReplace;
	private List<Integer> condOrValueList; 
	
	public void init(byte compareType){
		conditionCompareType =  CondCompareType.get(compareType);
		if(name.indexOf(REPLACE_SIGN) != -1){
			strReplace = true;
		}
		if(compareType != CondCompareType._OR.getType()){
			return ;
		}
		String[] values = Util.splitStr(condOrValue, CAT);
		if(Util.isEmpty(values)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("ExchangeCondition id = " + id + "compareType is or but condOrValue has no value");
			return ;
		}
		
		for(String str : values){
			if(Util.isEmpty(str)){
				continue ;
			}
			if(null == condOrValueList){
				condOrValueList = new ArrayList<Integer>();
			}
			condOrValueList.add(Integer.valueOf(str));
		}
	}
	
	@Override
	public Integer getKey() {
		return this.id;
	}
	
	public void getConditionTypeInstance(){
		ConditionType conditonAttriType = ConditionType.get(type);
		if(null != conditonAttriType){
			conditionLogic = conditonAttriType.createConditionAtrri();
		}
		if(null == conditionLogic){
			//类型不存在会报异常，启不了服务器
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("ExchangeCondition id = " + id + " ConditionType error,canot get conditionLogic");
		}
	}
	
	/**
	 * 当前条件是否满足
	 * @return
	 */
	public boolean isMeet(RoleInstance role) {
		return conditionLogic.isMeet(role, this);
	}
}
