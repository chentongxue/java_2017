package sacred.alliance.magic.app.attri;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.message.item.RoleAttriHelpItem;
import com.game.draco.message.response.C1107_RoleAttriHelpRespMessage;
import com.google.common.collect.Lists;
import sacred.alliance.magic.app.attri.config.*;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.data.RoleLevelupLoader;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.role.systemset.domain.RoleSystemSet;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.game.draco.message.push.C0404_RoleAttributeCeilingNotifyMessage;
public class AttriAppImpl implements AttriApp{
	
	private RoleLevelupLoader roleLevelupLoader;
	private Map<Byte, AttriBattleScore> battleScoreMap = null;
	private Map<Byte, AttriHurtSeries> hurtSeriesMap = null;
	private Map<String, AttriHurtRestrict> hurtRestrictMap = null;
	private Map<String, AttriRestrictGear> restrictGearMap = null;
	private Map<String,AttriCeilRoleValue> attriCeilPotentialMap = null ;
	private Map<String,AttriCeilRoleValue> attriCeilExpHoopMap = null ;
	private List<ExpHookClean> expHookCleanList = null ;
    private List<AttriHelpConfig> attriHelpConfigList = null ;
	
	@Override
	public ExpHookClean getExpHookCleanByVip(int vipLevel) {
		if(Util.isEmpty(this.expHookCleanList)){
			return null ;
		}
		for(int index = this.expHookCleanList.size() -1 ;index  >= 0 ;index--){
			ExpHookClean config = this.expHookCleanList.get(index);
			if(vipLevel >= config.getVipLevel()){
				return config ;
			}
		}
		return null ;
	}
	
	@Override
	public int getCleanFatigueMaxTimes(){
		if(Util.isEmpty(expHookCleanList)){
			return 1 ;
		}
		return expHookCleanList.get(expHookCleanList.size()-1).getTimes();
	}
	
	@Override
	public void autoCleanFatigue(RoleInstance role) {
		if(role.get(AttributeType.expHook) 
				< role.get(AttributeType.maxExpHook)){
			//疲劳度未到100%
			return ;
		}
		RoleCount rc = role.getRoleCount() ;
		int cleanTimes = rc.getRoleTimesToInt(CountType.TodayHookCleanTimes);//getTodayHookCleanTimes() ;
		//判断是否大于了自己的设置
		RoleSystemSet systemSet = role.getSystemSet() ;
		if(null == systemSet){
			return ;
		}
		if(cleanTimes >= systemSet.getFatigue() ){
			return ;
		}
		ExpHookClean config = this.getExpHookClean(cleanTimes +1);
		if(null == config || GameContext.getVipApp().getRoleVipExp(role) < config.getVipLevel()){
			return ;
		}
		if(role.getGoldMoney() < config.getRmbMoney()){
			return ;
		}
		// 扣除同时更新疲劳度
		GameContext.getUserAttributeApp().changeRoleMoney(role,
						AttributeType.goldMoney, OperatorType.Decrease,
						config.getRmbMoney(),
						OutputConsumeType.role_fatigue_auto_clean_consume);
//		rc.setTodayHookCleanTimes(cleanTimes + 1);
//		rc.setTodayHookExp(0);
		rc.changeTimes(CountType.TodayHookCleanTimes,cleanTimes + 1);
		rc.changeTimes(CountType.TodayHookExp, 0);
		role.getBehavior().notifyAttribute();
	}
	
	
	@Override
	public ExpHookClean getExpHookClean(int times){
		if(times <= 0){
			times = 1 ;
		}else if(times > expHookCleanList.size()){
			times = expHookCleanList.size() ;
		}
		return expHookCleanList.get(times-1) ;
	}
	
	@Override
	public void setArgs(Object args) {
		
	}
	
	@Override
	public boolean isEffectBattleScore(byte attriType) {
		if(Util.isEmpty(battleScoreMap)){
			return false ;
		}
		return battleScoreMap.containsKey(attriType);
	}
	
	
	
	private void notifyCeilAttribute(RoleInstance role,AttributeType attType){
		C0404_RoleAttributeCeilingNotifyMessage notifyMsg = new C0404_RoleAttributeCeilingNotifyMessage();
		notifyMsg.setAttriType(attType.getType());
		GameContext.getMessageCenter().sendSysMsg(role, notifyMsg);
	}
	
