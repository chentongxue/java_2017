package com.game.draco.app.copy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.fall.LootList;
import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.GoodsUseType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.SettlementType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.MapConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapMultiCopyContainer;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleBorn;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.copy.config.AttrConfig;
import com.game.draco.app.copy.config.CopyBaseConfig;
import com.game.draco.app.copy.config.CopyBuyConfig;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.app.copy.config.CopyFirstFallConfig;
import com.game.draco.app.copy.config.CopyMapConfig;
import com.game.draco.app.copy.config.CopyMapRoleRule;
import com.game.draco.app.copy.config.FallConfig;
import com.game.draco.app.copy.domain.RoleCopyCount;
import com.game.draco.app.copy.team.vo.ApplyInfo;
import com.game.draco.app.copy.vo.CopyBuyNumResult;
import com.game.draco.app.copy.vo.CopyNpcRuleType;
import com.game.draco.app.copy.vo.CopyPanelStatus;
import com.game.draco.app.copy.vo.CopyRaidsResult;
import com.game.draco.app.copy.vo.CopyType;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.app.team.Team;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.CopyPanelItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.push.C0226_CopySettlementRespMessage;
import com.game.draco.message.request.C0221_CopyTeamCancelReqMessage;
import com.game.draco.message.request.C0258_CopyBuyNumReqMessage;
import com.game.draco.message.response.C0256_CopyPanelRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CopyLogicAppImpl implements CopyLogicApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private CopyBaseConfig copyBaseConfig = new CopyBaseConfig();// 副本基本配置
	private Map<Short, CopyConfig> copyConfigMap = Maps.newHashMap();// 副本配置
	private Map<CopyType, List<CopyConfig>> copyConfigTypeMap = Maps.newHashMap();// 按副本类型区分
	private Map<String, CopyMapConfig> mapConfigMap = Maps.newHashMap();// 副本地图配置
	private Map<Short, List<CopyMapConfig>> copyMapRelation = Maps.newHashMap();// 副本的有序地图列表
	private Map<Short, List<Integer>> fallMap = Maps.newHashMap();// 副本掉落
	private Map<Short, List<AttrConfig>> attrMap = Maps.newHashMap();// 副本掉落
	private Map<String, List<CopyMapRoleRule>> copyMapRoleRuleMap = Maps.newHashMap();// 地图刷怪匹配规则
	private Map<String, CopyBuyConfig> copyBuyMap = Maps.newHashMap();// 普通副本购买
	private Map<Short, CopyFirstFallConfig> firstFallMap = Maps.newHashMap();// 首次通关额外奖励
	private static final short EXEC_CMDID = new C0258_CopyBuyNumReqMessage().getCommandId();// 副本购买确认消息
	private static final short CANCEL_CMDID = new C0221_CopyTeamCancelReqMessage().getCommandId();// 取消组队副本匹配队列协议
	// ------------------------------------------------------------------------------------------------
	private Map<String, Map<Short, RoleCopyCount>> roleCopyCountMap = Maps.newConcurrentMap();// 副本记数信息

	/**
	 * 获取副本地图配置
	 * @param mapId
	 * @return
	 */
	@Override
	public CopyMapConfig getMapConfig(String mapId) {
		return this.mapConfigMap.get(mapId);
	}

	/**
	 * 会长是否开启公会副本
	 * @param union
	 * @return
	 */
	@Override
	public boolean hadCreateUnionInstance(Union union) {
		if (union == null) {
			return false;
		}
		return false;
	}

	/**
	 * 获取进入失败传送点
	 * @return
	 */
	@Override
	public Point getFailurePoint() {
		return this.copyBaseConfig.getFailurePoint();
	}

	/**
	 * 进入副本（普通，英雄，组队）
	 * @param role
	 * @param copyId
	 */
	@Override
	public Result enterCopy(RoleInstance role, short copyId) {
		Result result = new Result();
		// 是否已在自由组队匹配队列中
		ApplyInfo applyInfo = GameContext.getCopyTeamApp().getApplyInfo(role);
		if (null != applyInfo) {
			this.sendCancelConfirmMessage(role, copyId);
			result.setIgnore(true);
			return result;
		}
		// 判断副本类型
		CopyConfig copyConfig = this.getCopyConfig(copyId);
		if (copyConfig == null) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 必须删除副本掉线的标记信息
		role.setCopyLostReLoginInfo(null);
		// 进入副本
		CopyType copyType = copyConfig.getCopyType();
		if (CopyType.personal == copyType || CopyType.hero == copyType) {
			result = this.enterPersonalCopy(role, copyConfig);
		} else {
			result = this.enterTeamCopy(role, copyId);
		}
		return result;
	}
	
	/**
	 * 发送取消队列二次确认消息
	 * @param role
	 * @param copyId
	 */
	private void sendCancelConfirmMessage(RoleInstance role, short copyId) {
		try {
			C0007_ConfirmationNotifyMessage message = new C0007_ConfirmationNotifyMessage();
			message.setAffirmCmdId(CANCEL_CMDID);
			message.setAffirmParam(String.valueOf(copyId));
			message.setInfo(GameContext.getI18n().getText(TextId.Copy_Enter_Cancel_Apply));
			role.getBehavior().sendMessage(message);
		} catch (Exception e) {
			logger.error("CopyLogicAppImpl.sendCancelConfirmMessage error!", e);
		}
	}

	/**
	 * 进入个人副本（普通，英雄）
	 * @param role
	 * @param copyConfig
	 */
	private Result enterPersonalCopy(RoleInstance role, CopyConfig copyConfig) {
		// 判断是否满足进入条件
		Result result = copyConfig.enterCondition(role);
		if (!result.isSuccess()) {
			return result;
		}
		// 判断自己的次数
		result.failure();
		if (!this.isEnterCountEnough(role, copyConfig)) {
			result.setInfo(GameContext.getI18n().getText(TextId.Copy_Count_Finished));
			return result;
		}
		// 创建副本容器
		MapMultiCopyContainer container = new MapMultiCopyContainer();
		container.initByCreate(role, copyConfig);
		// 进入副本地图
		this.enterCopyMap(role, container);
		return result.success();
	}

	/**
	 * 副本参与次数是否足够
	 * @param role
	 * @param copyConfig
	 * @return
	 */
	@Override
	public boolean isEnterCountEnough(RoleInstance role, CopyConfig copyConfig) {
		// 判断副本大类次数是否足够
		int typeMax = this.getCopyTypeMaxCount(role, copyConfig.getType());
		if (-1 != typeMax) {
			int typeCur = this.getCopyTypeCurCount(role, copyConfig.getType());
			if (typeCur >= typeMax) {
				return false;
			}
		}
		// 判断副本次数是否足够
		int maxCount = this.getCopyMaxCount(role, copyConfig.getCopyId());
		if (maxCount <= 0) {
			return true;
		}
		int currCount = this.getCopyCurrCount(role, copyConfig.getCopyId());
		return currCount < maxCount;
	}
	
	/**
	 * 副本购买次数是否足够
	 * @param role
	 * @param copyConfig
	 * @return
	 */
	private boolean isBuyCountEnough(RoleInstance role, RoleCopyCount roleCopyCount, CopyConfig copyConfig) {
		return roleCopyCount.getBuyNum(role) < this.getCopyMaxBuyNum(roleCopyCount.getRoleId(), copyConfig);
	}
	
	/**
	 * 获得某个副本的最大购买次数
	 * @return
	 */
	private int getCopyMaxBuyNum(String roleId, CopyConfig copyConfig) {
		// 副本单独可购买次数 + 副本大类每个副本可购买次数
		int maxBuy = GameContext.getVipApp().getVipPrivilegeTimes(roleId, VipPrivilegeType.COPY_SINGLE_BUY_TIMES.getType(), String.valueOf(copyConfig.getCopyId()));
		if (CopyType.hero == copyConfig.getCopyType()) {
			maxBuy += this.getCopyBaseConfig().getBuyHeroCopyNum();
		}
		return maxBuy;
	}
	
	/**
	 * 进入副本地图
	 * @param role
	 * @param container
	 */
	private void enterCopyMap(RoleInstance role, MapMultiCopyContainer container) {
		try {
			CopyConfig copyConfig = container.getCopyConfig();
			short copyId = container.getCopyId();
			// 判断是否曾进入过该副本
			if (!container.haveEnterCopy(role.getRoleId())) {
				// 扣除相应的道具
				int goodsId = copyConfig.getNeedGoodsId();
				if (goodsId > 0) {
					GoodsUseType goodsUseType = copyConfig.getGoodsUseType();
					int goodsNum = copyConfig.getNeedGoodsNum();
					if (GoodsUseType.Consume == goodsUseType && goodsNum > 0) {
						GameContext.getUserGoodsApp().deleteForBag(role, goodsId, goodsNum, OutputConsumeType.npc_transmit);
					}
				}
				// 扣除副本次数
				if (CopyConfig.ENTER_INCR == copyConfig.getIncrType()) {
					RoleCopyCount roleCopyCount = this.getRoleCopyCount(role.getRoleId(), copyId);
					if (null == roleCopyCount) {
						roleCopyCount = this.createRoleCopyCount(role, copyId);
						this.setRoleCopyCount(roleCopyCount);
					}
					this.incrCopyEnterNum(role, roleCopyCount, copyConfig);
					// 必须调用，让容器知道哪些角色是合法的
					container.deductRoleCopyCount(role);
				}
				// 将角色添加到容器中
				container.addRoleToEnterSet(role.getRoleId());
			}
			// 进入消息
			this.sendEnterMessage(role, copyConfig);
			// 切换地图
			Point targetPoint = new Point(copyConfig.getEnterMapId(), copyConfig.getMapX(), copyConfig.getMapY());
			// 跳转地图
			GameContext.getUserMapApp().changeMap(role, targetPoint);
			// 重置副本内的兑换
			GameContext.getExchangeApp().resetExchangeByCopyId((RoleInstance) role, copyId);
			// 重置副本内的召唤
			GameContext.getSummonApp().resetSummonByCopyId((RoleInstance) role, copyId);
		} catch (Exception e) {
			logger.error("copyLogicApp.enterCopyMap error: ", e);
		}
	}

	/**
	 * 是否通关,能进入下一层
	 * @param instance
	 * @return
	 */
	@Override
	public String isCopyPass(RoleInstance role) {
		CopyMapConfig config = this.getMapConfig(role.getMapId());
		if (config == null) {
			return null;
		}
		if (config.isNeedKillAll() && this.hasEnemy(role)) {
			return config.getNeedKillAllTips();
		}
		if (this.hasBoss(role, config.getNeedKillNpcId())) {
			return config.getNeedKillAllTips();
		}
		return null;
	}

	/**
	 * 是否通关,能进入下一层
	 * @param instance
	 * @return
	 */
	@Override
	public boolean isCopyPass(MapInstance instance) {
		if (instance == null) {
			return true;
		}
		CopyMapConfig config = this.getMapConfig(instance.getMap().getMapId());
		if (config == null) {
			return true;
		}
		// 没有通关配置
		if (!config.hasPassCondition()) {
			return true;
		}
		if (config.isNeedKillAll() && this.hasEnemy(instance.getNpcList())) {
			return false;
		}
		if (this.hasBoss(instance.getNpcList(), config.getNeedKillNpcId())) {
			return false;
		}
		return true;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try {
			// 处理副本中掉线的情况（必须放在进入地图之前）
			this.disposeCopyLostReLogin(role);
			// 登录地图不存在时，把角色放回出生点
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(role.getMapId());
			if (map == null) {
				RoleBorn roleBorn = GameContext.getRoleBornApp().getRoleBorn();
				role.setMapX(roleBorn.getBornX());
				role.setMapY(roleBorn.getBornY());
				role.setMapId(roleBorn.getBornMapId());
			}
			// 获取副本记录
			List<RoleCopyCount> copyCountList = GameContext.getBaseDAO().selectList(RoleCopyCount.class, RoleCopyCount.ROLE_ID, role.getRoleId());
			if (!Util.isEmpty(copyCountList)) {
				for (RoleCopyCount roleCopyCount : copyCountList) {
					if (null == roleCopyCount) {
						continue;
					}
					// 触发过期判断
					roleCopyCount.reset(role);
					this.setRoleCopyCount(roleCopyCount);
				}
			}
		} catch (Exception e) {
			this.logger.error("CopyLogicApp.login error: ", e);
			return 0;
		}
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			Map<Short, RoleCopyCount> roleCopyCountMap = this.getRoleCopyCountMap(role.getRoleId());
			if (!Util.isEmpty(roleCopyCountMap)) {
				for (RoleCopyCount roleCopyCount : roleCopyCountMap.values()) {
					if (null == roleCopyCount) {
						continue;
					}
					roleCopyCount.updateDB();
				}
			}
		} catch (Exception e) {
			logger.error("CopyLogicAppImpl.onLogout error!", e);
		}
		return 1;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		try {
			this.roleCopyCountMap.remove(roleId);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 在副本掉线保护之外放到之前所在地图出生点(地图容错 角色上线所在地图为副本类型，则放到容错点)
	 * @param role
	 */
	private void mapFaultTolerant(RoleInstance role, String mapId) {
		sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(role.getMapId());
		if (map.getMapConfig().getLogictype() == MapLogicType.unionInstanceLogic.getType() || map.getMapConfig().getLogictype() == MapLogicType.copyLogic.getType()) {
			Point point = this.getCopyBeforePoint(role, mapId);
			if (null == point) {
				point = this.getFailurePoint();
			}
			role.setMapId(point.getMapid());
			role.setMapX(point.getX());
			role.setMapY(point.getY());
		}
	}

	/**
	 * 根据副本ID获取副本配置
	 * @param copyId
	 * @return
	 */
	@Override
	public CopyConfig getCopyConfig(short copyId) {
		return this.copyConfigMap.get(copyId);
	}

	/**
	 * 获取副本掉落信息
	 * 
	 * @param copyId
	 * @return
	 */
	@Override
	public List<Integer> getCopyFalls(short copyId) {
		return fallMap.get(copyId);
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		this.loadCopyBaseConfig();
		this.loadCopyConfig();// 基本配置
		this.loadMapConfig();// 副本地图配置必须在副本配置和刷怪规则之后加载
		this.loadMapRoleRule();// 地图与刷怪匹配关系须在二者加载之后
		this.loadCopyBuyConfig();// 购买配置
		this.loadCopyFallConfig();// 展示掉落物品
		this.loadAttr();// 展示掉落属性
		this.clearAllCopyLostReLoginInfo();// 清除副本掉线标识
		this.loadFirstFallConfig();// 加载首次通关额外奖励
	}

	@Override
	public void stop() {
	}

	/**
	 * 清除所有角色的副本掉线标识（服务器启动的时候调用）
	 */
	private void clearAllCopyLostReLoginInfo() {
		GameContext.getRoleDAO().clearAllCopyLostReLoginInfo();
	}

	/** 加载副本进入配置 */
	private void loadCopyConfig() {
		String fileName = XlsSheetNameType.copy_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_config.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<CopyConfig> configList = XlsPojoUtil.sheetToList(sourceFile, sheetName, CopyConfig.class);
			// 按等级需求排序（配置表中可能不是有序的）
			this.sortCopyConfigList(configList);
			for (CopyConfig config : configList) {
				// 验证副本配置信息
				config.checkAndInit("load fileName=" + fileName + " sheetName=" + sheetName + ".");
				this.copyConfigMap.put(config.getCopyId(), config);
				CopyType copyType = config.getCopyType();
				if (!this.copyConfigTypeMap.containsKey(copyType)) {
					this.copyConfigTypeMap.put(copyType, new ArrayList<CopyConfig>());
				}
				this.copyConfigTypeMap.get(copyType).add(config);
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("load fileName=" + fileName + " sheetName=" + sheetName + " is error.");
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载首次通关掉落配置
	 */
	private void loadFirstFallConfig() {
		String fileName = XlsSheetNameType.copy_first_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_first_config.getSheetName();
		String fileInfo = "load fileName=" + fileName + " sheetName=" + sheetName + " is error.";
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.firstFallMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, CopyFirstFallConfig.class);
			for (CopyFirstFallConfig config : this.firstFallMap.values()) {
				if (null == config) {
					continue;
				}
				config.init(fileInfo);
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error(fileInfo);
			Log4jManager.checkFail();
		}
	}

	/**
	 * 将副本配置表排序
	 * @param list
	 */
	private void sortCopyConfigList(List<CopyConfig> list) {
		Collections.sort(list, new Comparator<CopyConfig>() {
			@Override
			public int compare(CopyConfig conf1, CopyConfig conf2) {
				if (conf1.getMinLevel() > conf2.getMinLevel()) {
					return 1;
				}
				if (conf1.getMinLevel() < conf2.getMinLevel()) {
					return -1;
				}
				if (conf1.getCopyId() > conf2.getCopyId()) {
					return 1;
				}
				if (conf2.getCopyId() < conf2.getCopyId()) {
					return -1;
				}
				return 0;
			}
		});
	}

	/**
	 * 加载地图配置
	 */
	private void loadMapConfig() {
		String fileName = XlsSheetNameType.copy_map_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_map_config.getSheetName();
		String tip = "load fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<CopyMapConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CopyMapConfig.class);
			for (CopyMapConfig config : list) {
				if (null == config) {
					continue;
				}
				String mapId = config.getMapId();
				short copyId = config.getCopyId();
				String info = tip + "copyId=" + copyId + ",";
				if (null == this.getCopyConfig(copyId)) {
					Log4jManager.CHECK.error(info + "this copy is not exist");
					Log4jManager.checkFail();
					continue;
				}
				info += "mapId=" + mapId + ",";
				sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
				if (map == null) {
					this.checkFail(info + "the map is not exist");
					continue;
				}
				MapConfig mapConfig = map.getMapConfig();
				boolean flag = map.getMapConfig().changeLogicType(MapLogicType.copyLogic);
				if (!flag) {
					this.checkFail(info + "it's MapLogicType != MapLogicType.copyLogic.");
					continue;
				}
				if (mapConfig.getCopyId() > 0) {
					this.checkFail(info + "this map is in copyId=" + mapConfig.getCopyId());
					continue;
				}
				String ruleId = config.getRuleId();
				CopyNpcRuleType npcRuleType = CopyNpcRuleType.getCopyType(config.getRuleType());
				if (null == npcRuleType) {
					this.checkFail(info + "npcRuleType=" + config.getRuleType() + ", the npcRuleType is not exist!");
				}
				// 刷怪规则是固定配置的必须要配规则ID
				if (CopyNpcRuleType.Default == npcRuleType && Util.isEmpty(ruleId)) {
					this.checkFail(info + "ruleType=" + config.getRuleType() + ",but ruleId is empty.");
					continue;
				}
				// 判断规则是否存在
				if (!Util.isEmpty(ruleId)) {
					boolean ruleIsExist = GameContext.getRefreshRuleApp().ruleIsExist(Integer.parseInt(ruleId));
					if (!ruleIsExist) {
						this.checkFail(info + "ruleId=" + ruleId + ",this rule is not exist.");
						continue;
					}
				}
				// 验证刷新的跳转点是否在卡死复位点附近
				if (Util.inCircle(mapConfig.getMaporiginx(), mapConfig.getMaporiginy(), config.getJumpX(), config.getJumpY(), MapConstant.JUMP_POINT_EFFECT_RADIOS)) {
					this.checkFail(info + "jumpX and jumpY config error,it's nearby maporigin point.");
				}
				mapConfig.setCopyId(copyId);
				mapConfigMap.put(mapId, config);
				if (!this.copyMapRelation.containsKey(copyId)) {
					this.copyMapRelation.put(copyId, new ArrayList<CopyMapConfig>());
				}
				this.copyMapRelation.get(copyId).add(config);
			}
			// 副本地图排序
			for (List<CopyMapConfig> mapList : this.copyMapRelation.values()) {
				if (Util.isEmpty(mapList)) {
					continue;
				}
				this.sortCopyMapRelation(mapList);
				CopyMapConfig firstMap = mapList.get(0);
				firstMap.setFirstMap(true);
				CopyMapConfig lastMap = mapList.get(mapList.size() - 1);
				lastMap.setLastMap(true);
			}
		} catch (Exception e) {
			this.checkFail(tip + "is error.");
		}
	}

	/**
	 * 配置错误
	 * @param info
	 */
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	/**
	 * 地图配置列表排序
	 * @param mapList
	 */
	private void sortCopyMapRelation(List<CopyMapConfig> mapList) {
		Collections.sort(mapList, new Comparator<CopyMapConfig>() {
			@Override
			public int compare(CopyMapConfig map1, CopyMapConfig map2) {
				if (map1.getMapIndex() < map2.getMapIndex()) {
					return -1;
				}
				if (map1.getMapIndex() > map2.getMapIndex()) {
					return 1;
				}
				return 0;
			}
		});
	}

	/**
	 * 加载地图刷怪匹配关系配置
	 */
	private void loadMapRoleRule() {
		String fileName = XlsSheetNameType.copy_rule_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_rule_config.getSheetName();
		String info = "load fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<CopyMapRoleRule> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CopyMapRoleRule.class);
			for (CopyMapRoleRule config : list) {
				if (null == config) {
					continue;
				}
				String mapId = config.getMapId();
				if (!this.mapConfigMap.containsKey(mapId)) {
					Log4jManager.CHECK.error(info + "mapId=" + mapId + ",this mapId is not exist.");
					Log4jManager.checkFail();
					continue;
				}
				String ruleId = config.getRuleId();
				boolean ruleIsExist = GameContext.getRefreshRuleApp().ruleIsExist(Integer.parseInt(ruleId));
				if (!ruleIsExist) {
					Log4jManager.CHECK.error(info + "ruleId=" + ruleId + ",this ruleId is not exist.");
					Log4jManager.checkFail();
					continue;
				}
				if (!this.copyMapRoleRuleMap.containsKey(mapId)) {
					this.copyMapRoleRuleMap.put(mapId, new ArrayList<CopyMapRoleRule>());
				}
				this.copyMapRoleRuleMap.get(mapId).add(config);
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error(info, e);
			Log4jManager.checkFail();
		}
	}

	/**
	 * 加载掉落列表
	 */
	private void loadCopyFallConfig() {
		String fileName = XlsSheetNameType.copy_fall_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_fall_config.getSheetName();

		List<FallConfig> list = null;
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName, FallConfig.class);
		} catch (Exception e) {
			Log4jManager.CHECK.error("load fileName=" + fileName + " sheetName=" + sheetName + " is error.");
			Log4jManager.checkFail();
		}

		if (Util.isEmpty(list)) {
			return;
		}

		for (FallConfig config : list) {
			short copyId = config.getCopyId();
			int fallGoodsId = config.getFallGoodsId();
			if (fallGoodsId <= 0) {
				continue;
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(fallGoodsId);
			if (goodsBase == null) {
				Log4jManager.CHECK.error("load fileName=" + fileName + " sheetName=" + sheetName + " goodsId=" + fallGoodsId + " goods is no exist.");
				Log4jManager.checkFail();
				continue;
			}

			if (fallMap.containsKey(copyId)) {
				List<Integer> goods = fallMap.get(copyId);
				goods.add(fallGoodsId);
				continue;
			}
			List<Integer> goodsList = new ArrayList<Integer>();
			goodsList.add(fallGoodsId);
			fallMap.put(copyId, goodsList);
		}
	}

	/**
	 * 加载掉落列表
	 */
	private void loadAttr() {
		String fileName = XlsSheetNameType.copy_attr_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_attr_config.getSheetName();

		List<AttrConfig> list = null;
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName, AttrConfig.class);
		} catch (Exception e) {
			Log4jManager.CHECK.error("load fileName=" + fileName + " sheetName=" + sheetName + " is error.");
			Log4jManager.checkFail();
		}
		if (Util.isEmpty(list)) {
			return;
		}
		for (AttrConfig config : list) {
			short copyId = config.getCopyId();
			byte attrType = config.getAttrType();
			if (attrType <= 0) {
				continue;
			}
			if (attrMap.containsKey(copyId)) {
				List<AttrConfig> attr = attrMap.get(copyId);
				attr.add(config);
				continue;
			}
			List<AttrConfig> attrList = Lists.newArrayList();
			attrList.add(config);
			attrMap.put(copyId, attrList);
		}
	}

	/**
	 * 加载传出副本时，原进入点非法，传送此点
	 */
	private void loadCopyBaseConfig() {
		String fileName = XlsSheetNameType.copy_base_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_base_config.getSheetName();
		String info = "load fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.copyBaseConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, CopyBaseConfig.class);
			String mapId = this.copyBaseConfig.getMapId();
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
			if (null == map) {
				this.checkFail(info + "the map is not exist");
			}
			MapConfig mapConfig = map.getMapConfig();
			// 容错点所在地图不能是副本
			if (mapConfig.getCopyId() > 0) {
				this.checkFail(info + "the map is copy.");
			}
			// 容错点赋值，该地图的卡死复位点
			Point failurePoint = new Point();
			failurePoint.setMapid(mapId);
			failurePoint.setX(mapConfig.getMaporiginx());
			failurePoint.setY(mapConfig.getMaporiginy());
			this.copyBaseConfig.setFailurePoint(failurePoint);
		} catch (Exception e) {
			this.checkFail(info);
		}
	}

	/**
	 * 加载副本购买配置
	 */
	private void loadCopyBuyConfig() {
		String fileName = XlsSheetNameType.copy_buy_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_buy_config.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			copyBuyMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, CopyBuyConfig.class);
		} catch (Exception e) {
			Log4jManager.CHECK.error("load fileName=" + fileName + " sheetName=" + sheetName + " is error.");
			Log4jManager.checkFail();
		}
	}

	/**
	 * 组队副本进入逻辑
	 * @param role
	 * @param copyId
	 */
	private Result enterTeamCopy(RoleInstance role, short copyId) {
		Result result = new Result();
		Team team = role.getTeam();
		if (team == null) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return result;
		}
		MapMultiCopyContainer container = null;
		String containerId = team.getCopyContainerId(copyId);
		if (!Util.isEmpty(containerId)) {
			container = (MapMultiCopyContainer) GameContext.getMapApp().getCopyContainer(containerId);
		}
		CopyConfig copyConfig = this.getCopyConfig(copyId);
		// 副本是否可进入
		boolean canEnter = false;
		// 副本已经存在，判断可否直接进入
		if (null != container) {
			// 人数限制判断
			if (!this.hasExceedRoleCount(role, container, copyId)) {
				String context = GameContext.getI18n().getText(TextId.Copy_Role_Too_More);
				result.setInfo(context);
				return result;
			}
			// 拿到副本容器，需要将状态切为占有状态。
			// 如果切换失败，说明已经是销毁状态了，需要重新创建副本。
			// 如果切换成功，说明不需要创建副本，可直接进入地图。
			if (container.change_containerState_to_own()) {
				canEnter = true;
			}
		}
		if (!canEnter) {
			// 副本不存在，需要创建副本
			container = this.createTeamCopyContainer(role, copyConfig, false);
		}
		// 副本存在并且可进入，直接进入副本
		this.enterCopyMap(role, container);
		return result.success();
	}

	/**
	 * 人数限制判断
	 * @param role
	 * @param container
	 * @param copyId
	 * @return
	 */
	private boolean hasExceedRoleCount(RoleInstance role, MapMultiCopyContainer container, short copyId) {
		CopyConfig copyConfig = this.getCopyConfig(copyId);
		if (!copyConfig.roleCountIsFull(container.getRoleCount())) {
			return true;
		}
		return false;
	}

	/**
	 * 创建组队副本容器
	 * @param role
	 * @param copyConfig
	 * @param isConfirm
	 */
	private MapMultiCopyContainer createTeamCopyContainer(RoleInstance role, CopyConfig copyConfig, boolean isConfirm) {
		Team team = role.getTeam();
		if (null == team) {
			return null;
		}
		// 创建副本容器
		MapMultiCopyContainer container = new MapMultiCopyContainer();
		container.initByCreate(role, copyConfig);
		return container;
	}

	/**
	 * 确定进入组队副本
	 * @param role
	 * @param param
	 */
	@Override
	public void teamCopyCreateConfirm(RoleInstance role, String param) {
		try {
			short copyId = Short.valueOf(param);
			CopyConfig copyConfig = this.getCopyConfig(copyId);
			if (null == copyConfig) {
				return;
			}
			this.createTeamCopyContainer(role, copyConfig, true);
		} catch (Exception e) {
			this.logger.error("copyLogicApp.teamCopyCreateConfirm error: ", e);
		}
	}

	/**
	 * 组队副本给队伍内成员发送进入消息
	 * @param role
	 * @param copyConfig
	 */
	private void sendEnterMessage(RoleInstance role, CopyConfig copyConfig) {
		CopyType copyType = copyConfig.getCopyType();
		if (copyType != CopyType.team) {
			return;
		}
		String msg = GameContext.getI18n().messageFormat(TextId.Copy_Role_Enter_Copy, role.getRoleName(), copyConfig.getCopyName());
		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Team, msg, null, role.getTeam());
	}

	/**
	 * 是否还有未杀死的敌对NPC
	 * @param npcList
	 * @return
	 */
	private boolean hasEnemy(Collection<NpcInstance> npcList) {
		if (Util.isEmpty(npcList)) {
			return false;
		}
		for (NpcInstance npc : npcList) {
			if (npc.getNpc().getNpctype() == NpcType.monster.getType()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否还有未杀死的BOSS
	 * @param npcList
	 * @param npcId
	 * @return
	 */
	private boolean hasBoss(Collection<NpcInstance> npcList, String npcId) {
		if (Util.isEmpty(npcList)) {
			return false;
		}
		for (NpcInstance npc : npcList) {
			if (npc.getNpcid().equals(npcId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否还有对立npc
	 * @param role
	 * @return
	 */
	private boolean hasEnemy(RoleInstance role) {
		MapInstance instance = role.getMapInstance();
		if (instance == null) {
			return false;
		}
		return this.hasEnemy(instance.getNpcList());
	}

	/**
	 * @param role
	 * @param npcId
	 * @return
	 */
	private boolean hasBoss(RoleInstance role, String npcId) {
		MapInstance instance = role.getMapInstance();
		if (instance == null) {
			return false;
		}
		return this.hasBoss(instance.getNpcList(), npcId);
	}

	/**
	 * 获取副本面板信息
	 * @param role
	 * @param selectCopyId
	 * @return
	 */
	@Override
	public C0256_CopyPanelRespMessage getCopyPanelRespMessage(RoleInstance role, short reqCopyId, byte copyType) {
		C0256_CopyPanelRespMessage resp = new C0256_CopyPanelRespMessage();
		// 处理选择副本类型
		if (0 != reqCopyId) {
			CopyConfig copyConfig = this.getCopyConfig(reqCopyId);
			if (null == copyConfig) {
				reqCopyId = 0;
			} else {
				copyType = copyConfig.getType();
			}
		}
		resp.setReqCopyId(reqCopyId);
		resp.setCopyType(copyType);
		// 获得副本列表并排序
		List<CopyPanelItem> copyPanelList = this.getCopyPanelItemList(role, copyType);
		if (Util.isEmpty(copyPanelList)) {
			return resp;
		}
		resp.setCopyPanelList(copyPanelList);
		resp.setRemCount((byte) this.getCopyTypeRemCount(role, copyType));
		resp.setMaxCount((byte) this.getCopyTypeMaxCount(role, copyType));
		// 快速组队信息
		ApplyInfo applyInfo = GameContext.getCopyTeamApp().getApplyInfo(role);
		if (null != applyInfo) {
			resp.setApplyState((byte) 1);
			resp.setApplyCopyId(applyInfo.getCopyId());
			resp.setWaitTime((int) ((System.currentTimeMillis() - applyInfo.getApplyTime()) / 1000));
		}
		return resp;
	}

	/**
	 * 获得副本列表结构体
	 * @param role
	 * @param selectCopyId
	 * @param applyInfo
	 * @param copyType
	 * @return
	 */
	private List<CopyPanelItem> getCopyPanelItemList(RoleInstance role, byte copyType) {
		List<CopyConfig> copyConfigList = this.getTypeCopyConfigList(copyType);
		if (Util.isEmpty(copyConfigList)) {
			return null;
		}
		// 按需求等级升序排列
		this.sortCopyConfigList(copyConfigList);
		List<CopyPanelItem> copyPanelList = Lists.newArrayList();
		for (CopyConfig copyConfig : copyConfigList) {
			if (null == copyConfig) {
				continue;
			}
			if (!copyConfig.canShow(role)) {
				// 不能在副本列表中显示
				continue;
			}
			CopyPanelItem item = new CopyPanelItem();
			short copyId = copyConfig.getCopyId();
			item.setCopyId(copyId);
			item.setCopyType(copyConfig.getType());
			item.setCopyName(copyConfig.getCopyName());
			item.setMinLevel((byte) copyConfig.getMinLevel());
			item.setMaxLevel((byte) copyConfig.getMaxLevel());
			item.setRemCount((byte) this.getCopyRemainCount(role, copyId));
			item.setMaxCount((byte) this.getCopyMaxCount(role, copyId));
			item.setCountType((byte) copyConfig.getCountType());
			item.setImageId(copyConfig.getImageId());
			this.setCopyPanelStatus(role, copyConfig, item);
			copyPanelList.add(item);
		}
		return copyPanelList;
	}

	/**
	 * 获得某一类型副本列表
	 * @param type
	 * @return
	 */
	private List<CopyConfig> getTypeCopyConfigList(byte type) {
		CopyType copyType = CopyType.get(type);
		if (null == copyType) {
			return null;
		}
		return this.copyConfigTypeMap.get(copyType);
	}

	/**
	 * 设置副本状态
	 * @param role
	 * @param copyConfig
	 * @param item
	 */
	private void setCopyPanelStatus(RoleInstance role, CopyConfig copyConfig, CopyPanelItem item) {
		CopyPanelStatus status = CopyPanelStatus.Not_Open;
		if (item.getMaxCount() > 0 && item.getRemCount() <= 0) {
			status = CopyPanelStatus.Finished;
		} else if (copyConfig.showCondition(role).isSuccess()) {
			status = CopyPanelStatus.Can_Enter;
		}
		item.setStatus(status.getType());
	}

	/**
	 * 获取适合角色的刷怪匹配关系
	 * @param role
	 * @param mapId
	 * @return
	 */
	@Override
	public CopyMapRoleRule getCopyMapRoleRule(RoleInstance role, String mapId) {
		if (null == role || Util.isEmpty(mapId)) {
			return null;
		}
		List<CopyMapRoleRule> list = this.copyMapRoleRuleMap.get(mapId);
		if (!Util.isEmpty(list)) {
			for (CopyMapRoleRule item : list) {
				if (null == item) {
					continue;
				}
				if (item.isSuitLevel(role)) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 获取副本今日进入次数
	 * @param role
	 * @param copyId
	 * @return
	 */
	@Override
	public int getCopyCurrCount(RoleInstance role, short copyId) {
		RoleCopyCount count = this.getRoleCopyCount(role.getRoleId(), copyId);
		if (null == count) {
			return 0;
		}
		return count.getEnterNum(role);
	}
	
	/**
	 * 获取副本的当前剩余次数
	 * @param role
	 * @param copyId
	 * @return
	 */
	private int getCopyRemainCount(RoleInstance role, short copyId) {
		return this.getCopyMaxCount(role, copyId) - this.getCopyCurrCount(role, copyId);
	}

	/**
	 * 获得副本信息列表
	 * @param roleId
	 * @return
	 */
	private Map<Short, RoleCopyCount> getRoleCopyCountMap(String roleId) {
		return this.roleCopyCountMap.get(roleId);
	}

	/**
	 * 获得副本计数信息
	 * @param roleId
	 * @param copyId
	 * @return
	 */
	@Override
	public RoleCopyCount getRoleCopyCount(String roleId, short copyId) {
		Map<Short, RoleCopyCount> copyCountMap = this.getRoleCopyCountMap(roleId);
		if (Util.isEmpty(copyCountMap)) {
			return null;
		}
		return copyCountMap.get(copyId);
	}

	/**
	 * 添加副本计数信息到内存中
	 * @param roleId
	 * @param copyId
	 * @param roleCopyCount
	 */
	private void setRoleCopyCount(RoleCopyCount roleCopyCount) {
		Map<Short, RoleCopyCount> copyCountMap = this.roleCopyCountMap.get(roleCopyCount.getRoleId());
		if (Util.isEmpty(copyCountMap)) {
			copyCountMap = Maps.newHashMap();
			this.roleCopyCountMap.put(roleCopyCount.getRoleId(), copyCountMap);
		}
		copyCountMap.put(roleCopyCount.getCopyId(), roleCopyCount);
	}

	/**
	 * 获取副本最大可进入次数
	 * @param copyId
	 * @return
	 */
	@Override
	public int getCopyMaxCount(RoleInstance role, short copyId) {
		CopyConfig copyConfig = this.copyConfigMap.get(copyId);
		if (null == copyConfig) {
			return -1;
		}
		int maxCount = copyConfig.getCount();
		RoleCopyCount copyCount = this.getRoleCopyCount(role.getRoleId(), copyId);
		if (copyCount != null) {
			maxCount += copyCount.getBuyNum(role);// 如果副本不限次数（不允许购买次数）,返回-1
		}
		maxCount += GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(), VipPrivilegeType.COPY_SINGLE_MORE_TIMES.getType(), String.valueOf(copyId));
		return maxCount;
	}

	/**
	 * 获得英雄或组队副本可参与次数
	 * @param role
	 * @param copyType
	 * @return
	 */
	private int getCopyTypeMaxCount(RoleInstance role, byte type) {
		int maxNum = -1;
		CopyType copyType = CopyType.get(type);
		switch (copyType) {
		// 配置最大次数 + 购买次数 + VIP额外次数
		case hero:
			maxNum = this.getHeroCopyMaxCount(role, type);
			break;
		case team:
			maxNum = this.getTeamCopyMaxCount(role, type);
			break;
		default:
			break;
		}
		return maxNum;
	}
	
	/**
	 * 获取英雄副本的最大次数
	 * @param role
	 * @param type
	 * @return
	 */
	private int getHeroCopyMaxCount(RoleInstance role, byte type) {
		return this.getCopyBaseConfig().getMaxHeroCopyEnter() + role.getRoleCount().getRoleTimesToInt(CountType.HeroCopyBuy)
				+ GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(), VipPrivilegeType.COPY_TYPE_MORE_TIME.getType(), String.valueOf(type));
	}

	/**
	 * 获取组队副本的最大次数
	 * @param role
	 * @param type
	 * @return
	 */
	private int getTeamCopyMaxCount(RoleInstance role, byte type) {
		return this.getCopyBaseConfig().getMaxTeamCopyEnter() + role.getRoleCount().getRoleTimesToInt(CountType.TeamCopyBuy)
				+ GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(), VipPrivilegeType.COPY_TYPE_MORE_TIME.getType(), String.valueOf(type));
	}
	
	/**
	 * 获取当前参与英雄或组队副本次数
	 * @param role
	 * @param type
	 * @return
	 */
	@Override
	public int getCopyTypeCurCount(RoleInstance role, byte type) {
		int currNum = -1;
		CopyType copyType = CopyType.get(type);
		switch (copyType) {
		case hero:
			currNum = role.getRoleCount().getRoleTimesToInt(CountType.HeroCopyEnter);
			break;
		case team:
			currNum = role.getRoleCount().getRoleTimesToInt(CountType.TeamCopyEnter);
			break;
		default:
			break;
		}
		return currNum;
	}
	
	/**
	 * 获取副本类型总剩余可参与次数
	 * @param role
	 * @param type
	 * @return
	 */
	private int getCopyTypeRemCount(RoleInstance role, byte type) {
		int remCount = -1;
		CopyType copyType = CopyType.get(type);
		switch (copyType) {
		case hero:
			remCount = this.getHeroCopyMaxCount(role, type) - role.getRoleCount().getRoleTimesToInt(CountType.HeroCopyEnter);
			break;
		case team:
			remCount = this.getTeamCopyMaxCount(role, type) - role.getRoleCount().getRoleTimesToInt(CountType.TeamCopyEnter);
			break;
		default:
			break;
		}
		return remCount;
	}

	/**
	 * 增加副本通关次数
	 * @param role
	 * @param copyId
	 */
	private void incrCopyEnterNum(RoleInstance role, RoleCopyCount roleCopyCount, CopyConfig copyConfig) {
		roleCopyCount.incrEnterNum(new Date());
		switch (copyConfig.getCopyType()) {
		case hero:
			role.getRoleCount().changeTimes(CountType.HeroCopyEnter);
			break;
		case team:
			role.getRoleCount().changeTimes(CountType.TeamCopyEnter);
			break;
		default:
			break;
		}
		// 添加活跃度
		GameContext.getDailyPlayApp().incrCompleteTimes(role, 1, 
				DailyPlayType.copy_map, String.valueOf(copyConfig.getCopyId()));
		//某类型副本
		GameContext.getDailyPlayApp().incrCompleteTimes(role, 1, 
				DailyPlayType.copy_type_map, String.valueOf(copyConfig.getType()));
	}

	/**
	 * 增加副本购买次数
	 * @param role
	 * @param copyId
	 */
	private void incrCopyBuyNum(RoleInstance role, RoleCopyCount roleCopyCount, CopyConfig copyConfig) {
		roleCopyCount.incrBuyNum(new Date());
		switch (copyConfig.getCopyType()) {
		case hero:
			role.getRoleCount().changeTimes(CountType.HeroCopyBuy);//.incrHeroCopyBuy();
			break;
		case team:
			role.getRoleCount().changeTimes(CountType.TeamCopyBuy);//.incrTeamCopyBuy();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取副本系统基本配置
	 * @return
	 */
	private CopyBaseConfig getCopyBaseConfig() {
		return this.copyBaseConfig;
	}

	/**
	 * 副本中掉线再次登录的处理
	 * @param role
	 */
	private void disposeCopyLostReLogin(RoleInstance role) {
		try {
			// 掉线的副本标记信息
			String lostReLoginInfo = role.getCopyLostReLoginInfo();
			if (Util.isEmpty(lostReLoginInfo)) {
				Point point = new Point(role.getMapId(), role.getMapX(), role.getMapY());
				if (null == point || !point.isDefaultMap()) {
					point = this.getFailurePoint();
					role.setMapId(point.getMapid());
					role.setMapX(point.getX());
					role.setMapY(point.getY());
				}
				return ;
			}
			String[] infos = lostReLoginInfo.split(Cat.comma);
			if (3 > infos.length) {
				return ;
			}
			// 必须删除副本掉线的标记信息
			role.setCopyLostReLoginInfo(null);
			// 判断是否在副本掉线保护时间之内
			if (!this.isCopySafety(role)) {
				// 将玩家放在进入副本之前的地图
				this.mapFaultTolerant(role, infos[2]);
				return ;
			}
			Result result = this.reloadCopyMap(role, infos[0], infos[1], infos[2]);
			if (!result.isSuccess()) {
				// 将玩家放在进入副本之前的地图
				this.mapFaultTolerant(role, infos[2]);
			}
		} catch (Exception e) {
			this.logger.error("CopyLogicApp.disposeCopyLost error: ", e);
		}
	}
	
	/**
	 * 将玩家放到地图中
	 * @param role
	 * @param containerId
	 * @param mapInstanceId
	 */
	private Result reloadCopyMap(RoleInstance role, String containerId, String mapInstanceId, String mapId) {
		Result result = new Result();
		MapMultiCopyContainer container = (MapMultiCopyContainer) GameContext.getMapApp().getCopyContainer(containerId);
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(mapInstanceId);
		// 容器必须存在，并且切换占有状态成功，才可以进入
		if (null == mapInstance || null == container || !container.change_containerState_to_own()) {
			return result;
		}
		// 组队副本判断所有者是否是自己的队伍
		if (CopyType.team == container.getCopyType()) {
			Team team = role.getTeam();
			if (null == team) {
				return result;
			}
			// !!!! 下面条件得加上否则会出现队伍内全部玩家下线，再上线可以无限刷组队副本的bug
			if (container.getRoleCount() <= 0) {
				// 全部成员都已经下线
				return result;
			}
			// 当前队伍已经没有进度
			String existContainerId = team.getCopyContainerId(mapInstance.getMap().getMapConfig().getCopyId());
			if (Util.isEmpty(existContainerId) || !existContainerId.equals(containerId)) {
				return result;
			}
			if (!team.getTeamId().equals(container.getOwnerId())) {
				return result;
			}
		} else if (CopyType.personal == container.getCopyType() || CopyType.hero == container.getCopyType()) {
			role.setCopyContainerId(containerId);
		}
		
		// 设置进副本之前的点为下线的地点
		Point point = this.getCopyBeforePoint(role, mapId);
		if (null == point) {
			point = this.getFailurePoint();
		}
		role.setCopyBeforePoint(point.getMapid(), point.getX(), point.getY());
		
		// 设置角色的当前位置为该副本地图的卡死复位点
		role.setMapId(mapInstance.getMap().getMapId());
		role.setMapX(mapInstance.getMap().getMapConfig().getMaporiginx());
		role.setMapY(mapInstance.getMap().getMapConfig().getMaporiginy());
		return result.success();
	}
	
	/**
	 * 获取进入副本之前的点
	 * @param role
	 * @return
	 */
	private Point getCopyBeforePoint(RoleInstance role, String mapId) {
		Point point = null;
		try {
			sacred.alliance.magic.app.map.Map befMap = GameContext.getMapApp().getMap(mapId);
			point = new Point(befMap.getMapId(), befMap.getMapConfig().getMaporiginx(), befMap.getMapConfig().getMaporiginy());
			if (!point.isDefaultMap()) {
				point = this.getFailurePoint();
			}
		} catch (Exception e) {
			logger.error("CopyLogicAppImpl.getCopyBeforePoint error!", e);
		}
		return point;
	}

	/**
	 * 是否在副本保护时间内
	 * @param role
	 * @return
	 */
	private boolean isCopySafety(RoleInstance role) {
		Date lastOffTime = role.getLastOffTime();
		if (null == lastOffTime) {
			return false;
		}
		long safetyTime = GameContext.getParasConfig().getCopyLostReLogin();
		return System.currentTimeMillis() - lastOffTime.getTime() <= safetyTime;
	}

	/**
	 * 创建新的副本计数信息（记得放在内存中）
	 * @param role
	 * @param copyId
	 * @return
	 */
	private RoleCopyCount createRoleCopyCount(RoleInstance role, short copyId) {
		RoleCopyCount roleCopyCount = new RoleCopyCount();
		roleCopyCount.setRoleId(role.getRoleId());
		roleCopyCount.setCopyId(copyId);
		roleCopyCount.setUpdateTime(new Date());
		roleCopyCount.setInsertDB(true);
		return roleCopyCount;
	}

	/**
	 * 购买副本次数
	 * @param copyId
	 * @return
	 */
	@Override
	public CopyBuyNumResult copyBuyNum(RoleInstance role, short copyId, byte confirm) {
		CopyBuyNumResult result = new CopyBuyNumResult();
		try {
			CopyConfig copyConfig = this.getCopyConfig(copyId);
			// 如果找不到副本或者副本不可购买参与次数
			if (null == copyConfig || !copyConfig.showBuy()) {
				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return result;
			}
			RoleCopyCount roleCopyCount = this.getRoleCopyCount(role.getRoleId(), copyId);
			// 如果没有该副本记录，创建并放置到内存中
			if (null == roleCopyCount) {
				roleCopyCount = this.createRoleCopyCount(role, copyId);
				this.setRoleCopyCount(roleCopyCount);
			}
			// 如果已达到当前VIP等级最大购买次数
			if (!this.isBuyCountEnough(role, roleCopyCount, copyConfig)) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.Copy_Vip_Buy_More));
				return result;
			}
			CopyBuyConfig copyBuyConfig = this.getCopyBuyConfig(this.getCopyBuyConfigKey(copyId, roleCopyCount.getBuyNum(role)));
			// 已达副本最大购买次数
			if (null == copyBuyConfig) {
				result.setInfo(GameContext.getI18n().messageFormat(TextId.COPY_Buy_Empty));
				return result;
			}
			// 【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, copyBuyConfig.getMoney());
			if (ar.isIgnore()) {
				result.setIgnore(true);
				return result;
			}
			// 钻石不够
			if (!ar.isSuccess()) {
				result.setInfo(GameContext.getI18n().getText(TextId.NOT_ENOUGH_GOLD_MONEY));
				return result;
			}
			// 二次确认消息
			if (confirm == (byte) 0) {
				String tips = GameContext.getI18n().messageFormat(TextId.COPY_BUY_TIPS, String.valueOf(copyBuyConfig.getMoney()));
				Message notifyMsg = QuickCostHelper.getMessage(role, EXEC_CMDID, String.valueOf(copyId) + ",1", (short) 0, "", copyBuyConfig.getMoney(), 0, tips);
				role.getBehavior().sendMessage(notifyMsg);
				result.setIgnore(true);
				return result;
			}
			// 通过验证，扣钱并增加次数
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Decrease, copyBuyConfig.getMoney(), OutputConsumeType.copy_buy_money);
			role.getBehavior().notifyAttribute();
			this.incrCopyBuyNum(role, roleCopyCount, copyConfig);
			result.setCopyMaxCount((byte) this.getCopyMaxCount(role, copyId));
			result.setCopyRemCount((byte) this.getCopyRemainCount(role, copyId));
			result.setTypeMaxCount((byte) this.getCopyTypeMaxCount(role, copyConfig.getType()));
			result.setTypeRemCount((byte) this.getCopyTypeRemCount(role, copyConfig.getType()));
			result.setInfo(GameContext.getI18n().getText(TextId.COPY_BUY_SUCCESS));
			result.success();
		} catch (Exception e) {
			logger.error("CopyLogicAppImpl.copyBuyNum error", e);
		}
		return result;
	}
	
	/**
	 * 获取副本购买配置主键
	 * @param copyId
	 * @param currNum
	 * @return
	 */
	private String getCopyBuyConfigKey(short copyId, int currNum) {
		int nextNum = currNum + 1;
		return copyId + Cat.underline + nextNum;
	}

	/**
	 * 获得购买数据
	 * @param key
	 * @return
	 */
	@Override
	public CopyBuyConfig getCopyBuyConfig(String key) {
		try {
			if (this.copyBuyMap.containsKey(key)) {
				return this.copyBuyMap.get(key);
			}
		} catch (Exception e) {
			logger.error("getCopyBuyConfig", e);
		}
		return null;
	}

	/**
	 * 副本重置前记录一键追回信息
	 * @param role
	 * @param copyCount
	 */
	@Override
	public void onCopyCountDataReset(RoleInstance role, RoleCopyCount copyCount) {
		// 判断是否昨天
		Date yesterday = DateUtil.addDayToDate(new Date(), -1);
		int count = copyCount.getEnterNum();
		if (!DateUtil.sameDay(copyCount.getUpdateTime(), yesterday)) {
			// 昨天以前的记录,昨天没有参加任何活动
			count = 0;
		}
		int remainCount = this.getYesterdayMaxCount(role, copyCount) - count;
		if (remainCount <= 0) {
			return;
		}
		GameContext.getRecoveryApp().saveCopyRecovery(role, remainCount, copyCount.getCopyId());
	}
	
	/**
	 * 获得副本昨天的最大次数
	 * @param role
	 * @param copyId
	 * @return
	 */
	private int getYesterdayMaxCount(RoleInstance role, RoleCopyCount copyCount) {
		CopyConfig copyConfig = this.copyConfigMap.get(copyCount.getCopyId());
		if (null == copyConfig) {
			return -1;
		}
		int maxCount = copyConfig.getCount();
		if (null != copyCount) {
			maxCount += copyCount.getBuyNum();// 如果副本不限次数（不允许购买次数）,返回-1
		}
		// VIP特权（副本额外次数）
		maxCount += GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(), VipPrivilegeType.COPY_SINGLE_MORE_TIMES.getType(), String.valueOf(copyCount.getCopyId()));
		return maxCount;
	}

	/**
	 * 获取副本掉落信息属性
	 * @param copyId
	 * @return
	 */
	@Override
	public List<AttrConfig> getCopyAttrList(short copyId) {
		return attrMap.get(copyId);
	}

	/**
	 * 副本是否可扫荡
	 * @param role
	 * @param copyId
	 * @return
	 */
	@Override
	public byte copyShowRaids(RoleInstance role, short copyId) {
		CopyConfig copyConfig = this.getCopyConfig(copyId);
		if (null == copyConfig || !copyConfig.showRaids()) {
			return 0;
		}
		return 1;
	}

	/**
	 * 副本扫荡
	 * @param role
	 * @param copyId
	 * @return
	 */
	@Override
	public CopyRaidsResult raidsCopy(RoleInstance role, short copyId) {
		CopyRaidsResult result = new CopyRaidsResult();
		CopyConfig copyConfig = this.getCopyConfig(copyId);
		if (null == copyConfig || !copyConfig.showRaids()) {
			result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return result;
		}
		// 副本未曾通关过
		RoleCopyCount roleCopyCount = this.getRoleCopyCount(role.getRoleId(), copyId);
		if (null == roleCopyCount || !roleCopyCount.havePassCopy()) {
			result.setInfo(GameContext.getI18n().getText(TextId.Copy_Raids_No_Pass));
			return result;
		}
		// 等级或VIP等级不符合
		if (!this.canRaids(role)) {
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Copy_Raids_Open_Rules, this.copyBaseConfig.getOpenRaidsLevel(), GameContext.getVipApp().getOpenVipLevel(VipPrivilegeType.COPY_OPEN_RAIDS.getType(), "")));
			return result;
		}
		if (!this.isEnterCountEnough(role, copyConfig)) {
			result.setInfo(GameContext.getI18n().getText(TextId.Copy_Count_Finished));
			return result;
		}
		// 获取掉落物品
		LootList lootList = GameContext.getFallApp().getLootList(String.valueOf(copyConfig.getLootId()));
		if (null == lootList) {
			result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return result;
		}
		// 添加物品
		Result goodsResult = this.giveCopyPassReward(role, lootList.getGoodsBean());
		if (!goodsResult.isSuccess()) {
			return result;
		}
		// 增加英雄副本的进入次数
		this.incrCopyEnterNum(role, roleCopyCount, copyConfig);
		result.setCopyRemCount((byte) this.getCopyRemainCount(role, copyId));
		result.setTypeRemCount((byte) this.getCopyTypeRemCount(role, copyConfig.getType()));
		result.setGoodsLiteList(GoodsHelper.getGoodsLiteList(lootList.getGoodsBean()));
		result.success();
		return result;
	}
	
	/**
	 * 角色等级或VIP等级是否满足扫荡资格
	 * @param role
	 * @return
	 */
	private boolean canRaids(RoleInstance role) {
		if (role.getLevel() >= this.copyBaseConfig.getOpenRaidsLevel() || GameContext.getVipApp().getVipLevel(role) >= GameContext.getVipApp().getOpenVipLevel(VipPrivilegeType.COPY_OPEN_RAIDS.getType(), "")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 发放通关奖励
	 * @param role
	 * @param copyConfig
	 */
	private Result giveCopyPassReward(RoleInstance role, List<GoodsOperateBean> goodsList) {
		Result result = new Result();
		try {
			// 添加物品
			GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, goodsList, OutputConsumeType.copy_hero_raids);
			if (!goodsResult.isSuccess()) {
				// 如果添加物品失败，发送邮件
				GameContext.getMailApp().sendMail(role.getRoleId(), MailSendRoleType.CopyPass.getName(), "", MailSendRoleType.CopyPass.getName(),
						OutputConsumeType.copy_pass_reward.getType(), goodsList);
			}
		} catch (Exception e) {
			logger.error("CopyLogicAppImpl.giveCopyPassReward error!", e);
		}
		return result.success();
	}
	
	/**
	 * 获取副本首次通关奖励
	 * @param copyId
	 * @return
	 */
	private CopyFirstFallConfig getCopyFirstFallConfig(short copyId) {
		return this.firstFallMap.get(copyId);
	}
	
	/**
	 * 处理通关逻辑
	 * @param role
	 * @param copyConfig
	 */
	@Override
	public void copyPass(RoleInstance role, CopyConfig copyConfig) {
		switch (copyConfig.getCopyType()) {
		case personal:
			this.personalCopyPass(role, copyConfig);
			break;
		case hero:
			this.heroCopyPass(role, copyConfig);
			break;
		case team:
			this.teamCopyPass(role, copyConfig);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 普通副本通关逻辑
	 * @param role
	 * @param config
	 */
	private void personalCopyPass(RoleInstance role, CopyConfig config) {
		// 标记通关并记录次数
		RoleCopyCount roleCopyCount = this.getRoleCopyCount(role.getRoleId(), config.getCopyId());
		if (null == roleCopyCount) {
			roleCopyCount = this.createRoleCopyCount(role, config.getCopyId());
			this.setRoleCopyCount(roleCopyCount);
		}
		// 如果是首次通关副本，标记通关
		if (!roleCopyCount.havePassCopy()) {
			roleCopyCount.copyPass();
		}
		// 发送结算面板消息
		this.pushCopySettlementMessage(role, (byte)1, (byte)1, SettlementType.Hand.getType(), null, null, null);
		// 通关计数副本
		if (CopyConfig.PASS_INCR == config.getIncrType()) {
			this.incrCopyEnterNum(role, roleCopyCount, config);
		}
	}
	
	/**
	 * 英雄副本通关逻辑
	 * @param role
	 * @param config
	 */
	private void heroCopyPass(RoleInstance role, CopyConfig config) {
		// 标记通关并记录次数
		RoleCopyCount roleCopyCount = this.getRoleCopyCount(role.getRoleId(), config.getCopyId());
		if (null == roleCopyCount) {
			roleCopyCount = this.createRoleCopyCount(role, config.getCopyId());
			this.setRoleCopyCount(roleCopyCount);
		}
		// 如果有通关掉落
		List<GoodsOperateBean> goodsOperateList = Lists.newArrayList();// 掉落物品（添加）
		if (config.getLootId() > 0) {
			// 获得掉落
			LootList lootList = GameContext.getFallApp().getLootList(String.valueOf(config.getLootId()));
			if (null != lootList) {
				goodsOperateList.addAll(lootList.getGoodsBean());
			}
		}
		// 首次通关额外奖励
		if (!roleCopyCount.havePassCopy()) {
			CopyFirstFallConfig firstFallConfig = this.getCopyFirstFallConfig(config.getCopyId());
			if (null != firstFallConfig) {
				goodsOperateList.addAll(firstFallConfig.getGoodsList());
			}
			roleCopyCount.copyPass();
		}
		// 整理奖励，将相同的奖励归类
		List<GoodsOperateBean> goodsBeanList = this.orderGoodsLiteList(goodsOperateList);
		// 发放奖励
		Result result = this.giveCopyPassReward(role, goodsBeanList);
		if (!result.isSuccess()) {
			return ;
		}
		// 发送结算面板消息
		this.pushCopySettlementMessage(role, (byte)1, (byte)0, SettlementType.Hand.getType(), GoodsHelper.getGoodsLiteList(goodsBeanList), null, null);
		// 通关计数副本
		if (CopyConfig.PASS_INCR == config.getIncrType()) {
			this.incrCopyEnterNum(role, roleCopyCount, config);
		}
	}
	
	/**
	 * 整理奖励物品
	 * @param goodsLiteList
	 * @return
	 */
	private List<GoodsOperateBean> orderGoodsLiteList(List<GoodsOperateBean> goodsList) {
		List<GoodsOperateBean> list = Lists.newArrayList();
		Map<String, GoodsOperateBean> map = Maps.newHashMap();
		for (GoodsOperateBean bean : goodsList) {
			if (null == bean) {
				continue;
			}
			String key = bean.getGoodsId() + Cat.underline + bean.getBindType().getType();
			GoodsOperateBean goodsBean = map.get(key);
			if (null == goodsBean) {
				goodsBean = new GoodsOperateBean();
				goodsBean.setGoodsId(bean.getGoodsId());
				goodsBean.setBindType(bean.getBindType());
				map.put(key, goodsBean);
			}
			goodsBean.setGoodsNum(goodsBean.getGoodsNum() + bean.getGoodsNum());
		}
		list.addAll(map.values());
		return list;
	}
	
	/**
	 * 组队副本通关逻辑
	 * @param role
	 * @param config
	 */
	private void teamCopyPass(RoleInstance role, CopyConfig config) {
		// 标记通关并记录次数
		RoleCopyCount roleCopyCount = this.getRoleCopyCount(role.getRoleId(), config.getCopyId());
		if (null == roleCopyCount) {
			roleCopyCount = this.createRoleCopyCount(role, config.getCopyId());
			this.setRoleCopyCount(roleCopyCount);
		}
		// 如果是首次通关副本，标记通关
		if (!roleCopyCount.havePassCopy()) {
			roleCopyCount.copyPass();
		}
		// 发放威望奖励
		this.giveCopyPassPrestige(role, config);
		// 发送结算面板消息
		this.pushCopySettlementMessage(role, (byte)1, (byte)0, SettlementType.Default.getType(), null, null, null);
		// 通关计数副本
		if (CopyConfig.PASS_INCR == config.getIncrType()) {
			this.incrCopyEnterNum(role, roleCopyCount, config);
		}
	}
	
	/**
	 * 奖励威望
	 * @param role
	 * @param config
	 */
	private void giveCopyPassPrestige(RoleInstance role, CopyConfig config) {
		try {
			Team team = role.getTeam();
			if (null == team) {
				return;
			}
			int number = 0;
			for (AbstractRole absRole : team.getMembers()) {
				if (null == absRole) {
					continue;
				}
				// 如果不再副本内
				if (!role.getMapId().equals(config.getEnterMapId())) {
					continue;
				}
				if (absRole.getLevel() + this.getCopyBaseConfig().getPrestigeLevel() < role.getLevel()) {
					number ++;
				}
			}
			if (number > 0) {
				int prestige = number * config.getPrestigePoint();
				role.getBehavior().changeAttribute(AttributeType.prestigePoints, OperatorType.Add, prestige);
				role.getBehavior().changeAttribute(AttributeType.totalPrestigePoints, OperatorType.Add, prestige);
				role.getBehavior().notifyAttribute();
			}
		} catch (Exception e) {
			logger.error("CopyLogicAppImpl.giveCopyPassPrestige error!", e);
		}
	}
	
	/**
	 * PUSH副本结算面板
	 * @param role
	 * @param status
	 * @param pop
	 * @param type
	 * @param goodsLiteList
	 * @param attriTypeValueList
	 * @param info
	 */
	@Override
	public void pushCopySettlementMessage(RoleInstance role, byte status, byte pop, byte type, List<GoodsLiteItem> goodsLiteList,
			List<AttriTypeValueItem> attriTypeValueList, String info) {
		try{
			if (null == role) {
				return ;
			}
			C0226_CopySettlementRespMessage message = new C0226_CopySettlementRespMessage();
			message.setPop(pop);
			message.setType(type);
			message.setStatus(status);
			if (!Util.isEmpty(goodsLiteList)) {
				message.setGoodsLiteList(goodsLiteList);
			}
			if (!Util.isEmpty(attriTypeValueList)) {
				message.setAttriTypeValueList(attriTypeValueList);
			}
			if (!Util.isEmpty(info)) {
				message.setInfo(info);
			}
			role.getBehavior().sendMessage(message);
		} catch	(Exception e) {
			logger.error("CopyLogicAppImpl.pushPVESettlementMessage error!", e);
		}
	}

	/**
	 * 副本是否显示购买次数
	 * @param role
	 * @param copyId
	 * @return
	 */
	@Override
	public byte copyShowBuyNumber(short copyId) {
		CopyConfig copyConfig = this.getCopyConfig(copyId);
		if (null == copyConfig) {
			return 0;
		}
		return copyConfig.getCanBuyNum();
	}

}
