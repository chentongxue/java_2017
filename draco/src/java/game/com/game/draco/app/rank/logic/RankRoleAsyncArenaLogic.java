package com.game.draco.app.rank.logic;

import java.util.List;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.domain.AsyncArenaRole;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;

/**
 * 异步竞技场排名,无删选条件
 * log日志格式：roleId#roleName#honor#
 * 返回的日志格式：sort#log日志格式
 */
public class RankRoleAsyncArenaLogic extends RankRoleLogic {
	
	private static RankRoleAsyncArenaLogic instance = new RankRoleAsyncArenaLogic();
	
	public static RankRoleAsyncArenaLogic getInstance(){
		return instance;
	}

	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
		
	}
	/**
	 * #keyId#historyhonor#battlescore#rolelevel#createtime#
	 * roleId#roleName#gender#level#campId#uionId#unionName
	 */
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo, boolean schedulerFlag, String timeStr) {
		if (role == null) {
			return;
		}

		AsyncArenaRole asyncArenaRole = GameContext.getRoleAsyncArenaApp().getRoleAsyncArenaInfo(role);
		if (asyncArenaRole == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		//获得角色战斗力
		int roleBattleScore = GameContext.getAttriApp().getEffectBattleScore(role);
		sb.append(role.getRoleId()).append(CAT);
		sb.append(asyncArenaRole.getHistoryHonor()).append(CAT);
		sb.append(roleBattleScore).append(CAT);
		sb.append(role.getLevel()).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		sb.append(getRoleBaseInfo(role));

		doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
	}

	@Override
	public RankType getRankType() {
		return RankType.ROLE_ASYNC_ARENA;
	}
	/*
	 * #keyId#score#historyhonor#battlescore#rolelevel#createtime#roleId#roleName#gender#level#campId#uionId#unionName
	 1 #keyId
	 2 #historyhonor
	 3 #battlescore
	 4 #rolelevel
	 5 #createtime
	 6 #roleId
	 7 #roleName
	 8 #gender
	 9 #level
	 10 #campId
	 11 #uionId
	 12 #unionName
	    积分 > 角色战斗力 > 角色等级 > 角色创建时间
	 */
	@Override
	public RankDetailItem parseLog(String row) {
		//名次、角色名、所属公会名、角色战斗力、竞技场积分
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols,7));
		//所属公会名
		item.setData2(this.get(cols,12));
		//角色战斗力
		item.setData3(this.get(cols,3));
		//竞技场积分
		item.setData4(this.get(cols,2));
		//roleId
		item.setKey(this.get(cols,1)) ;
		return item ;
	}
	/*
	 * 获得所有的竞技场有名次的玩家
	 */
	@Override
	public void initLogData(RankInfo rankInfo){
		int limit = getRecordLimit(rankInfo);//100
		List<AsyncArenaRole> roleArenaList = GameContext.getRankDAO().selectAllAsyncArena("limit", limit);
		
		for(AsyncArenaRole roleArena : roleArenaList){
			if(null == roleArena){
				continue;
			}
			RoleInstance role = GameContext.getRankDAO().selectRole("roleId", roleArena.getRoleId()+"");
			if(null == role){
				continue ;
			}
			try {
				//获得角色战斗力
				StringBuilder sb = new StringBuilder();
				int roleBattleScore = GameContext.getAttriApp().getEffectBattleScore(role);
				sb.append(role.getRoleId()).append(CAT);
				sb.append(roleArena.getHistoryHonor()).append(CAT);
				sb.append(roleBattleScore).append(CAT);
				sb.append(role.getLevel()).append(CAT);
				sb.append(getRoleCreatTimeStr(role)).append(CAT);
				sb.append(getRoleBaseInfo(role));
				doWriteLogFile(rankInfo, false, null, sb.toString());
			} catch (Exception e) {
			}
		}
	}

}
