package sacred.alliance.magic.app.carnival;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.carnival.logic.CarnivalLogic;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.CondCompareType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class CarnivalRule implements KeySupport<Integer> {
	private int id;
	private int carnivalTypeId;
	//比较类型 0:小于, 1:大于
	private byte compareType;
	//比较的上下限值，如果只有一个参数用第一个
	private int minValue;
	private int maxValue;
	private String condOrValue; 
	
	//根据条件的类型生成相关属性类
	private CarnivalLogic carnivalLogic;
	private CondCompareType conditionCompareType;
	//是否替换名字中的相应属性值
	private boolean strReplace;
	private List<Integer> condOrValueList;
	private CarnivalType carnivalType;
	@Override
	public Integer getKey() {
		return this.id;
	} 
	
	private void getCarnivalInstance(){
		this.carnivalType = CarnivalType.get(carnivalTypeId);
		if(null == carnivalType){
			return;
		}
		carnivalLogic = carnivalType.createCarnivalLogic(carnivalType);
	}
	
	public void init(){
		this.getCarnivalInstance();
		if(null == carnivalLogic) {
			return;
		}
		
		conditionCompareType =  CondCompareType.get(compareType);
		if(compareType != CondCompareType._OR.getType()){
			return ;
		}
		String[] values = Util.splitStr(condOrValue, Cat.comma);
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
	
	public List<CarnivalRankInfo> getCarnivalRank(int itemId){
		return carnivalLogic.getCarnivalRank(this, itemId);
	}
	
	public void reward(int itemId, List<CarnivalRankInfo> rankList) {
		carnivalLogic.reward(itemId, this, rankList);
	}
}
