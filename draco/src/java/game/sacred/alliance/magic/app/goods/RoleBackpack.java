package sacred.alliance.magic.app.goods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.SaveDbStateType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;


public class RoleBackpack extends DefaultBackpack{
	
	public RoleBackpack(RoleInstance role, int gridCount) {
		super(role, gridCount);
	}
	
	
	@Override
	protected void delete(RoleGoods roleGoods){
		super.delete(roleGoods);
		//将物品从排行榜下架
		GameContext.getRankApp().equipOffRank(roleGoods);
	}
	
	
	/** 返回指定物品id、绑定规则的物品数量 */
	public int countByGoodsId(int goodsId, BindingType bindType){
		lock.lock();
		try{
			int count = 0;
			for(RoleGoods roleGoods : grids){
				if(roleGoods == null){
					continue;
				}
				if(roleGoods.getGoodsId() == goodsId 
						&& roleGoods.getBind() == bindType.getType()){
					count += roleGoods.getCurrOverlapCount();
				}
			}
			return count;
		}finally{
			lock.unlock();
		}
	}
	
	/** 返回剩余格子数目 */
	public int freeGridCount(){
		lock.lock();
		try{
			int count = role.getBackpackCapacity() - this.overlapCount();
			return count;
		}finally{
			lock.unlock();
		}
	}
	
	/** 返回空闲格子数目与预留格子数 */
	protected int freeGridAndRemainCount(){
		int count = 0;
		lock.lock();
		try{
			for(RoleGoods roleGoods : grids){
				if(roleGoods == null || roleGoods.isSpaceOccupying()){
					count ++;
				}
			}
			return count;
		}finally{
			lock.unlock();
		}
	}
	
	
	/** 获取所有预留物品 */
	protected List<RoleGoods> getAllRemainGoods(){
		List<RoleGoods> list = new ArrayList<RoleGoods>();
		lock.lock();
		try{
			for(RoleGoods roleGoods : grids){
				if(roleGoods == null){
					continue;
				}
				if(roleGoods.isSpaceOccupying()){
					list.add(roleGoods);
				}
			}
		} finally {
			lock.unlock();
		}
		return list;
	}
	
	
	/** 扩充格子 */
	public void expansionStorage(int addGridCount){
		lock.lock();
		try{
			if(addGridCount<=0){
				return ;
			}
			super.expansionStorage(addGridCount);
			role.setBackpackCapacity(this.grids.length);
		} finally {
			lock.unlock();
		}
	}
	
