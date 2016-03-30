package sacred.alliance.magic.app.treasure;

import java.util.Map;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.npc.domain.NpcInstance;

public interface TreasureApp extends Service{
	
	public abstract Map<Integer, TreasureMap> getAllMapsMap();
	
	public void setAllMapsMap(Map<Integer, TreasureMap> allMapsMap);
	
	public Map<Integer, TreasureMonster> getAllMonstersMap();
	
	public void setAllMonstersMap(Map<Integer, TreasureMonster> allMonstersMap);
	
	//public Message triggerCostMessage(RoleInstance role, String goodsInstanceId, int needGold);
	
	//public Result transferTargetPoint(RoleInstance role, int needGold,Point tarPoint);
	
	public TreasurePosResult handleWrongPoint(RoleInstance role, RoleGoods roleGoods) ;
	
	public void doIdentify(RoleInstance role,RoleGoods roleGoods) ;

	public boolean summonDeath(NpcInstance npc);
}
