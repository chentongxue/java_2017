package com.game.draco.app.unionbattle;

import java.util.List;
import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.unionbattle.config.UnionIntegralRewGroup;
import com.game.draco.app.unionbattle.domain.UnionIntegralRank;
import com.game.draco.app.unionbattle.domain.UnionIntegralState;


public interface UnionIntegralBattleApp{
	
	void initIntegralBattle();
	
	boolean isTimeOpen();
	
	//公会对战列表
	List<UnionIntegralState> getUnionIntegralStateRecordList(int round); 
	
	//公会积分列表
	public List<UnionIntegralRank> getUnionIntegralRankList(); 
	
	//添加公会积分
	void addUnionIntegral(String unionId,int integral,long resetTime,boolean isSaveDB);
	
	//修改公会状态
	void updUnionIntegralState(int round,String unionId,byte state,byte integral,boolean isSaveDB);
	
	//通知进入公会积分战
	void notifyFight();
	
	//进入战场
	Result joinIntegralBattle(RoleInstance role);
	
	//查看玩家所属信息
	UnionIntegralState getIntegralState(RoleInstance role);
	
	//查看玩家所属信息
	List<UnionIntegralState> getIntegralGroupInfoList(int groupId);
	
	//查看玩家所属信息
	Map<String,UnionIntegralState> getIntegralGroupInfoMap(int groupId);

	//创建对战分组
	void createFightList();

	//获得分组数据 false 不检查 true 检查
	Map<Integer, Set<String>> getIntegralGroupMap(boolean falg);

	//重置记录
	void resetIntegral();
	
	void resetIntegralState();
	
	//发奖 积分奖励
	void awardIntegral();

	//发奖 击杀指挥官奖励
	void reward(int rank,String unionId, String instanceId,List<UnionIntegralRewGroup> rewGroupList, boolean valid,boolean isAuction);

	//奖励DKP
	Set<Integer> awardDkp(String unionId, int dkp, String instanceId,
			boolean valid);

	long getOverTime();
	
	Map<String,String> getTargetMap();
	
	void modifyTargetMap(String unionId,String npcId);
	
	//公会是否在活动列表中
	boolean inIntegtalActive(String unionId);
	
	Map<String, UnionIntegralRank> getIntegralMap();

	int getRound();

	void delUnion(String unionId);

}
