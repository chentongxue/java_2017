package sacred.alliance.magic.app.goods.derive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public @Data class EquipRecatingAttrWeightConfig implements KeySupport<String> {

	private byte attrType;//属性类型
	private int quality;//原装备品质
	private int star ; //原装备星
	private int minValue1;//区间1下限
	private int maxValue1;//区间1上限
	private int weight1;//区间1权重
	private int goldWeight1;//钻石权重1
	private int minValue2;
	private int maxValue2;
	private int weight2;
	private int goldWeight2;
	private int minValue3;
	private int maxValue3;
	private int weight3;
	private int goldWeight3;
	private int minValue4;
	private int maxValue4;
	private int weight4;
	private int goldWeight4;
	private int minValue5;
	private int maxValue5;
	private int weight5;
	private int goldWeight5;
	
	private List<RecatingBoundBean> valueBoundList = new ArrayList<RecatingBoundBean>();
	private Map<Integer,Integer> weightMap = new HashMap<Integer,Integer>();//普通权重
	private Map<Integer,Integer> goldWeightMap = new HashMap<Integer,Integer>();//钻石权重
	
	@Override
	public String getKey() {
		return genKey(this.attrType,this.quality,this.star);
	}
	
	public static String genKey(byte attrType,int quality,int star) {
		return attrType + Cat.underline + quality + Cat.underline + star;
	}
	
	public void init(String fileInfo){
		String info = fileInfo + "attrType=" + this.attrType + ", quality=" + this.quality + " start=" + this.star + " ";
		if(null == AttributeType.get(this.attrType)){
			this.checkFail(info + "attrType not exist.");
		}
		if(null == QualityType.get(this.quality)){
			this.checkFail(info + "quality not exist.");
		}
		this.addValueWeight(info, this.minValue1, this.maxValue1, 3, this.weight1, this.goldWeight1);
		this.addValueWeight(info, this.minValue2, this.maxValue2, 4, this.weight2, this.goldWeight2);
		this.addValueWeight(info, this.minValue3, this.maxValue3, 5, this.weight3, this.goldWeight3);
		this.addValueWeight(info, this.minValue4, this.maxValue4, 6, this.weight4, this.goldWeight4);
		this.addValueWeight(info, this.minValue5, this.maxValue5, 7, this.weight5, this.goldWeight5);
	}
	
	private void addValueWeight(String errorInfo, int minValue, int maxValue, int qualityType, int weight, int goldWeight){
		if(minValue <= 0 || maxValue <= 0 || weight <= 0 || goldWeight <= 0){
			this.checkFail(errorInfo + "minValue,maxValue,weight,goldWeight must be greater than 0.");
		}
		int index = this.valueBoundList.size();
		this.valueBoundList.add(new RecatingBoundBean(minValue, maxValue, qualityType));
		this.weightMap.put(index, weight);
		this.goldWeightMap.put(index, goldWeight);
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public RecatingBoundBean getRecatingBoundBean(int value){
		for(RecatingBoundBean bean : this.valueBoundList){
			if(null == bean){
				continue;
			}
			if(bean.isSuitValue(value)){
				return bean;
			}
		}
		return null;
	}
	
	public RecatingBoundBean randomAttrBound(){
		return this.randomAttrBound(false);
	}
	
	public RecatingBoundBean randomAttrBound(boolean isUseGold){
		Integer key;
		if(isUseGold){
			key = Util.getWeightCalct(this.goldWeightMap);
		}else{
			key = Util.getWeightCalct(this.weightMap);
		}
		if(null == key){
			key = 0;
		}
		return this.valueBoundList.get(key);
	}
	
	public RecatingBoundBean maxQualityBound(){
		int maxIndex = this.valueBoundList.size()-1;
		if(maxIndex < 0){
			maxIndex = 0;
		}
		return this.valueBoundList.get(maxIndex);
	}
	
}