	@Override
	public float ceilAttributeProcess(RoleInstance role, AttributeType attType,
			float value) {
		if (value <= 0.0) {
			return value;
		}
		// 获得
		int maxValue = this.getMaxValue(role, attType);
		if (maxValue <= 0) {
			return value;
		}
		// 获得今天已经获得的挂机经验
		int curValue = this.getCurrValue(role, attType);
		int realValue = (int) Math.min(Math.max(0, maxValue - curValue), value);
		if (realValue <= 0) {
			this.notifyCeilAttribute(role, attType);
		} else {
			this.realValueProcess(role, attType, realValue,curValue,maxValue);
		}
		return realValue;
	}
	
	
	private void realValueProcess(RoleInstance role,AttributeType attType,
			int realValue,int curValue,int maxValue){
		if(AttributeType.expHook  != attType){
			return ;
		}
		role.getRoleCount().changeTimes(CountType.TodayHookExp,curValue + realValue);//incrTodayHookExp(realValue);
		if((realValue + curValue) == maxValue){
			//自动清除
			GameContext.getAttriApp().autoCleanFatigue(role);
		}
	}
	
	
	private int getCurrValue(RoleInstance role,AttributeType attType){
		if(AttributeType.expHook  == attType){
			return role.getRoleCount().getRoleTimesToInt(CountType.TodayHookExp);//getTodayHookExp() ; 
		}
		return role.get(attType) ;
	}
	
