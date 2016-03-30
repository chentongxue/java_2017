package sacred.alliance.magic.app.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.DefaultBackpack;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.RoleBackpack;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.goods.msgsender.GoodsGridMessageSender;
import sacred.alliance.magic.app.trading.TradingMatch;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleBorn;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

/**
 * 角色物品相关操作APP
 * 
 * @author Wang.K
 * 
 */
public class UserGoodsAppImpl implements UserGoodsApp {
	private GoodsGridMessageSender goodsGridMessageSender;// 封装背包同步消息

	public UserGoodsAppImpl() {
		goodsGridMessageSender = new GoodsGridMessageSender();
	}

	/** 通过背包类型与物品实例ID获取物品实例对象 */
	@Override
	public RoleGoods getRoleGoods(RoleInstance role, StorageType storageType,
			String goodsInstanceId,int targetId) {
		if (role == null || Util.isEmpty(goodsInstanceId)) {
			return null;
		}
		if (StorageType.trading == storageType) {
			// 交易
			TradingMatch match = GameContext.getTradingApp().getTradingMatch(
					role);
			if (null == match) {
				return null;
			}
			return match.getRoleGoods(role.getRoleId(), goodsInstanceId);
		}
		DefaultBackpack storage = this.getStorage(role, storageType,targetId);
		if (null == storage) {
			return null;
		}
		return storage.getRoleGoodsByInstanceId(goodsInstanceId);
	}
	

	@Override
	public List<RoleGoods> getRoleGoodsForBag(RoleInstance role, int goodsId) {
		if (role == null || goodsId <= 0) {
			return null;
		}

		return role.getRoleBackpack().getRoleGoodsByGoodsId(goodsId);
	}

	/** 返回容器对象 */
	@Override
	public DefaultBackpack getStorage(RoleInstance role, StorageType storageType,int targetId) {
		if (role == null || null == storageType) {
			return null;
		}
		switch (storageType) {
		case bag:
			return role.getRoleBackpack();
		case hero:
			return GameContext.getUserHeroApp().getEquipBackpack(
					role.getRoleId(),targetId);
		}
		return null;
	}

	/** 根据物品ID判断背包中是否存在此物品 */
	@Override
	public boolean isExistGoodsForBag(RoleInstance role, int goodsId) {
		if (role == null || goodsId <= 0) {
			return false;
		}
		return role.getRoleBackpack().existGoods(goodsId);
	}

	@Override
	public boolean canPutGoodsBean(RoleInstance role,
			List<GoodsOperateBean> list) {

		if (Util.isEmpty(list)) {
			return true;
		}
		Status status = role.getRoleBackpack().canPutBean(list);
		return status.isSuccess();
	}

	/** 判断是否能放入所传入物品集合 */
	@Override
	public boolean canPutGoods(RoleInstance role, List<RoleGoods> list) {

		if (role == null) {
			return false;
		}

		if (Util.isEmpty(list)) {
			return true;
		}

		return role.getRoleBackpack().canPutGoods(list);
	}

	@Override
	public boolean canPutGoods(RoleInstance role, List<RoleGoods> addList,
			List<RoleGoods> delList) {

		if (role == null) {
			return false;
		}

		if (Util.isEmpty(addList)) {
			return true;
		}

		return role.getRoleBackpack().canPutGoods(addList, delList);
	}

	/** 判断是否放入所传入物品（包括预留格子） */
	@Override
	public boolean canPutGoodsPlusRemain(RoleInstance role, List<RoleGoods> list) {
		if (role == null) {
			return false;
		}

		if (Util.isEmpty(list)) {
			return true;
		}
		return role.getRoleBackpack().canPutGoodsPlusRemain(list);
	}

	@Override
	public boolean canPutGoodsPlusRemain(RoleInstance role,
			List<RoleGoods> addList, List<RoleGoods> delList) {

		if (role == null) {
			return false;
		}

		if (Util.isEmpty(addList)) {
			return true;
		}
		return role.getRoleBackpack().canPutGoodsPlusRemain(addList, delList);
	}

