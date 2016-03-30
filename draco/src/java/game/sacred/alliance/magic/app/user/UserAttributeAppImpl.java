package sacred.alliance.magic.app.user;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.calct.CalctManager;
import sacred.alliance.magic.app.user.config.AttributeEnoughConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.RoleNotifyAttribute;

import com.game.draco.GameContext;
import com.game.draco.app.target.cond.TargetCondType;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.app.worldlevel.config.WorldLevelBaseConfig;
import com.game.draco.message.item.RoleAttributeChangeItem;
import com.game.draco.message.push.C0630_AttributeEnoughPopDialogMessage;
import com.game.draco.message.response.C0400_RoleAttributeChangeRespMessage;
import com.google.common.collect.Maps;

public class UserAttributeAppImpl implements UserAttributeApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private CalctManager calctManager;

	private Map<String,AttributeEnoughConfig> attMap = Maps.newHashMap();
	@Override
	public void changeRoleMoney(AbstractRole player, AttributeType attType,
			OperatorType operatorType, int value,OutputConsumeType outputConsumeType){
		
		if (null == player) {
			return;
		}
		try{
			synchronized (player) {
				//如果值为负数，不符合正常逻辑，返回。
				if(value <0){
					Log4jManager.CHANGE_MONEY_ERROR.error("[金额为负数]\troleId="+player.getRoleId()+"\ttype="+attType.getName()+"\tvalue="+value);
					return ;
				}
				
				int _oldMoney = getMoney(player,attType);//记录修改前属性
				int originValue = player.get(attType);
				
				int result = this.compute(originValue, operatorType, value);
				if (result <= 0) {
					result = 0;
				}
				
				if(OperatorType.Add.getType() == operatorType.getType()){
					//充值表金条信息
					if(attType.getType() == AttributeType.goldMoney.getType()){
						player.getRolePayRecord().addCurrMoney(value,0);
						//已经在充值处处理
						//player.setRolePayGold(player.getRolePayGold() + value);
					}
					player.set(attType.getType(), result);
				}else if(OperatorType.Decrease.getType() == operatorType.getType()){
					RoleInstance role = (RoleInstance) player;
					if(attType.getType() == AttributeType.goldMoney.getType()
							&& (null != outputConsumeType && outputConsumeType
									.isCountGoldMoney())) {
						// 首次消费日志
						GameContext.getStatLogApp().roleConsumeLog(role, value, outputConsumeType);
						// 统计消耗
						player.set(AttributeType.residueGoldMoney.getType(),
								Util.safeIntAdd(role.getRoleConsumeGold(),
										value));
						// 玩家帐号消耗
						player.getRolePayRecord().addConsumeMoney(value);
						// 兑换需要
						GameContext.getCountApp().updateRoleBuy(role, value, outputConsumeType);
					}
					player.set(attType.getType(), result);
				}else{
					return;//如果不是加减返回。
				}
				//money变化日志
				GameContext.getStatLogApp().roleMoneyLog((RoleInstance) player, attType, outputConsumeType, value, "");
				//重新计算判断是否正确
				int _newMoney = OperatorType.compute(_oldMoney, operatorType, value);
				if( _newMoney != getMoney(player,attType)){ 
					Log4jManager.CHANGE_MONEY_ERROR.error("[金钱变化有误]\troleId="+player.getRoleId()+"\ttype="+attType.getName()
							+"\tOperatorType="+operatorType.toString()
							+"\toldRoleMoney="+_oldMoney+"\tnowRoleMoney="+getMoney(player,attType)+"\tvalue="+value);
				}
			}
		}catch(Exception e){
			logger.error("changeRoleMoney Exception:roleId="+player.getRoleId()+"\ttype="+attType.getName()+"\troleMoney="+getMoney(player,attType)+"\tvalue="+value,e);
		}
	}
	
	private int compute(int attributeValue, OperatorType operatorType, int operateValue){
		if(operatorType==OperatorType.Add){
			attributeValue = Util.safeIntAdd(attributeValue,operateValue);
		}else if(operatorType == OperatorType.Decrease){
			attributeValue -= operateValue;
		}else if(operatorType == OperatorType.Equal){
			attributeValue = operateValue;
		}
		if(attributeValue < 0){
			return 0;
		}
		return attributeValue;
	}
	
	
	//返回相应玩家金钱
	private int getMoney(AbstractRole role,AttributeType attType){
		RoleInstance player = (RoleInstance)role;
		if(attType == AttributeType.goldMoney){
			return player.getGoldMoney();
		}
		if(attType == AttributeType.gameMoney){
			return  player.getSilverMoney();
		}
		return 0;
	}
	
	/**
	 * 在出生,复活情况下修改curHP,不能调用此方法,必须调用setCurHP
	 */
	@Override
	public void changeAttribute(AbstractRole player, AttributeType attType,
			OperatorType operatorType, int value,OutputConsumeType ocType) {
		//如果是钱属性类型直接调用 changeRoleMoney
		if(null == attType){
			return ;
		}
		if(player.getRoleType() == RoleType.PLAYER 
				&& operatorType == OperatorType.Add) {
			//处理有上限的属性
			AttributeType ceilAttriType = GameContext.getAttriApp().getCeilAttributeType(attType) ;
			if(null != ceilAttriType){
				value = (int)GameContext.getAttriApp().ceilAttributeProcess((RoleInstance)player,attType, value) ;
				attType = ceilAttriType ;
			}
			//目标系统
			//GameContext.getTargetApp().updateTarget((RoleInstance)player,
			//		TargetCondType.AttributeTotal, String.valueOf(attType.getType()), value);
		}
		if(attType.isMoney()){
			this.changeRoleMoney(player, attType, operatorType, value, ocType);
			return ;
		}
		if(AttributeType.dkp == attType){
			this.changeRoleDkp(player,attType,operatorType,value,ocType);
			return ;
		}
		
		if(operatorType == OperatorType.Equal){
			player.set(attType.getType(), value);
			return ;
		}
		if(attType == AttributeType.exp){
			// 世界等级比率
			value = (int) (value * (GameContext.getWorldLevelApp().getWorldLevelRatio((RoleInstance) player) / WorldLevelBaseConfig.PROPORTION));
		}
		if(0 == value){
			return ;
		}
		int prefix = (operatorType == OperatorType.Add)?1:-1;
		this.changeAttribute(player, AttriBuffer.createAttriBuffer().append(attType, prefix*value));
		//经验日志
		if(attType == AttributeType.exp){
			GameContext.getStatLogApp().roleExpLog((RoleInstance) player, value, "", ocType);
		}
		//TODO:属性变化影响相关的勋章
		if(player.getRoleType() == RoleType.PLAYER) {
			GameContext.getMedalApp().updateMedal((RoleInstance) player, attType);
		}
	}


	@Override
	public RoleNotifyAttribute getRoleNotifyAttribute(AbstractRole player) {
		RoleNotifyAttribute notify = new RoleNotifyAttribute();
		
		notify.setHpCur(player.get(AttributeType.curHP));
		notify.setHpMax(player.get(AttributeType.maxHP));
		notify.setLv(player.get(AttributeType.level));
		notify.setSpeed(player.get(AttributeType.speed));
		notify.setState(player.get(AttributeType.state));
		
		if(player.getRoleType() == RoleType.PLAYER){
			notify.setExp(player.get(AttributeType.exp.getType()));
			notify.setGoldMoney(player.get(AttributeType.goldMoney));
			notify.setSilverMoney(player.get(AttributeType.gameMoney));
			notify.setMaxExp(player.get(AttributeType.maxExp));
			notify.setCampPrestige(player.get(AttributeType.campPrestige));
			notify.setPotential(player.get(AttributeType.potential));
			notify.setBattleScore(player.get(AttributeType.battleScore));
			notify.setTodayCoupon(player.get(AttributeType.todayCoupon));
			notify.setCoupon(player.get(AttributeType.coupon));
			notify.setLq(player.get(AttributeType.lq));
			notify.setDkp(player.get(AttributeType.dkp));
			notify.setHonor(player.get(AttributeType.honor));
			notify.setExpHook(player.get(AttributeType.expHook));
			notify.setMaxExpHook(player.get(AttributeType.maxExpHook));
			notify.setBraveSoul(player.get(AttributeType.braveSoul));
			notify.setWildBlood(player.get(AttributeType.wildBlood));
			notify.setTalent(player.get(AttributeType.talent));
			notify.setArena3v3Score(player.get(AttributeType.arena3V3Score));
			notify.setHeroCoin(player.get(AttributeType.heroCoin));
			notify.setPrestigePoints(player.get(AttributeType.prestigePoints));
		}
		return notify;
	}

	@Override
	public C0400_RoleAttributeChangeRespMessage getRoleAttributeChangeMessage(
			RoleNotifyAttribute lastStatus, RoleNotifyAttribute nowStatus) {
		List<RoleAttributeChangeItem> changeList = new ArrayList<RoleAttributeChangeItem>();
		this.buildAttrChangeList(lastStatus.getHpMax(), nowStatus.getHpMax(), 
				AttributeType.maxHP, changeList);
		this.buildAttrChangeList(lastStatus.getHpCur(), nowStatus.getHpCur(),
				AttributeType.curHP, changeList);
		this.buildAttrChangeList(lastStatus.getLv(), nowStatus.getLv(),
				AttributeType.level, changeList);
		this.buildAttrChangeList(lastStatus.getSpeed(), nowStatus.getSpeed(),
				AttributeType.speed, changeList);
		this.buildAttrChangeList(lastStatus.getState(), nowStatus.getState(),
				AttributeType.state, changeList);
		this.buildAttrChangeList(lastStatus.getExp(), nowStatus.getExp(),
				AttributeType.exp, changeList);
		this.buildAttrChangeList(lastStatus.getGoldMoney(), nowStatus.getGoldMoney(), 
				AttributeType.goldMoney, changeList);
		this.buildAttrChangeList(lastStatus.getSilverMoney(), nowStatus.getSilverMoney(), 
				AttributeType.gameMoney, changeList);
		this.buildAttrChangeList(lastStatus.getMaxExp(), nowStatus.getMaxExp(),
				AttributeType.maxExp, changeList);
		this.buildAttrChangeList(lastStatus.getCampPrestige(), nowStatus.getCampPrestige(),
				AttributeType.campPrestige, changeList);
		this.buildAttrChangeList(lastStatus.getPotential(), nowStatus.getPotential(),
				AttributeType.potential, changeList);
		this.buildAttrChangeList(lastStatus.getHeroCoin(), nowStatus.getHeroCoin(),
				AttributeType.heroCoin, changeList);
		this.buildAttrChangeList(lastStatus.getBattleScore(), nowStatus.getBattleScore(),
				AttributeType.battleScore, changeList);
		this.buildAttrChangeList(lastStatus.getTodayCoupon(), nowStatus.getTodayCoupon(),
				AttributeType.todayCoupon, changeList);
		this.buildAttrChangeList(lastStatus.getCoupon(), nowStatus.getCoupon(),
				AttributeType.coupon, changeList);
		
		this.buildAttrChangeList(lastStatus.getExpChange(), nowStatus.getExpChange(),
				AttributeType.expChange, changeList);
		
		this.buildAttrChangeList(lastStatus.getDkp(), nowStatus.getDkp(), 
				AttributeType.dkp, changeList);
		
		this.buildAttrChangeList(lastStatus.getLq(), nowStatus.getLq(),
				AttributeType.lq, changeList);
		this.buildAttrChangeList(lastStatus.getHonor(), nowStatus.getHonor(),
				AttributeType.honor, changeList);
		
		this.buildAttrChangeList(lastStatus.getExpHook(), nowStatus.getExpHook(),
				AttributeType.expHook, changeList);
		this.buildAttrChangeList(lastStatus.getMaxExpHook(), nowStatus.getMaxExpHook(),
				AttributeType.maxExpHook, changeList);
		
		this.buildAttrChangeList(lastStatus.getBraveSoul(), nowStatus.getBraveSoul(),
				AttributeType.braveSoul, changeList);
		this.buildAttrChangeList(lastStatus.getWildBlood(), nowStatus.getWildBlood(),
				AttributeType.wildBlood, changeList);
		this.buildAttrChangeList(lastStatus.getTalent(), nowStatus.getTalent(),
				AttributeType.talent, changeList);
		this.buildAttrChangeList(lastStatus.getArena3v3Score(), nowStatus.getArena3v3Score(),
				AttributeType.arena3V3Score, changeList);
		this.buildAttrChangeList(lastStatus.getPrestigePoints(), nowStatus.getPrestigePoints(),
				AttributeType.prestigePoints, changeList);
		if (0 == changeList.size()) {
			return null;
		}
		C0400_RoleAttributeChangeRespMessage resp = new C0400_RoleAttributeChangeRespMessage();
		resp.setItems(changeList);
		return resp;
	}
	
	private void buildAttrChangeList(int originValue,
			int nowValue, AttributeType attType, List<RoleAttributeChangeItem> changeList) {
		if(nowValue == originValue){
			return;
		}
		RoleAttributeChangeItem item = new RoleAttributeChangeItem();
		item.setAttType(attType.getType());
		item.setValue(nowValue);
		//!!!! 经验变化特殊,参数调用反过来
		if(attType.getType() == AttributeType.expChange.getType()){
			item.setValue(originValue);
		}
		changeList.add(item);
	}

	public boolean levelUp(AbstractRole player) {
		RoleInstance role = (RoleInstance) player;
		RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(role.getLevel());
		//减去升级所需经验
		int upExp = roleLevelup.getUpExp();
		if(upExp>0){
			this.changeAttribute(player, AttributeType.exp, OperatorType.Decrease, upExp, OutputConsumeType.role_level_up);
		}
		role.getBehavior().changeAttribute(AttributeType.level,OperatorType.Add, 1);

		//有效玩家日志
		GameContext.getStatLogApp().userValidLog(role);
		
		role.roleLevelUp(1);
		//this.pushRoleLevelAttriChangeNotifyMessage(role, roleLevelup);
		//动态菜单
		GameContext.getMenuApp().onRoleLevelUp(role);
		//阵营平衡相关（暂时不处理）
		//GameContext.getCampBalanceApp().roleLevelUp(role);
		//目标相关
		GameContext.getTargetApp().updateTarget(role, TargetCondType.RoleLevel);
		GameContext.getHeroApp().onRoleLevelUp(role);
		// 红点提示
		GameContext.getLevelGiftApp().onRoleLevelUp(role);
		// 世界等级
		GameContext.getWorldLevelApp().pushRatioChange(role);
		//天赋升级
		GameContext.getRoleTalentApp().onRoleLevelUp(role);
		//打印日志
		GameContext.getLogApp().userGradeLog(role);
		
		/* ****************** 【测试】期间用 *********************************/
		
		GameContext.getVipApp().onRoleLevelUp(role);
		
		/* *****************************************************************/
		
		/****新属性修改****/
		return this.reCalctForLevelup(role);
	}
	
	
	/**
	 * 角色等级改变后，重计算属性
	 * @param role
	 * @param roleLevelup
	 * @return
	 */
	private boolean reCalctForLevelup(RoleInstance role){
		this.reCalct(role);
		
		try{
			//通知任务
			GameContext.getUserQuestApp().roleUpgrade(role);
		}catch(Exception ex){
			logger.error("",ex);
		}
		try {
			RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(role.getLevel());
			if (null == roleLevelup) {
				logger.error("attri upgrade map is null,roleId = "+ role.getRoleId());
				return false;
			}
			// 计算升级后的最大经验
			int upgNeedExp = (int) roleLevelup.getUpExp();
			role.getBehavior().changeAttribute(AttributeType.maxExp,OperatorType.Equal, upgNeedExp);
			role.setCurHP(role.getMaxHP());
			role.getBehavior().notifyAttribute();
		} catch (Exception e) {
			logger.error("", e);
		}

		//通知技能更新
		try {
			GameContext.getUserSkillApp().roleLevelup(role);
		} catch (Exception e) {
			logger.error("roleLevelup notify skill update error", e);
		}
		//达到满级输出的日志
		return true;
	}

	@Override
	public void reCalctAndNotify(AbstractRole player) {
		if(null == player){
			return ;
		}
		// 属性计算重计算
		this.reCalct(player);
		// 通知属性
		player.getBehavior().notifyAttribute();
	}

	public void setCalctManager(CalctManager calctManager) {
		this.calctManager = calctManager;
	}

	@Override
	public void changeAttribute(AbstractRole role, AttriBuffer attribuffer) {
		this.changeAttribute(role, attribuffer, true);	
	}
	
	@Override
	public void changeAttribute(AbstractRole role, AttriBuffer attribuffer, boolean isEffectBattleScore) {
		calctManager.getCalct(role).changeAttri(role, attribuffer, isEffectBattleScore);
	}

	@Override
	public void reCalct(AbstractRole role) {
		if(null == role){
			return ;
		}
		calctManager.getCalct(role).reCalct(role);
	}

	@Override
	public void reCalctAfterChangeLevel(RoleInstance role, OperatorType operType, int value) {
		this.reCalctForLevelup(role);
	}

	@Override
	public void changeRoleDkp(AbstractRole player, AttributeType attType,
			OperatorType operatorType, int value,
			OutputConsumeType outputConsumeType) {
		if (player == null) {
			return;
		}
		try{
			synchronized (player) {
				//如果值为负数，不符合正常逻辑，返回。
				if(value <0){
					Log4jManager.CHANGE_DKP_ERROR.error("[DKP为负数]\troleId="+player.getRoleId()+"\ttype="+attType.getName()+"\tvalue="+value);
					return ;
				}
				RoleInstance role = (RoleInstance)player;
				UnionMember member = GameContext.getUnionApp().getUnionMember(role.getUnionId(),role.getIntRoleId());
				if(member != null){
					if(OperatorType.Add == operatorType){
						member.setDkp(member.getDkp() + value);
					}else{
						member.setDkp(member.getDkp() - value);
					}
					GameContext.getUnionApp().saveOrUpdUnionMember(member);
					
					//DKP变化日志
					GameContext.getStatLogApp().roleMoneyLog((RoleInstance) player, attType, outputConsumeType, value, "");
				}
			}
		}catch(Exception e){
			logger.error("changeRoleDkp Exception:roleId="+player.getRoleId()+"\ttype="+attType.getName()+ "\troledkp=" +getMoney(player,attType)+"\tvalue="+value,e);
		}
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context) {
		boolean isDieNow = false;
		if (role.getCurHP() <= 0) {
			isDieNow = true;
		}
		// 登录重算属性
		this.reCalct(role);
		
		// 最后处理上线所在地图问题(必须放到属性重算的后面)
		GameContext.getUserMapApp().roleOnEntranceWhenLogon(
				role, isDieNow);
		
		// 初始属性通知状态
		role.getBehavior().resetCurrentAttributeStatus();
		return 1;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		return 1;
	}

    //
	
	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		loadAttributeEnoughConfigMap();
	}

	private void loadAttributeEnoughConfigMap() {
		attMap = loadConfigMap(XlsSheetNameType.attribute_enough_config, AttributeEnoughConfig.class, true);
		init(attMap);
	}

	private void init(Map<String, AttributeEnoughConfig> map) {
		for(AttributeEnoughConfig cf:map.values()){
			cf.init();
		}
	}

	@Override
	public void stop() {

	}
	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, clazz, linked);
		if (Util.isEmpty(map)) {
			Log4jManager.CHECK.error("not config the " + clazz.getSimpleName() + " ,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
		return map;
	}


	@Override
	public Result getEnoughResult(RoleInstance role, AttributeType att, int num) {
		Result result = new Result();
		if(att==null){
			logger.error("userAttributeAppImpl.getEnoughResult(...) err: the type【"+att+"】 dose not exist in attMap");
			return result.failure().setInfo(getText(TextId.MONEY_TYPE_ERROR));
		}

		
		//是否足够
		boolean enough = isAttributeEnough(role, att, num);
		if(enough){
			return result.success();
		}
		result.failure().setInfo(getTipformat(TextId.MONEY_NOT_ENOUGH,att.getName()));
		String typeKey = String.valueOf(att.getType());
		AttributeEnoughConfig cf = attMap.get(typeKey);
		//表里无配置,属性不够，必然弹出
		if(cf == null){//err: never happen
			return result;
		}
		//不足，是否有弹板
		C0630_AttributeEnoughPopDialogMessage msg = new C0630_AttributeEnoughPopDialogMessage();
		msg.setForwardId(cf.getForwardId());
		msg.setTips(cf.getTips());
		role.getBehavior().sendMessage(msg);
		result.setIgnore(true);
		return result;
	}

	@Override
	public Result getEnoughResult(RoleInstance role, AttributeType att, int num, boolean popDialog) {
		if(popDialog){
			return getEnoughResult(role, att, num);
		}
		if(isAttributeEnough(role, att, num)){
			return new Result().success();
		}
		return new Result().failure().setInfo(getTipformat(TextId.MONEY_NOT_ENOUGH,att.getName()));
	}
	/**
	 * @param role
	 * @param att
	 * @param num
	 * @return
	 * @date 2014-9-12 下午12:04:36
	 */
	@Override
	public boolean isAttributeEnough(RoleInstance role, AttributeType att, int num) {
		int roleAttrNum = role.get(att);
		return roleAttrNum >= num;
	}

	private String getText(String textId) {
		  return GameContext.getI18n().getText(textId);
	}
	public static String getTipformat(String pattern, Object ... arguments) {
	    String pStr = GameContext.getI18n().getText(pattern);
	    return MessageFormat.format(pStr, arguments);
	}




}
