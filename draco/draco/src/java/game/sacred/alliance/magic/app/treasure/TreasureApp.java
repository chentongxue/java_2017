package sacred.alliance.magic.app.treasure;

import java.util.Map;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class TreasureApp implements Service{
	protected Map<Integer, TreasureMap> allMapsMap = null;
	protected Map<Integer, TreasureMonster> allMonstersMap = null;
	protected Map<Integer, TreasureGood> allGoodsMap = null;
	
	public Map<Integer, TreasureMap> getAllMapsMap() {
		return allMapsMap;
	}
	public void setAllMapsMap(Map<Integer, TreasureMap> allMapsMap) {
		this.allMapsMap = allMapsMap;
	}
	public Map<Integer, TreasureMonster> getAllMonstersMap() {
		return allMonstersMap;
	}
	public void setAllMonstersMap(Map<Integer, TreasureMonster> allMonstersMap) {
		this.allMonstersMap = allMonstersMap;
	}
	
	/**
	 * 換冞葆煤枑尨, 
	 * */
	public abstract Message triggerCostMessage(RoleInstance role, String goodsInstanceId, int needGold, int needBinding);
	
	/**
	 * 紲惘芞換冞
	 * @param role
	 * @param type
	 * @param needGold
	 * @param needBinding
	 * @param tarPoint
	 * @return
	 */
	public abstract Result transferTargetPoint(RoleInstance role, AttributeType type, int needGold, int needBinding, Point tarPoint);
	
	
	public abstract TreasurePosResult handleWrongPoint(RoleInstance role, RoleGoods roleGoods) ;
	
	public abstract void doIdentify(RoleInstance role,RoleGoods roleGoods) ;
}
