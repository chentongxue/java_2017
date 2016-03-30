package sacred.alliance.magic.app.goods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.title.domain.TitleRecord;
import com.game.draco.message.push.C0003_TipNotifyMessage;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.SaveDbStateType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;



public abstract class DefaultBackpack {
	public final static Logger logger = LoggerFactory.getLogger(DefaultBackpack.class);
	protected RoleGoods[] grids ;
	protected Lock lock;
	protected RoleInstance role;
	
	protected void init(int gridCount){
		this.grids = new RoleGoods[gridCount];
	}
	
	/** 绑定规则 */
	public abstract BindingType getBindingType(BindingType bindType);
	
	public abstract StorageType getStorageType() ;
	
	/**向容器中存放物品时触发任务通知**/
	protected  void putNotify(int goodsId, int num){
	}
	/**向容器中移除物品时触发任务通知**/
	protected  void removeNotify(int goodsId, int num){
	}
	
	public DefaultBackpack(RoleInstance role, int gridCount){
		this.lock = role.getGoodsLock();
		this.role = role;
		init(gridCount);
	}
	
	public RoleGoods[] getGrids(){
		return this.grids ;
	}
	
	protected String currentRoleId(){
		return this.role.getRoleId() ;
	}
	
	protected void insert(RoleGoods roleGoods){
		if(roleGoods == null || roleGoods.isSpaceOccupying()){
			return ;
		}
		roleGoods.setRoleId(this.currentRoleId());
		roleGoods.setSaveDbState(SaveDbStateType.Insert);
	}
	
	protected void delete(RoleGoods roleGoods){
		if(null == roleGoods) {
			return;
		}
		if(roleGoods.getSaveDbState() == SaveDbStateType.Update) {
			GameContext.getBaseDAO().delete(RoleGoods.class, RoleGoods.INSTANCEID, roleGoods.getId());	
		}
		roleGoods.setSaveDbState(SaveDbStateType.Insert);
	}
	
	
	
	protected Status canPut(RoleGoods roleGoods){
		lock.lock();
		try{
			if(roleGoods == null){
				return Status.GOODS_NO_FOUND;
			}
			
			int needGridCount = this.needGridCount(roleGoods);
			if(this.freeGridCount() >= needGridCount){
				return Status.SUCCESS;
			}
			return Status.GOODS_BACKPACK_FULL;
		}finally{
			lock.unlock();
		}
	}
	
	protected Status canPut(List<RoleGoods> goodsList){
		lock.lock();
		try{
			if(Util.isEmpty(goodsList)){
				return Status.SUCCESS;
			}
			int needGridCount = 0;
			for(RoleGoods roleGoods : goodsList){
				needGridCount += this.needGridCount(roleGoods);
			}
			if(this.freeGridCount() >= needGridCount){
				return Status.SUCCESS;
			}
			return Status.GOODS_BACKPACK_FULL;
		} finally {
			lock.unlock();
		}
	}
	
	protected Status canPut(int goodsId, int num){
		lock.lock();
		try{
			int needGridCount = this.needGridCount(goodsId, num);
			if(this.freeGridCount() >= needGridCount){
				return Status.SUCCESS;
			}
			return Status.GOODS_BACKPACK_FULL;
		} finally {
			lock.unlock();
		}
	}
	
	protected Status canPut(int goodsId, int num, BindingType bindType){
		lock.lock();
		try{
			int needGridCount = this.needGridCount(goodsId, num, bindType);
			if(this.freeGridCount() >= needGridCount){
				return Status.SUCCESS;
			}
			return Status.GOODS_BACKPACK_FULL;
		} finally {
			lock.unlock();
		}
	}
	
	
	protected Status canPut(Map<Integer, Integer> addGoodsMap){
		lock.lock();
		try{
			if(Util.isEmpty(addGoodsMap)){
				return Status.SUCCESS;
			}
			int needGridCount = 0;
			for(int goodsId : addGoodsMap.keySet()){
				needGridCount += this.needGridCount(goodsId, addGoodsMap.get(goodsId));
			}
			if(this.freeGridCount() >= needGridCount){
				return Status.SUCCESS;
			}
			return Status.GOODS_BACKPACK_FULL;
		} finally {
			lock.unlock();
		}
	}
	
	
	
