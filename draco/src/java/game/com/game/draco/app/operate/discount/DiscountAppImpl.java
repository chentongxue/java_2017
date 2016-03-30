package com.game.draco.app.operate.discount;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.config.DiscountCond;
import com.game.draco.app.operate.discount.config.DiscountReward;
import com.game.draco.app.operate.discount.domain.RoleDiscount;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.message.item.ActiveDiscountDetailItem;
import com.game.draco.message.response.C2316_ActiveDiscountDetailRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DiscountAppImpl implements DiscountApp {
	private static final Logger logger = LoggerFactory.getLogger(DiscountAppImpl.class);
	
	private Map<String, Map<Integer, RoleDiscount>> onlineRoleDiscountMap = Maps.newConcurrentMap();// 玩家折扣活动信息
	private Map<Integer, Discount> allListMap;// 所有折扣活动配置

	/**
	 * 获得玩家所有折扣信息
	 * @param roleId
	 * @return
	 */
	@Override
	public Map<Integer, RoleDiscount> getRoleDiscountMap(String roleId) {
		return onlineRoleDiscountMap.get(roleId);
	}

	/**
	 * 获得在线玩家折扣信息
	 * @param roleId
	 * @param activeId
	 * @return
	 */
	@Override
	public RoleDiscount getRoleDiscount(String roleId, int activeId) {
		Map<Integer, RoleDiscount> roleDiscountMap = this.getRoleDiscountMap(roleId);
		if (Util.isEmpty(roleDiscountMap)) {
			return null;
		}
		Discount discount = this.getDiscount(activeId);
		if (null == discount) {
			return null;
		}
		RoleDiscount roleDiscount = roleDiscountMap.get(activeId);
		Date now = new Date();
		if (null != roleDiscount && !discount.getDiscountTypeLogic().isSameCycle(roleDiscount, now)) {
			roleDiscount.resetAllCount(now);
		}
		return roleDiscount;
	}

	/**
	 * 获得离线玩家折扣信息
	 * @param role
	 * @param activetId
	 * @return
	 */
	@Override
	public RoleDiscount getOfflineRoleDiscount(String roleId, int activetId) {
		RoleDiscount roleDiscountInfo = null;
		try {
			// 查询数据库
			roleDiscountInfo = GameContext.getBaseDAO().selectEntity(RoleDiscount.class, RoleDiscount.ROLE_ID, roleId,
					RoleDiscount.ACTIVE_ID, activetId);
			if (null != roleDiscountInfo) {
				roleDiscountInfo.setInsertDB(false);
			}
		} catch (Exception e) {
			logger.error("DiscountAppImpl.getOfflineRoleDiscount error!", e);
		}
		return roleDiscountInfo;
	}

	/**
	 * 获得所有折扣配置
	 * @return
	 */
	@Override
	public Map<Integer, Discount> getAllDiscountConfigMap() {
		return allListMap;
	}

	@Override
	public Discount getDiscount(int discountId) {
		if (Util.isEmpty(this.allListMap)) {
			return null;
		}
		return this.allListMap.get(discountId);
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		Result result = this.loadConfig();
		if (!result.isSuccess()) {
			Log4jManager.CHECK.error(result.getInfo());
			Log4jManager.checkFail();
		}
	}

	@Override
	public Result reLoad() {
		Result result = this.loadConfig();
		return result;
	}

	private Result loadConfig() {
		Map<Integer, DiscountReward> allRewardMap = this.loadDiscountReward();
		Map<Integer, DiscountCond> allCondMap = this.loadDiscountCond();
		Map<Integer, Discount> allListMap = this.loadDiscountList();
		Result result = this.init(allRewardMap, allCondMap, allListMap);
		if (!result.isSuccess()) {
			return result;
		}
		// 注册活动
		Result regResult = GameContext.getOperateActiveApp().registerOperateActive(this.getOperateActiveList(allListMap), OperateActiveType.discount);
		if (!regResult.isSuccess()) {
			return regResult;
		}
		this.allListMap = allListMap;
		return result;
	}

	private List<OperateActive> getOperateActiveList(Map<Integer, Discount> allListMap) {
		if (Util.isEmpty(allListMap)) {
			return null;
		}
		List<OperateActive> operateList = Lists.newArrayList();
		for (Discount discount : allListMap.values()) {
			if (null == discount) {
				continue;
			}
			operateList.add(new DiscountActive(discount));
		}
		return operateList;
	}

	private Result init(Map<Integer, DiscountReward> allRewardMap, Map<Integer, DiscountCond> allCondMap, Map<Integer, Discount> allListMap) {
		Result loadResult = new Result();
		loadResult.failure();
		if (allRewardMap == null || allCondMap == null || allListMap == null) {
			// 加载配置文件异常
			return loadResult;
		}
		// 奖励
		if (!Util.isEmpty(allRewardMap)) {
			DiscountReward discountReward = null;
			for (Integer key : allRewardMap.keySet()) {
				discountReward = allRewardMap.get(key);
				if (null == discountReward) {
					continue;
				}
				if (!discountReward.init()) {
					loadResult.setInfo("in active_discount.xls reward config error!");
					return loadResult;
				}
			}
		}

		// 折扣活动项
		if (allListMap != null && allListMap.size() > 0) {
			Discount discount = null;
			try {
				for (Integer key : allListMap.keySet()) {
					discount = allListMap.get(key);
					if (null == discount) {
						continue;
					}
					Result result = discount.init();
					if (!result.isSuccess()) {
						Log4jManager.CHECK.error(result.getInfo());
						Log4jManager.checkFail();
						continue;
					}
					// 初始化条件和reward
					for (int index = 1; index <= Discount.MAX_COND_NUM; index++) {
						initListCondAndReward(discount, allRewardMap, allCondMap, index, discount.getCond(index), discount.getReward(index));
					}
					// 如果配置了活动但是没有配置相应的条件和奖励
					if (Util.isEmpty(discount.getCondList()) || Util.isEmpty(discount.getRewardList())) {
						Log4jManager.checkFail();
						Log4jManager.CHECK.error("activeDiscountApp init error, Discount id= " + discount.getActiveId() + "not config conds or rewards");
						loadResult.setInfo("in active_discount.xls list config error!");
						return loadResult;
					}
				}
			} catch (Exception e) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("activeDiscountApp init error, Discount id= " + discount.getActiveId(), e);
				loadResult.setInfo(e.toString());
				return loadResult;
			}

		}
		loadResult.success();
		return loadResult;
	}

	private void initListCondAndReward(Discount discount, Map<Integer, DiscountReward> allRewardMap, Map<Integer, DiscountCond> allCondMap, int index,
			int condId, int rewardId) {
		if (condId <= 0) {
			return;
		}
		if (!allCondMap.containsKey(condId) || !allRewardMap.containsKey(rewardId)) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("discount active, cond" + index + " or reward " + index + " in list id= " + discount.getActiveId() + " don't exsit");
		}
		DiscountCond discountCond = allCondMap.get(condId);
		Result condResult = discountCond.init();
		if (!condResult.isSuccess()) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("discount active," + condResult.getInfo() + " in list id= " + discount.getActiveId());
		}
		discount.getCondList().add(index - 1, discountCond);
		discount.getRewardList().add(index - 1, allRewardMap.get(rewardId));
	}

	@Override
	public void stop() {
	}

	private Map<Integer, DiscountReward> loadDiscountReward() {
		// 加载折扣活动配置
		String fileName = XlsSheetNameType.operate_discount_reward.getXlsName();
		String sheetName = XlsSheetNameType.operate_discount_reward.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			Map<Integer, DiscountReward> allRewardMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, DiscountReward.class);
			return allRewardMap;
		} catch (Exception ex) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		return null;
	}

	private Map<Integer, DiscountCond> loadDiscountCond() {
		// 加载折扣活动配置
		String fileName = XlsSheetNameType.operate_discount_cond.getXlsName();
		String sheetName = XlsSheetNameType.operate_discount_cond.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			Map<Integer, DiscountCond> allCondMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, DiscountCond.class);
			return allCondMap;
		} catch (Exception ex) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		return null;
	}

	private Map<Integer, Discount> loadDiscountList() {
		// 加载折扣活动配置
		String fileName = XlsSheetNameType.operate_discount_list.getXlsName();
		String sheetName = XlsSheetNameType.operate_discount_list.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			Map<Integer, Discount> allListMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, Discount.class);
			return allListMap;
		} catch (Exception ex) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		return null;
	}

	/**
	 * 批量保存玩家折扣信息s
	 * @param roleId
	 */
	@Override
	public void saveRoleActiveDiscount(String roleId) {
		Map<Integer, RoleDiscount> roleDiscountMap = this.getRoleDiscountMap(roleId);
		if (!Util.isEmpty(roleDiscountMap)) {
			for (Map.Entry<Integer, RoleDiscount> entry : roleDiscountMap.entrySet()) {
				RoleDiscount discountDbInfo = entry.getValue();
				if (null == discountDbInfo || discountDbInfo.getActiveId() <= 0) {
					continue;
				}
				discountDbInfo.updateDB();
			}
		}
	}

	/**
	 * 判断是否有领奖资格
	 * @param role
	 * @return
	 */
	@Override
	public boolean canRecvReward(RoleInstance role) {
		Map<Integer, Discount> discountListMap = this.getAllDiscountConfigMap();
		if (Util.isEmpty(discountListMap)) {
			return false;
		}
		for (Entry<Integer, Discount> entry : discountListMap.entrySet()) {
			Discount discount = entry.getValue();
			if (!discount.canShow(role)) {
				continue;
			}
			if (discount.canReward(role)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		// 从库里加载角色活动信息
		Map<Integer, RoleDiscount> roleDiscountMap = this.loadRoleActiveDiscount(role.getRoleId());
		if (Util.isEmpty(roleDiscountMap)) {
			return 1;
		}
		this.onlineRoleDiscountMap.put(role.getRoleId(), roleDiscountMap);
		return 1;
	}
	
	/**
	 * 上线加载数据库中所有折扣活动记录
	 * @param roleId
	 * @return
	 */
	@Override
	public Map<Integer, RoleDiscount> loadRoleActiveDiscount(String roleId) {
		List<RoleDiscount> roleDiscountList = GameContext.getBaseDAO().selectList(RoleDiscount.class, RoleDiscount.ROLE_ID, roleId);
		if (Util.isEmpty(roleDiscountList)) {
			return null;
		}
		Map<Integer, RoleDiscount> roleDiscountMap = Maps.newHashMap();
		for (RoleDiscount roleDiscount : roleDiscountList) {
			if (null == roleDiscount) {
				continue;
			}
			roleDiscount.setInsertDB(false);
			roleDiscountMap.put(roleDiscount.getActiveId(), roleDiscount);
		}
		return roleDiscountMap;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			this.saveRoleActiveDiscount(role.getRoleId());
			this.onlineRoleDiscountMap.remove(role.getRoleId());
		} catch (Exception e) {
			this.offlineLog(this.getRoleDiscountMap(role.getRoleId()));
			Log4jManager.OFFLINE_ERROR_LOG.error("activeDiscountApp.offline error,roleId=" + role.getRoleId() + ",userId=" + role.getUserId(), e);
			return 0;
		}
		return 1;
	}
	
	/**
	 * 离线时异常记录日志
	 * @param discountDbInfoMap
	 */
	private void offlineLog(Map<Integer, RoleDiscount> discountDbInfoMap) {
		try {
			if (discountDbInfoMap.size() == 0) {
				return;
			}
			for (Map.Entry<Integer, RoleDiscount> entry : discountDbInfoMap.entrySet()) {
				RoleDiscount discountDbInfo = entry.getValue();
				if (null == discountDbInfo) {
					continue;
				}
				Log4jManager.OFFLINE_DISCOUNT_ACTIVE_DB_LOG.info(discountDbInfo.getSelfInfo());
			}
		} catch (Exception e) {
		}
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		this.onlineRoleDiscountMap.remove(roleId);
		return 1;
	}

	/**
	 * 创建活动详细信息
	 * @param role
	 * @param discount
	 * @return
	 */
	@Override
	public Message buildDiscountDetailMessage(RoleInstance role, Discount discount) {
		C2316_ActiveDiscountDetailRespMessage resp = new C2316_ActiveDiscountDetailRespMessage();
		resp.setTime(discount.getTimeDesc());
		resp.setDesc(discount.getActiveDesc());
		resp.setStatus(discount.getDiscountType().isPay() ? (byte) 1 : (byte) 0);
		// 封装层级信息
		List<ActiveDiscountDetailItem> list = this.getActiveDIscountDetailItems(role, discount);
		resp.setDetailItemList(list);
		return resp;
	}
	
	@Override
	public List<ActiveDiscountDetailItem> getActiveDIscountDetailItems(RoleInstance role, Discount discount) {
		List<ActiveDiscountDetailItem> list = Lists.newArrayList();
		List<DiscountCond> condList = discount.getCondList();
		RoleDiscount roleDiscount = this.getRoleDiscount(role.getRoleId(), discount.getActiveId());// roleDiscount可能为null
		for (int i = 0; i < condList.size(); i++) {
			DiscountCond cond = condList.get(i);
			if (null == cond) {
				continue;
			}
			ActiveDiscountDetailItem item = new ActiveDiscountDetailItem();
			item.setCondDesc(cond.getDesc());
			DiscountReward discountReward = discount.getRewardList().get(i);
			item.setAttriTypeValueList(discountReward.getAttriTypeValueList(roleDiscount));
			item.setRewardStatus((byte) discount.getRewardStatus(roleDiscount, i).getType());
			item.setRewardList(GoodsHelper.getGoodsLiteNamedList(discountReward.getGoodsList()));
			if (null != roleDiscount) {
				item.setCurValue(discount.getDiscountTypeLogic().getCurrValue(roleDiscount));
			}
			item.setCondValue(cond.getParam1());
			list.add(item);
		}
		return list;
	}

	/**
	 * 创建新的折扣活动
	 * @param roleId
	 * @param activeId
	 * @return
	 */
	@Override
	public RoleDiscount createRoleDiscount(String roleId, int activeId, boolean online) {
		RoleDiscount roleDiscount = new RoleDiscount();
		roleDiscount.setRoleId(roleId);
		roleDiscount.setActiveId(activeId);
		roleDiscount.setInsertDB(true);
		// 如果玩家在线，放到内存中
		if (online) {
			Map<Integer, RoleDiscount> discountMap = this.getRoleDiscountMap(roleId);
			if (Util.isEmpty(discountMap)) {
				discountMap = Maps.newConcurrentMap();
				this.onlineRoleDiscountMap.put(roleId, discountMap);
			}
			discountMap.put(activeId, roleDiscount);
		}
		return roleDiscount;
	}

}