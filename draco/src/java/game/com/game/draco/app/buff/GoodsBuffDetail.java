package com.game.draco.app.buff;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.buff.config.BuffBase;
import com.game.draco.app.buff.domain.BHurtC;
import com.game.draco.app.skill.vo.SkillFormula;

public @Data class GoodsBuffDetail extends BuffBase implements BuffDetail{
	
	private int level ;
	private String desc ;
	
	private int addHp;//立即回复HP值
	private int addHpRate;//立即回复HP百分比(相对最大)
	private int addAnger;//立即增加气力值
	
	private String addGroupId;//BUFF立即影响属性id组
	private String addValueGroupId;//BUFF立即影响属性加值组
	private String addRateGroupId;//BUFF立即影响属性倍值组
	
	private String continueGroupId;//BUFF持续影响属性id组
	private String continueValueGroupId;//BUFF持续影响属性加值组
	private String continueRateGroupId;//BUFF持续影响属性倍值组
	
	private int everyAddHp;//BUFF每跳回复HP值
	private int everyAddHpRate;//BUFF每跳回复HP百分比
	private int everyAddAnger;//BUFF每跳回复气力值
	
	private int clearNum;//立即清除BUFF数量
	private String clearGroupId;//清除BUFFid组
	private int useSkillId;//立即使用技能id
	private int useSkillLv;//使用技能等级
	
	private List<AttriItem> beginGroupAttriList = null ;
	private List<AttriItem> continueGroupAttriList = null ;
	private List<AddAttriItem> beginAddAttriItems = null ;
	private List<AddAttriItem> processAddAttriItems = null ;
	
	
	//立即影响的属性及数值 + 持续影响的属性及数值
	public List<AttriItem> getBeginAttriList(AbstractRole role){
		List<AttriItem> values = new ArrayList<AttriItem>();
		values.addAll(beginGroupAttriList);
		values.addAll(continueGroupAttriList);
		if(Util.isEmpty(beginAddAttriItems)){
			return values ;
		}
		for(AddAttriItem item : beginAddAttriItems ){
			AttriItem ai = item.toAttriItem(role);
			if(null == ai){
				continue ;
			}
			values.add(ai);
		}
		return values ;
	}
	
	//每跳影响的属性及数值
	public List<AttriItem> getProcessAttriList(AbstractRole role){
		List<AttriItem> values = new ArrayList<AttriItem>();
		if(Util.isEmpty(processAddAttriItems)){
			return values ;
		}
		for(AddAttriItem item : processAddAttriItems ){
			AttriItem ai = item.toAttriItem(role);
			if(null == ai){
				continue ;
			}
			values.add(ai);
		}
		return values ;
	}
	
	
	public @Data class AddAttriItem {
		private AttributeType attriType;
		private int value;
		private int maxRate;
		private static final float TEN_THOUSAND = SkillFormula.TEN_THOUSAND ;
		
		public AddAttriItem(AttributeType attriType, int value, int maxRate) {
			this.attriType = attriType;
			this.value = value;
			this.maxRate = maxRate;
		}
		
		public AttriItem toAttriItem(AbstractRole role){
			int total = this.value; 
			if(this.maxRate != 0){
				int max = this.getMax(attriType, role);
				total += (int)(this.maxRate/TEN_THOUSAND*max) ;
			}
			if(0 == total){
				return null ;
			}
			return new AttriItem(attriType.getType(),total,0);
		}
		
		
		private int getMax(AttributeType type,AbstractRole role){
			if(AttributeType.curHP == type){
				return role.getMaxHP() ;
			}
			return 0 ;
		}
	}
	
	/**
	 * 初始化消耗品buff信息
	 */
	@Override
	public void init(){
		this.beginGroupAttriList = this.getGroupBuffAttri(this.addGroupId, 
				this.addValueGroupId, this.addRateGroupId);
		
		this.continueGroupAttriList = this.getGroupBuffAttri(this.continueGroupId, 
				this.continueValueGroupId, this.continueRateGroupId);
		
		this.beginAddAttriItems = this.getBeginAddAttriItems();
		this.processAddAttriItems = this.getProcessAddAttriItems();
	}
	
	 private AddAttriItem create(AttributeType attriType, int value,
			int maxRate) {
		 if(null == attriType ){
			 return null ;
		 }
		 if(0 == value && 0 == maxRate){
			 return null ;
		 }
		return new AddAttriItem(attriType,value,maxRate) ;
	}
	
	
	
	/**
	 * 获得出发后立即回复（影响）的属性及影响值
	 */
	private List<AddAttriItem> getBeginAddAttriItems(){
		List<AddAttriItem> values = new ArrayList<AddAttriItem>();
		AddAttriItem item = this.create(AttributeType.curHP, addHp, addHpRate);
		if(null != item){
			values.add(item);
		}
		return values;
	}
	
	private List<AddAttriItem> getProcessAddAttriItems(){
		List<AddAttriItem> values = new ArrayList<AddAttriItem>();
		AddAttriItem item = this.create(AttributeType.curHP,everyAddHp, everyAddHpRate);
		if(null != item){
			values.add(item);
		}
		return values;
	}
	
	private String getArrayValue(String[] arr,int index){
		if(null == arr || 0 == arr.length 
				|| index < 0 
				|| index >=arr.length
				){
			return "" ;
		}
		return arr[index];
	}
	/**
	 * 获得组影响
	 * @return
	 */
	private List<AttriItem> getGroupBuffAttri(String attriIds,String values,String rates){
		List<AttriItem> attriList = new ArrayList<AttriItem>();
		if(Util.isEmpty(attriIds)){
			return attriList ;
		}
		String[] addGroups = attriIds.split(Cat.comma);
		String[] addValues = values.split(Cat.comma);
		String[] addRates = rates.split(Cat.comma);
		for(int i=0;i<addGroups.length;i++){
			String value = this.getArrayValue(addValues, i);
			if(Util.isEmpty(value)){
				value = "0";
			}
			String rate = this.getArrayValue(addRates, i);
			if(Util.isEmpty(rate)){
				rate = "0";
			}
			if(value.equals("0") && rate.equals("0")){
				continue ;
			}
			attriList.add(new AttriItem(Byte.parseByte(addGroups[i]), 
					Integer.parseInt(value), Integer.valueOf(rate)/*/ TEN_THOUSAND*/));
		}
		return attriList;
	}
	
	
	/**
	 * 获得可以清除的全部buffID
	 * @return
	 */
	public List<Short> getClearBuffGroup(){
		if(null == clearGroupId){
			return null;
		}
		List<Short> list = new ArrayList<Short>();
		String[] buffIds = clearGroupId.split(Cat.comma);
		for(String buffId : buffIds){
			if(null == buffId || ("").equals(buffId)){
				continue;
			}
			list.add(Short.parseShort(buffId));
		}
		
		return list;
	}
	
	/**
	 * 加载时，验证持续影响属性配置的正确性
	 * @return
	 */
	@Override
	public void check(){
		//Log4jManager.CHECK.error("initConsumeBuff error : ----buffId:" + this.getBuffId() + "----The buff Attribute does not exist!");
		//Log4jManager.checkFail();
	}
	
	
	/**
	 * 初始化消耗品Buff
	 * @param detail
	 * @return
	 */
	public Buff newBuff(){
		GoodsBuff buff = new GoodsBuff(this.getBuffId());
		buff.setBuffName(this.getName());
		buff.setEffectType(EffectType.attribute);
		buff.setTimeType(BuffTimeType.continued);
		if(this.getTimeType() == 0){
			buff.setTimeType(BuffTimeType.single);
		}
		buff.setIconId(this.getIconId());
		buff.setPersistTime(this.getPersistTime());
		buff.setIntervalTime(this.getIntervalTime());
		buff.setGroupId(this.getGroupId());
		buff.setReplaceType(this.getReplaceType());
		buff.setDieLost(this.isDieLost());
		buff.setOfflineLost(this.isOfflineLost());
		buff.setOfflineTiming(this.isOfflineTiming());
		buff.setSwitchOn(true);
		buff.setExitInsLost(this.isExitInsLost());
		buff.setTransLost(this.isTransLost());
		return buff;
	}

	@Override
	public List<BHurtC> getBHurtList() {
		return null;
	}
}