	/** 绑定规则 */
	@Override
	public BindingType getBindingType(BindingType bindType){
		if(bindType == BindingType.gain_binding){
			return BindingType.already_binding;
		}
		return bindType;
	}
	
	
	
	
	/**向容器中存放物品时触发任务通知**/
	@Override
	protected  void putNotify(int goodsId, int num){
		if(role == null || goodsId <= 0 || num <= 0){
			return ;
		}
		try{
			GameContext.getUserQuestApp().pickupGoods(role, goodsId, num);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	/**向容器中移除物品时触发任务通知**/
	protected  void removeNotify(int goodsId, int num){
		if(role == null || goodsId <= 0 || num <= 0){
			return ;
		}
		try{
			GameContext.getUserQuestApp().discardGoodsNotify(role.getRoleId(), goodsId, num);
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	
	
	/** 判断是否能放入所传入物品集合 */
	public boolean canPutGoods(List<RoleGoods> list){
		return this.canPut(list).isSuccess();
	}
	
	
	/**
	 * 删除物品所释放的格子数目
	 * 只计算指定消耗绑定类型所释放的格子数目
	 */
	protected int releaseGridCount(int goodsId, int delNum, BindingType bindType){
		if(bindType == BindingType.template){
			return 0;
		}
		List<RoleGoods> goodsList = this.getRoleGoodsByGoodsId(goodsId, bindType);
		if(Util.isEmpty(goodsList)){
			return 0;
		}
		
		int overlapCount;
		int count = 0;
		for(RoleGoods roleGoods : goodsList){
			if(delNum <= 0){
				break;
			}
			overlapCount = roleGoods.getCurrOverlapCount();
			if(overlapCount <= delNum){
				count ++;
				delNum -= overlapCount;
			}
		}
		
		return count;
	}
	
	
	
	/** 
	 * ==============================================================
	 * 判断是否能放入所传入物品集合 
	 * 1.计算所添加物品所需要的格子数目
	 * 2.计算消耗物品所释放的格子数目
	 * 3.判断是否能放入
	 * =============================================================
	 * */
	public boolean canPutGoods(List<RoleGoods> addList, List<RoleGoods> delList){
		
		int needGridCount = 0;
		for(RoleGoods roleGoods : addList){
			needGridCount += this.needGridCount(roleGoods);
		}
		
		int releaseGridCount = 0;
		if(!Util.isEmpty(delList)){
			releaseGridCount = delList.size();
		}
		
		int freeGridCount = this.freeGridCount();
		freeGridCount += releaseGridCount;
		
		if(freeGridCount >=  needGridCount){
			return true;
		}
		
		return false;
	}
	
	
	/** 判断是否能放入所传入物品集合 */
	public boolean canPutGoodsPlusRemain(List<RoleGoods> list){
		
		int needGridCount = 0;
		for(RoleGoods roleGoods : list){
			needGridCount += this.needGridCount(roleGoods);
		}
		
		int freeGridCount = this.freeGridAndRemainCount();
		
		if(freeGridCount >=  needGridCount){
			return true;
		}
		
		return false;
	}
	
	/**
	 * ============================================================
	 * 判断是否能放入所传入的物品
	 * 1.计算所添加物品所需要的格子数目
	 * 2.计算消耗物品所释放的格子数目
	 * 4.计算预留格子的数目和空闲格子数目
	 * 5.判断是否能放入
	 * ============================================================
	 */
	public boolean canPutGoodsPlusRemain(List<RoleGoods> addList, List<RoleGoods> delList){
		
		int needGridCount = 0;
		for(RoleGoods roleGoods : addList){
			needGridCount += this.needGridCount(roleGoods);
		}
		
		int releaseGridCount = 0;
		if(!Util.isEmpty(delList)){
			releaseGridCount = delList.size();
		}
		
		int freeGridCount = this.freeGridAndRemainCount();
		freeGridCount += releaseGridCount;
		
		if(freeGridCount >=  needGridCount){
			return true;
		}
		
		return false;
	}
	
	
	public Status canPutBean(List<GoodsOperateBean> list){
		if(Util.isEmpty(list)){
			return Status.SUCCESS;
		}
		//中间变量，合并ID相同且绑定类型相同的物品，修改数量
		Collection<GoodsOperateBean> collection = list;
		if(list.size() > 1){
			Map<String,GoodsOperateBean> map = new HashMap<String,GoodsOperateBean>();
			for(GoodsOperateBean bean : list){
				if(null == bean){
					continue;
				}
				int goodsId = bean.getGoodsId();
				BindingType bindType = bean.getBindType();
				int bind = bindType.getType();
				if(BindingType.template == bindType){
					GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
					if(null != goodsBase){
						bind = goodsBase.getBindType();
					}
				}
				int goodsNum = bean.getGoodsNum();
				String key = goodsId + Cat.underline + bind;
				if(!map.containsKey(key)){
					map.put(key, new GoodsOperateBean(goodsId, goodsNum, bind));
					continue;
				}
				GoodsOperateBean goodsBean = map.get(key);
				int num = goodsBean.getGoodsNum() + goodsNum;
				goodsBean.setGoodsNum(num);
			}
			collection = map.values();
		}
		lock.lock();
		try{
			int needGridCount = 0;
			for(GoodsOperateBean bean : collection){
				needGridCount += this.needGridCount(bean.getGoodsId(), bean.getGoodsNum(), bean.getBindType());
			}
			int freeGridCount = this.freeGridCount();
			
			if(freeGridCount >= needGridCount){
				return Status.SUCCESS;
			}
			
			return Status.GOODS_BACKPACK_FULL;
		}finally{
			lock.unlock();
		}
	}
	
	
	public Status canRemoveByBean(List<GoodsOperateBean> goodsBeanList){
		if(Util.isEmpty(goodsBeanList)){
			return Status.SUCCESS;
		}
		lock.lock();
		try{
			Map<Integer, Integer> map = this.filterGoodsOperateBeanList(goodsBeanList);
			for(int goodsId : map.keySet()){
				int num = map.get(goodsId);
				Status status = this.canRemove(goodsId, num);
				if(!status.isSuccess()){
					return status;
				}
			}
			return Status.SUCCESS;
		}finally{
			lock.unlock();
		}
	}
	
	
	//过滤掉重复删除物品
	private Map<Integer, Integer> filterGoodsOperateBeanList(List<GoodsOperateBean> goodsBeanList){
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for(GoodsOperateBean bean : goodsBeanList){
			int goodsId = bean.getGoodsId();
			int goodsNum = bean.getGoodsNum();
			if(map.containsKey(goodsId)){
				goodsNum += map.get(goodsId);
				map.put(goodsId, goodsNum);
				continue ;
			}
			map.put(goodsId, goodsNum);
		}
		return map;
	}
	
	/**
	 * =============================================================================
	 * 放入物品实例操作
	 * 1.判断背包容量(包括可叠加与不可叠加情况)
	 * 2.遍历背包中是否有相同类型物品实例（同类、同绑定规则），判断是否可叠放
	 * 3.当2满足，更新其叠放数目，封装更新消息
	 * 4.当2不满足，将该物品实例放入背包数组，封装添加消息，实时入库
	 * =============================================================================
	 */
	public GoodsResult put(RoleGoods roleGoods){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(Util.isEmpty(roleGoods)){
				return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			
			Status status = this.canPut(roleGoods);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			return this.innerPut(roleGoods);
		} finally {
			lock.unlock();
		}
	}
	
	 

	
	/**
	 * ============================================================================
	 * 批量放入物品操作：
	 * 1.累计所有物品所需的格子数，并判断背包容量
	 * 2.循环调用put(RoleGoods)，叠加所返回的更新物品集合
	 * ============================================================================
	 */
	public GoodsResult put(List<RoleGoods> goodsList){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(Util.isEmpty(goodsList)){
				result.success();
				return result;
			}
			Status status = this.canPut(goodsList);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			GoodsResult tempResult ;
			for(RoleGoods roleGoods : goodsList){
				tempResult = this.innerPut(roleGoods);
				result.collect(tempResult,GoodsResult.ALL_GOODS);
			}
			return result.setResult(GoodsResult.SUCCESS);
			
		} finally {
			lock.unlock();
		}
	}
	
	
	
	
	
	
	
	/**
	 * ===========================================================================
	 * 放入物品操作
	 * 1.判断背包容量(包括可叠加与不可叠加情况)
	 * 2.判断相同物品是否可再叠加
	 * 3.当2满足更新叠加数，否则创建新物品
	 * 4.当2不满足，根据模版可叠放最大数目计算需创建几个物品实例
	 * 4.添加物品，实时入库
	 * ===========================================================================
	 */
	public GoodsResult put(int goodsId, int num){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(goodsId <= 0 || num <= 0){
				return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			
			Status status = this.canPut(goodsId, num);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			
			return this.innerPut(role.getRoleId(), goodsId, num);
			
		}finally{
			lock.unlock();
		}
		
	}
	
	
	public GoodsResult put(int goodsId, int num, BindingType bind){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(goodsId <= 0 || num <= 0){
				return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			
			Status status = this.canPut(goodsId, num, bind);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			
			return this.innerPut(role.getRoleId(), goodsId, num, bind);
			
		}finally{
			lock.unlock();
		}
		
	}
	
	
	/**
	 * =========================================================
	 * 批量放入物品操作：
	 * 1.累计所有物品所需的格子数，并判断背包容量
	 * 2.循环调用put(int goodsId, int num)，叠加所返回的更新物品集合
	 * =========================================================
	 */
	public GoodsResult put(Map<Integer,Integer> addGoodsMap){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(Util.isEmpty(addGoodsMap)){
				result.success();
				return result;
			}
			
			Status status = this.canPut(addGoodsMap);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			
			GoodsResult tempResult;
			for(int goodsId : addGoodsMap.keySet()){
				tempResult = this.innerPut(role.getRoleId(), goodsId, addGoodsMap.get(goodsId));
				result.collect(tempResult, GoodsResult.ALL_GOODS);
			}
			
			return result.setResult(GoodsResult.SUCCESS);
			
		} finally {
			lock.unlock();
		}
	}
	
	
	/**
	 * ====================================================================
	 * 向背包中删除并添加物品
	 * 1.判断是否具备所删除物品
	 * 2.计算删除物品后能空出的背包格子数
	 * 3.计算添加物品所占用格子数
	 * 4.判断是否能进行删除添加操作
	 * 5.删除、添加入库，封装同步消息
	 * =====================================================================
	 */
	public GoodsResult putRemove(Map<Integer, Integer> addGoodsMap, Map<Integer, Integer> delGoodsMap){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			Status status = this.canRemove(delGoodsMap);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			status = this.canPut(addGoodsMap);
			GoodsResult tempResult;
			if(status.isSuccess()){
				tempResult = this.remove(delGoodsMap);
				result.collect(tempResult,GoodsResult.ADD_DEL_GOODS_FOR_DEL);
				
				tempResult = this.put(addGoodsMap);
				result.collect(tempResult,GoodsResult.ALL_GOODS);
				
				return result.setResult(GoodsResult.SUCCESS);
			}
			
			Iterator<Integer> it = delGoodsMap.keySet().iterator();
			
			int goodsId;
			int delNum;
			int releaseGridCount = 0;
			while(it.hasNext()){
				goodsId = it.next();
				delNum = delGoodsMap.get(goodsId);
				releaseGridCount += this.releaseGridCount(goodsId, delNum);
			}
			
			int freeGridCount = this.freeGridCount();
			int needGridCount = 0;
			
			int num;
			it = addGoodsMap.keySet().iterator();
			while(it.hasNext()){
				goodsId = it.next();
				num = addGoodsMap.get(goodsId);
				needGridCount += this.needGridCount(goodsId, num);
			}
			
			
			int hadGridCount = freeGridCount + releaseGridCount;
			if(hadGridCount < needGridCount){
				return result.setInfo(Status.GOODS_BACKPACK_FULL.getTips());
			}
			
			tempResult = this.remove(delGoodsMap);
			result.collect(tempResult,GoodsResult.ADD_DEL_GOODS_FOR_DEL);
			
			tempResult = this.put(addGoodsMap);
			result.collect(tempResult,GoodsResult.ALL_GOODS);
			
			return result.setResult(GoodsResult.SUCCESS);
			
		}finally{
			lock.unlock();
		}
	}
	
	public GoodsResult putRemove(List<GoodsOperateBean> addList, Map<Integer, Integer> delGoodsMap){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			Status status = this.canRemove(delGoodsMap);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			
			status = this.canPutBean(addList);
			GoodsResult tempResult;
			if(status.isSuccess()){
				tempResult = this.remove(delGoodsMap);
				result.collect(tempResult, GoodsResult.ADD_DEL_GOODS_FOR_DEL);
				
				tempResult = this.putBean(addList);
				result.collect(tempResult, GoodsResult.ALL_GOODS);
				return result.setResult(GoodsResult.SUCCESS);
			}
			
			Iterator<Integer> it = delGoodsMap.keySet().iterator();
			int goodsId;
			int delNum;
			int releaseGridCount = 0;
			while(it.hasNext()){
				goodsId = it.next();
				delNum = delGoodsMap.get(goodsId);
				releaseGridCount += this.releaseGridCount(goodsId, delNum);
			}
			
			int freeGridCount = this.freeGridCount();
			int needGridCount = 0;
			
			int num;
			BindingType bind;
			for(GoodsOperateBean bean : addList){
				goodsId = bean.getGoodsId();
				num = bean.getGoodsNum();
				bind = bean.getBindType();
				needGridCount += this.needGridCount(goodsId, num, bind);
			}
			
			int hadGridCount = freeGridCount + releaseGridCount;
			if(hadGridCount < needGridCount){
				return result.setInfo(Status.GOODS_BACKPACK_FULL.getTips());
			}
			
			tempResult = this.remove(delGoodsMap);
			result.collect(tempResult, GoodsResult.ADD_DEL_GOODS_FOR_DEL);
			
			tempResult = this.putBean(addList);
			result.collect(tempResult, GoodsResult.ALL_GOODS);
			
			return result.setResult(GoodsResult.SUCCESS);
		}finally{
			lock.unlock();
		}
	}
	
	
	public GoodsResult putRemove(List<GoodsOperateBean> addList, List<GoodsOperateBean> delList){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			Status status = this.canRemoveByBean(delList);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			
			status = this.canPutBean(addList);
			GoodsResult tempResult;
			if(status.isSuccess()){
				tempResult = this.removeByBeans(delList);
				result.collect(tempResult, GoodsResult.ADD_DEL_GOODS_FOR_DEL);
				
				tempResult = this.putBean(addList);
				result.collect(tempResult, GoodsResult.ALL_GOODS);
				return result.setResult(GoodsResult.SUCCESS);
			}
			
			int releaseGridCount = 0;
			for(GoodsOperateBean bean : delList){
				int goodsId = bean.getGoodsId();
				int delNum = bean.getGoodsNum();
				BindingType bind = bean.getBindType();
				releaseGridCount += this.releaseGridCount(goodsId, delNum, bind);
			}
			
			int freeGridCount = this.freeGridCount();
			int needGridCount = 0;
			for(GoodsOperateBean bean : addList){
				int goodsId = bean.getGoodsId();
				int num = bean.getGoodsNum();
				BindingType bind = bean.getBindType();
				needGridCount += this.needGridCount(goodsId, num, bind);
			}
			
			int hadGridCount = freeGridCount + releaseGridCount;
			if(hadGridCount < needGridCount){
				return result.setInfo(Status.GOODS_BACKPACK_FULL.getTips());
			}
			
			tempResult = this.removeByBeans(delList);
			result.collect(tempResult, GoodsResult.ADD_DEL_GOODS_FOR_DEL);
			
			tempResult = this.putBean(addList);
			result.collect(tempResult, GoodsResult.ALL_GOODS);
			
			return result.setResult(GoodsResult.SUCCESS);
			
		}finally{
			lock.unlock();
		}
	}
	
	
	public GoodsResult putRemove(List<GoodsOperateBean> addList, 
			RoleGoods delGoods, int removeCount, Map<Integer, Integer> delMap){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			int currOverlapCount = delGoods.getCurrOverlapCount();
			if(currOverlapCount < removeCount){
				return result.setInfo(Status.GOODS_NO_ENOUGH.getTips());
			}
			Status status = this.canPutBean(addList);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			
			status = this.canRemove(delMap);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			
			result = this.putBean(addList);
			int consumeCount = currOverlapCount - removeCount;
			delGoods.setCurrOverlapCount((short)consumeCount);
			if(consumeCount <= 0){
				int gridId = delGoods.getGridPlace();
				this.grids[gridId] = null;
				this.delete(delGoods);
			}
			result.addNeedUpdateGrid(delGoods);
			result.addDelGoodsRecords(delGoods, removeCount);
			
			GoodsResult removeResult = this.remove(delMap);
			result.collect(removeResult, GoodsResult.ADD_DEL_GOODS_FOR_DEL);
			
			return result.setResult(GoodsResult.SUCCESS);
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * 根据封装bean添加物品方法
	 */
	public GoodsResult putBean(List<GoodsOperateBean> list){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(Util.isEmpty(list)){
				return result.setResult(GoodsResult.SUCCESS);
			}
			
			Status status = canPutBean(list);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			
			int goodsId;
			int addNum;
			BindingType bind;
			
			GoodsResult tempRst;
			for(GoodsOperateBean bean : list){
				goodsId = bean.getGoodsId();
				addNum = bean.getGoodsNum();
				bind = bean.getBindType();
				
				if(bind != BindingType.template){
					tempRst = this.put(goodsId, addNum, bind);
				}else{
					tempRst = this.put(goodsId, addNum);
				}
				result.collect(tempRst,GoodsResult.ALL_GOODS);
			}
			
			return result.setResult(GoodsResult.SUCCESS);
			
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * 根据封装bean尽可能添加物品方法
	 */
	public AddGoodsBeanResult putSomeBean(List<GoodsOperateBean> list){
		lock.lock();
		try{
			AddGoodsBeanResult result = new AddGoodsBeanResult();
			
			int goodsId;
			int addNum;
			BindingType bind;
			GoodsResult tempRst;
			for(GoodsOperateBean bean : list){
				goodsId = bean.getGoodsId();
				addNum = bean.getGoodsNum();
				bind = bean.getBindType();
				
				if(bind != BindingType.template){
					tempRst = this.put(goodsId, addNum, bind);
				}
				else{
					tempRst = this.put(goodsId, addNum);
				}
				
				if(tempRst.isSuccess()){
					result.collect(tempRst, GoodsResult.ALL_GOODS);
					result.getPutSuccessList().add(bean);
					continue;
				}
				result.getPutFailureList().add(bean);
			}
			
			return result.success();
		
		}finally{
			lock.unlock();
		}
	}
	
	
	
	
	/**
	 * ==================================================
	 * 往预留格子和空闲格子中添加物品
	 * 1.先清除预留格子
	 * 2.往容器中添加物品
	 * ==================================================
	 */
	public GoodsResult putRemain(List<RoleGoods> list){
		lock.lock();
		try{
			this.clearRemainGrid();
			GoodsResult result = new GoodsResult();
			if(Util.isEmpty(list)){
				result.success();
				return result;
			}
			
			int freeGridCount = this.freeGridCount();
			int needGridCount = list.size();
			if(freeGridCount < needGridCount){
				return result.setInfo(Status.GOODS_BACKPACK_FULL.getTips());
			}
			
			for(RoleGoods roleGoods : list){
				this.toGridsPut(roleGoods);
				this.insert(roleGoods);
				result.addRoleGoodsRecord(roleGoods, roleGoods.getCurrOverlapCount());
				result.addNewGrid(roleGoods);
			}
			result.success();
			return result;
			
		}finally{
			lock.unlock();
		}
	}
	
	
	
	/**
	 * ===================================================================
	 * 根据物品实例删除物品
	 * 1.此方法不判断传入roleGoods是否存在
	 * 2.判断消耗数目是否足够
	 * 3.减去叠放数目
	 * 4.叠放数 <= 0 时删除物品，添加更新消息，实时入库
	 * ===================================================================
	 */
	public GoodsResult remove(RoleGoods roleGoods, int num){
		lock.lock();
		try{
			return this.innerRemove(roleGoods, num);
		}finally{
			lock.unlock();
		}
	}
	
	public GoodsResult remove(RoleGoods roleGoods){
		lock.lock();
		try{
			int num = roleGoods.getCurrOverlapCount();
			return this.remove(roleGoods, num);
		}finally{
			lock.unlock();
		}
	}
	
	
	public GoodsResult remove(List<RoleGoods> list){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(Util.isEmpty(list)){
				result.success();
				return result;
			}
			
			Status status = this.canRemove(list);
			if(!status.isSuccess()){
				return new GoodsResult().setInfo(status.getTips());
			}
			
			GoodsResult tempRst ;
			for(RoleGoods roleGoods : list){
				tempRst = this.remove(roleGoods);
				result.collect(tempRst, GoodsResult.ALL_GOODS);
			}
			return result.setResult(GoodsResult.SUCCESS);
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * =============================================================
	 * 根据物品实例和个数删除物品
	 * 1.判断是否存在该物品实例
	 * 2.判断消耗数目是否足够
	 * 3.减去叠放数目
	 * 4.叠放数 <= 0 时删除物品，添加更新消息，实时入库
	 * =============================================================
	 */
	public GoodsResult remove(String goodsInstanceId, int num){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			RoleGoods roleGoods = this.getRoleGoodsByInstanceId(goodsInstanceId);
			if(roleGoods == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			return this.innerRemove(roleGoods, num);
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * ================================================================
	 * 删除物品
	 * 1.判断背包物品是否足够
	 * 2.优先消耗绑定物品（已考虑未整理背包情况）
	 * 3.添加更新背包，删除格子实时入库
	 * ================================================================
	 */
	public GoodsResult remove(int goodsId, int num){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(goodsId <= 0 || num <= 0){
				return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			
			Status status = this.canRemove(goodsId, num);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			
			return this.innerRemove(goodsId, num);
			
		} finally {
			lock.unlock();
		}
	}
	
	
	
	
	/**
	 * ===================================================================
	 * 批量删除物品
	 * 1.判断物品是否存在
	 * 2.循环调用remove(goodsId,num)方法
	 * 3.累计背包更新消息返回
	 * ===================================================================
	 */
	public GoodsResult remove(Map<Integer,Integer> removeMap){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(Util.isEmpty(removeMap)){
				result.success();
				return result;
			}
			
			Status status = this.canRemove(removeMap);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips()); 
			}
			
			int consumeCount ;
			GoodsResult tempResult ;
			for(int goodsId : removeMap.keySet()){
				consumeCount = removeMap.get(goodsId);
				tempResult = this.innerRemove(goodsId, consumeCount);
				result.collect(tempResult, GoodsResult.ALL_GOODS);
			}
			
			return result.setResult(GoodsResult.SUCCESS);
			
		} finally {
			lock.unlock();
		}
	}
	
	
	
	/**
	 * ==================================================================
	 * 根据物品绑定类型删除背包物品
	 * 1.绑定类型不为templete类型时
	 * 		a.需判断是否具有该绑定类型物品
	 * 		b.调用按绑定类型删除物品
	 * 2.绑定类型为templete类型时，调用删除物品
	 * ==================================================================
	 */
	public GoodsResult remove(int goodsId, int num, BindingType bind){
		lock.lock();
		try{
			if(bind == BindingType.template){
				return this.remove(goodsId, num);
			}
			GoodsResult result = new GoodsResult();
			Status status = this.canRemove(goodsId, num);
			if(!status.isSuccess()){
				result.setInfo(status.getTips());
				result.failure();
				return result;
			}
			return this.innerRemove(goodsId, num, bind);
			
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * 根据bean集合删除背包物品
	 */
	public GoodsResult removeByBeans(List<GoodsOperateBean> goodsBeanList){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			Status status = this.canRemoveByBean(goodsBeanList);
			if(!status.isSuccess()){
				result.setInfo(status.getTips());
				result.failure();
				return result;
			}
			
			GoodsResult tempResult;
			for(GoodsOperateBean bean : goodsBeanList){
				int goodsId = bean.getGoodsId();
				int num = bean.getGoodsNum();
				BindingType bind = bean.getBindType();
				tempResult = this.remove(goodsId, num, bind);
				result.collect(tempResult, GoodsResult.ALL_GOODS);
			}
			
			result.success();
			return result;
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * ===================================================================
	 * 根据物品实例id删除物品
	 * 1.判断此物品是否存在
	 * 2.删除物品，添加更新消息，实时入库
	 * ===================================================================
	 */
	public GoodsResult remove(String instanceId){
		lock.lock();
		try{ 
			GoodsResult result = new GoodsResult();
			if(Util.isEmpty(instanceId)){
				return result.setInfo(Status.GOODS_NO_FOUND.getTips());
			}
			
			RoleGoods roleGoods = this.getRoleGoodsByInstanceId(instanceId);
			return this.innerRemove(roleGoods, roleGoods.getCurrOverlapCount());
			
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * =======================================================
	 * 删除物品并为所删除的物品预留物品栏格子
	 * 不对传入物品对象进行属性操作
	 * 1.计算删除物品释放的格子数 + 空闲格子数，是否够被占用
	 * 2.克隆物品集合用作操作集合
	 * 3.删除物品；成功，添加预留物品，预留格子
	 * 4.把传入的物品对象集合循环遍历改为insert状态
	 * 注：传入的list可能为空，但需预留格子，无需判断
	 * =======================================================
	 */
	public GoodsResult removeRemainGrid(List<RoleGoods> removeList, int remainCount){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			int releaseGridCount = Util.isEmpty(removeList)?0:removeList.size();
			int freeGridCount = this.freeGridCount() + releaseGridCount;
			if(remainCount > freeGridCount){
				result.failure();
				result.setInfo(Status.GOODS_BACKPACK_FULL.getTips());
				return result;
			}
			if(!Util.isEmpty(removeList)){
				List<RoleGoods> list = new ArrayList<RoleGoods>();
				for(RoleGoods roleGoods : removeList){
					RoleGoods clone = roleGoods.clone();
					list.add(clone);
				}
				
				result = this.remove(list);
				if(!result.isSuccess()){
					return result;
				}
				for(RoleGoods goods : removeList){
					goods.setSaveDbState(SaveDbStateType.Insert);
				}
			}
			for(int index = 0; index < remainCount; index++){
				RoleGoods remainGoods = new RoleGoods();
				remainGoods.setSpaceOccupying(true);
				this.toGridsPut(remainGoods);
			}
			result.success();
			return result;
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * =================================================================
	 * 尽可能的删除拥有物品
	 * 1.角色背包中有的删除
	 * 2.角色背包中不存在的物品不做处理
	 * 3.物品数量不足者，有多少删多少
	 * =================================================================
	 */
	public GoodsResult removeSome(Map<Integer, Integer> map){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(Util.isEmpty(map)){
				result.success();
				return result;
			}
			
			int delNum ;
			int currNum;
			GoodsResult tempRst ;
			for(int goodsId : map.keySet()){
				delNum = map.get(goodsId);
				currNum = this.countByGoodsId(goodsId);
				if(currNum <= 0){
					continue;
				}
				if(currNum < delNum){
					delNum = currNum;
				}
				tempRst = this.remove(goodsId, delNum);
				result.collect(tempRst, GoodsResult.ALL_GOODS);
			}
			return result.setResult(GoodsResult.SUCCESS);
		}finally{
			lock.unlock();
		}
	}
	
	
	
	/**
	 * =================================================================
	 * 整理背包操作
	 * 1.将背包中所有物品排序
	 * 2.合并物品（克隆模式合并）
	 * 3.清空当背包，循环加入合并后的物品
	 * 4.操作库，删除被合并物品
	 * =================================================================
	 */
	public GoodsResult reorganization(){
		GoodsResult result = new GoodsResult();
		lock.lock();
		try{
			List<RoleGoods> list = this.getAllGoods();
			GoodsComparator sort = new GoodsComparator();
			Collections.sort(list, sort);
			
			RoleGoods mergeGoods = null; //设为Null很关键
			List<RoleGoods> mergeList = new ArrayList<RoleGoods>();
			List<RoleGoods> deleteList = new ArrayList<RoleGoods>();
			for(RoleGoods currGoods : list){
				if(mergeGoods == null){
					mergeGoods = currGoods.clone();
					continue;
				}
				GoodsBase mergeBase = GameContext.getGoodsApp().getGoodsBase(mergeGoods.getGoodsId());
				if (mergeBase == null) {
					mergeGoods = null;
					continue;
				}
				// 判断是否满足合并条件, 不满足放入整理列表
				if (!this.canMerage(mergeGoods, currGoods, mergeBase)) {
					mergeList.add(mergeGoods);
					mergeGoods = currGoods.clone();
					continue;
				}
				int maxOverlapCount = mergeBase.getOverlapCount();
				int putCount = mergeGoods.getCurrOverlapCount() + currGoods.getCurrOverlapCount();
				// 当前叠放数量 < 最大叠放数量
				if (putCount < maxOverlapCount) {
					mergeGoods.setCurrOverlapCount(putCount);
					result.addRoleGoodsRecord(mergeGoods, currGoods.getCurrOverlapCount());
					deleteList.add(currGoods);
					continue;
				}
				// 当前叠放数量 == 最大叠放数量
				if(putCount == maxOverlapCount) {
					mergeGoods.setCurrOverlapCount((short)maxOverlapCount);
					result.addRoleGoodsRecord(mergeGoods, currGoods.getCurrOverlapCount());
					mergeList.add(mergeGoods);
					deleteList.add(currGoods);
					mergeGoods = null;
					continue;
				}
				// 当前叠放数量 > 最大叠放数量
				int changeCount = maxOverlapCount - mergeGoods.getCurrOverlapCount();
				mergeGoods.setCurrOverlapCount(maxOverlapCount);
				mergeList.add(mergeGoods);
				result.addRoleGoodsRecord(mergeGoods, changeCount);
				
				// 剩余待合并的物品
				mergeGoods = currGoods.clone();
				mergeGoods.setCurrOverlapCount(putCount - maxOverlapCount);
				result.addRoleGoodsRecord(mergeGoods, 0 - changeCount);
			}
			
			// 最后一个待合并的物品
			if (mergeGoods != null) {
				mergeList.add(mergeGoods);
			}
			// 更新容器
			this.clearAll();
			//加入之前预留格子物品
			mergeList.addAll(this.getAllRemainGoods());
			for (RoleGoods roleGoods : mergeList) {
				this.toGridsPut(roleGoods);
			}
			//整理完才能删除库中物品
			for(RoleGoods roleGoods : deleteList){
				int changeCount = 0 - roleGoods.getCurrOverlapCount();
				roleGoods.setCurrOverlapCount(0);
				result.addRoleGoodsRecord(roleGoods, changeCount);
				this.delete(roleGoods);
			}
			
			int storageType = StorageType.bag.getType();
			GameContext.getUserGoodsApp().syncAllGoodsGridMessage(role, mergeList, storageType);
			
			return result.setResult(GoodsResult.SUCCESS);
			
		}catch(Exception e){
			logger.error("",e);
			return result.setInfo(Status.FAILURE.getTips());
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * 是否满足合并条件
	 * 
	 */
	private boolean canMerage(RoleGoods mergeGoods, RoleGoods currGoods, GoodsBase mergeBase) {
		if (!mergeBase.hasOverlap()) {
			return false;
		}
		if (mergeGoods.getGoodsId() != currGoods.getGoodsId()) {
			return false;
		}
		if (mergeGoods.getBind() != currGoods.getBind()) {
			return false;
		}
		if(mergeBase.isMaxOverlapCount(mergeGoods.getCurrOverlapCount())) {
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * ===============================================================
	 * 1.获取物品实例
	 * 2.判断叠放数是否大于拆分数
	 * 3.复制物品实例，更改叠放数
	 * 4.获取空闲格子索引
	 * 5.指定格子索引添加物品
	 * 6.入库,发送同步消息
	 * ===============================================================
	 */
	public GoodsResult split(RoleGoods roleGoods, int splitNum){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(roleGoods == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
			if(goodsBase == null){
				return result.setInfo(Status.GOODS_NO_FOUND.getTips());
			}
			
			int currOverlapCount = roleGoods.getCurrOverlapCount();
			if(splitNum < 1 
					|| currOverlapCount <= splitNum 
					|| currOverlapCount <= 1 
					|| splitNum >= goodsBase.getOverlapCount()){
				return result.setInfo(Status.GOODS_NO_SPLIT.getTips());
			}
			
			int freeGridId = this.freeGridId();
			if(freeGridId < 0){
				return result.setInfo(Status.GOODS_BACKPACK_FULL.getTips());
			}
			
			RoleGoods splitRoleGoods = this.copyRoleGoods(roleGoods);
			currOverlapCount = roleGoods.getCurrOverlapCount() - splitNum;
			
			roleGoods.setCurrOverlapCount(currOverlapCount);
			splitRoleGoods.setCurrOverlapCount(splitNum);
			
			grids[freeGridId] = splitRoleGoods;
			this.insert(splitRoleGoods);
			
			splitRoleGoods.setGridPlace((short)freeGridId);
			result.addNeedUpdateGrid(roleGoods);
			result.addNewGrid(splitRoleGoods);
			result.addRoleGoodsRecord(roleGoods, -splitNum);
			result.addRoleGoodsRecord(splitRoleGoods, splitNum);
			
			return result.setResult(GoodsResult.SUCCESS);
			
		}finally{
			lock.unlock();
		}
		
	}
	
	/** 清除预留格子 */
	private void clearRemainGrid(){
		RoleGoods roleGoods;
		for(int index = 0; index < grids.length; index++){
			roleGoods = grids[index];
			if(roleGoods == null){
				continue;
			}
			if(roleGoods.isSpaceOccupying()){
				grids[index] = null;
			}
		}
	}
	
	/*
	 * 不对叠放数、背包格子位置赋值
	 */
	private RoleGoods copyRoleGoods(RoleGoods roleGoods){
		String goodsInstanceId = GameContext.getGoodsApp().newGoodsInstanceId();
		RoleGoods roleGoodsTemp = roleGoods.clone();
		roleGoodsTemp.setId(goodsInstanceId);
		/*RoleGoods roleGoodsTemp = new RoleGoods();
		roleGoodsTemp.setId(goodsInstanceId);
		roleGoodsTemp.setArmorn(roleGoods.getArmorn());
		roleGoodsTemp.setAttrVar(roleGoods.getAttrVar());
		roleGoodsTemp.setBind(roleGoods.getBind());
		roleGoodsTemp.setCurrDurable(roleGoods.getCurrDurable());
		roleGoodsTemp.setGoodsId(roleGoods.getGoodsId());
		roleGoodsTemp.setMaxHP(roleGoods.getMaxHP());
		roleGoodsTemp.setMaxMP(roleGoods.getMaxMP());
		roleGoodsTemp.setMosaic(roleGoods.getMosaic());
		roleGoodsTemp.setOtherParm(roleGoods.getOtherParm());
		roleGoodsTemp.setPunched(roleGoods.getPunched());
		roleGoodsTemp.setRepairFlag(roleGoods.getRepairFlag());
		roleGoodsTemp.setRoleId(roleGoods.getRoleId());
		roleGoodsTemp.setStarNum(roleGoods.getStarNum());
		roleGoodsTemp.setStorageType(roleGoods.getStorageType());
		roleGoodsTemp.setWeaponATKMax(roleGoods.getWeaponATKMax());
		roleGoodsTemp.setWeaponATKMin(roleGoods.getWeaponATKMin());*/
		RoleGoodsHelper.init(roleGoodsTemp);
		
		return roleGoodsTemp;
	}
	
	
	
	
	@Override
	public void init(int gridCount) {
		lock.lock();
		try{
			List<RoleGoods> list = GameContext.getBaseDAO().selectList(
					RoleGoods.class, RoleGoods.ROLE_ID, role.getRoleId(), 
					RoleGoods.STORAGE_TYPE, this.getStorageType().getType());
			
			int goodsSize = list.size();
			if(goodsSize > gridCount){
				gridCount = goodsSize;
			}
			
			super.init(gridCount);
			GoodsComparator sort = new GoodsComparator();
			Collections.sort(list, sort);
			
			this.clearAll();
			
			for(RoleGoods roleGoods : list){
				RoleGoodsHelper.init(roleGoods);
				this.toGridsPut(roleGoods);
				roleGoods.changeNotWrite();
			}
			
		} finally{
			lock.unlock();
		}
	}
	
	
	@Override
	public void offline(){
		lock.lock();
		try{
			for(RoleGoods roleGoods : this.grids){
				if(!this.isEffectiveGoods(roleGoods)){
					continue;
				}
				if(this.isOfflineDieGoods(roleGoods)){
					int index = roleGoods.getGridPlace();
					grids[index] = null;
					try{
						this.delete(roleGoods);
					}catch(Exception ex){
					}
					continue ;
				}
				try{
					roleGoods.offlineSaveDb();
				}catch(Exception ex){
					//物品入库日志
					roleGoods.offlineLog();
				}
			}
		}finally{
			lock.unlock();
		}
	}
	
	public void timingWriteDB(){
		lock.lock();
		try{
			for(RoleGoods roleGoods : this.grids){
				if(!this.isEffectiveGoods(roleGoods)){
					continue;
				}
				if(this.isOfflineDieGoods(roleGoods)){
					continue ;
				}
				try{
					roleGoods.offlineSaveDb();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} finally {
			lock.unlock();
		}
	}
	
	private boolean isOfflineDieGoods(RoleGoods roleGoods){
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if(goodsBase == null){
			return false;
		}
		return goodsBase.hasOfflineDie();
	}
	
	public List<RoleGoods> getCanFallGoodsList(){
		List<RoleGoods> list = new ArrayList<RoleGoods>();
		lock.lock();
		try{
			for(RoleGoods roleGoods : this.grids){
				if(!canFallGoods(roleGoods)){
					continue;
				}
				list.add(roleGoods);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return list;
	}
	
	private boolean canFallGoods(RoleGoods roleGoods){
		if(!this.isEffectiveGoods(roleGoods)){
			return false;
		}
		int goodsId = roleGoods.getGoodsId();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (null == goodsBase) {
			return false;
		}
		if(goodsBase.hasOfflineDie()){
			return false;
		}
		if (!goodsBase.hasDiscard()) {
			return false;
		}
		return true;
	}


	@Override
	public StorageType getStorageType() {
		return StorageType.bag ;
	}
}


