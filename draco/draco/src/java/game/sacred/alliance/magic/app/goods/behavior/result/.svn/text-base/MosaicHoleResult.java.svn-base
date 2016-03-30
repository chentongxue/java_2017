package sacred.alliance.magic.app.goods.behavior.result;

import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.GoodsGem;
import sacred.alliance.magic.domain.RoleGoods;

public class MosaicHoleResult extends GoodsResult{

	private RoleGoods roleGoods ;
	private GoodsEquipment goodsTemplate ;
	private RoleGoods roleGem ;
	private GoodsGem gemTemplate ;
	private int matchHoleId = -1 ;
	private int mosaicMoney = 0 ;
	
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
	
	public static MosaicHoleResult newSuccess(){
		MosaicHoleResult result = new MosaicHoleResult();
		result.setResult(GoodsResult.SUCCESS);
		return result;
	}
	public static MosaicHoleResult newFail(){
		MosaicHoleResult result = new MosaicHoleResult();
		result.setResult(GoodsResult.FAIL);
		return result;
	}
	public static MosaicHoleResult newFail(String info){
		MosaicHoleResult result = new MosaicHoleResult();
		result.setInfo(info);
		return result;
	}
	
	public RoleGoods getRoleGem() {
		return roleGem;
	}
	public void setRoleGem(RoleGoods roleGem) {
		this.roleGem = roleGem;
	}
	public GoodsGem getGemTemplate() {
		return gemTemplate;
	}
	public void setGemTemplate(GoodsGem gemTemplate) {
		this.gemTemplate = gemTemplate;
	}
	public int getMatchHoleId() {
		return matchHoleId;
	}
	public void setMatchHoleId(int matchHoleId) {
		this.matchHoleId = matchHoleId;
	}
	public int getMosaicMoney() {
		return mosaicMoney;
	}
	public void setMosaicMoney(int mosaicMoney) {
		this.mosaicMoney = mosaicMoney;
	}
	
	
	
}
