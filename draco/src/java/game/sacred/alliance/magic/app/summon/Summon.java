package sacred.alliance.magic.app.summon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.summon.vo.SummonResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ConsumeNumberType;
import sacred.alliance.magic.base.FrequencyType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.SummonType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.condition.Condition;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.SummonDbInfo;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.ShowNumberItem;

public @Data class Summon implements KeySupport<Integer>{
	//public static final byte FACTION_NOT_HAS = 0;
	//public static final byte FACTION_INTEGAL_NOT_ENOUGH = 1; 
	//public static final byte FACTION_SUCCESS = 2;
	private int id;
	private byte type;
	private String npcId;
	private String name;
	private String desc;
	private int groupId;
	private String startDate;
	private String endDate;
	//每日兑换时间段
	private String day;
	private byte frequencyType;
	private byte frequencyValue;
	private String conditionIds;
	private int consumeSilver;
	private int consumeBindingGold;
	private int consumeGold;
	private String consumeGoodIds;
	private String consumeGoodCount;
	private int consumeIntegral; //消耗公会积分
	//全员奖励
	private String allGoodIds;
	private String allGoodCount;
	private String allBindType;
	private int allGainSilver;
	private int allGainBindingGold;
	private int allGainExp;
	private int allContribute;
	private int allMagicSoul;
	
	//拿conditionIds来初始化列表
	private List<Condition> conditionList = new ArrayList<Condition>();
	//消耗的物品<goodId, goodCount>
	private Map<Integer, Integer> consumeGoods = new HashMap<Integer, Integer>();
	//参与者获得的物品
	private List<GoodsOperateBean> allGainGoods = new ArrayList<GoodsOperateBean>();
	private Date start;
	private Date end;
	
	private int consumeContribute;//消耗的公会贡献度
	private int consumeHonor;//消耗荣誉
	private int consumeFactionMoney;//消耗门派资金
	
	private boolean awardSummonRole;
	
	private List<ShowNumberItem> consumeNumList = new ArrayList<ShowNumberItem>();
	
	private boolean timeOpen = false;//是否有时间控制
	
	private short enterResetCopyId;
	
	/**
	 * 初始化兑换的实例
	 * @return false:兑换过期,else true
	 */
	public boolean init(){
		String fileName = XlsSheetNameType.summon.getXlsName();
		String sheetName = XlsSheetNameType.summon.getSheetName();
		//日期
		if(timeOpen) {
			start = DateUtil.getDateZero(DateUtil.strToDate(startDate,DateUtil.format_yyyy_MM_dd));
			end = DateUtil.getDateEndTime(DateUtil.strToDate(endDate,DateUtil.format_yyyy_MM_dd));
			if(isOutDate())
				return false;
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
				if(null == GameContext.getGoodsApp().getGoodsBase(goodsId)){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", summon id= " + this.id + "  consume goodId=" +tGoodIds[i] + " is not exsit!");
					continue ;
				}
				consumeGoods.put(Integer.valueOf(tGoodIds[i]), Integer.valueOf(tGoodCount[i]));
			}
		}
		else{
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", summon id= " + this.id +" consume goodId num is not equal goodCount num");
		}
		//获得物品
		tGoodIds = Util.splitString(allGoodIds);
		tGoodCount = Util.splitString(allGoodCount);
		String[] bindTypes = Util.splitString(allBindType);
		if(tGoodIds.length == tGoodCount.length && tGoodIds.length == bindTypes.length){
			for(int i=0; i<tGoodIds.length; i++){
				if(null == GameContext.getGoodsApp().getGoodsBase(Integer.valueOf(tGoodIds[i]))){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", summon id=" + this.id + " gain goodId=" +tGoodIds[i] + " is not exsit!");
				}
				GoodsOperateBean good = new GoodsOperateBean(Integer.valueOf(tGoodIds[i]), Integer.valueOf(tGoodCount[i]), Integer.valueOf(bindTypes[i]));
				allGainGoods.add(good);
			}
		}
		else{
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", summon id=" + this.id + " gain goodId num is not equal goodCount num or gain goodId num is not equal bindType num");
		}
		
		initShowNumber();
		return true;
	}
	
	/**
	 * 加载数值类型的消耗
	 */
	private void initShowNumber(){
		ShowNumberItem sn = null;
		if(this.consumeSilver > 0) {
			sn = new ShowNumberItem();
			sn.setNum(this.consumeSilver);
			sn.setType(ConsumeNumberType.Silver.getType());
			consumeNumList.add(sn);
		}
		if(this.consumeBindingGold > 0) {
			sn = new ShowNumberItem();
			sn.setNum(this.consumeBindingGold);
			sn.setType(ConsumeNumberType.BindingGold.getType());
			consumeNumList.add(sn);
		}
		if(this.consumeGold > 0) {
			sn = new ShowNumberItem();
			sn.setNum(this.consumeGold);
			sn.setType(ConsumeNumberType.Gold.getType());
			consumeNumList.add(sn);
		}
		if(this.consumeHonor > 0) {
			sn = new ShowNumberItem();
			sn.setNum(this.consumeHonor);
			sn.setType(ConsumeNumberType.Honor.getType());
			consumeNumList.add(sn);
		}
		if(this.consumeFactionMoney > 0) {
			sn = new ShowNumberItem();
			sn.setNum(this.consumeFactionMoney);
			sn.setType(ConsumeNumberType.FactionMoney.getType());
			consumeNumList.add(sn);
		}
		if(this.consumeContribute > 0) {
			sn = new ShowNumberItem();
			sn.setNum(this.consumeContribute);
			sn.setType(ConsumeNumberType.Contribute.getType());
			consumeNumList.add(sn);
		}
	}
	
	@Override
	public Integer getKey() {
		return this.id;
	}
	/**
	 * 判断是否能兑换,判断项:兑换日期，每天兑换时间段，兑换次数，兑换条件，消耗物品
	 * @return
	 */
	public SummonResult isMeet(RoleInstance role, boolean popAttrDialog){
		SummonResult statusResult = new SummonResult().failure();
		
		if(!isInDate()){
			return statusResult.setStatus(Status.Summon_Not_InDate).failure();
		}
		if(!isInDay()){
			return statusResult.setStatus(Status.Summon_Not_InDate).failure();
		}
		if(!isHasTimes(role)){
			return statusResult.setStatus(Status.Summon_Frequency_Not_Enough).failure();
		}
		if(!isMeetConditions(role)){
			return statusResult.setStatus(Status.Summon_Condition_Not_Meet).failure();
		}
		if(!isHasConsumeGoods(role)){
			return statusResult.setStatus(Status.Summon_ConsumeGood_Not_Enough).failure();
		}
		if(!isHasEnoughMoney(role, popAttrDialog, statusResult)){
			return statusResult.setStatus(Status.Summon_Menoy_Not_Enough).ignore();
		}
		if(!isHasEnoughContribute(role)){
			return statusResult.setStatus(Status.Summon_Faction_Contribute_Not_Enough).failure();
		}
		/*
		if(!isHasEnoughFactionMoney(role)) {
			return Status.Summon_Faction_Money_Not_Enough;
		}
		Status factionStatus = canFactionMeet(role);
		if(factionStatus != Status.SUCCESS){
			return factionStatus;
		}
		*/
		return statusResult.setStatus(Status.Summon_Can_Summon).success();
		
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
	 * 是否在当天兑换时间段内
	 * @return
	 */
	public boolean isInDay(){
		if(day == null || day.equals(""))
			return true;
		Date now = new Date();
		return DateUtil.inOpenTime(now, day);
	}
	
	
	/**
	 * 是否满足兑换次数条件
	 * @return
	 */
	public boolean isHasTimes(RoleInstance role){
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_NONE.getType()){
			return true;
		}
		SummonDbInfo summonDbInfo = getSummonDbInfo(role);
		if(null == summonDbInfo){
			return true;
		}
		//重置
		this.resetSummon(summonDbInfo);
		//比较次数
		return summonDbInfo.getTimes() < frequencyValue ;
	}
	
	public void updateDbInfo(RoleInstance role){
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_NONE.getType()){
			return;
		}
		Date now = new Date();
		SummonDbInfo summonDbInfo = getSummonDbInfo(role);
		if(null == summonDbInfo){
			if(this.type == SummonType.SUMMON_ROLE.getType()) {
				summonDbInfo = new SummonDbInfo(id, role.getRoleId(), this.type, (byte)1, now, end);
				summonDbInfo.setExistRecord(false);
				role.getSummonDbInfo().put(id, summonDbInfo);
			}
			/*else if(this.type == SummonType.SUMMON_FACTION.getType()) {
				Faction faction = GameContext.getFactionApp().getFaction(role);
				FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
				if(null == faction || null == factionRole){
					return;
				}
				summonDbInfo = new SummonDbInfo(id, faction.getFactionId(), this.type, (byte)1, now, end);
				summonDbInfo.setExistRecord(false);
				faction.getSummonDbInfo().put(id, summonDbInfo);
				faction.setSaveDbStateType(SaveDbStateType.Update);
			}else if(this.type == SummonType.SUMMON_CAMP_WAR.getType()) {
				Camp camp = GameContext.getCampApp().getCamp(role.getCampId());
				if(null == camp) {
					return;
				}
				summonDbInfo = new SummonDbInfo(id, String.valueOf(camp.getCampId()), this.type, (byte)1, now, end);
				summonDbInfo.setExistRecord(false);
				camp.getSummonDbInfo().put(id, summonDbInfo);
				camp.setSaveDbStateType(SaveDbStateType.Update);
			}*/
			return;
		}
		
		//决定是否更新数据库数据
		/*if(this.type == SummonType.SUMMON_FACTION.getType()) {
			Faction faction = GameContext.getFactionApp().getFaction(role);
			FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
			if(null == faction || null == factionRole){
				return;
			}
			faction.setSaveDbStateType(SaveDbStateType.Update);
		}
		if(this.type == SummonType.SUMMON_CAMP_WAR.getType()) {
			Camp camp = GameContext.getCampApp().getCamp(role.getCampId());
			camp.setSaveDbStateType(SaveDbStateType.Update);
		}*/
		
		this.resetSummon(summonDbInfo);
		//次数+1
		summonDbInfo.setTimes(summonDbInfo.getTimes() + 1);
	}
	
	
	/**
	 * 是否满足所有条件
	 */
	public boolean isMeetConditions(RoleInstance role){
		if(null == conditionList || conditionList.size() == 0){
			return true;
		}
		for(Condition condition : conditionList){
			if(!condition.isMeet(role)){
				return false;
			}
		}
		return true;
	}
	
	public boolean isMeetConditionsAndDis(RoleInstance role){
		//如果不在时间段不显示
		if(!isInDay()) {
			return false;
		}
		if(null == conditionList || conditionList.size() == 0)
			return true;
		//条件不满足的话兑换项不显示
		for(Condition condition : conditionList){
			if(!condition.isMeet(role) && !condition.isDisplay()){
				return false;
			}
		}
		//如果是兑换次数是永久的那种，如果已经兑换过了就不显示
		if(frequencyType == FrequencyType.FREQUENCY_TYPE_FOREVER.getType()){
			SummonDbInfo summonDbInfo = getSummonDbInfo(role);
			if(null != summonDbInfo){
				if(summonDbInfo.getTimes() >= frequencyValue){
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
	
	public boolean isHasEnoughMoney(RoleInstance role,boolean popAttrDialog, SummonResult statusResult){
		if(!popAttrDialog){
			//money
			if(role.getSilverMoney() < consumeSilver 
					|| role.getGoldMoney() < consumeGold){
				return false;
			}
			return true;
		}
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, consumeSilver);
		if(!ar.isSuccess()||ar.isIgnore()){
			statusResult.setResult(ar);
			return false;
		}
		ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, consumeGold);
		if(!ar.isSuccess()||ar.isIgnore()){
			statusResult.setResult(ar);
			return false;
		}
		return true;
	}
	
//	/**
//	 * 是否有足够的公会积分 
//	 * @return 0:没有公会，1:有公会积分不够，2:满足积分条件
//	 */
//	public Status canFactionMeet(RoleInstance role){
//		if(consumeIntegral <= 0){
//			return Status.SUCCESS;
//		}
//		
//		Faction faction = role.getFaction();
//		if(null == faction){
//			return Status.Summon_Faction_Not_Has;
//		}
//		
//		if(faction.getIntegral() < consumeIntegral){
//			return Status.Summon_Faction_Integal_Not_Enough;
//		}
//		return Status.SUCCESS;
//	}
	
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
	 * 判断公会贡献度是否够
	 * @param role
	 * @return
	 */
	public boolean isHasEnoughContribute(RoleInstance role){
		if(consumeContribute <= 0) {
			return true;
		}
		/*Faction faction = GameContext.getFactionApp().getFaction(role);
		FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
		if(null == faction || null == factionRole){
			return false;
		}
		
		//Contribute
		if(factionRole.getContribution() < consumeContribute){
			return false;
		}
		**/
		return true;
	}
	
//	/**
//	 * 判断有没有使用帮派资金的权限和帮派资金是否足够
//	 * @param role
//	 * @return
//	 */
//	public boolean isHasEnoughFactionMoney(RoleInstance role) {
//		if(consumeFactionMoney <= 0) {
//			return true;
//		}
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//		if(null == faction || null == factionRole){
//			return false;
//		}
//		if(!GameContext.getFactionApp().getPowerTypeSet(role).contains(FactionPowerType.SummonSoul)){
//			return false;
//		}
//		if(faction.getFactionMoney() < consumeFactionMoney){
//			return false;
//		}
//		return true;
//	}
	
	/**
	 * 判断荣誉是否够
	 * @param role
	 * @return
	 */
	public boolean isHasEnoughHonor(RoleInstance role){
		if(role.getHonor() < consumeHonor){
			return false;
		}
		return true;
	}
	
	private SummonDbInfo getSummonDbInfo(RoleInstance role){
		SummonDbInfo summonDbInfo = null;
		if(this.type == SummonType.SUMMON_ROLE.getType()) {
			summonDbInfo = role.getSummonDbInfo().get(id);
		}
		/*else if(this.type == SummonType.SUMMON_FACTION.getType()) {
			Faction faction = GameContext.getFactionApp().getFaction(role);
			FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
			if(null == faction || null == factionRole){
				return null;
			}
			summonDbInfo = faction.getSummonDbInfo().get(id);
		}else if(this.type == SummonType.SUMMON_CAMP_WAR.getType()){
			Camp camp = GameContext.getCampApp().getCamp(role.getCampId());
			if(null == camp) {
				return null;
			}
			summonDbInfo = camp.getSummonDbInfo().get(id);
		}*/
		return summonDbInfo;
	}
	
	/**
	 * 获取次数
	 * @param role
	 * @return
	 */
	public int getSummonCount(RoleInstance role) {
		SummonDbInfo summonDbInfo = getSummonDbInfo(role);
		if(null == summonDbInfo) {
			return 0;
		}
		return summonDbInfo.getTimes();
	}
	
	
	public void resetSummon(SummonDbInfo info){
		if(null == info){
			return ;
		}
		FrequencyType ft = FrequencyType.get(this.frequencyType);
		if(null == ft || FrequencyType.FREQUENCY_TYPE_NONE == ft){
			//没有限制
			return ;
		}
		Date now = new Date();
		if(ft.isInCycle(now, info.getLastExTime())){
			//在同一周期
			return ;
		}
		//重置次数
		info.setLastExTime(now);
		info.setTimes(0);
	}
	
}