	public int getMaxValue(RoleInstance role,AttributeType attType){
		Map<String,AttriCeilRoleValue> ceilMap = null ;
		if(AttributeType.expHook  == attType){
			ceilMap = this.attriCeilExpHoopMap ;
		} else if(AttributeType.potential == attType){
			ceilMap = this.attriCeilPotentialMap ;
		}
		AttriCeilRoleValue value = this.fromMap(ceilMap,String.valueOf(role.getLevel()));
		if (null == value) {
			return 0;
		}
		int maxValue = value.getMaxValue();
		int vipRate = GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(),
				VipPrivilegeType.ATTRIBUTE_CEIL_PERCENT.getType(),String.valueOf(attType.getType())) ;
		if(vipRate <= 0){
			return maxValue ;
		}
		return (int)((1 + vipRate/ParasConstant.PERCENT_BASE_VALUE) * maxValue);
	}

	
	@Override
	public AttributeType getCeilAttributeType(AttributeType attType) {
		if(AttributeType.expHook  == attType){
			return AttributeType.exp ;
		}
		if(AttributeType.potential == attType){
			return AttributeType.potential ;
		}
		return null ;
	}
	
	private <T> T fromMap(Map<String,T> map,String key){
		if(null == map){
			return null ;
		}
		return map.get(key);
	}
	
	
	@Override
	public int getEffectBattleScore(RoleInstance role) {
		return role.getBattleScore() ;
		/*int score = role.getBattleScore();
		if(!GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())){
			return score ;
		}
		
		role.syncBattleScore();
		return role.getBattleScore() ;*/
	}
	

	@Override
	public void start() {
		roleLevelupLoader.load();
		
		this.loadBattleScore();
		this.loadHurtSeries();
		this.loadAttriRestrict();
		this.loadRestrictGear();
		this.loadAttriCeilExpHoop();
		this.loadAttriCeilPotential();
		this.loadExpHookClean();
        this.loadAttriHelp();
	}
	

	@Override
	public void stop() {
		
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		//计算有效战斗力,避免用户刷嘉年华和战神榜战斗力
		try {
			int battleScore = GameContext.getAttriApp().getEffectBattleScore(
					role);
			role.setBattleScore(battleScore);
		} catch (Exception nex) {
			
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	

	@Override
	public RoleLevelup getLevelup(int level) {
		return this.roleLevelupLoader.getDataMap().get(String.valueOf(level));
	}

	public void setRoleLevelupLoader(RoleLevelupLoader roleLevelupLoader) {
		this.roleLevelupLoader = roleLevelupLoader;
	}
	
	private void loadExpHookClean(){
		String fileName = XlsSheetNameType.attri_ceiling_exp_hook_clean.getXlsName();
		String sheetName = XlsSheetNameType.attri_ceiling_exp_hook_clean.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		this.expHookCleanList = XlsPojoUtil.sheetToList(sourceFile,sheetName, ExpHookClean.class) ;
		if(Util.isEmpty(this.expHookCleanList)){
			Log4jManager.CHECK.error("AttriApp loadExel sourceFile = " + sourceFile 
					+ " sheetName =" + sheetName + ", not config");
			Log4jManager.checkFail();
			return ;
		}
		this.expHookCleanList.get(this.expHookCleanList.size()-1).setMax(true);
	}
	
	private void loadAttriCeilPotential(){
		this.attriCeilPotentialMap = XlsPojoUtil.loadMap(XlsSheetNameType.attri_ceiling_potential,
				AttriCeilRoleValue.class, false) ;
		this.checkMap(XlsSheetNameType.attri_ceiling_potential, this.attriCeilPotentialMap);
	}
	
	private void loadAttriCeilExpHoop(){
		this.attriCeilExpHoopMap = XlsPojoUtil.loadMap(XlsSheetNameType.attri_ceiling_exp_hook,
				AttriCeilRoleValue.class, false) ;
		this.checkMap(XlsSheetNameType.attri_ceiling_exp_hook, this.attriCeilExpHoopMap);
	}
	
	
	private void checkMap(XlsSheetNameType xls,Map map){
		if(Util.isEmpty(map)) {
			Log4jManager.CHECK.error("AttriApp loadExel sourceFile = " + xls.getXlsName() + " sheetName =" + xls.getSheetName() + ", not config");
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载属性战斗力配置
	 */
	private void loadBattleScore() {
		String fileName = "";
		String sheetName = "";
		try {			
			fileName = XlsSheetNameType.attri_battle_score.getXlsName();
			sheetName = XlsSheetNameType.attri_battle_score.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			battleScoreMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AttriBattleScore.class);
			if(Util.isEmpty(battleScoreMap)) {
				Log4jManager.CHECK.error("AttriApp loadExel sourceFile = " + fileName + " sheetName =" + sheetName + ", not config");
				Log4jManager.checkFail();
				return ;
			}
			for(Entry<Byte, AttriBattleScore> entry : battleScoreMap.entrySet()) {
				AttriBattleScore abs = entry.getValue();
				if(null == abs) {
					continue;
				}
				abs.init();
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载系伤害配置
	 */
	private void loadHurtSeries() {
		String fileName = "";
		String sheetName = "";
		try {			
			fileName = XlsSheetNameType.attri_hurt_series.getXlsName();
			sheetName = XlsSheetNameType.attri_hurt_series.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			hurtSeriesMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AttriHurtSeries.class);
			if(Util.isEmpty(hurtSeriesMap)) {
				Log4jManager.CHECK.error("AttriApp loadExel sourceFile = " + fileName + " sheetName =" + sheetName + ", not config");
				Log4jManager.checkFail();
				return ;
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}

    private void loadAttriHelp(){
        String fileName = "";
        String sheetName = "";
        try {
            fileName = XlsSheetNameType.attri_help.getXlsName();
            sheetName = XlsSheetNameType.attri_help.getSheetName();
            String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
            this.attriHelpConfigList = XlsPojoUtil.sheetToList(sourceFile, sheetName, AttriHelpConfig.class);
           /* if(Util.isEmpty(attriHelpConfigList)) {
                Log4jManager.CHECK.error("AttriApp loadExel sourceFile = " + fileName + " sheetName =" + sheetName + ", not config");
                Log4jManager.checkFail();
                return ;
            }*/
        } catch (Exception e) {
            Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
            Log4jManager.checkFail();
        }
    }
	
	/**
	 * 加载系伤害相克配置表
	 */
	private void loadAttriRestrict() {
		String fileName = "";
		String sheetName = "";
		try {			
			fileName = XlsSheetNameType.attri_restrict_info.getXlsName();
			sheetName = XlsSheetNameType.attri_restrict_info.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			hurtRestrictMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AttriHurtRestrict.class);
			if(Util.isEmpty(hurtRestrictMap)) {
				Log4jManager.CHECK.error("AttriApp loadExel sourceFile = " + fileName + " sheetName =" + sheetName + ", not config");
				Log4jManager.checkFail();
				return ;
			}
			for(Entry<String, AttriHurtRestrict> entry : hurtRestrictMap.entrySet()) {
				AttriHurtRestrict restrict = entry.getValue();
				if(null == restrict) {
					continue;
				}
                restrict.init();
				byte seriesId = restrict.getSeriesId();
				byte restrictId = restrict.getRestrictId();
				if(this.hurtSeriesMap.containsKey(seriesId)
						&& this.hurtSeriesMap.containsKey(restrictId)) {
					continue;
				}
				Log4jManager.CHECK.error("AttriApp loadAttriRestrict sourceFile = " + fileName + " sheetName =" + sheetName
						+ ", seriesId=" + seriesId + ", restrictId=" + restrictId + "not exist!");
				Log4jManager.checkFail();
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载属性相克伤害倍率
	 */
	private void loadRestrictGear() {
		String fileName = "";
		String sheetName = "";
		try {			
			fileName = XlsSheetNameType.attri_restrict_gear.getXlsName();
			sheetName = XlsSheetNameType.attri_restrict_gear.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			restrictGearMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AttriRestrictGear.class);
			if(Util.isEmpty(restrictGearMap)) {
				Log4jManager.CHECK.error("AttriApp loadExel sourceFile = " + fileName + " sheetName =" + sheetName + ", not config");
				Log4jManager.checkFail();
				return ;
			}
            for(AttriRestrictGear item : restrictGearMap.values()){
                item.init();
            }
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public int getAttriBattleScore(AttriBuffer attriBuffer) {
		if(null == attriBuffer) {
			return 0;
		}
		Map<Byte, AttriItem> map = attriBuffer.getMap();
		if(Util.isEmpty(map)) {
			return 0;
		}
		float totalScore = 0;
		for(Entry<Byte, AttriItem> entry : map.entrySet()) {
			AttriItem item = entry.getValue();
			if(null == item) {
				continue;
			}
			totalScore += this.getBattleScoreFactor(entry.getKey()) * item.getValue();
		}
		return (int)(totalScore);
	}

	private float getBattleScoreFactor(byte attriType) {
		AttriBattleScore battleScore = battleScoreMap.get(attriType);
		if(null == battleScore) {
			return 0;
		}
		return battleScore.getFactor();
	}

	@Override
	public Map<Byte, AttriBattleScore> getBattleScoreMap() {
		return battleScoreMap;
	}
	
	private AttriHurtRestrict getAttriHurtRestrict(byte attackerSeriesId,byte defenderSeriesId){
		String key = attackerSeriesId + "_" + defenderSeriesId ;
		return hurtRestrictMap.get(key);
	}


	private boolean isRoleRestrict(AbstractRole role){
		if(null == role){
			return false ;
		}
		RoleType roleType = role.getRoleType();
		if(RoleType.PLAYER == roleType){
			return true ;
		}
		if(RoleType.NPC == roleType){
			NpcInstance npc = (NpcInstance)role ;
			NpcTemplate template = npc.getNpc() ;
			return null != template && !template.isPureNpc() ;
		}
		return false ;
	}

	private boolean isNpcRestrict(AbstractRole role){
		if(null == role){
			return false ;
		}
		RoleType roleType = role.getRoleType();
		if(RoleType.NPC != roleType){
			return false ;
		}
		if(RoleType.NPC == roleType){
			NpcInstance npc = (NpcInstance)role ;
			NpcTemplate template = npc.getNpc() ;
			return null != template && template.isPureNpc() ;
		}
		return false ;
	}

	@Override
	public float getAttriRestrictHurtRate(AbstractRole attacker,
			AbstractRole defender) {
		if(null == attacker || null == defender){
			return 0 ;
		}
		byte attackerSeriesId = attacker.getAttriSeriesId();
		byte defenderSeriesId = defender.getAttriSeriesId();
        AttriHurtRestrict hurtConfig = this.getAttriHurtRestrict(attackerSeriesId,defenderSeriesId) ;
		if(null == hurtConfig){
			return 0 ;
		}
		//属性相克
		byte battleType = -1;
		if(this.isRoleRestrict(attacker) && this.isRoleRestrict(defender)) {
			battleType = 0;
		}else if (this.isRoleRestrict(attacker) && this.isNpcRestrict(defender)) {
			battleType = 1;
		}else if (this.isNpcRestrict(attacker) && this.isRoleRestrict(defender)) {
			battleType = 2;
		}
		//int gearSum = attacker.getAttriGearId() + defender.getAttriGearId();
		//只计算攻击者的档位
		AttriRestrictGear restrictGear = this.restrictGearMap.get(battleType + Cat.underline + attacker.getAttriGearId());
		if(null == restrictGear) {
			return 0;
		}
		return restrictGear.getFloatRate() * hurtConfig.getFloatRate();
	}

	@Override
	public Collection<AttriHurtSeries> getAttriHurtSeriesList() {
		return this.hurtSeriesMap.values();
	}

	@Override
	public Collection<AttriHurtRestrict> getAttriHurtRestrictList() {
		return this.hurtRestrictMap.values();
	}


    @Override
    public Message getAttriHelpMessage(RoleInstance role){
        if(Util.isEmpty(this.attriHelpConfigList)){
            return null ;
        }
        C1107_RoleAttriHelpRespMessage respMsg = new C1107_RoleAttriHelpRespMessage();
        List<RoleAttriHelpItem> itemList = Lists.newArrayList() ;
        for(AttriHelpConfig config : this.attriHelpConfigList){
            RoleAttriHelpItem item = new RoleAttriHelpItem();
            item.setAttriType(config.getAttriType());
            item.setAttriValue(role.get(config.getAttriType()));
            item.setForwardId(config.getForwardId());
            item.setClosePanel(config.getClosePanel());
            itemList.add(item);
        }
        respMsg.setItemList(itemList);
        return respMsg ;
    }

}
