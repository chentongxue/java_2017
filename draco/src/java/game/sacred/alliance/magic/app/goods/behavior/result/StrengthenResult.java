package sacred.alliance.magic.app.goods.behavior.result;

import sacred.alliance.magic.base.GoodsStrengthenType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;

public class StrengthenResult extends Result{
	private RoleGoods roleGoods ;
	private GoodsEquipment goodsTemplate ;
	private int starNumChanged = 0 ;
	private GoodsStrengthenType strengthenType; 
	private int fee; 
	
	public RoleGoods getRoleGoods() {
		return roleGoods;
	}
	public void setRoleGoods(RoleGoods roleGoods) {
		this.roleGoods = roleGoods;
	}
	public GoodsEquipment getGoodsTemplate() {
		return goodsTemplate;
	}
	public void setGoodsTemplate(GoodsEquipment goodsTemplate) {
		this.goodsTemplate = goodsTemplate;
	}
	
	public int getStarNumChanged() {
		return starNumChanged;
	}
	public void setStarNumChanged(int starNumChanged) {
		this.starNumChanged = starNumChanged;
	}
	public int getFee() {
		return fee;
	}
	public void setFee(int fee) {
		this.fee = fee;
	}
	
	public static StrengthenResult newSuccess(){
		StrengthenResult result = new StrengthenResult();
		result.setResult(Result.SUCCESS);
		return result;
	}
	public static StrengthenResult newFail(){
		StrengthenResult result = new StrengthenResult();
		result.setResult(Result.FAIL);
		return result;
	}
	public static StrengthenResult newFail(String info){
		StrengthenResult result = new StrengthenResult();
		result.setInfo(info);
		return result;
	}
	public GoodsStrengthenType getStrengthenType() {
		return strengthenType;
	}
	public void setStrengthenType(GoodsStrengthenType strengthenType) {
		this.strengthenType = strengthenType;
	}
	
}
