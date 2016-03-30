package sacred.alliance.magic.app.goods.behavior.result;

import java.util.Map;

import sacred.alliance.magic.base.GoodsStrengthenType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;

public class StrengthenResult extends Result{
	private RoleGoods roleGoods ;
	private GoodsEquipment goodsTemplate ;
	private int starNumChanged = 0 ;
	private GoodsStrengthenType strengthenType; 
	//一次强化消耗的道具<itemId, num>
	private Map<Integer, Integer> consumeIdAndNum;
	//一次强化消耗的钱币
	private int fee; 
	//一次强化执行次数
	private int execCount;
	
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
	public Map<Integer, Integer> getConsumeIdAndNum() {
		return consumeIdAndNum;
	}
	public void setConsumeIdAndNum(Map<Integer, Integer> consumeIdAndNum) {
		this.consumeIdAndNum = consumeIdAndNum;
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
	public int getExecCount() {
		return execCount;
	}
	public void setExecCount(int execCount) {
		this.execCount = execCount;
	}
	
}
