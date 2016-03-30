package sacred.alliance.magic.app.goods;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class EquipBackpack extends BaseEquipBackpack{

	public EquipBackpack(RoleInstance role, int gridCount) {
		super(role, gridCount);
	}

	@Override
	protected StorageType getStorageType(){
		return StorageType.equip ;
	}

	
	/** 获取装备 */
	public List<RoleGoods> getEquipGoods(){
		return this.getGoods(0);
	}
	
	/** 获取时装 */
	public List<RoleGoods> getFashionGoods(){
		return this.getGoods(ParasConstant.ROLE_EQUIP_FASHIONE_INDEX);
	}
	
	private List<RoleGoods> getGoods(int start){
		int end = start + ParasConstant.ROLE_EQUIP_FASHIONE_INDEX;
		List<RoleGoods> list = new ArrayList<RoleGoods>();
		lock.lock();
		try{
			for(int i= start; i < end; i++){
				RoleGoods roleGoods = grids[i];
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
	
}
