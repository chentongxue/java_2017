package com.game.draco.app.rank.logic;

import java.util.List;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.RoleArena;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;
/**
 * 
 * log日志格式：roleId#arenaLevel3v3#cycleWin3v3#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 排行榜小类型和阵营对应，根据排行榜小类型判断是否打印日志
 */
public class RankRoleArena3V3 extends RankRoleLogic {
	
	private static RankRoleArena3V3 instance = new RankRoleArena3V3();
	private RankRoleArena3V3(){
		
	}
	
	public static RankRoleArena3V3 getInstance() {
		return instance;
	}

	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//没有小类
		if(rankInfo.getSubType() != RANK_ALL){
			return false;
		}
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance t, RankInfo rankInfo, int data1, int data2) {

	}

	@Override
	public RankType getRankType() {
		return RankType.Role_Arena_3v3;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		//排名 角色名 等级 胜利场次 竞技场等级
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols,5));
		//等级
		item.setData2(this.get(cols,7));
		//胜利场次
		item.setData3(this.get(cols,3));
		//竞技场等级
		item.setData4(this.get(cols,2));
		//roleId
		item.setKey(this.get(cols,1)) ;
		return item ;
	}
	
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RoleArena> roleArenaList = GameContext.getRankDAO().selectAllArena3V3("limit", getRecordLimit(rankInfo)*5);
		for(RoleArena roleArena : roleArenaList){
			if(null == roleArena){
				continue;
			}
			RoleInstance role = GameContext.getRankDAO().selectRole("roleId", roleArena.getRoleId());
			if(null == role){
				continue ;
			}
			role.setRoleArena(roleArena);
			this.doPrintLog(role, rankInfo, false, null);
		}
	}

	
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo,
			boolean schedulerFlag, String timeStr) {
		RoleArena arena = role.getRoleArena();
		if(null == arena) {
			return ;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId());
		sb.append(CAT);
		sb.append((int)arena.getArenaLevel3v3());
		sb.append(CAT);
		sb.append(arena.getCycleWin3v3());
		sb.append(CAT);
		sb.append(getRoleBaseInfo(role));
		doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
	}

}
