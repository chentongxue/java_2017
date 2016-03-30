package com.game.draco.app.target;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.target.cond.TargetCondType;
import com.game.draco.app.target.cond.TargetRolePetMosaic;
import com.game.draco.app.target.cond.TargetPetNum;
import com.game.draco.app.target.cond.TargetHeroBattleScore;
import com.game.draco.app.target.cond.TargetHeroEquipMosaic;
import com.game.draco.app.target.cond.TargetHeroEquipQuality;
import com.game.draco.app.target.cond.TargetHeroEquipStrength;
import com.game.draco.app.target.cond.TargetHeroLevel;
import com.game.draco.app.target.cond.TargetHeroNum;
import com.game.draco.app.target.cond.TargetHeroQualityStar;
import com.game.draco.app.target.cond.TargetHeroSkillLevel;
import com.game.draco.app.target.cond.TargetHorseNum;
import com.game.draco.app.target.cond.TargetLogic;
import com.game.draco.app.target.cond.TargetQuestFinish;
import com.game.draco.app.target.cond.TargetRoleLevel;
import com.game.draco.app.target.config.TargetCond;
import com.game.draco.app.target.config.TargetConfig;
import com.game.draco.app.target.domain.RoleTarget;
import com.game.draco.app.target.vo.TargetRewardResult;
import com.game.draco.message.item.TargetDetailItem;
import com.game.draco.message.push.C1120_TargetHintNotifyMessage;
import com.game.draco.message.response.C1121_TargetPanelRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TargetAppImpl implements TargetApp {
	
	private static final byte NOHINT = 0;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Short, TargetConfig> targetConfigMap = Maps.newHashMap();
	private Map<Short, TargetCond> targetCondMap = null;
	private Map<TargetCondType, TargetLogic> targetLogicMap = Maps.newHashMap();
	/**
	 * 每一线的开始目标id
	 */
	private Map<Byte, Short> lineStartIdMap = Maps.newHashMap();
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadTargetCond();
		this.loadTargetConfig();
		this.initTargetLogic();
	}
	
	@Override
	public void stop() {

	}
	
	/**
	 * 初始化目标条件逻辑
	 */
	private void initTargetLogic() {
		this.register(new TargetRoleLevel());
		this.register(new TargetQuestFinish());
		this.register(new TargetHeroNum());
		this.register(new TargetHeroLevel());
		this.register(new TargetHeroQualityStar());
		this.register(new TargetHeroEquipStrength());
		this.register(new TargetHeroEquipQuality());
		this.register(new TargetHeroEquipMosaic());
		this.register(new TargetHorseNum());
		this.register(new TargetPetNum());
		this.register(new TargetHeroSkillLevel());
		this.register(new TargetHeroBattleScore());
		this.register(new TargetRolePetMosaic());
	}
	
	private void register(TargetLogic logic) {
		this.targetLogicMap.put(logic.getTargetCondType(), logic);
	}
	
	private void loadTargetCond() {
		String fileName = XlsSheetNameType.target_condition.getXlsName();
		String sheetName = XlsSheetNameType.target_condition.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			targetCondMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TargetCond.class);
			if(Util.isEmpty(targetCondMap)) {
				Log4jManager.CHECK.error("targetApp not config the targetCond,file=" 
						+ sourceFile + ", sheet=" + sheetName);
				Log4jManager.checkFail();
				return ;
			}
			String info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName;
			for(Entry<Short, TargetCond> entry : targetCondMap.entrySet()) {
				TargetCond cond = entry.getValue();
				if(null == cond) {
					continue;
				}
				cond.init(info);
			}
		}catch (Exception ex){
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载目标配置
	 */
	private void loadTargetConfig() {
		String fileName = XlsSheetNameType.target_config.getXlsName();
		String sheetName = XlsSheetNameType.target_config.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<TargetConfig> xlsConfigList = XlsPojoUtil.sheetToList(sourceFile, sheetName, TargetConfig.class);
			if(Util.isEmpty(xlsConfigList)) {
				Log4jManager.CHECK.error("targetApp not config the targetConfig,file=" 
						+ sourceFile + ", sheet=" + sheetName);
				Log4jManager.checkFail();
				return ;
			}
			
			Collections.sort(xlsConfigList, new Comparator<TargetConfig>() {

				@Override
				public int compare(TargetConfig o1, TargetConfig o2) {
					if(o1.getLine() < o2.getLine()) {
						return -1;
					}
					if(o1.getLine() > o2.getLine()) {
						return 1;
					}
					if(o1.getTargetId() > o2.getTargetId()) {
						return -1;
					}
					if(o1.getTargetId() < o2.getTargetId()) {
						return 1;
					}
					return 0;
				}
			});
			
			String info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName;
			Map<Byte, List<Short>> lineIdMap = Maps.newHashMap();
			Map<Byte, List<Short>> lineNextIdMap = Maps.newHashMap();
			for(TargetConfig config : xlsConfigList) {
				config.init(info);
				byte line = config.getLine();
				short targetId = config.getTargetId();
				
				List<Short> lineIdList = lineIdMap.get(line);
				if(null == lineIdList) {
					lineIdList = Lists.newArrayList();
					lineIdMap.put(line, lineIdList);
				}
				lineIdList.add(targetId);
				
				
				//条件类型
				TargetCond targetCond = this.targetCondMap.get(config.getConditionId());
				if(null == targetCond) {
					Log4jManager.CHECK.error("targetId= " + targetId + ", config conditionId no exist, file=" 
							+ sourceFile + ", sheet=" + sheetName);
					Log4jManager.checkFail();
					continue;
				}
				config.setTargetCond(targetCond);
				if(this.targetConfigMap.containsKey(targetId)) {
					Log4jManager.CHECK.error("targetId= " + targetId + ", has the same key, file=" 
							+ sourceFile + ", sheet=" + sheetName);
					Log4jManager.checkFail();
					continue;
				}
				this.targetConfigMap.put(targetId, config);
			}
			//检查目标
			for(TargetConfig config : xlsConfigList){
				byte line = config.getLine();
				short targetId = config.getTargetId();
				//后续目标
				short nextTargetId = config.getNextTargetId();
				if(nextTargetId <= 0) {
					continue ;
				}
				TargetConfig nextTarget = targetConfigMap.get(nextTargetId);
				if(null == nextTarget) {
					Log4jManager.CHECK.error("targetId= " +targetId + ", config nextTargetId no exist, file=" 
							+ sourceFile + ", sheet=" + sheetName);
					Log4jManager.checkFail();
					continue;
				}
				config.setNextTarget(nextTarget);
				List<Short> lineNextIdList = lineNextIdMap.get(line);
				if(null == lineNextIdList) {
					lineNextIdList = Lists.newArrayList();
					lineNextIdMap.put(line, lineNextIdList);
				}
				lineNextIdList.add(nextTargetId);
			}
			
			//找出每一线的第一个目标id
			for(Entry<Byte, List<Short>> entry : lineIdMap.entrySet()) {
				byte line = entry.getKey();
				List<Short> lineIdList = entry.getValue();
				List<Short> lineNextIdList = lineNextIdMap.get(line);
				lineIdList.removeAll(lineNextIdList);
				if(lineIdList.size() > 1) {
					Log4jManager.CHECK.error("line= " + line + ", targetId's num - nextTargetId's num > 1, file=" 
							+ sourceFile + ", sheet=" + sheetName);
					Log4jManager.checkFail();
					continue;
				}
				this.lineStartIdMap.put(line, lineIdList.get(0));
			}
		}catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try {
			int roleId = role.getIntRoleId();
			RoleTarget roleTarget = GameContext.getBaseDAO().selectEntity(RoleTarget.class, 
					RoleTarget.ROLE_ID, roleId);
			if(null == roleTarget) {
				//初始化
				roleTarget = new RoleTarget();
				roleTarget.setRoleId(roleId);
				roleTarget.setExistRecord(false);
				for(Short targetId : this.lineStartIdMap.values()) {
					TargetConfig config = this.targetConfigMap.get(targetId);
					if(null == config) {
						continue;
					}
					roleTarget.updateLine(config.getLine(), targetId, RoleTarget.STATUS_ACHIEVE_NO);
				}
				roleTarget.updateLine4(roleTarget.getLine1Id());
			} else {
				roleTarget.setExistRecord(true);
				dealRoleFinishTarget(roleTarget);
			}
			GameContext.getUserTargetApp().addRoleTarget(roleTarget);
		}catch (Exception ex) {
			logger.error("targetApp.login() error, roleId= " + role.getRoleId(), ex);
			return 0;
		}
		
		return 1;
	}
	
	/**
	 *	处理角色已经完成某一线全部目标
	 * @param roleTarget
	 */
	private void dealRoleFinishTarget(RoleTarget roleTarget) {
		this.dealRoleFinishTarget(roleTarget, TargetConfig.line1);
		this.dealRoleFinishTarget(roleTarget, TargetConfig.line2);
		this.dealRoleFinishTarget(roleTarget, TargetConfig.line3);
	}
	
	private void dealRoleFinishTarget(RoleTarget roleTarget, byte lineType) {
		short targetId = roleTarget.getLineId(lineType) ;
		//异常数据处理
		if (0 == targetId && lineType != TargetConfig.line4) {
			targetId = this.lineStartIdMap.get(lineType);
			roleTarget.updateLine(lineType, targetId,RoleTarget.STATUS_ACHIEVE_NO);
		}
		byte lineStatus = roleTarget.getLineStatus(lineType);
		if(lineStatus != RoleTarget.STATUS_AWARD_YES) {
			return ;
		}
		TargetConfig target = this.getTargetConfig(targetId);
		if(null == target) {
			return ;
		}
		TargetConfig nextTarget = target.getNextTarget();
		if(null == nextTarget) {
			return ;
		}
		roleTarget.updateLine(lineType, nextTarget.getTargetId(), RoleTarget.STATUS_ACHIEVE_NO);
		if(lineType == TargetConfig.line1) {
			roleTarget.updateLine4(nextTarget.getTargetId());
		}
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			int roleId = role.getIntRoleId();
			RoleTarget roleTarget = GameContext.getUserTargetApp().removeRoleTarget(roleId);
			if(null != roleTarget) {
				if(roleTarget.isExistRecord()) {
					GameContext.getBaseDAO().update(roleTarget);
				} else {
					GameContext.getBaseDAO().insert(roleTarget);
				}
			}
		}catch (Exception ex) {
			logger.error("targetApp.logout() error, roleId= " + role.getRoleId(), ex);
			return 0;
		}
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		int intRoleId = Integer.parseInt(roleId) ;
		GameContext.getUserTargetApp().removeRoleTarget(intRoleId) ;
		return 0;
	}

	@Override
	public void pushTargetHintMessage(RoleInstance role) {
		RoleTarget roleTarget = GameContext.getUserTargetApp().getRoleTarget(role.getIntRoleId());
		this.updateRoleTargetOnLogin(role, roleTarget);
		short targetId = roleTarget.getLineId(TargetConfig.line4);
		//如果目标都完成
		if(targetId == TargetConfig.DEFAULT_TARGET) {
			sendHintNotifyMessage(role, TargetConfig.DEFAULT_ICON_ID, (short)0, 
					roleTarget.getHintAwardStatus());
			return ;
		}
		
		TargetConfig config = this.targetConfigMap.get(targetId);
		if(null == config) {
			return ;
		}
		short condId = config.getConditionId() ;
		TargetCond cond = targetCondMap.get(condId);
		short level = 0 ;
		if(null != cond && 
				cond.getType() == TargetCondType.RoleLevel.getType()){
			//只有等级类型才发生等级
			level = (short)cond.getValue() ;
		}
		sendHintNotifyMessage(role, config.getHintIcon(), 
				level, roleTarget.getHintAwardStatus());
		
	}
	
	/**
	 * 登录时更新角色目标数据
	 * @param role
	 * @param roleTarget
	 */
	private void updateRoleTargetOnLogin(RoleInstance role, RoleTarget roleTarget) {
		this.updateLineTarget(role, roleTarget, TargetConfig.line1, null);
		this.updateLineTarget(role, roleTarget, TargetConfig.line2, null);
		this.updateLineTarget(role, roleTarget, TargetConfig.line3, null);
		this.updateLineTarget(role, roleTarget, TargetConfig.line4, null);
	}
	
	private void sendHintNotifyMessage(RoleInstance role, short icon,
			short level, byte award) {
		C1120_TargetHintNotifyMessage msg = new C1120_TargetHintNotifyMessage();
		msg.setIcon(icon);
		msg.setLevel(level);
		msg.setAward(award);
		role.getBehavior().sendMessage(msg);
	}

	@Override
	public Message createTargetPanelMessage(RoleInstance role) {
		RoleTarget roleTarget = GameContext.getUserTargetApp().getRoleTarget(role.getIntRoleId());
		List<TargetDetailItem> itemList = Lists.newArrayList();
		itemList.add(getLineTargetDetailItem(role, roleTarget, TargetConfig.line1));
		itemList.add(getLineTargetDetailItem(role, roleTarget, TargetConfig.line2));
		itemList.add(getLineTargetDetailItem(role, roleTarget, TargetConfig.line3));
		C1121_TargetPanelRespMessage respMsg = new C1121_TargetPanelRespMessage();
		respMsg.setTargetDetailItemList(itemList);
		return respMsg;
	}
	
	/**
	 * 取某线上当前目标的详情
	 * @param role
	 * @param roleTarget
	 * @param line
	 * @return
	 */
	private TargetDetailItem getLineTargetDetailItem(RoleInstance role,
			RoleTarget roleTarget, byte line) {
		short targetId = roleTarget.getLineId(line);
		if(targetId > 0) {
			TargetDetailItem item = createTargetDetailItem(role, roleTarget, targetId);
			if(null != item){
				return item ;
			}
		}
		//该线未配置目标
		return createDefaultTargetDetailItem(line) ;
	}
	
	@Override
	public TargetDetailItem createTargetDetailItem(RoleInstance role, 
			RoleTarget roleTarget, short targetId) {
		TargetConfig config  = this.targetConfigMap.get(targetId);
		if(null == config) {
			return null;
		}
		//如果某线的最后一个目标也完成了并且已经领取了奖励 
		TargetConfig nextTarget = config.getNextTarget();
		byte status = 0;
		byte line = config.getLine();
		if(null != roleTarget) {
			status = roleTarget.getLineStatus(line);
			if(null == nextTarget 
					&& status == RoleTarget.STATUS_AWARD_YES) {
				return this.createDefaultTargetDetailItem(line);
			}
		}
		TargetDetailItem item = new TargetDetailItem();
		item.setLine(line);
		item.setTargetId(targetId);
		item.setIcon(config.getHintIcon());
		item.setName(config.getName());
		TargetCond cond = config.getTargetCond();
		item.setDesc(cond.getDesc());
		item.setButtonText(cond.getButtonText());
		TargetLogic logic = this.targetLogicMap.get(cond.getCondType());
		item.setCurValue((short)logic.getCurValue(role, cond));
		item.setTargetValue((short)cond.getValue());
		item.setState(status);
		item.setGameMoney(config.getGameMoney());
		item.setGoldMoney(config.getGoldMoney());
		item.setPotential(config.getPotential());
		item.setGoodsItemList(config.getGoodsLiteItemList());
		item.setForwardId(cond.getForwardId()) ;
		return item;
	}
	
	@Override
	public TargetDetailItem createDefaultTargetDetailItem(byte line) {
		TargetDetailItem item = new TargetDetailItem();
		item.setLine(line);
		item.setTargetId(TargetConfig.DEFAULT_TARGET);
		return item;
	}

	@Override
	public TargetRewardResult reward(RoleInstance role, short targetId) {
		TargetRewardResult result = new TargetRewardResult();
		TargetConfig config = this.targetConfigMap.get(targetId);
		if(null == config) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return result;
		}
		RoleTarget roleTarget = GameContext.getUserTargetApp().getRoleTarget(role.getIntRoleId());
		if(null == roleTarget) {
			result.setInfo(GameContext.getI18n().getText(TextId.Target_not_achieve));
			return result;
		}
		byte line = config.getLine();
		short lineTargetId = roleTarget.getLineId(line);
		if(targetId != lineTargetId) {
			//出现这种情况一般都是多次点击领取奖励导致
			result.setInfo(GameContext.getI18n().getText(TextId.Target_has_rewarded));
			return result;
		}
		byte lineStatus = roleTarget.getLineStatus(line);
		if(RoleTarget.STATUS_AWARD_YES == lineStatus) {
			result.setInfo(GameContext.getI18n().getText(TextId.Target_has_rewarded));
			return result;
		}
		if(RoleTarget.STATUS_ACHIEVE_YES != lineStatus) {
			result.setInfo(GameContext.getI18n().getText(TextId.Target_not_achieve));
			return result;
		}
		//达到领取条件
		GoodsResult gr = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, 
				config.getGoodsList(), OutputConsumeType.target_award_output);
		if(!gr.isSuccess()) {
			result.setInfo(gr.getInfo());
			return result;
		}
		
		int gameMoney = config.getGameMoney();
		int goldMoney = config.getGoldMoney();
		int potential = config.getPotential();
		boolean needNotify = false;
		if(gameMoney > 0){
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Add, 
					gameMoney, OutputConsumeType.target_award_output);
			needNotify = true;
		}
		if(goldMoney > 0){
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Add, 
					goldMoney, OutputConsumeType.target_award_output);
			needNotify = true;
		}
		if(potential > 0){
			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.potential, OperatorType.Add, 
					potential, OutputConsumeType.target_award_output);
			needNotify = true;
		}
		if(needNotify){
			role.getBehavior().notifyAttribute();
		}

		roleTarget.updateLine(line, targetId, RoleTarget.STATUS_AWARD_YES);
