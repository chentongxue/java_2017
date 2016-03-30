package sacred.alliance.magic.app.user;

import java.util.List;
import java.util.Map;

import com.game.draco.app.AppSupport;

import sacred.alliance.magic.app.goods.DefaultBackpack;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;


public interface UserGoodsApp extends AppSupport{
	
	/** 事务型物品入库 */
	public void insertDbRoleGoods(List<RoleGoods> list)throws ServiceException;
	/** 事务型物品删除 */
	public void deleteDbRoleGoods(List<RoleGoods> list)throws ServiceException;
	public void deleteDbRoleGoods(RoleGoods roleGoods) throws ServiceException;
	
	/** 通过背包类型与物品实例ID获取物品实例对象 */
	public RoleGoods getRoleGoods(RoleInstance role, StorageType storageType, 
			String goodsInstanceId,int targetId);
	
	
	public List<RoleGoods> getRoleGoodsForBag(RoleInstance role, int goodsId);
	
	/** 根据物品ID判断背包中是否存在此物品 */
	public boolean isExistGoodsForBag(RoleInstance role, int goodsId);
	
	/** 获取玩家背包空闲物品栏数目 **/
	public int freeGoodsGridCountForBag(RoleInstance role);
	
	/** 判断是否能放入所传入物品集合 */
	public boolean canPutGoods(RoleInstance role, List<RoleGoods> list);
	public boolean canPutGoodsBean(RoleInstance role, List<GoodsOperateBean> list);
	public boolean canPutGoods(RoleInstance role, List<RoleGoods> addList, List<RoleGoods> delList);
	/** 判断是否放入所传入物品（包括预留格子） */
	public boolean canPutGoodsPlusRemain(RoleInstance role, List<RoleGoods> list);
	public boolean canPutGoodsPlusRemain(RoleInstance role, List<RoleGoods> addList, List<RoleGoods> delList);
	
	/** 返回容器对象 */
	public DefaultBackpack getStorage(RoleInstance role, StorageType storageType,int targetId);
	
	/** 丢弃物品 */
	public GoodsResult discardGoods(RoleInstance role, RoleGoods roleGoods, OutputConsumeType outputConsumeType);
	
	/** 从角色背包中消耗物品 */
	public GoodsResult deleteForBagByGoodsId(RoleInstance role, int goodsId, OutputConsumeType outputConsumeType);
	public GoodsResult deleteForBag(RoleInstance role, int goodsId, int goodsNum, OutputConsumeType outputConsumeType);
	public GoodsResult deleteForBag(RoleInstance role, List<RoleGoods> delList, OutputConsumeType outputConsumeType);
	public GoodsResult deleteForBean(RoleInstance role, List<GoodsOperateBean> delBeanList, OutputConsumeType outputConsumeType);
	public GoodsResult deleteForBagByMap(RoleInstance role, Map<Integer, Integer> delMap, OutputConsumeType outputConsumeType);
	public GoodsResult deleteForBagByInstanceId(RoleInstance role, String goodsInstanceId, OutputConsumeType outputConsumeType);
	public GoodsResult deleteForBagByInstanceId(RoleInstance role, String goodsInstanceId, int goodsNum, OutputConsumeType outputConsumeType);
	public GoodsResult deleteForBagByRoleGoods(RoleInstance role, RoleGoods roleGoods, int goodsNum, OutputConsumeType outputConsumeType);
	/** 删除物品并删除库中物品 **/
	public void deleteRoleGoods(RoleInstance role, RoleGoods roleGoods, OutputConsumeType outputConsumeType);
	
	/** 删除背包中物品，并预留存放空间（不对传入RoleGoods做属性操作） */
	public GoodsResult deleteForBagRemainPlace(RoleInstance role, List<RoleGoods> delList, int remainCount, OutputConsumeType outputConsumeType);
	
	/** 删除所拥有的物品，物品不存在的不做操作 */
	public GoodsResult deleteSomeForBagByMap(RoleInstance role, Map<Integer, Integer> delMap, OutputConsumeType outputConsumeType);
	
