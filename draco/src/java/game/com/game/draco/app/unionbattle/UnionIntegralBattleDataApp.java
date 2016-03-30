package com.game.draco.app.unionbattle;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.core.Service;

import com.game.draco.app.unionbattle.config.UnionIntegral;
import com.game.draco.app.unionbattle.config.UnionIntegralMail;
import com.game.draco.app.unionbattle.config.UnionIntegralNpc;
import com.game.draco.app.unionbattle.config.UnionIntegralReborn;
import com.game.draco.app.unionbattle.config.UnionIntegralRewGroup;
import com.game.draco.app.unionbattle.config.UnionIntegralReward;
import com.game.draco.app.unionbattle.config.UnionIntegralSummon;

public interface UnionIntegralBattleDataApp extends Service{
	
	UnionIntegral getIntegral();
	
	UnionIntegralNpc getIntegralNpc(int id);
	
	Map<String,UnionIntegralNpc> getIntegralNpcMap();
	
	Map<String,UnionIntegralSummon> getIntegralSummonMap();
	
	List<UnionIntegralRewGroup> getIntegralRewGroupList(int groupId);
	
	Map<Byte,UnionIntegralReborn> getIntegralRebornMap();

	Map<Byte,List<UnionIntegralReward>> getIntegralRewardMap();
	
	Map<Byte,UnionIntegralMail> getIntegralMailMap();
}
