package sacred.alliance.magic.app.goods;

import java.util.List;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.EquipslotType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public abstract class BaseEquipBackpack extends DefaultBackpack{

	public BaseEquipBackpack(RoleInstance role, int gridCount) {
		super(role, gridCount);
	}
	
	public abstract int getEquipLocation(RoleGoods roleGoods) ;
	
	public int totalEquipQuality(int quality){
		int total = 0 ;
		for(int i=0;i<this.grids.length;i++){
			EquipslotType et = EquipslotType.get(i);
			if(null == et || !et.isEffect()){
				continue;
			}
			RoleGoods rg = this.grids[i];
			if(null == rg){
				continue ;
			}
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(rg.getGoodsId());
			if(null == gb){
				continue ;
			}
			if(gb.getQualityType() >= quality){
				total ++ ;
			}
		}
		return total ;
	}

	/**
	 * 获得身上镶嵌宝石>level的宝石数
	 * @param level
	 * @return
	 */
	public int totalEffectMosaicLevel(int level){
		int total = 0 ;
		for(int i=0;i<this.grids.length;i++){
			EquipslotType et = EquipslotType.get(i);
			if(null == et || !et.isEffect()){
				continue;
			}
			RoleGoods rg = this.grids[i];
			if(null == rg){
				continue ;
			}
			total += RoleGoodsHelper.countGemLevel(rg, level);
		}
		return total ;
	}
	
	/**
	 * 获得强化等级>level的装备数
	 * @param level
	 * @return
	 */
	public int totalEffectStrengthenLevel(int level){
		int total = 0 ;
		for(int i=0;i<this.grids.length;i++){
			EquipslotType et = EquipslotType.get(i);
			if(null == et || !et.isEffect()){
				continue;
			}
			RoleGoods rg = this.grids[i];
			if(null == rg || rg.getStrengthenLevel() < level){
				continue ;
			}
			total ++ ;
		}
		return total ;
	}
	
	public int totalEffectStar(int quality,int star){
		int total = 0 ;
		for(int i=0;i<this.grids.length;i++){
			EquipslotType et = EquipslotType.get(i);
			if(null == et || !et.isEffect()){
				continue;
			}
			RoleGoods rg = this.grids[i];
			if(null == rg || rg.getQuality() < quality){
				continue ;
			}
			if(rg.getQuality() == quality && rg.getStar() < star){
				continue ;
			}
			total ++ ;
		}
		return total ;
	}
	
	public int minEffectStrengthenLevel(){
		int value = Integer.MAX_VALUE ;
		for(int i=0;i<this.grids.length;i++){
			EquipslotType et = EquipslotType.get(i);
			if(null == et || !et.isEffect()){
				continue;
			}
			RoleGoods rg = this.grids[i];
			if(null == rg){
				return 0 ;
			}
			if(value > rg.getStrengthenLevel()){
				value = rg.getStrengthenLevel();
			}
		}
		return value ;
	}
	
	/**
	 * 获得不低于XX颜色的洗练属性总数
	 * @param quality
	 * @return
	 */
	public int totalEffectRecastingQualityNum(int quality){
		int total = 0 ;
		for(int i=0;i<this.grids.length;i++){
			EquipslotType et = EquipslotType.get(i);
			if(null == et || !et.isEffect()){
				continue;
			}
			RoleGoods rg = this.grids[i];
			if(null == rg){
				continue ;
			}
			total += RoleGoodsHelper.countRecastingAttribute(rg, quality);
		}
		return total ;
	}
	
	
	public BindingType getBindingType(BindingType bindType){
		return BindingType.already_binding;
	}
	
	
	/**
	 * ===============================================================================
	 * 穿装备操作
	 * 参数：所要穿的装备
	 * 返回：所穿装备位置替换下的装备
	 * 1.此方法不判断要穿装备是否存在与人物背包中。
	 * 2.此方法不判断该角色是否符合要穿装备的条件。
	 * 3.获取所穿装备的穿戴位置，修改绑定规则。
	 * 4.修改所穿装备的存放容器类型、装备背包索引位置。
	 * 5.如果穿戴位置有装备，则修改此装备的存放容器类型、背包位置（采用所穿装备的背包位置），返回
	 * ==============================================================================
	 */
	public RoleGoods wear(RoleGoods wearGoods){
		if(wearGoods == null){
			return null;
		}
		lock.lock();
		try{
			//获取所穿装备的穿戴位置
			int gridId = this.getEquipLocation(wearGoods);
			if(gridId < 0){
				return null;
			}
			//穿装备后一律变为已绑定
			wearGoods.setBind(BindingType.already_binding.getType());
			RoleGoods doffGoods = grids[gridId];
			//所穿装备在背包中的位置索引
			int gridPace = wearGoods.getGridPlace();
			//删除内存背包中所穿上的物品
			role.getRoleBackpack().grids[gridPace] = null;
			
			if(doffGoods != null){
				//脱的装备设置为背包容器
				doffGoods.setStorageType(StorageType.bag.getType());
				//所脱的装备位置设为所穿装备的背包位置
				doffGoods.setGridPlace((short)gridPace);
				//把脱的装备放入内存背包中
				role.getRoleBackpack().grids[gridPace] = doffGoods;
			}
			
			//更改所穿装备的容器类型
			wearGoods.setStorageType(this.getStorageType().getType());
			wearGoods.setRoleId(this.currentRoleId());
			//更改所穿装备的装备容器位置
			wearGoods.setGridPlace((short)gridId);
			this.grids[gridId] = wearGoods;
			this.wearSuccessCallback(wearGoods);
			return doffGoods;
		}finally{
			lock.unlock();
		}
	}
	
	
	protected void wearSuccessCallback(RoleGoods wearGoods){
	}
	
	protected void doffSuccessCallback(RoleGoods doffGoods){
	}
	
	protected void initPutSuccessCallback(RoleGoods roleGoods){
	}
	
	/**
	 * ==============================================
	 * 脱装备操作
	 * 参数：所要脱的装备
	 * 返回：所脱下的装备
	 * 1.获取所脱装备的容器索引，并删除并不入库
	 * 2.直接调用角色背包put方法
	 * 3.返回所脱装备
	 * ==============================================
	 */
	public RoleGoods doff(RoleGoods doffGoods){
		if(doffGoods == null){
			return null;
		}
		lock.lock();
		try{
			//不能使用下面语句
			//int gridId = this.getEquipLocation(doffGoods);
			int gridId = doffGoods.getGridPlace() ;
			if(gridId < 0){
				return null;
			}
			int freeGridId = role.getRoleBackpack().freeGridId();
			if(freeGridId < 0){
				return null;
			}
			grids[gridId] = null;
			doffGoods.setStorageType(StorageType.bag.getType());
			
			role.getRoleBackpack().grids[freeGridId] = doffGoods;
			doffGoods.setGridPlace((short)freeGridId);
			doffSuccessCallback(doffGoods);
			return doffGoods;
		}catch(Exception ex){
			logger.error("",ex);
		}finally{
			lock.unlock();
		}
		return null ;
	}
	
	
	
	
	/**
	 * =========================================================
	 * 向装备容器中添加装备
	 * 1.此方法只在初始化时使用，不适于脱穿装备
	 * 2.此方法并不关心格子位置是否有装备
	 * 3.此方法不入库
	 * =========================================================
	 */
	private GoodsResult initPut(RoleGoods roleGoods, int gridId){
		lock.lock();
		try{
			GoodsResult result = new GoodsResult();
			if(gridId > grids.length || gridId < 0){
				return result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			} 
			grids[gridId] = roleGoods;
			roleGoods.setStorageType(this.getStorageType().getType());
			roleGoods.setGridPlace((short)gridId);
			result.addNewGrid(roleGoods);
			initPutSuccessCallback(roleGoods);
			return result.setResult(GoodsResult.SUCCESS);
		}finally{
			lock.unlock();
		}
	}
	
	
	/** 通过装备位置获取装备对象 */
	public RoleGoods getEquipGoods(int gridId){
		lock.lock();
		try{
			if(gridId >= grids.length){
				return null;
			}
			return grids[gridId];
		}finally{
			lock.unlock();
		}
	}
	
	
	
	
	/** 删除物品 **/
	public void remove(RoleGoods roleGoods){
		if(roleGoods == null) {
			return ;
		}
		lock.lock();
		try{
			int gridPlace = roleGoods.getGridPlace();
			this.grids[gridPlace] = null;
			roleGoods.setCurrOverlapCount((short)0);
			this.delete(roleGoods);
		}finally{
			lock.unlock();
		}
	}
	
	protected List<RoleGoods> selectFromStorage(){
		 return GameContext.getBaseDAO().selectList(RoleGoods.class, RoleGoods.ROLE_ID, this.currentRoleId(), 
				 RoleGoods.STORAGE_TYPE,this.getStorageType().getType());
	}
	

	@Override
	protected void init(int gridCount) {
		lock.lock();
		try{
			super.init(gridCount);
			this.initGoods(this.selectFromStorage());
		}finally{
			lock.unlock();
		}
	}
	
	public void initGoods(List<RoleGoods> list){
		if(null == list){
			return ;
		}
		for(RoleGoods roleGoods : list){
			RoleGoodsHelper.init(roleGoods);
			int locationType = this.getEquipLocation(roleGoods);
			if(locationType < 0){
				continue;
			}
			this.initPut(roleGoods, locationType);
			roleGoods.changeNotWrite();
			RoleGoodsHelper.checkGoodsExpiredTime(roleGoods);
		}
	}

	
	@Override
	public void offline(){
		lock.lock();
		try{
			for(RoleGoods roleGoods : this.grids){
				try{
					if(!this.isEffectiveGoods(roleGoods)){
						continue;
					}
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
	
	
}