//		// 判断是否取消红点提示
//		this.hintChange(role, HintType.target);
		result.setRoleTarget(roleTarget);
		result.setTargetConfig(config);
		result.setLine(line);
		result.success();
		return result;
	}

	@Override
	public TargetLogic getTargetLogic(TargetCondType condType) {
		return this.targetLogicMap.get(condType);
	}

	@Override
	public void updateTarget(RoleInstance role, TargetCondType condType, String type, int value) {
		try {
			//记录玩家数据
			//this.updateTargetData(role, condType, type, value);
			RoleTarget roleTarget = GameContext.getUserTargetApp().getRoleTarget(role.getIntRoleId());
			this.updateLineTarget(role, roleTarget, TargetConfig.line1, condType);
			this.updateLineTarget(role, roleTarget, TargetConfig.line2, condType);
			this.updateLineTarget(role, roleTarget, TargetConfig.line3, condType);
			this.updateLineTarget(role, roleTarget, TargetConfig.line4, condType);
		} catch (Exception ex) {
			logger.error("targetApp.updateTarget() error = ", ex);
		}
	}
	

	/**
	 * 更新某线的角色目标状态
	 * @param role
	 * @param roleTarget
	 * @param line
	 * @param condType 如果为null则不需要判断contype是否符合
	 */
	private void updateLineTarget(RoleInstance role, RoleTarget roleTarget, 
			byte line, TargetCondType condType) {
		byte status = roleTarget.getLineStatus(line);
		if(status != RoleTarget.STATUS_ACHIEVE_NO) {
			return ;
		}
		short targetId = roleTarget.getLineId(line);
		if(targetId <= 0) {
			return ;
		}
		TargetConfig config = this.targetConfigMap.get(targetId);
		if(null == config) {
			return ;
		}
		TargetCond cond = config.getTargetCond();
		if(null == cond) {
			return ;
		}
		boolean isJudgeCondType = true;
		if(condType == null) {
			isJudgeCondType = false;
			condType = cond.getCondType();
		}
		if(isJudgeCondType && cond.getCondType() != condType) {
			return ;
		}
		TargetLogic logic = this.getTargetLogic(condType);
		if(null == logic) {
			return ;
		}
		if(!logic.isMeetCond(role, cond)) {
			return ;
		}
		//完成目标更新目标状态
		roleTarget.updateLine(line, targetId, RoleTarget.STATUS_ACHIEVE_YES);
		// 红点提示领取奖励
		if (this.isHavaHint(role)) {
			GameContext.getHintApp().hintChange(role, HintType.target, true);
		}
		if(line == TargetConfig.line4) {
			this.pushTargetHintMessage(role);
		}
	}

	@Override
	public void updateTarget(RoleInstance role, TargetCondType condType) {
		this.updateTarget(role, condType, null, 0);
	}

	@Override
	public TargetConfig getTargetConfig(short targetId) {
		return this.targetConfigMap.get(targetId);
	}

	@Override
	public Set<HintType> getHintTypeSet(RoleInstance role) {
		if (!isHavaHint(role)) {
			return null;
		}
		Set<HintType> set = new HashSet<HintType>();
		set.add(HintType.target);
		return set;
	}
	
	@Override
	public boolean isHavaHint(RoleInstance role) {
		RoleTarget roleTarget = GameContext.getUserTargetApp().getRoleTarget(Integer.parseInt(role.getRoleId()));
		if (null == roleTarget) {
			return false;
		}
		byte type = roleTarget.getHintAwardStatus();
		if (NOHINT == type) {
			return false;
		}
		return true;
	}

	@Override
	public void onRoleLevelUp(RoleInstance role) {
		if (this.isHavaHint(role)) {
			GameContext.getHintApp().hintChange(role, HintType.target, true);
		}
	}
	
}
