package com.game.draco.app.copy.line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapCopyLineContainer;

import com.game.draco.GameContext;
import com.game.draco.app.copy.config.CopyMapRoleRule;
import com.game.draco.app.copy.line.config.CopyLineConfig;
import com.game.draco.app.copy.line.config.CopyLineReward;
import com.game.draco.app.copy.line.config.CopyLineRewardStatus;
import com.game.draco.app.copy.line.config.CopyLineStatus;
import com.game.draco.app.copy.line.domain.RoleCopyLineReward;
import com.game.draco.app.copy.line.domain.RoleCopyLineScore;
import com.game.draco.app.copy.line.vo.CopyLineRoleData;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.CopyLineAwardItem;
import com.game.draco.message.item.CopyLinePanelItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C0270_CopyLinePanelRespMessage;

public class CopyLineAppImpl implements CopyLineApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private BaseDAO baseDAO;
	
	/** 副本配置：KEY=副本ID,VALUE=副本配置 */
	private Map<Short,CopyLineConfig> copyLineMap = new HashMap<Short,CopyLineConfig>();
	/** 章节副本配置：KEY=章节ID,VALUE=(KEY=副本序列,VALUE=副本配置) */
	private Map<Byte,TreeMap<Byte,CopyLineConfig>> chapterCopyMap = new HashMap<Byte,TreeMap<Byte,CopyLineConfig>>();
	/** 章节奖励配置：KEY=章节ID,VALUE=奖励配置 */
	private Map<Byte,Map<Short,CopyLineReward>> chapterRewardMap = new HashMap<Byte,Map<Short,CopyLineReward>>();
	
	/** 地图刷怪匹配规则：KEY=地图ID,VALUE=匹配关系对象 */
	private Map<String,List<CopyMapRoleRule>> copyMapRoleRuleMap = new HashMap<String,List<CopyMapRoleRule>>();
	
	/** 角色数据 */
	private Map<String,CopyLineRoleData> roleDataMap = new HashMap<String,CopyLineRoleData>();
	
	/** 章节副本地图容器 */
	private MapCopyLineContainer mapContainer = new MapCopyLineContainer();
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadCopyLineConfig();
	}

	@Override
	public void stop() {
		
	}
	
	private void loadCopyLineConfig(){
		String fileName = XlsSheetNameType.copy_line_map_rule.getXlsName();
		String sheetName = XlsSheetNameType.copy_line_map_rule.getSheetName();
		String info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
		try {
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			//①加载地图刷怪规则配置
			List<CopyMapRoleRule> ruleList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, CopyMapRoleRule.class);
			for(CopyMapRoleRule config : ruleList){
				if(null == config){
					continue;
				}
				String mapId = config.getMapId();
				String ruleId = config.getRuleId();
				boolean ruleIsExist = GameContext.getRefreshRuleApp().ruleIsExist(Integer.valueOf(ruleId));
				if(!ruleIsExist){
					this.checkFail(info + "ruleId=" + ruleId + ",this ruleId is not exist.");
					continue;
				}
				if(!this.copyMapRoleRuleMap.containsKey(mapId)){
					this.copyMapRoleRuleMap.put(mapId, new ArrayList<CopyMapRoleRule>());
				}
				this.copyMapRoleRuleMap.get(mapId).add(config);
			}
			//②加载章节副本配置
			fileName = XlsSheetNameType.copy_line_config.getXlsName();
			sheetName = XlsSheetNameType.copy_line_config.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			List<CopyLineConfig> lineList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, CopyLineConfig.class);
			for(CopyLineConfig config : lineList){
				if(null == config){
					continue;
				}
				config.checkInit(info);
				short copyId = config.getCopyId();
				//判断地图是否配置刷怪规则
				String mapId = config.getMapId();
				if(!this.copyMapRoleRuleMap.containsKey(mapId)){
					this.checkFail(info + "copyId = " + copyId + ", it's not config in map_rule.");
				}
				//章节副本ID不能重复
				if(this.copyLineMap.containsKey(copyId)){
					this.checkFail(info + "copyId = " + copyId + ", this copyId is exist.");
					continue;
				}
				this.copyLineMap.put(copyId, config);
				byte chapterId = config.getChapterId();
				byte copyIndex = config.getCopyIndex();
				TreeMap<Byte,CopyLineConfig> clMap = this.chapterCopyMap.get(chapterId);
				if(null == clMap){
					clMap = new TreeMap<Byte,CopyLineConfig>();
					this.chapterCopyMap.put(chapterId, clMap);
				}
				//每个章节中，副本序列不能重复
				if(clMap.containsKey(copyIndex)){
					this.checkFail(info + "copyId = " + copyId + ", copyIndex = " + copyIndex + ", ");
				}
				clMap.put(copyIndex, config);
			}
			//每个章节的第一个副本和最后一个副本
			for(TreeMap<Byte,CopyLineConfig> tm : this.chapterCopyMap.values()){
				if(null == tm){
					continue;
				}
				tm.firstEntry().getValue().setFirstCopy(true);
				tm.lastEntry().getValue().setLastCopy(true);
			}
			//③加载章节奖励配置
			fileName = XlsSheetNameType.copy_line_reward_list.getXlsName();
			sheetName = XlsSheetNameType.copy_line_reward_list.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			List<CopyLineReward> rewardList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, CopyLineReward.class);
			for(CopyLineReward reward : rewardList){
				if(null == reward){
					continue;
				}
				reward.checkInit(info);
				byte chapterId = reward.getChapterId();
				if(!this.chapterRewardMap.containsKey(chapterId)){
					this.chapterRewardMap.put(chapterId, new HashMap<Short,CopyLineReward>());
				}
				this.chapterRewardMap.get(chapterId).put(reward.getStarNum(), reward);
			}
		} catch (Exception e) {
			this.checkFail(info);
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	@Override
	public CopyLineConfig getCopyLineConfig(short copyId) {
		return this.copyLineMap.get(copyId);
	}

	@Override
	public MapCopyLineContainer getMapContainer() {
		return this.mapContainer;
	}

	@Override
	public void enterCopy(RoleInstance role, short copyId) {
		try {
			Result result = this.canEnter(role, copyId);
			if(!result.isSuccess()){
				//副本不能进入，发飘字提示
				C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
				msg.setMsgContext(result.getInfo());
				role.getBehavior().sendMessage(msg);
				return;
			}
			CopyLineConfig config = this.getCopyLineConfig(copyId);
			//副本可进入时，进入副本
			Point point = new Point(config.getMapId(), config.getMapX(), config.getMapY());
			GameContext.getUserMapApp().changeMap(role, point);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".enterCopy error: ", e);
		}
	}
	
	@Override
	public Result canEnter(RoleInstance role, short copyId) {
		Result result = new Result();
		try {
			CopyLineConfig config = this.getCopyLineConfig(copyId);
			if(null == config){
				return result.setInfo(this.getText(TextId.Copyline_Param_Error));
			}
			if(role.getLevel() < config.getMinLevel()){
				return result.setInfo(this.getText(TextId.Copyline_Enter_Level_Lower));
			}
			CopyLineStatus status = this.getCopyLineStatus(role, config.getChapterId(), config.getCopyIndex());
			if(CopyLineStatus.Not_Open == status){
				return result.setInfo(this.getText(TextId.Copyline_Enter_Not_Open));
			}
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".canEnter error: ", e);
			return result.setInfo(this.getText(TextId.Copyline_Param_Error));
		}
	}

	@Override
	public C0270_CopyLinePanelRespMessage getCopyLinePanelRespMessage(RoleInstance role, byte chapterId) {
		if(chapterId <= 0){
			chapterId = this.getCurrChapterId(role);
		}
		C0270_CopyLinePanelRespMessage message = new C0270_CopyLinePanelRespMessage();
		message.setChapterId(chapterId);
		TreeMap<Byte,CopyLineConfig> copyMap = this.chapterCopyMap.get(chapterId);
		if(Util.isEmpty(copyMap)){
			return message;
		}
		int starSum = 0;
		//副本列表
		List<CopyLinePanelItem> copyItems = new ArrayList<CopyLinePanelItem>();
		for(CopyLineConfig config : copyMap.values()){
			if(null == config){
				continue;
			}
			byte copyIndex = config.getCopyIndex();
			CopyLinePanelItem item = new CopyLinePanelItem();
			item.setCopyId(config.getCopyId());
			item.setCopyName(config.getCopyName());
			byte historyStar = this.getHistoryStar(role, chapterId, copyIndex);
			starSum += historyStar;
			item.setHistoryStar(historyStar);
			//有历史星级说明已通关
			CopyLineStatus status;
			if(historyStar > 0){
				status = CopyLineStatus.Passed;
			}else{
				status = this.getCopyLineStatus(role, chapterId, copyIndex);
			}
			item.setStatus(status.getType());
			item.setRoleLevel((byte) config.getMinLevel());
			item.setPower(config.getPower());
			copyItems.add(item);
		}
		message.setCopyItems(copyItems);
		//奖励列表
		Map<Short,CopyLineReward> rewardMap = this.getRewardMap(chapterId);//this.chapterRewardMap.get(chapterId);
		if(!Util.isEmpty(rewardMap)){
			List<CopyLineAwardItem> awardItems = new ArrayList<CopyLineAwardItem>();
			for(CopyLineReward reward : rewardMap.values()){
				if(null == reward){
					continue;
				}
				short startCount = reward.getStarNum();
				CopyLineAwardItem item = new CopyLineAwardItem();
				item.setStartCount(startCount);
				CopyLineRewardStatus rewardStatus = this.getCopyLineRewardStatus(role, chapterId, starSum, startCount);
				item.setStatus(rewardStatus.getType());
				item.setAttrList(reward.getAttrTypeValueList());
				GoodsLiteItem liteItem = reward.getGoodsLiteItem();
				if(null != liteItem){
					item.setGoodsLiteItem(liteItem);
				}
				awardItems.add(item);
			}
			message.setAwardItems(awardItems);
		}
		return message;
	}
	
	private byte getCurrChapterId(RoleInstance role){
		CopyLineRoleData roleData = this.getCopyLineRoleData(role.getRoleId());
		if(null == roleData){
			return 0;
		}
		byte chapterId = roleData.getScoreMaxChapterId();
		if(chapterId <= 0){
			return minChapterId;
		}
		return chapterId;
	}
	
	private CopyLineStatus getCopyLineStatus(RoleInstance role, byte chapterId, byte copyIndex){
		CopyLineRoleData roleData = this.getCopyLineRoleData(role.getRoleId());
		byte maxChapterId = roleData.getScoreMaxChapterId();
		//没有通关记录，判断第一章的第一个副本是否能打
		if(0 == maxChapterId){
			//如果是第一关的第一个副本
			if(minChapterId == chapterId && minCopyIndex == copyIndex){
				CopyLineConfig config = this.getCopyLineConfig(minChapterId, minCopyIndex);
				if(null != config && config.isSuitCondition(role)){
					return CopyLineStatus.Can_Enter;
				}
			}
			return CopyLineStatus.Not_Open;
		}
		//小于maxChapterId，则说明已经通关
		if(chapterId < maxChapterId){
			return CopyLineStatus.Passed;
		}
		//小于等于maxCopyIndex，则说明已通关
		byte maxCopyIndex = roleData.getScoreMaxCopyIndex(maxChapterId);
		if(copyIndex <= maxCopyIndex){
			return CopyLineStatus.Passed;
		}
		byte nextChapterId = (byte) (maxChapterId + 1);
		//等于maxChapterId+1时，如果maxCopyIndex是maxChapterId章节的最后一个副本，则maxChapterId+1章的第1个副本可进入
		if(chapterId == nextChapterId && maxCopyIndex == this.getConfigMaxCopyIndex(maxChapterId)){
			TreeMap<Byte,CopyLineConfig> map = this.getCopyLineConfigMap(nextChapterId);
			if(!Util.isEmpty(map)){
				byte firstIndex = map.firstKey();
				if(copyIndex == firstIndex){
					CopyLineConfig firstConfig = map.get(firstIndex);
					if(firstConfig.isSuitCondition(role)){
						return CopyLineStatus.Can_Enter;
					}
				}
			}
		}
		return CopyLineStatus.Not_Open;
	}
	
	private byte getHistoryStar(RoleInstance role, byte chapterId, byte copyIndex){
		CopyLineRoleData roleData = this.getCopyLineRoleData(role.getRoleId());
		return roleData.getHistoryStar(chapterId, copyIndex);
	}
	
	private CopyLineRewardStatus getCopyLineRewardStatus(RoleInstance role, byte chapterId, int starSum, short startCount){
		//星级总和没有达到，不能领取
		if(starSum < startCount){
			return CopyLineRewardStatus.Can_Not_Take;
		}
		CopyLineRoleData roleData = this.getCopyLineRoleData(role.getRoleId());
		int takeStarNum = roleData.getChapterTakeStarNum(chapterId);
		//超过已领取的最大星级，可以领取
		if(takeStarNum > 0 && starSum > takeStarNum){
			return CopyLineRewardStatus.Can_Take;
		}
		return CopyLineRewardStatus.Received;
	}

	@Override
	public Result takeAward(RoleInstance role, byte chapterId) {
		Result result = new Result();
		try {
			CopyLineRoleData roleData = this.getCopyLineRoleData(role.getRoleId());
			int currStarSum = roleData.getChapterStarSum(chapterId);
			if(currStarSum <= 0){
				return result.setInfo(this.getText(TextId.Copyline_Param_Error));
			}
			int takeStarNum = roleData.getChapterTakeStarNum(chapterId);
			if(takeStarNum > 0 && currStarSum < takeStarNum){
				return result.setInfo(this.getText(TextId.Copyline_Can_Not_Take));
			}
			List<CopyLineReward> rewardList = this.getCopyLineRewardList(chapterId, takeStarNum, currStarSum);
			if(Util.isEmpty(rewardList)){
				return result.setInfo(this.getText(TextId.Copyline_Can_Not_Take));
			}
			int maxStar = currStarSum;
			int goldMoney = 0;
			int silverMoney = 0;
			List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
			for(CopyLineReward reward : rewardList){
				if(null == reward){
					continue;
				}
				short starNum = reward.getStarNum();
				if(starNum > maxStar){
					maxStar = starNum;
				}
				goldMoney += reward.getGold();
				silverMoney += reward.getSilver();
				addList.add(new GoodsOperateBean(reward.getGoodsId(), reward.getGoodsNum(), BindingType.template));
			}
			//奖励物品（背包满了发邮件）
			GoodsHelper.addGoodsForBagOrMail(role, addList, OutputConsumeType.copyline_take, MailSendRoleType.System);
			//奖励金钱
			if(goldMoney > 0){
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, 
						OperatorType.Add, goldMoney, OutputConsumeType.copyline_take);
			}
			if(silverMoney > 0){
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, 
						OperatorType.Add, silverMoney, OutputConsumeType.copyline_take);
			}
			//同步属性（通知金钱增加）
			if(goldMoney > 0 || silverMoney > 0){
				role.getBehavior().notifyAttribute();
			}
			//修改领奖记录
			roleData.updateTakeStarNum(role, chapterId, maxStar);
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".takeAward error: ", e);
			return result.setInfo(this.getText(TextId.Copyline_Take_Fail));
		}
	}
	
	/**
	 * 获取可领取奖励列表
	 * @param chapterId 章节ID
	 * @param takeStarNum 已领取最大星级
	 * @param currStarSum 当前星级总和
	 * @return
	 */
	private List<CopyLineReward> getCopyLineRewardList(byte chapterId, int takeStarNum, int currStarSum){
		List<CopyLineReward> list = new ArrayList<CopyLineReward>();
		Map<Short,CopyLineReward> rewardMap = this.getRewardMap(chapterId);
		if(Util.isEmpty(rewardMap)){
			return list;
		}
		for(CopyLineReward reward : rewardMap.values()){
			if(null == reward){
				continue;
			}
			int starNum = reward.getStarNum();
			//已经领取了
			if(takeStarNum >= starNum){
				continue;
			}
			//没有达到领取条件
			if(currStarSum < starNum){
				continue;
			}
			list.add(reward);
		}
		return list;
	}
	
	private String getText(String i18nKey){
		return GameContext.getI18n().getText(i18nKey);
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try {
			String roleId = role.getRoleId();
			CopyLineRoleData roleData = new CopyLineRoleData();
			roleData.setRoleId(roleId);
			this.roleDataMap.put(roleId, roleData);
			//加载评分数据
			List<RoleCopyLineScore> scoreList = this.baseDAO.selectList(RoleCopyLineScore.class, RoleCopyLineScore.ROLEID, roleId);
			if(!Util.isEmpty(scoreList)){
				TreeMap<Byte,TreeMap<Byte,RoleCopyLineScore>> scoreMap = roleData.getRoleScoreMap();
				for(RoleCopyLineScore score : scoreList){
					if(null == score){
						continue;
					}
					byte chapterId = score.getChapterId();
					if(!scoreMap.containsKey(chapterId)){
						scoreMap.put(chapterId, new TreeMap<Byte,RoleCopyLineScore>());
					}
					scoreMap.get(chapterId).put(score.getCopyIndex(), score);
				}
			}
			//加载领奖数据
			List<RoleCopyLineReward> rewardList = this.baseDAO.selectList(RoleCopyLineReward.class, RoleCopyLineReward.ROLEID, roleId);
			if(!Util.isEmpty(rewardList)){
				for(RoleCopyLineReward reward : rewardList){
					if(null == reward){
						continue;
					}
					roleData.getRoleRewardMap().put(reward.getChapterId(), reward);
				}
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".login error: ", e);
			return 0;
		}
		return 1;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			this.roleDataMap.remove(role.getRoleId());
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".logout error: ", e);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private CopyLineRoleData getCopyLineRoleData(String roleId){
		return this.roleDataMap.get(roleId);
	}
	
	private TreeMap<Byte,CopyLineConfig> getCopyLineConfigMap(byte chapterId){
		return this.chapterCopyMap.get(chapterId);
	}
	
	private byte getConfigMaxCopyIndex(byte chapterId){
		TreeMap<Byte,CopyLineConfig> map = this.getCopyLineConfigMap(chapterId);
		if(Util.isEmpty(map)){
			return 0;
		}
		return map.lastKey();
	}
	
	private CopyLineConfig getCopyLineConfig(byte chapterId, byte copyIndex){
		TreeMap<Byte,CopyLineConfig> map = this.getCopyLineConfigMap(chapterId);
		if(Util.isEmpty(map)){
			return null;
		}
		return map.get(copyIndex);
	}
	
	private Map<Short,CopyLineReward> getRewardMap(byte chapterId){
		return this.chapterRewardMap.get(chapterId);
	}

	@Override
	public CopyMapRoleRule getCopyMapRoleRule(RoleInstance role, String mapId) {
		if(null == role || Util.isEmpty(mapId)){
			return null;
		}
		List<CopyMapRoleRule> list = this.copyMapRoleRuleMap.get(mapId);
		if(!Util.isEmpty(list)){
			for(CopyMapRoleRule item : list){
				if(null == item){
					continue;
				}
				if(item.isSuitLevel(role)){
					return item;
				}
			}
		}
		return null;
	}

	@Override
	public void disposeCopyPass(RoleInstance role, short copyId, byte starScore) {
		try {
			CopyLineConfig config = this.getCopyLineConfig(copyId);
			CopyLineRoleData roleData = this.getCopyLineRoleData(role.getRoleId());
			roleData.updateCopyScore(role, config.getChapterId(), config.getCopyIndex(), starScore);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".disposeCopyPass error: ", e);
		}
	}

}
