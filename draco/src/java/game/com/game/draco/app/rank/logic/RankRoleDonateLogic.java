package com.game.draco.app.rank.logic;

import java.util.List;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.donate.domain.RoleDonate;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;

/**
 * log日志格式：roleId#count#roleId#roleName#gender#level#campId#uionId#unionName
 * 返回的日志格式：sort#log日志格式
 * 排行榜小类型和careerType对应，根据排行榜小类型判断是否打印日志
 */
public class RankRoleDonateLogic extends RankRoleLogic {
	
	private static RankRoleDonateLogic instance = new RankRoleDonateLogic();
	
	private RankRoleDonateLogic() {
	}
	
	public static RankRoleDonateLogic getInstance() {
		return instance;
	}
	
	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//根据职业匹配
		byte subType = rankInfo.getSubType();
		if(subType != RANK_ALL && subType != role.getCareer()){
			return false;
		}
		// 是否在统计时间内
		return true ;
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
		//统计活动期间内充值
		RoleDonate roleDonate = GameContext.getDonateApp().getRoleDonate(role, rankInfo.getId());
		int data = GameContext.getDonateApp().getEffectData4Rank(roleDonate);
		this.logData(role, rankInfo, data);
	}

	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo,
			boolean schedulerFlag, String timeStr) {
		int data = GameContext.getDonateApp().getEffectData4Rank(this.getData(role, rankInfo)) ;
		this.logData(role, rankInfo, data);
	}
	
	private void logData(RoleInstance role, RankInfo rankInfo,int data){
		if(data <=0){
			return ;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId());
		sb.append(CAT);
		sb.append(data);
		sb.append(CAT);
		sb.append(getRoleBaseInfo(role));
		rankInfo.getLogger().info(sb.toString()) ;
	}
	

	@Override
	public RankType getRankType() {
		return RankType.Role_Donate;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		//排名 角色名 门派 阵营 捐献值
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols,4));
		//门派名
		item.setData2(this.getUnionName(this.get(cols,7)));//8
		//阵营名
//		item.setData3(CampType.get(Byte.parseByte(this.get(cols,7))).getName());
		//捐献值
		item.setData3(this.get(cols,2));
		//roleId
		item.setKey(this.get(cols,1)) ;
		return item ;
	}
	
	private RoleDonate getData(RoleInstance role, RankInfo rankInfo){
		return GameContext.getDonateApp().getRoleDonate(role, rankInfo.getId());
	}
	
	@Override
	public void initLogData(RankInfo rankInfo){
		int rankId = rankInfo.getId();
		List<RoleDonate> roleDonateList = GameContext.getRankDAO().selectRankIdRoleDonate("rankId", 
				rankId, "limit", getRecordLimit(rankInfo));
		if(Util.isEmpty(roleDonateList)) {
			return ;
		}
		for(RoleDonate roleDonate : roleDonateList) {
			RoleInstance role = GameContext.getRankDAO().selectRole("roleId", roleDonate.getRoleId());
			if(null == role){
				continue ;
			}
			this.logData(role, rankInfo, roleDonate.getCurCount());
		}
	}

}
