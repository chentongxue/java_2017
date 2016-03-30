package sacred.alliance.magic.app.goods;

import java.util.List;

import com.game.draco.GameContext;

import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class HeroEquipBackpack extends BaseEquipBackpack{
	
	public int getHeroId(){
		return this.heroId ;
	}

	private int heroId ;
	public HeroEquipBackpack(RoleInstance role, int gridCount,int heroId ) {
		super(role, gridCount);
		this.heroId = heroId ;
	}

	@Override
	public StorageType getStorageType(){
		return StorageType.hero ;
	}
	
	@Override
	protected List<RoleGoods> selectFromStorage(){
		//英雄的物品初始化用 initGoods(list)
		return null ;
	}
	
	
	@Override
	protected void wearSuccessCallback(RoleGoods wearGoods){
		wearGoods.setOtherParm(String.valueOf(heroId));
	}
	
	@Override
	protected void doffSuccessCallback(RoleGoods doffGoods){
		doffGoods.setOtherParm("");
	}
	
	@Override
	protected void initPutSuccessCallback(RoleGoods roleGoods){
		roleGoods.setOtherParm(String.valueOf(heroId));
	}

	@Override
	public int getEquipLocation(RoleGoods roleGoods) {
		return GameContext.getEquipApp().getHeroEquipslotType(heroId, roleGoods.getGoodsId());
	}
	
}