	@Override
	public void insertDbRoleGoods(List<RoleGoods> list) throws ServiceException {
		if (Util.isEmpty(list)) {
			return;
		}
		for (RoleGoods roleGoods : list) {
			RoleGoodsHelper.destructor(roleGoods);
		}
		GameContext.getBaseDAO().insertBatch(list);
	}

	@Override
	public void deleteDbRoleGoods(List<RoleGoods> list) throws ServiceException {

		if (Util.isEmpty(list)) {
			return;
		}

		for (RoleGoods roleGoods : list) {
			GameContext.getBaseDAO().delete(RoleGoods.class,
					RoleGoods.INSTANCEID, roleGoods.getId());
		}
	}

	@Override
	public void deleteDbRoleGoods(RoleGoods roleGoods) throws ServiceException {
		if (roleGoods == null) {
			return;
		}
		GameContext.getBaseDAO().delete(RoleGoods.class, RoleGoods.INSTANCEID,
				roleGoods.getId());
	}

	/** 获取玩家背包空闲物品栏数目 **/
	@Override
	public int freeGoodsGridCountForBag(RoleInstance role) {
		return role.getRoleBackpack().freeGridCount();
	}

	/** 从角色背包中消耗物品,并发送背包同步消息 */
	@Override
	public GoodsResult deleteForBagByGoodsId(RoleInstance role, int goodsId,
			OutputConsumeType outputConsumeType) {

		if (role == null || goodsId <= 0) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		GoodsResult result = role.getRoleBackpack().remove(goodsId, 1);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	/** 从角色背包中消耗物品,并发送背包同步消息 */
	@Override
	public GoodsResult deleteForBagByMap(RoleInstance role,
			Map<Integer, Integer> delMap, OutputConsumeType outputConsumeType) {

		if (role == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		if (Util.isEmpty(delMap)) {
			return new GoodsResult().success();
		}

		GoodsResult result = role.getRoleBackpack().remove(delMap);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public GoodsResult deleteForBag(RoleInstance role, int goodsId,
			int goodsNum, OutputConsumeType outputConsumeType) {

		if (role == null || goodsId <= 0 || goodsNum <= 0) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		GoodsResult result = role.getRoleBackpack().remove(goodsId, goodsNum);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public GoodsResult deleteForBag(RoleInstance role, List<RoleGoods> delList,
			OutputConsumeType outputConsumeType) {
		if (role == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		if (Util.isEmpty(delList)) {
			return new GoodsResult().success();
		}

		GoodsResult result = role.getRoleBackpack().remove(delList);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public GoodsResult deleteForBean(RoleInstance role,
			List<GoodsOperateBean> delBeanList,
			OutputConsumeType outputConsumeType) {
		if (role == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		if (Util.isEmpty(delBeanList)) {
			return new GoodsResult().success();
		}
		GoodsResult result = role.getRoleBackpack().removeByBeans(delBeanList);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public GoodsResult deleteForBagByRoleGoods(RoleInstance role,
			RoleGoods roleGoods, int goodsNum,
			OutputConsumeType outputConsumeType) {
		if (role == null || roleGoods == null || goodsNum <= 0) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}
		GoodsResult result = role.getRoleBackpack().remove(roleGoods, goodsNum);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public GoodsResult deleteForBagByInstanceId(RoleInstance role,
			String goodsInstanceId, int goodsNum,
			OutputConsumeType outputConsumeType) {

		if (role == null || Util.isEmpty(goodsInstanceId) || goodsNum <= 0) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		GoodsResult result = role.getRoleBackpack().remove(goodsInstanceId,
				goodsNum);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public GoodsResult deleteForBagByInstanceId(RoleInstance role,
			String goodsInstanceId, OutputConsumeType outputConsumeType) {

		if (role == null || Util.isEmpty(goodsInstanceId)) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		GoodsResult result = role.getRoleBackpack().remove(goodsInstanceId);

		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	/** 删除物品并删除库中物品 **/
	public void deleteRoleGoods(RoleInstance role, RoleGoods roleGoods,
			OutputConsumeType outputConsumeType) {
		if (roleGoods == null) {
			return;
		}
		byte storageType = roleGoods.getStorageType();
		if (StorageType.bag.getType() == storageType) {
			role.getRoleBackpack().remove(roleGoods);
			return;
		}
	}

	/* 丢弃物品 */
	@Override
	public GoodsResult discardGoods(RoleInstance role, RoleGoods roleGoods,
			OutputConsumeType outputConsumeType) {
		if (role == null || roleGoods == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		GoodsResult result = role.getRoleBackpack().remove(roleGoods);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}
		return result;
	}

	@Override
	public GoodsResult deleteSomeForBagByMap(RoleInstance role,
			Map<Integer, Integer> delMap, OutputConsumeType outputConsumeType) {

		if (role == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		if (Util.isEmpty(delMap)) {
			return new GoodsResult().success();
		}

		GoodsResult result = role.getRoleBackpack().removeSome(delMap);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	/** 交易删除物品（不对传入RoleGoods进行属性操作） */
	@Override
	public GoodsResult deleteForBagRemainPlace(RoleInstance role,
			List<RoleGoods> delList, int remainCount,
			OutputConsumeType outputConsumeType) {

		if (role == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		if (Util.isEmpty(delList) && remainCount <= 0) {
			return new GoodsResult().success();
		}

		GoodsResult result = role.getRoleBackpack().removeRemainGrid(delList,
				remainCount);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	/** 往角色背包中添加物品 */
	@Override
	public GoodsResult addGoodsForBag(RoleInstance role,
			Map<Integer, Integer> addMap, OutputConsumeType outputConsumeType) {

		if (role == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		if (Util.isEmpty(addMap)) {
			return new GoodsResult().success();
		}

		GoodsResult result = role.getRoleBackpack().put(addMap);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public GoodsResult addGoodsForBag(RoleInstance role, int goodsId,
			int goodsNum, OutputConsumeType outputConsumeType) {

		if (role == null || goodsId <= 0 || goodsNum <= 0) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		GoodsResult result = role.getRoleBackpack().put(goodsId, goodsNum);

		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public GoodsResult addGoodsForBag(RoleInstance role, int goodsId,
			int goodsNum, BindingType bindType,
			OutputConsumeType outputConsumeType) {

		if (role == null || goodsId <= 0 || goodsNum <= 0) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		GoodsResult result;
		if (bindType != BindingType.template) {
			result = role.getRoleBackpack().put(goodsId, goodsNum, bindType);
		} else {
			result = role.getRoleBackpack().put(goodsId, goodsNum);
		}
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public GoodsResult addGoodsForBag(RoleInstance role, RoleGoods roleGoods,
			OutputConsumeType outputConsumeType) {

		if (role == null || roleGoods == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		GoodsResult result = role.getRoleBackpack().put(roleGoods);

		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	

	/** 添加物品（包含往预留物品格子中添加物品） */
	@Override
	public GoodsResult addGoodsForBagPlusRemain(RoleInstance role,
			List<RoleGoods> addList, OutputConsumeType outputConsumeType) {

		if (role == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		/*
		 * 一定得去掉以下判断，即时添加物品为空，也要清除预留格子 清除预留格子应放入背包事务中 if(Util.isEmpty(addList)){
		 * return new GoodsResult().success(); }
		 */

		GoodsResult result = role.getRoleBackpack().putRemain(addList);
		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	
	@Override
	public GoodsResult addGoodsBeanForBag(RoleInstance role,
			List<GoodsOperateBean> addList, OutputConsumeType outputConsumeType) {

		if (role == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		if (Util.isEmpty(addList)) {
			return new GoodsResult().success();
		}

		GoodsResult result = role.getRoleBackpack().putBean(addList);

		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public AddGoodsBeanResult addSomeGoodsBeanForBag(RoleInstance role,
			List<GoodsOperateBean> addList, OutputConsumeType outputConsumeType) {

		if (role == null) {
			return new AddGoodsBeanResult().setInfo(GameContext.getI18n()
					.getText(TextId.SYSTEM_ERROR));
		}

		if (Util.isEmpty(addList)) {
			return new AddGoodsBeanResult().success();
		}

		AddGoodsBeanResult result = role.getRoleBackpack().putSomeBean(addList);

		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	@Override
	public GoodsResult addGoodsForBag(RoleInstance role,
			List<RoleGoods> addList, OutputConsumeType outputConsumeType) {

		if (role == null) {
			return new GoodsResult().setInfo(GameContext.getI18n().getText(
					TextId.SYSTEM_ERROR));
		}

		if (Util.isEmpty(addList)) {
			return new GoodsResult().success();
		}

		GoodsResult result = role.getRoleBackpack().put(addList);

		if (result.isSuccess()) {
			result.syncBackpack(role, outputConsumeType);
		}

		return result;
	}

	/** 往背包中添加和删除物品 */
	@Override
	public GoodsResult addDelGoodsForBag(RoleInstance role,
			Map<Integer, Integer> addMap, OutputConsumeType addOptType,
			Map<Integer, Integer> delMap, OutputConsumeType delOptType) {

		if (role == null) {
			return new GoodsResult().setInfo(Status.FAILURE.getTips());
		}

		if (Util.isEmpty(addMap) && Util.isEmpty(delMap)) {
			return new GoodsResult().success();
		}

		GoodsResult result = role.getRoleBackpack().putRemove(addMap, delMap);
		if (result.isSuccess()) {
			result.syncBackpack(role, addOptType, delOptType);
		}

		return result;
	}

	@Override
	public GoodsResult addDelGoodsForBag(RoleInstance role,
			List<GoodsOperateBean> addList, OutputConsumeType addOcType,
			Map<Integer, Integer> delMap, OutputConsumeType delOcType) {

		if (role == null) {
			return new GoodsResult().setInfo(Status.FAILURE.getTips());
		}

		if (Util.isEmpty(addList) && Util.isEmpty(delMap)) {
			return new GoodsResult().success();
		}
		GoodsResult result = role.getRoleBackpack().putRemove(addList, delMap);
		if (result.isSuccess()) {
			result.syncBackpack(role, addOcType, delOcType);
		}

		return result;
	}

	@Override
	public GoodsResult addDelGoodsForBag(RoleInstance role,
			List<GoodsOperateBean> addList, OutputConsumeType addOcType,
			List<GoodsOperateBean> delList, OutputConsumeType delOcType) {

		if (role == null) {
			return new GoodsResult().setInfo(Status.FAILURE.getTips());
		}

		if (Util.isEmpty(addList) && Util.isEmpty(delList)) {
			return new GoodsResult().success();
		}

		GoodsResult result = role.getRoleBackpack().putRemove(addList, delList);
		if (result.isSuccess()) {
			result.syncBackpack(role, addOcType, delOcType);
		}

		return result;
	}

	@Override
	public GoodsResult addDelGoodsForBag(RoleInstance role,
			List<GoodsOperateBean> addList, OutputConsumeType addOcType,
			RoleGoods roleGoods, int delNum, Map<Integer, Integer> delMap,
			OutputConsumeType delOcType) {

		if (role == null) {
			return new GoodsResult().setInfo(Status.FAILURE.getTips());
		}

		GoodsResult result = role.getRoleBackpack().putRemove(addList,
				roleGoods, delNum, delMap);
		if (result.isSuccess()) {
			result.syncBackpack(role, addOcType, delOcType);
		}

		return result;
	}

	/**
	 * 同步部分物品栏信息（包含物品基本信息体）
	 *//*
	@Override
	public void syncSomeGoodsGridMessage(RoleInstance role,
			Map<Integer, List<RoleGoods>> map) {

		if (Util.isEmpty(map) || !this.isOnline(role)) {
			return;
		}
		goodsGridMessageSender.syncSomeGoodsGridMessage(role, map);
	}*/

	/**
	 * 同步部分物品栏信息（包含物品基本信息体）
	 */
	@Override
	public void syncSomeGoodsGridMessage(RoleInstance role,
			List<RoleGoods> goodsList, int storageType) {

		if (Util.isEmpty(goodsList) || !this.isOnline(role)) {
			return;
		}
		goodsGridMessageSender.syncSomeGoodsGridMessage(role, goodsList,
				storageType);
	}

	/**
	 * 同步部分物品栏信息（包含物品基本信息体）
	 */
	@Override
	public void syncSomeGoodsGridMessage(RoleInstance role, RoleGoods roleGoods) {
		if (roleGoods == null || !this.isOnline(role)) {
			return;
		}
		goodsGridMessageSender.syncSomeGoodsGridMessage(role, roleGoods);
	}

	/**
	 * 同步所有物品栏信息（包含物品基本信息体）
	 */
	@Override
	public void syncAllGoodsGridMessage(RoleInstance role,
			Map<Integer, List<RoleGoods>> map) {
		if (Util.isEmpty(map) || !this.isOnline(role)) {
			return;
		}
		goodsGridMessageSender.syncAllGoodsGridMessage(role, map);
	}
	
	private boolean isOnline(RoleInstance role){
		if(null == role){
			return false ;
		}
		return GameContext.getOnlineCenter().isOnlineByRoleId(
				role.getRoleId());
	}

	/**
	 * 同步所有物品栏信息（包含物品基本信息体）
	 */
	@Override
	public void syncAllGoodsGridMessage(RoleInstance role,
			List<RoleGoods> goodsList, int storageType) {
		if (!this.isOnline(role)) {
			return;
		}
		goodsGridMessageSender.syncAllGoodsGridMessage(role, goodsList,
				storageType);
	}

	/**
	 * 扩容背包同步消息 此方法只关心消息，不对角色属性做任何处理
	 */
	@Override
	public void notifyBackpackExpansionMessage(RoleInstance role, int addGirdNum) {
		if (addGirdNum <= 0 ||! this.isOnline(role)) {
			return;
		}
		goodsGridMessageSender.notifyBackpackExpansionMessage(role, addGirdNum);
	}

	/**
	 * 物品栏状态更新信息（物品叠放数、删除物品栏） 参数列表：角色ID，物品<容器类型,对应的物品集合>，操作提示（1：成功 0：失败），错误信息
	 */
	@Override
	public void updateGoodsGridMessage(RoleInstance role,
			Map<Integer, List<RoleGoods>> map) {
		if (Util.isEmpty(map) ||!this.isOnline(role)) {
			return;
		}
		goodsGridMessageSender.updateGoodsGridMessage(role, map);
	}

	/**
	 * 物品栏状态更新信息（物品叠放数、删除物品栏） 参数列表：角色ID，物品<容器类型,对应的物品集合>，容器类型，操作提示（1：成功
	 * 0：失败），错误信息
	 */
	@Override
	public void updateGoodsGridMessage(RoleInstance role,
			List<RoleGoods> goodsList, StorageType storageType) {
		if (Util.isEmpty(goodsList) || !this.isOnline(role)) {
			return;
		}
		goodsGridMessageSender.updateGoodsGridMessage(role, goodsList,
				storageType);
	}


	/**
	 * 加载人物所有物品 1.初始化背包对象、装备容器对象 2.调用其初始化方法
	 */
	@Override
	public int onLogin(RoleInstance role, Object context) {
		initRoleBackpack(role);
		return 1;
	}

	/** 保存角色所有物品 */
	@Override
	public void saveRoleAllGoods(RoleInstance role) {
		role.getRoleBackpack().savaDB();
	}

	/** 下线入库处理 */
	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			role.getRoleBackpack().offline();
		} catch (Exception ex) {
			// offline内部已经调用
			// userGoodsApplication.offlineLog(roleInstance);
			Log4jManager.OFFLINE_ERROR_LOG.error(
					"offlineStoreGoods error,roleId=" + role.getRoleId() + ",userId="
							+ role.getUserId(), ex);
			return 0;
		}
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void offlineLog(RoleInstance role) {
		try {
			List<RoleGoods> rgList = new ArrayList<RoleGoods>();
			rgList.addAll(role.getRoleBackpack().getAllGoods());
			if (rgList.size() == 0) {
				return;
			}
			for (RoleGoods rg : rgList) {
				try {
					rg.offlineLog();
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
		}
	}


	/** 创建角色时，初始默认物品 放创建角色action */
	@Override
	public void createRoleInitGoods(RoleInstance role)
			throws ServiceException {

		// 初始化背包对象
		int gridCount = role.getBackpackCapacity();
		RoleBackpack backpack = new RoleBackpack(role, gridCount);
		role.setRoleBackpack(backpack);

		RoleBorn roleBorn = GameContext.getRoleBornApp().getRoleBorn();

		// int[0] = 物品id
		// int[1] = 容器类型
		// int[2] = 绑定类型
		List<int[]> noviceList = roleBorn.getNoviceEquipList();

		int gridIndex = -1;
		int bindType;
		List<RoleGoods> list = new ArrayList<RoleGoods>();
		for (int[] novice : noviceList) {
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(
					novice[0]);
			if (null == goodsBase) {
				continue;
			}
			RoleGoods roleGoods = goodsBase.createSingleRoleGoods(
					role.getRoleId(), 1);
			RoleGoodsHelper.init(roleGoods);
			BindingType bindingType = BindingType.get(novice[2]);
			if (BindingType.template == bindingType) {
				bindingType = goodsBase.getBindingType();
			}
			
			bindType = backpack.getBindingType(bindingType).getType();
			gridIndex++;

			roleGoods.setGridPlace((short) gridIndex);
			roleGoods.setBind((byte) bindType);
			roleGoods.setStorageType((byte) novice[1]);
			list.add(roleGoods);
			GameContext.getStatLogApp().createRoleGoodsRecord(role, roleGoods,
					roleGoods.getCurrOverlapCount(), "",
					OutputConsumeType.role_born);
		}
		// 入库
		GameContext.getUserGoodsApp().insertDbRoleGoods(list);
	}

	@Override
	public GoodsResult reorganization(RoleInstance role, StorageType storageType) {

		if (role == null) {
			return new GoodsResult().setInfo(Status.Role_No_Online.getTips());
		}
		GoodsResult result = null;
		if (StorageType.bag == storageType) {
			result = role.getRoleBackpack().reorganization();
			if (result.isSuccess()) {
				result.syncBackpack(role, OutputConsumeType.goods_backpack_tidy);
			}
			return result;
		}
		if (StorageType.warehouse == storageType) {
			result = role.getWarehousePack().reorganization();
			if (result.isSuccess()) {
				result.sync(role, StorageType.warehouse,
						OutputConsumeType.goods_warehouse_tidy);
			}
			return result;
		}
		return new GoodsResult().setInfo(GameContext.getI18n().getText(
				TextId.USER_GOODS_STORAGE_TYPE_ERROR));
	}
	

	/* 加载角色背包 */
	private void initRoleBackpack(RoleInstance role) {
		int gridCount = role.getBackpackCapacity();
		RoleBackpack backpack = role.getRoleBackpack();
		if (backpack == null) {
			backpack = new RoleBackpack(role, gridCount);
		}
		role.setRoleBackpack(backpack);
	}


}