	/** 往角色背包中添加物品 */
	public GoodsResult addGoodsForBag(RoleInstance role, Map<Integer, Integer> addMap, OutputConsumeType outputConsumeType);
	public GoodsResult addGoodsForBag(RoleInstance role, int goodsId, int goodsNum, OutputConsumeType outputConsumeType);
	public GoodsResult addGoodsForBag(RoleInstance role, int goodsId, int goodsNum, BindingType bindType, OutputConsumeType outputConsumeType);
	public GoodsResult addGoodsForBag(RoleInstance role, List<RoleGoods> addList, OutputConsumeType outputConsumeType);
	public GoodsResult addGoodsForBag(RoleInstance role, RoleGoods roleGoods, OutputConsumeType outputConsumeType);
	public GoodsResult addGoodsBeanForBag(RoleInstance role, List<GoodsOperateBean> addList, OutputConsumeType outputConsumeType);
	/** 添加物品（包含往预留物品格子中添加物品） */
	public GoodsResult addGoodsForBagPlusRemain(RoleInstance role, List<RoleGoods> addList, OutputConsumeType outputConsumeType);
	
	/** 往背包中放入物品,返回添加成功与失败的物品列表*/
//	public PutSomeGoodsResult addSomeGoodsForBag(RoleInstance role, Map<Integer, Integer> addMap, OutputConsumeType outputConsumeType);
	public AddGoodsBeanResult addSomeGoodsBeanForBag(RoleInstance role, List<GoodsOperateBean> addList, OutputConsumeType outputConsumeType);
	
	/** 往背包中添加和删除物品*/
	public GoodsResult addDelGoodsForBag(RoleInstance role, Map<Integer,Integer> addMap,OutputConsumeType addOcType,
			Map<Integer, Integer> delMap, OutputConsumeType delOcType);
	
	public GoodsResult addDelGoodsForBag(RoleInstance role, List<GoodsOperateBean> addList,OutputConsumeType addOcType,
			Map<Integer, Integer> delMap, OutputConsumeType delOcType);
	
	public GoodsResult addDelGoodsForBag(RoleInstance role, List<GoodsOperateBean> addList,OutputConsumeType addOcType,
			List<GoodsOperateBean> delList, OutputConsumeType delOcType);
	
	public GoodsResult addDelGoodsForBag(RoleInstance role, List<GoodsOperateBean> addList,OutputConsumeType addOcType,
			RoleGoods delGoods, int delNum, Map<Integer, Integer> delMap, OutputConsumeType delOcType);
	
	
	/** 整理背包 **/
	public GoodsResult reorganization(RoleInstance role, StorageType storageType);
	
	
	/** 保存角色所有物品 */
	public void saveRoleAllGoods(RoleInstance role);
	
	/**
	 * 同步部分物品栏信息（包含物品基本信息体）
	 */
	//public void syncSomeGoodsGridMessage(RoleInstance role, Map<Integer,List<RoleGoods>> map);
	public void syncSomeGoodsGridMessage(RoleInstance role, List<RoleGoods> goodsList, int storageType);
	public void syncSomeGoodsGridMessage(RoleInstance role, RoleGoods roleGoods);
	
	/**
	 * 扩容背包同步消息
	 * 此方法只关心消息，不对角色属性做任何处理
	 */
	public void notifyBackpackExpansionMessage(RoleInstance role, int addGirdNum);
	
	
	/**
	 * 同步所有物品栏信息（包含物品基本信息体）
	 */
	public void syncAllGoodsGridMessage(RoleInstance role, Map<Integer,List<RoleGoods>> map);
	public void syncAllGoodsGridMessage(RoleInstance role, List<RoleGoods> goodsList, int storageType);
	
	/**
	 * 物品栏状态更新信息（物品叠放数、删除物品栏）
	 * 参数列表：角色ID，物品<容器类型,对应的物品集合>，操作提示（1：成功 0：失败），错误信息
	 */
	public void updateGoodsGridMessage(RoleInstance role, Map<Integer,List<RoleGoods>> map);
	
	public void updateGoodsGridMessage(RoleInstance role, List<RoleGoods> goodsList, StorageType storageType);
	
	
	/** 创建角色时，初始默认物品 */
	public void createRoleInitGoods(RoleInstance role) throws ServiceException;
	
	/**
	 * 下线异常日志
	 * @param role
	 */
	public void offlineLog(RoleInstance role);
	
}