	protected Status canRemove(RoleGoods roleGoods){
		lock.lock();
		try{
			if(roleGoods == null){
				return Status.GOODS_NO_FOUND;
			}
			String instanceId = roleGoods.getId();
			RoleGoods myGoods = this.getRoleGoodsByInstanceId(instanceId);
			if(myGoods == null){
				return Status.GOODS_NO_FOUND;
			}
			return Status.SUCCESS;
		} finally {
			lock.unlock();
		}
	}
	
	protected Status canRemove(int goodsId, int num){
		lock.lock();
		try{
			int hadCount = this.countByGoodsId(goodsId);
			if(hadCount < num){
				return Status.GOODS_NO_ENOUGH;
				}
			return Status.SUCCESS;
		} finally {
			lock.unlock();
		}
	}
	
	
	
	
	protected Status canRemove(List<RoleGoods> list){
		lock.lock();
		try{
			if(Util.isEmpty(list)){
				return Status.SUCCESS;
			}
			Status status;
			for(RoleGoods roleGoods : list){
				status = this.canRemove(roleGoods);
				if(!status.isSuccess()){
					return Status.GOODS_NO_ENOUGH;
				}
			}
			return Status.SUCCESS;
		} finally {
			lock.unlock();
		}
	}
	
	
	
	
	protected Status canRemove(Map<Integer, Integer> map){
		lock.lock();
		try{
			if(Util.isEmpty(map)){
				return Status.SUCCESS;
			}
			Status status;
			for(int goodsId : map.keySet()){
				status = this.canRemove(goodsId, map.get(goodsId));
				if(!status.isSuccess()){
					return Status.GOODS_NO_ENOUGH;
				}
			}
			return Status.SUCCESS;
		}finally{
			lock.unlock();
		}
	}
	
	
	
	
	/**
	 * =============================================================================
	 * 放入物品实例操作（内部方法）
	 * 不进行背包容量判断，此方法供put(List<Goods>)调用，节约反复判断物品存在所产生的性能开销
	 * 1.调用物品初始化方法
	 * 2.设置容器类型、绑定规则
	 * 3.循环向同类型，同绑定规则的物品实例添加叠放数
	 * 4.当添加物品个数为0是，返回
	 * 5.任务通知
	 * 6.当4不满足，将该物品实例放入背包数组，封装添加消息，实时入库
	 * =============================================================================
	 */
	protected GoodsResult innerPut(RoleGoods roleGoods){
		lock.lock(); 
		try{
			GoodsResult result = new GoodsResult();
			int goodsId = roleGoods.getGoodsId();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(goodsBase == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			
			RoleGoodsHelper.init(roleGoods);
			roleGoods.setStorageType(StorageType.bag.getType());
			BindingType bind = BindingType.get(roleGoods.getBind());
			if(bind == BindingType.template){
				bind = goodsBase.getBindingType();
			}
			bind = this.getBindingType(bind);
			roleGoods.setBind(bind.getType());
			
			int maxOverlapCount = goodsBase.getOverlapCount();
			int num = roleGoods.getCurrOverlapCount();
			int addNum = addStackableRoleGoods(goodsId, num, maxOverlapCount, roleGoods.getBind(), result);
			if(addNum <= 0){
				this.putNotify(roleGoods.getGoodsId(), roleGoods.getCurrOverlapCount());
				return result.setResult(GoodsResult.SUCCESS);
			}
			roleGoods.setCurrOverlapCount((short)addNum);
			this.toGridsPut(roleGoods);
			this.insert(roleGoods);
			result.addNewGrid(roleGoods);
			result.addRoleGoodsRecord(roleGoods, addNum);
			
			this.putNotify(roleGoods.getGoodsId(), roleGoods.getCurrOverlapCount());
			
			return result.setResult(GoodsResult.SUCCESS);
		} finally {
			lock.unlock();
		}
	}
	
	

	
	protected GoodsResult innerPut(String roleId, int goodsId, int num){
		lock.lock();
		try{
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(goodsBase == null){
				return new GoodsResult().setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			BindingType bind = this.getBindingType(goodsBase.getBindingType());
			return this.innerPut(roleId, goodsId, num, bind);
		}finally{
			lock.unlock();
		}
	}
	
	
	/**
	 * ===========================================================================
	 * 放入物品操作(内部方法)
	 * 不进行背包容量判断，此方法供put(Map<int,int>)调用，节约反复判断物品存在所产生的性能开销
	 * 1.判断传入绑定状态，如为模板则其模板默认绑定规则
	 * 2.调用容器的绑定方法，转换拾取绑定或装备绑定状态。
	 * 3.循环向同类型，同绑定规则的物品实例添加叠放数
	 * 4.当添加物品个数为0是，返回
	 * 5.当4不满足，根据模版可叠放最大数目计算需创建几个物品实例
	 * 6.调用物品实例初始化方法
	 * 7.设置绑定规则
	 * 8.任务通知
	 * 9.添加物品，实时入库
	 * ===========================================================================
	 */
	protected GoodsResult innerPut(String roleId, int goodsId, int num, BindingType bind){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(goodsBase == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			int maxOverlapCount = goodsBase.getOverlapCount();
			if(bind == BindingType.template){
				bind = goodsBase.getBindingType();
			}
			
			int bindType = this.getBindingType(bind).getType();
			int addNum = addStackableRoleGoods(goodsId, num, maxOverlapCount, bindType, result);
			if(addNum <= 0){
				this.putNotify(goodsId, num);
				//放前面会导致没有物品获得日志
				//使用称号
				//this.autoUseTitle(goodsBase,bindType,null);
				return result.setResult(GoodsResult.SUCCESS);
			}
			
			List<RoleGoods> list = goodsBase.createRoleGoods(roleId, addNum);
			for(RoleGoods roleGoods : list){
				RoleGoodsHelper.init(roleGoods);
				roleGoods.setBind((byte)bindType);
				this.toGridsPut(roleGoods);
				this.insert(roleGoods);
				result.addNewGrid(roleGoods);
				result.addRoleGoodsRecord(roleGoods, roleGoods.getCurrOverlapCount());
			}
			this.putNotify(goodsId, num);
			//放前面会导致没有物品获得日志
			//使用称号
			//this.autoUseTitle(goodsBase,bindType,list);
			return result.setResult(GoodsResult.SUCCESS);
		}finally{
			lock.unlock();
		}
	}

    /**
     * 自动称号使用逻辑放客户端,
     * 放在服务器端的问题: 使用后依然同步了-503消息给客户端了，这样导致客户端背包中显示有此物品(其实此物品早使用)
     */
	/*private void autoUseTitle(GoodsBase goodsBase,int bindType,List<RoleGoods> knowList){
		if(goodsBase.getGoodsType() != GoodsType.GoodsTitle.getType()){
			return ;
		}
		
		GoodsTitle gt = GameContext.getGoodsApp().getGoodsTemplate(GoodsTitle.class, goodsBase.getId());
		if(gt.getDeadline() > 0){
			return;
		}
		
		AbstractGoodsBehavior behavior = GoodsType.GoodsTitle
				.getGoodsBehavior(GoodsBehaviorType.Use);
		if (null == behavior) {
			return ;
		}
		List<RoleGoods> list = knowList ;
		if(Util.isEmpty(list)){
			list = this.getRoleGoodsByGoodsId(goodsBase.getId(), BindingType.get(bindType)); 
		}
		if(Util.isEmpty(list)){
			return ;
		}
		UseGoodsParam param = new UseGoodsParam(role);
		param.setRoleGoods(list.get(0));
		behavior.operate(param);
	}*/
	
	

	/**
	 * =====================================================================================
	 * 删除物品(内部方法)
	 * 不进行物品是否存在判断，此方法供外部删除多物品调用，节约反复判断物品存在所产生的性能开销
	 * 1.优先叠放数少者（已考虑未整理背包情况）暂不考虑先消耗绑定物品情况
	 * 2.添加更新背包，删除格子实时入库
	 * =====================================================================================
	 */
	protected GoodsResult innerRemove(int goodsId, int num){
		lock.lock();
		try{
			List<RoleGoods> bindGoods = new ArrayList<RoleGoods>();
			List<RoleGoods> noBindGoods = new ArrayList<RoleGoods>();
			List<RoleGoods> list = this.getRoleGoodsByGoodsId(goodsId);
			
			int goodsBindCount = 0;
			int bindingType ;
			for(RoleGoods roleGoods : list){
				bindingType = roleGoods.getBind();
				if(bindingType == BindingType.already_binding.getType()){
					bindGoods.add(roleGoods);
					goodsBindCount += roleGoods.getCurrOverlapCount();
				}
				else{
					noBindGoods.add(roleGoods);
				}
			}
			
			if(goodsBindCount >= num){
				this.removeNotify(goodsId, num);
				return this.consumeGoods(bindGoods, num);
			}
			
			GoodsResult result = this.consumeGoods(bindGoods, goodsBindCount);
			int consumeCount = num - goodsBindCount;
			GoodsResult noBingResult = this.consumeGoods(noBindGoods, consumeCount);
			result.getUpdateGrids().addAll(noBingResult.getUpdateGrids());
			result.getGoodsRecords().addAll(noBingResult.getGoodsRecords());
			
			this.removeNotify(goodsId, num);
			
			return result.setResult(GoodsResult.SUCCESS);
			
		} finally {
			lock.unlock();
		}
	}
	
	
	/**
	 * ================================================================================
	 * 删除物品(内部方法)
	 * 不进行物品是否存在判断，此方法供外部删除多物品调用，节约反复判断物品存在所产生的性能开销
	 * 1.如果是模板绑定类型，则调用innerRemove(int goodsId, int num)。先删绑定，后删不绑定
	 * 2.如果指定绑定类型，则先删除指定的绑定类型。不足时，再先删绑定的，后删不绑定的。
	 * ===============================================================================
	 */
	protected GoodsResult innerRemove(int goodsId, int num, BindingType bindType){
		lock.lock();
		try{
			if(bindType == BindingType.template){
				return this.innerRemove(goodsId, num);
			}
			
			List<RoleGoods> list = this.getRoleGoodsByGoodsId(goodsId, bindType);
			int goodsBindCount = 0;
			for(RoleGoods roleGoods : list){
				goodsBindCount += roleGoods.getCurrOverlapCount();
			}
			
			if(goodsBindCount >= num){
				this.removeNotify(goodsId, num);
				return this.consumeGoods(list, num);
			}
			
			GoodsResult result = this.consumeGoods(list, num);
			int consumeCount = num - goodsBindCount;
			GoodsResult defaultResult = this.innerRemove(goodsId, consumeCount);
			result.getUpdateGrids().addAll(defaultResult.getUpdateGrids());
			result.getGoodsRecords().addAll(defaultResult.getGoodsRecords());
			result.setResult(GoodsResult.SUCCESS);
			
			this.removeNotify(goodsId, num);
			
			return result;
		} finally {
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
	protected GoodsResult innerRemove(RoleGoods roleGoods, int num){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(Util.isEmpty(roleGoods) || num <= 0){
				return result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NO_EXISTS));
			}
			
			int currOverlapCount = roleGoods.getCurrOverlapCount();
			if(currOverlapCount < num){
				return result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NO_ENOUGH));
			}
			
			currOverlapCount -= num;
			roleGoods.setCurrOverlapCount((short)currOverlapCount);
			if(currOverlapCount <= 0){
				int gridId = roleGoods.getGridPlace();
				grids[gridId] = null;
				this.delete(roleGoods);
			}
			result.addNeedUpdateGrid(roleGoods);
			result.addRoleGoodsRecord(roleGoods, num);
			
			this.removeNotify(roleGoods.getGoodsId(), num);
			
			return result.setResult(GoodsResult.SUCCESS);
			
		}finally{
			lock.unlock();
		}
	}
	
	
	
	
	/**
	 * =============================================================================
	 * 工具类:
	 * 1.循环向同类型，同绑定规则的物品实例添加叠放数
	 * 2.当添加物品个数为0是，返回；否则返回添加后所剩余数
	 * =============================================================================
	 */
	protected int addStackableRoleGoods(int goodsId, int addNum, int maxOverlapCount, int bindType, GoodsResult result){
		lock.lock();
		try{
			int available ; 
			int currOverlapCount;
			for(RoleGoods roleGoods : grids){
				if(Util.isEmpty(roleGoods)){
					continue;
				}
				if(goodsId != roleGoods.getGoodsId()){
					continue;
				}
				if(bindType != roleGoods.getBind()){
					continue;
				}
				currOverlapCount = roleGoods.getCurrOverlapCount(); 
				if(currOverlapCount >= maxOverlapCount){
					continue;
				}
				available = maxOverlapCount - currOverlapCount;
				if(available >= addNum){
					roleGoods.setCurrOverlapCount((short)(currOverlapCount + addNum));
					result.addNeedUpdateGrid(roleGoods);
					result.addRoleGoodsRecord(roleGoods, addNum);
					addNum = 0;
				} else {
					roleGoods.setCurrOverlapCount((short)maxOverlapCount);
					result.addNeedUpdateGrid(roleGoods);
					result.addRoleGoodsRecord(roleGoods, available);
					addNum -= available;
				}
				if(addNum <= 0){
					return 0;
				}
			}
			return addNum;
		} finally{
			lock.unlock();
		}
	}
	
	
	
	/** 消耗物品工具类，删除格子时，实时入库 */
	protected GoodsResult consumeGoods(List<RoleGoods> list, int delNum){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			Collections.sort(list, new Comparator<RoleGoods>(){
				@Override
				public int compare(RoleGoods rg1, RoleGoods rg2) {
					if(rg1.getCurrOverlapCount() < rg2.getCurrOverlapCount()){
						return -1;
					}
					return 1;
				}
			});
			
			int consumeCount ;
			int currOverlapCount;
			int currCount;
			for(RoleGoods roleGoods : list){
				if(delNum <= 0){
					break ;
				}
				currOverlapCount = roleGoods.getCurrOverlapCount();
				consumeCount = Math.min(delNum, currOverlapCount);
				currCount = currOverlapCount - consumeCount;
				roleGoods.setCurrOverlapCount((short)currCount);
				delNum -= consumeCount;
				result.addNeedUpdateGrid(roleGoods);
				result.addRoleGoodsRecord(roleGoods, consumeCount);
				if(currCount == 0){
					int gridId = roleGoods.getGridPlace();
					grids[gridId] = null;
					this.delete(roleGoods);
				}
			}
			return result.setResult(GoodsResult.SUCCESS);
		} finally {
			lock.unlock();
		}
	}
	
	
	
	
	
	
	
	/** 目前背包总容量 */
	public int allGridCount(){
		lock.lock();
		try{
			return grids.length;
		} finally{
			lock.unlock();
		}
	}
	
	/** 背包是否已满 */
	public boolean isFull(){
		lock.lock();
		try{
			for(RoleGoods roleGoods : grids){
				if(roleGoods == null){
					return false;
				}
			}
			return true;
		} finally {
			lock.unlock();
		}
	}
	
	/** 返回指定物品id的物品数量 */
	public int countByGoodsId(int goodsId){
		lock.lock();
		try{
			int count = 0;
			for(RoleGoods roleGoods : grids){
				if(roleGoods == null){
					continue;
				}
				if(roleGoods.getGoodsId() == goodsId){
					count += roleGoods.getCurrOverlapCount();
				}
			}
			return count;
		}finally{
			lock.unlock();
		}
	}
	
	
	/** 返回指定物品实例的物品数量 */
	public int countByInstanceId(String instanceId){
		lock.lock();
		try{
			for(RoleGoods roleGoods : grids){
				if(roleGoods == null){
					continue;
				}
				if(roleGoods.getId().equals(instanceId.trim())){
					return roleGoods.getCurrOverlapCount();
				}
			}
			return 0;
		}finally{
			lock.unlock();
		}
	}
	
	
	/** 返回剩余格子数目 */
	protected int freeGridCount(){
		lock.lock();
		try{
			int count = grids.length - this.overlapCount();
			return count;
		}finally{
			lock.unlock();
		}
	}
	
	
	/** 已占用的格子数 */
	protected int overlapCount(){
		int count = 0;
		lock.lock();
		try{
			for(RoleGoods roleGoods : grids){
				if(roleGoods != null){
					count ++ ;
				}
			}
			return count;
		}finally{
			lock.unlock();
		}
		
	}
	
	/** 物品是否存在 */
	public boolean existGoods(int goodsId){
		lock.lock();
		try{
			for(RoleGoods roleGoods : grids){
				if(roleGoods == null){
					continue;
				}
				if(roleGoods.getGoodsId() == goodsId){
					return true;
				}
			}
			return false;
		}finally{
			lock.unlock();
		}
	}
	
	/** 
	 * 返回空余格子Id 
	 * 没有空闲格子时，返回-1
	 */
	protected int freeGridId(){
		lock.lock();
		try{
			RoleGoods roleGoods;
			for(int i = 0 ; i < grids.length ; i++){
				roleGoods = grids[i];
				if(roleGoods == null){
					return i;
				}
			}
			return -1;
		}finally{
			lock.unlock();
		}
	}
	
	
	
	/** 根据格子位置索引，清空该位置物品 */
	protected void clear(int gridId){
		lock.lock();
		try{
			grids[gridId] = null;
		}finally{
			lock.unlock();
		}
	}
	
	
	/** 从背包中移除该物品实例 */
	protected void clear(RoleGoods roleGoods){
		lock.lock();
		try{
			String instanceId = roleGoods.getId();
			String tempInstanceId;
			int gridId = 0;
			for(RoleGoods tempGoods : grids){
				tempInstanceId = tempGoods.getId();
				if(tempInstanceId.equals(instanceId)){
					this.clear(gridId);
				}
				gridId ++;
			}
		}finally{
			lock.unlock();
		}
	}
	
	
	
	/** 返回物品实例所在的格子位置 */
	protected int getGridId(String instanceId){
		lock.lock();
		try{
			RoleGoods roleGoods;
			for(int i = 0 ; i < grids.length ; i++){
				roleGoods = grids[i];
				if(roleGoods == null){
					continue;
				}
				if(roleGoods.getId().equals(instanceId)){
					return i;
				}
			}
			return -1;
		}finally{
			lock.unlock();
		}
	}
	
	/** 
	 * ===============================================
	 * 直接向物品数组添加一个物品实例 
	 * 1.不考虑数组容量情况
	 * 2.不入库
	 * 3.不同步消息
	 * ===============================================
	 */
	protected void toGridsPut(RoleGoods roleGoods){
		lock.lock();
		try{
			roleGoods.setRoleId(this.currentRoleId());
			for(int index = 0 ; index < grids.length ; index++){
				if(null == grids[index]){
					roleGoods.setGridPlace((short)index);
					grids[index] = roleGoods;
					return ;
				}
			}
		}finally{
			lock.unlock();
		}
	}
	
	/** 
	 * =========================================================================
	 * 根据物品id和数目返回所需的格子数 
	 * 1.判断传入绑定状态，如为模板则其模板默认绑定规则
	 * 2.调用容器的绑定方法，转换拾取绑定或装备绑定状态。
	 * 3.判断所有相同类型物品的剩余可用叠放数是否大于该放入物品的个数（同模版、同绑定规则、未达到最大叠放数）
	 * 4.当3不满足时，计算剩余未能叠放数将占用多少格子数（计算时需考虑余数问题）
	 * =========================================================================
	 */
	protected int needGridCount(int goodsId, int num, BindingType bind){
		lock.lock();
		try{
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(goodsBase == null){
				return 0;
			}
			if(bind == BindingType.template){
				bind = goodsBase.getBindingType();
			}
			bind = this.getBindingType(bind);
			int maxOverlapCount = goodsBase.getOverlapCount();
			List<RoleGoods> list = getRoleGoodsByGoodsId(goodsId,bind);
			
			int currOverlapCount;
			int feeOverlapCount = 0;
			for(RoleGoods roleGoods : list){
				currOverlapCount = roleGoods.getCurrOverlapCount();
				if(currOverlapCount >= maxOverlapCount){
					continue;
				}
				feeOverlapCount += maxOverlapCount - currOverlapCount;
			}
			
			if(feeOverlapCount >= num){
				return 0;
			}
			
			int needOverlapCount = num - feeOverlapCount;
			if( needOverlapCount % maxOverlapCount > 0){
				return needOverlapCount / maxOverlapCount + 1;
			}
			
			return needOverlapCount / maxOverlapCount; 
			
		}finally {
			lock.unlock();
		}
	}
	
	
	protected int needGridCount(int goodsId, int num){
		lock.lock();
		try{
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(goodsBase == null){
				return 0;
			}
			BindingType bind = this.getBindingType(goodsBase.getBindingType());
			return needGridCount(goodsId, num, bind);
			
		}finally {
			lock.unlock();
		}
	}
	
	protected int needGridCount(RoleGoods roleGoods){
		lock.lock();
		try{
			int goodsId = roleGoods.getGoodsId();
			int num = roleGoods.getCurrOverlapCount();
			BindingType bind = BindingType.get(roleGoods.getBind());
			return this.needGridCount(goodsId, num, bind);
		} finally {
			lock.unlock();
		}
	}
	
	
	
	
	/**
	 * 删除物品所释放的格子数目
	 * 1.先判断删除绑定物品所释放的格子数目
	 * 2.再计算删除不绑定物品所释放的格子数目
	 */
	protected int releaseGridCount(int goodsId, int delNum){
		
		List<RoleGoods> bindGoods = new ArrayList<RoleGoods>();
		List<RoleGoods> noBindGoods = new ArrayList<RoleGoods>();
		
		for(RoleGoods roleGoods : grids){
			if(roleGoods == null){
				continue;
			}
			if(goodsId != roleGoods.getGoodsId()){
				continue;
			}
			if(BindingType.already_binding.getType() == roleGoods.getBind()){
				bindGoods.add(roleGoods);
				continue;
			}
			noBindGoods.add(roleGoods);
		}
		
		
		int overlapCount;
		int count = 0;
		for(RoleGoods roleGoods : bindGoods){
			if(delNum <= 0){
				break;
			}
			overlapCount = roleGoods.getCurrOverlapCount();
			if(overlapCount <= delNum){
				count ++;
				delNum -= overlapCount;
			}
		}
		
		if(delNum <= 0){
			return count;
		}
		
		for(RoleGoods roleGoods : noBindGoods){
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
	
	
	/** 扩充格子 */
	public void expansionStorage(int addGridCount){
		lock.lock();
		try{
			if(addGridCount <= 0){
				return ;
			}
			int length = grids.length + addGridCount;
			RoleGoods[] temp = new RoleGoods[length];
			int index = 0;
			for(RoleGoods roleGoods : grids){
				temp[index] = roleGoods;
				index ++;
			}
			grids = temp;
		} finally {
			lock.unlock();
		}
	}
	
	
	/** 清空 */
	protected void clearAll(){
		lock.lock();
		try{
			for(int index = 0 ; index < grids.length ; index++){
				grids[index] = null;
			}
		}finally{
			lock.unlock();
		}
	}
	
	
	/** 获取所有物品 */
	public List<RoleGoods> getAllGoods(){
		List<RoleGoods> list = new ArrayList<RoleGoods>();
		lock.lock();
		try{
			for(RoleGoods roleGoods : grids){
				if(!this.isEffectiveGoods(roleGoods)){
					continue;
				}
				list.add(roleGoods);
			}
		} finally {
			lock.unlock();
		}
		return list;
	}
	
	protected boolean isEffectiveGoods(RoleGoods roleGoods){
		return null != roleGoods && !roleGoods.isSpaceOccupying() ;
	}
	
	
	public RoleGoods getRoleGoodsByInstanceId(String instanceId){
		lock.lock();
		try{
			for(RoleGoods roleGoods : grids){
				if(roleGoods != null && roleGoods.getId().equals(instanceId.trim())){
					return roleGoods;
				}
			}
			return null;
		} finally {
			lock.unlock();
		}
	}
	
	public List<RoleGoods> getRoleGoodsByGoodsId(int goodsId, BindingType bindType){
		lock.lock();
		try{
			List<RoleGoods> list = new ArrayList<RoleGoods>();
			for(RoleGoods roleGoods : grids){
				if(roleGoods == null){
					continue;
				}
				if(roleGoods.getGoodsId() == goodsId
						&& bindType.getType() == roleGoods.getBind()){
					list.add(roleGoods);
				}
			}
			return list;
		} finally {
			lock.unlock();
		}
	}
	
	
	
	/** 根据物品ID返回物品实例对象集合（包括绑定与不绑定） */
	public List<RoleGoods> getRoleGoodsByGoodsId(int goodsId){
		lock.lock();
		try{
			List<RoleGoods> list = new ArrayList<RoleGoods>();
			for(RoleGoods roleGoods : grids){
				if(roleGoods == null){
					continue;
				}
				if(roleGoods.getGoodsId() == goodsId){
					list.add(roleGoods);
				}
			}
			return list;
		} finally {
			lock.unlock();
		}
	}
	
	public void savaDB(){
		lock.lock();
		try{
			for(RoleGoods roleGoods : this.grids){
				try{
					if(!this.isEffectiveGoods(roleGoods)){
						continue ;
					}
					roleGoods.offlineSaveDb();
				}catch(Exception e){
					//物品入库日志
					roleGoods.offlineLog();
				}
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void offline(){ 
		this.savaDB();
	}
}
