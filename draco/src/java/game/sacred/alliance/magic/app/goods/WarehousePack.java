package sacred.alliance.magic.app.goods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

/**
 * 仓库容器
 */
public class WarehousePack extends DefaultBackpack{
	public WarehousePack(RoleInstance role, int gridCount) {
		super(role, gridCount);
	}
	
	
	@Override
	public void init(int gridCount){
		lock.lock();
		try{
			List<RoleGoods> list = GameContext.getBaseDAO().selectList(RoleGoods.class, 
					RoleGoods.ROLE_ID, role.getRoleId(),
					RoleGoods.STORAGE_TYPE, this.getStorageType().getType());
			
			int goodsSize = list.size();
			if(goodsSize > gridCount){
				gridCount = goodsSize;
			}
			
			super.init(gridCount);
			this.clearAll();
			
			for(RoleGoods roleGoods : list){
				RoleGoodsHelper.init(roleGoods);
				this.toGridsPut(roleGoods);
				roleGoods.changeNotWrite();
			}
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * 不考虑所放入物品可叠放情况
	 */
	@Override
	protected Status canPut(RoleGoods roleGoods){
		lock.lock();
		try{
			if(roleGoods == null){
				return Status.GOODS_NO_FOUND;
			}
			if(this.freeGridCount() < 1){
				return Status.GOODS_WAREHOUSE_FULL;
			}
			return Status.SUCCESS;
		}finally{
			lock.unlock();
		}
	} 
	
	
	
	
	/**
	 * 不考虑所放入物品可叠放情况
	 */
	@Override
	protected Status canPut(List<RoleGoods> list){
		lock.lock();
		try{
			if(Util.isEmpty(list)){
				return Status.GOODS_NO_FOUND;
			}
			if(this.freeGridCount() < list.size()){
				return Status.GOODS_BACKPACK_FULL;
			}
			return Status.SUCCESS;
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * 能否取回至背包
	 */
	public Status canTake(RoleGoods roleGoods){
		lock.lock();
		try{
			if(roleGoods == null){
				return Status.GOODS_NO_FOUND;
			}
			int freeCount = role.getRoleBackpack().freeGridCount();
			if(freeCount < 1){
				return Status.GOODS_BACKPACK_FULL;
			}
			return Status.SUCCESS;
		}finally{
			lock.unlock();
		}
	}
	
	
	
	public Status canTake(List<RoleGoods> list){
		lock.lock();
		try{
			if(Util.isEmpty(list)){
				return Status.GOODS_NO_FOUND;
			}
			int freeCount = role.getRoleBackpack().freeGridCount();
			if(freeCount < list.size()){
				return Status.GOODS_BACKPACK_FULL;
			}
			return Status.SUCCESS;
		}finally{
			lock.unlock();
		}
	}
	
	
	
	@Override
	protected void insert(RoleGoods roleGoods){
		if(roleGoods == null || roleGoods.isSpaceOccupying()){
			return ;
		}
		roleGoods.setStorageType(StorageType.warehouse.getType());
	}
	
	
	/**
	 * 不考虑所放入物品可叠放情况，按照容量空闲位置逐一放入
	 * 在背包内存删除此物品
	 */
	public GoodsResult put(RoleGoods roleGoods){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			Status status = this.canPut(roleGoods);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			//删除背包中的该物品
			int gridIndex = roleGoods.getGridPlace();
			role.getRoleBackpack().grids[gridIndex] = null;
			
			this.insert(roleGoods);
			this.toGridsPut(roleGoods);
			result.addNewGrid(roleGoods);
			result.addRoleGoodsRecord(roleGoods, roleGoods.getCurrOverlapCount());
			
			return result.success();
		}finally{
			lock.unlock();
		}
	}
	
	
	/** 扩充格子 */
	public void expansionStorage(int addGridCount){
		lock.lock();
		try{
			if(addGridCount<=0){
				return ;
			}
			super.expansionStorage(addGridCount);
			role.setWarehoseCapacity(this.grids.length);
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 门派升级
	 * @param gridCount 一共有多少个格子
	 */
	public void expansionWarehouse(int gridCount){
		lock.lock();
		try{
			if(gridCount <= 0){
				return ;
			}
			RoleGoods[] temp = new RoleGoods[gridCount];
			int index = 0;
			for(RoleGoods roleGoods : grids){
				temp[index] = roleGoods;
				index ++;
			}
			grids = temp;
			role.setWarehoseCapacity(this.grids.length);
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 创建门派 加入门派时 重置仓库格子数量
	 * 因为这时仓库肯定是空的 所以不用考虑物品相关
	 * !!!!!!!如果不是空 不能调用此方法 否则会导致看不到仓库物品
	 * @param gridCount
	 */
	public void resetWarehouse(int gridCount) {
		lock.lock();
		try{
			if(gridCount <= 0){
				return ;
			}
			grids = new RoleGoods[gridCount];
			role.setWarehoseCapacity(this.grids.length);
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 不考虑所放入物品可叠放情况，按照容量空闲位置逐一放入
	 */
	public GoodsResult put(List<RoleGoods> list){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			Status status = this.canPut(list);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			for(RoleGoods roleGoods : list){
				result.collect(this.put(roleGoods), GoodsResult.ALL_GOODS);
			}
			return result.success();
		}finally{
			lock.unlock();
		}
	}
	
	
	
	/**
	 * 取回物品
	 * 删除仓库容器物品
	 * 设置容器类型，添加背包物品
	 */
	public GoodsResult take(RoleGoods roleGoods){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			Status status = this.canTake(roleGoods);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			int index = roleGoods.getGridPlace();
			this.grids[index] = null;
			
			roleGoods.setStorageType(StorageType.bag.getType());
			role.getRoleBackpack().toGridsPut(roleGoods);
			result.addNewGrid(roleGoods);
			result.addRoleGoodsRecord(roleGoods, roleGoods.getCurrOverlapCount());
			
			return result.success();
		}finally{
			lock.unlock();
		}
	}
	
	
	
	/**
	 * 全部提取
	 */
	public GoodsResult takeAll(){
		lock.lock();
		try{
			return this.take(this.getAllGoods());
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * 提取全部装备
	 */
	public GoodsResult takeAllEqu(){
		lock.lock();
		try{
			return this.take(this.getAllEqu());
		}finally{
			lock.unlock();
		}
	}
	
	
	
	/**
	 * 提取所有消耗品
	 */
	public GoodsResult takeAllConsume(){
		lock.lock();
		try{
			return this.take(this.getAllConsume());
		}finally{
			lock.unlock();
		}
	}
	
	
	
	/** 获取仓库中所有装备 */
	private List<RoleGoods> getAllEqu(){
		lock.lock();
		try{
			List<RoleGoods> list = new ArrayList<RoleGoods>();
			for(RoleGoods roleGoods : this.grids){
				if(roleGoods == null){
					continue;
				}
				GoodsType goodsType = RoleGoodsHelper.getGoodsType(roleGoods);
				if(GoodsType.GoodsEquHuman == goodsType){
					list.add(roleGoods);
				}
			}
			return list;
		}finally{
			lock.unlock();
		}
	}
	
	
	/** 获取仓库中的所有消耗品 */
	private List<RoleGoods> getAllConsume(){
		lock.lock();
		try{
			List<RoleGoods> list = new ArrayList<RoleGoods>();
			for(RoleGoods roleGoods : this.grids){
				if(roleGoods == null){
					continue;
				}
				if(GoodsType.GoodsFood == RoleGoodsHelper.getGoodsType(roleGoods)){
					list.add(roleGoods);
				}
			}
			return list;
		}finally{
			lock.unlock();
		}
	}
	
	
	
	public GoodsResult take(List<RoleGoods> list){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			Status status = this.canTake(list);
			if(!status.isSuccess()){
				return result.setInfo(status.getTips());
			}
			for(RoleGoods roleGoods : list){
				result.collect(this.take(roleGoods), GoodsResult.ALL_GOODS);
			}
			return result.success();
		}finally{
			lock.unlock();
		}
	}
	
	public GoodsResult clearWarehouseAndMail(){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			List<RoleGoods> list = this.getAllGoods();
			if(Util.isEmpty(list)){
				return result.success();
			}
				
			for(RoleGoods roleGoods : list){
				result.collect(this.clearWarehouse(roleGoods), GoodsResult.ALL_GOODS);
			}
			GameContext.getUserWarehouseApp().sendGoodsByMail(role.getRoleId(),list);
			this.clearAll();
			return result.success();
		}finally{
			lock.unlock();
		}
	}
	
	public GoodsResult clearWarehouse(RoleGoods roleGoods){
		GoodsResult result = new GoodsResult();
		lock.lock();
		try{
			int index = roleGoods.getGridPlace();
			this.grids[index] = null;
			GameContext.getBaseDAO().delete(RoleGoods.class, RoleGoods.INSTANCEID, roleGoods.getId());
			return result.success();
		}catch(Exception e){
			logger.error("clearWarehouse error",e);
			return result.failure();
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
					mergeGoods.setCurrOverlapCount((short)putCount);
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
				mergeGoods.setCurrOverlapCount((short)maxOverlapCount);
				mergeList.add(mergeGoods);
				result.addRoleGoodsRecord(mergeGoods, changeCount);
				
				// 剩余待合并的物品
				mergeGoods = currGoods.clone();
				mergeGoods.setCurrOverlapCount((short)(putCount - maxOverlapCount));
				result.addRoleGoodsRecord(mergeGoods, 0 - changeCount);
			}
			
			// 最后一个待合并的物品
			if (mergeGoods != null) {
				mergeList.add(mergeGoods);
			}
			// 更新容器
			this.clearAll();
			for (RoleGoods roleGoods : mergeList) {
				this.toGridsPut(roleGoods);
			}
			//整理完才能删除库中物品
			for(RoleGoods roleGoods : deleteList){
				int changeCount = 0 - roleGoods.getCurrOverlapCount();
				roleGoods.setCurrOverlapCount((short)0);
				result.addRoleGoodsRecord(roleGoods, changeCount);
				this.delete(roleGoods);
			}
			
			int storageType = StorageType.warehouse.getType();
			GameContext.getUserGoodsApp().syncAllGoodsGridMessage(role, mergeList, storageType);
			
			return result.setResult(GoodsResult.SUCCESS);
			
		}catch(Exception e){
			logger.error("",e);
			return result.setInfo(Status.FAILURE.getTips());
		}finally{
			lock.unlock();
		}
	}
	
	public void offline(){
		lock.lock();
		try{
			for(RoleGoods roleGoods : this.getAllGoods()){
				if(RoleGoodsHelper.isOfflineDie(roleGoods)){
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
	
	@Override
	public BindingType getBindingType(BindingType bindType) {
		return bindType;
	}


	@Override
	public StorageType getStorageType() {
		return StorageType.warehouse ;
	}

}
