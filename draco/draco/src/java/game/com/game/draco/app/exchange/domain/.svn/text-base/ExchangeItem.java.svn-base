package com.game.draco.app.exchange.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.FrequencyType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.condition.Condition;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.exchange.consumetype.ConsumeAttriType;
import com.game.draco.app.exchange.consumetype.ConsumeLogic;
public @Data class ExchangeItem implements KeySupport<Integer>{
	public static final byte FACTION_NOT_HAS = 0;
	public static final byte FACTION_INTEGAL_NOT_ENOUGH = 1; 
	public static final byte FACTION_SUCCESS = 2;
	private int menuId;
	private int id;
	private String name;
	private String startDate;
	private String endDate;
	private byte frequencyType;
	private byte frequencyValue;
	private short enterResetCopyId;
	private String conditionIds;
	
	private byte consumeType;
	private int consumeOriginal;
	private int consumeDiscount;
	
	private String consumeGoodIds;
	private String consumeGoodCount;
	private int gainGoodsId;
	private int gainGoodsCount;
	private byte bindType;
	//拿conditionIds来初始化列表
	private List<Condition> conditionList = new ArrayList<Condition>();
	//消耗的物品<goodId, goodCount>
	private Map<Integer, Integer> consumeGoods = new LinkedHashMap<Integer, Integer>();
	//获得的物品
	private GoodsOperateBean gainGoods;
	private List<GoodsOperateBean> gainGoodsList = new ArrayList<GoodsOperateBean>();
	private Date start;
	private Date end;
	private boolean timeOpen = false;//是否有时间控制
	private ConsumeLogic consumeLogic;
	private String broadcast;
	private int firstConsumeGoodsId = 0;//第一个消耗品的物品ID，喊话用
	/**
	 * 初始化兑换的实例
	 * @return false:兑换过期,else true
	 */
	public boolean init(){
		String fileName = XlsSheetNameType.exchange_item.getXlsName();
		String sheetName = XlsSheetNameType.exchange_item.getSheetName();
		//日期
		if(timeOpen) {
			start = DateUtil.getDateZero(DateUtil.strToDate(startDate,DateUtil.format2));
			end = DateUtil.getDateEndTime(DateUtil.strToDate(endDate,DateUtil.format2));
			if(isOutDate()){
				return false;
			}
		}
		//消耗物品
		String[] tGoodIds = Util.splitString(consumeGoodIds);
		String[] tGoodCount = Util.splitString(consumeGoodCount);
		if(tGoodIds.length == tGoodCount.length){
			int goodsId;
			for(int i=0; i<tGoodIds.length; i++){
				goodsId = Integer.valueOf(tGoodIds[i]);
				if(goodsId <= 0){
					continue ;
				}
				if(firstConsumeGoodsId == 0){
					firstConsumeGoodsId = goodsId;
				}
				if(null == GameContext.getGoodsApp().getGoodsBase(goodsId)){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", exchange id= " + this.id + "  consume goodId=" +tGoodIds[i] + " is not exsit!");
					continue ;
				}
				consumeGoods.put(Integer.valueOf(tGoodIds[i]), Integer.valueOf(tGoodCount[i]));
			}
		}
		else{
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", exchange id= " + this.id +" consume goodId num is not equal goodCount num");
		}
		//获得物品
		if(null == GameContext.getGoodsApp().getGoodsBase(gainGoodsId)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", exchange id=" + this.id + " gain goodId=" + gainGoodsId + " is not exsit!");
		}
		if(gainGoodsId <= 0 && gainGoodsCount <= 0) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", exchange id=" + this.id + " gain goodId num is not equal goodCount num or gain goodId num is not equal bindType num");
		}
		gainGoods = new GoodsOperateBean(gainGoodsId, gainGoodsCount, bindType);
		gainGoodsList.add(gainGoods);
		//加载消耗
		ConsumeAttriType type = ConsumeAttriType.get(consumeType);
		if(null != type) {
			consumeLogic = type.createConsumeLogic();
		}
		return true;
	}
	
	@Override
	public Integer getKey() {
		return this.id;
	}
	/**
	 * 判断是否能兑换,判断项:兑换日期，每天兑换时间段，兑换次数，兑换条件，消耗物品
	 * @return
	 */
	public Status isMeet(RoleInstance role){
		if(!isInDate()){
			return Status.Exchange_Not_InDate;
		}
		if(!isHasTimes(role)){
			return Status.Exchange_Frequency_Not_Enough;
		}
		if(!isMeetConditions(role)){
			return Status.Exchange_Condition_Not_Meet;
		}
		if(!isHasConsumeGoods(role)){
			return Status.Exchange_ConsumeGood_Not_Enough;
		}
		if(!isHasEnoughNum(role)){
			return consumeLogic.getFailureStatus();
		}
		return Status.Exchange_Can_Exchange;
	}
	
	/**
	 * 是否在兑换日期内
	 * @return
	 */
	public boolean isInDate(){
		if(start == null && end == null)
			return true;
		return DateUtil.dateInRegion(new Date(), start, end);
	}
	
	/**
	 * 是否满足兑换次数条件
	 * @return
	 */
	public boolean isHasTimes(RoleInstance role){
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_NONE.getType()){
			return true;
		}
		Date now = new Date();
		ExchangeDbInfo exchangeDbInfo = role.getExchangeDbInfo().get(id);
		if(null == exchangeDbInfo){
			return true;
		}
		//比较时间和次数
		Date lastExTime = exchangeDbInfo.getLastExTime();
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_DAY.getType()){
			boolean isSameDay = DateUtil.sameDay(now, lastExTime);
			if(isSameDay && exchangeDbInfo.getTimes() >= frequencyValue){
				return false;
			}
			if(!isSameDay){
				exchangeDbInfo.setTimes((byte)0);
				exchangeDbInfo.setLastExTime(now);
				return true;
			}
		}
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_WEEK.getType()){
			boolean isSameWeek = DateUtil.isSameWeek(now, lastExTime);
			if(isSameWeek && exchangeDbInfo.getTimes() >= frequencyValue){
				return false;
			}
			if(!isSameWeek){
				exchangeDbInfo.setTimes((byte)0);
				exchangeDbInfo.setLastExTime(now);
				return true;
			}
		}
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_MONTH.getType()){
			boolean isSameMonth = DateUtil.isSameMonth(now, lastExTime);
			if(isSameMonth && exchangeDbInfo.getTimes() >= frequencyValue){
				return false;
			}
			if(!isSameMonth){
				exchangeDbInfo.setTimes((byte)0);
				exchangeDbInfo.setLastExTime(now);
				return true;
			}
		}
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_COPY.getType() &&
				exchangeDbInfo.getTimes() >= frequencyValue){
			return false;
		}
		return true;
	}
	
	public void updateDbInfo(RoleInstance role){
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_NONE.getType()){
			return;
		}
		Date now = new Date();
		ExchangeDbInfo exchangeDbInfo = role.getExchangeDbInfo().get(id);
		if(null == exchangeDbInfo){
			exchangeDbInfo = new ExchangeDbInfo(id, role.getRoleId(), (byte)1, now, end);
			exchangeDbInfo.setExistRecord(false);
			role.getExchangeDbInfo().put(id, exchangeDbInfo);
			return;
		}
		
		Date lastExTime = exchangeDbInfo.getLastExTime();
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_DAY.getType()){
			if(DateUtil.sameDay(now, lastExTime)){
				exchangeDbInfo.setLastExTime(now);
				exchangeDbInfo.setTimes((byte)(exchangeDbInfo.getTimes() + 1));
			}
			else{
				exchangeDbInfo.setLastExTime(now);
				exchangeDbInfo.setTimes((byte)1);
			}
			return ;
		}
		
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_WEEK.getType()){
			if(DateUtil.isSameWeek(now, lastExTime)){
				exchangeDbInfo.setLastExTime(now);
				exchangeDbInfo.setTimes((byte)(exchangeDbInfo.getTimes() + 1));
			}
			else{
				exchangeDbInfo.setLastExTime(now);
				exchangeDbInfo.setTimes((byte)1);
			}
			return ;
		}
		
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_MONTH.getType()){
			if(DateUtil.isSameMonth(now, lastExTime)){
				exchangeDbInfo.setLastExTime(now);
				exchangeDbInfo.setTimes((byte)(exchangeDbInfo.getTimes() + 1));
			}
			else{
				exchangeDbInfo.setLastExTime(now);
				exchangeDbInfo.setTimes((byte)1);
			}
			return ;
		}
		
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_FOREVER.getType() || 
				frequencyType == FrequencyType.FREQUENCY_TYPE_COPY.getType()){
			exchangeDbInfo.setLastExTime(now);
			exchangeDbInfo.setTimes((byte)(exchangeDbInfo.getTimes() + 1));
		}
	}
	
	public int getFrequencyInfo(RoleInstance role){
		ExchangeDbInfo exchangeDbInfo = role.getExchangeDbInfo().get(id);
		if(null != exchangeDbInfo){
			return exchangeDbInfo.getTimes();
		}
		return 0;
	}
	
	
	/**
	 * 是否满足所有条件
	 */
	public boolean isMeetConditions(RoleInstance role){
		if(null == conditionList || conditionList.size() == 0)
			return true;
		for(Condition exchangeConditon : conditionList){
			if(null == exchangeConditon){
				continue;
			}
			if(!exchangeConditon.isMeet(role)){
				return false;
			}
		}
		return true;
	}
	
	public boolean isMeetConditionsAndDis(RoleInstance role){
		if(null == conditionList || conditionList.size() == 0)
			return true;
		//条件不满足的话兑换项不显示
		for(Condition exchangeConditon : conditionList){
			if(!exchangeConditon.isMeet(role) && !exchangeConditon.isDisplay()){
				return false;
			}
		}
		//如果是兑换次数是永久的那种，如果已经兑换过了就不显示
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_FOREVER.getType()){
			ExchangeDbInfo exchangeDbInfo = role.getExchangeDbInfo().get(id);
			if(null != exchangeDbInfo){
				if(exchangeDbInfo.getTimes() >= frequencyValue){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 消耗的物品是否满足
	 * @param role
	 * @return
	 */
	public boolean isHasConsumeGoods(RoleInstance role){
		if(null==consumeGoods || consumeGoods.size() == 0)
			return true;
		for(Integer key : consumeGoods.keySet()){
			if(!(role.getRoleBackpack().countByGoodsId(key) >= consumeGoods.get(key))){
				return false;
			}
		}
		return true;
	}
	
	public boolean isHasEnoughNum(RoleInstance role){
		if(null == consumeLogic) {
			return true;
		}
		int roleHasNum = consumeLogic.getRoleAttri(role);
		if(roleHasNum < consumeDiscount){
			return false;
		}
		return true;
	}
	
	/**
	 * 活动是否过期
	 * */
	public boolean isOutDate(){
		if(end == null){
			return false;
		}
		return System.currentTimeMillis() >= end.getTime();
	}
	
	/**
	 * 重置次数
	 * @param exchangeDbInfo
	 */
	public void resetExchange(ExchangeDbInfo exchangeDbInfo) {
		if(exchangeDbInfo.getTimes() == 0) {
			return;
		}
		Date now = new Date();
		//比较时间和次数
		Date lastExTime = exchangeDbInfo.getLastExTime();
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_DAY.getType()){
			boolean isSameDay = DateUtil.sameDay(now, lastExTime);
			if(!isSameDay){
				exchangeDbInfo.setTimes((byte)0);
				exchangeDbInfo.setLastExTime(now);
			}
		}
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_WEEK.getType()){
			boolean isSameWeek = DateUtil.isSameWeek(now, lastExTime);
			if(!isSameWeek){
				exchangeDbInfo.setTimes((byte)0);
				exchangeDbInfo.setLastExTime(now);
			}
		}
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_MONTH.getType()){
			boolean isSameMonth = DateUtil.isSameMonth(now, lastExTime);
			if(!isSameMonth){
				exchangeDbInfo.setTimes((byte)0);
				exchangeDbInfo.setLastExTime(now);
			}
		}
	}
	
	/**
	 * 判断荣誉是否够
	 * @param role
	 * @return
	 */
//	public boolean isHasEnoughHonor(RoleInstance role){
//		if(role.getHonor() < consumeHonor){
//			return false;
//		}
//		return true;
//	}
}
