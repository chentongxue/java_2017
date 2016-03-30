package com.game.draco.app.rank.logic;

import java.util.List;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.vo.RoleInstance;
import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.base.CampType;
import com.game.draco.message.item.RankDetailItem;

/**
 * 
 * log日志格式：roleId#exp#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 排行榜小类型和careerType对应，根据排行榜小类型判断是否打印日志
 */
public class RankRoleLevelLogic extends RankRoleLogic {

	private static RankRoleLevelLogic instance = new RankRoleLevelLogic();
	private RankRoleLevelLogic(){
	}
	
	public static RankRoleLevelLogic getInstance(){
		return instance ;
	}
	
	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//根据阵营匹配
		byte subType = rankInfo.getSubType();
		if(subType != RANK_ALL && subType != role.getCampId()){
			return false;
		}
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
		
	}
	/**
	 * 日志格式
	 * keyId#level#exp#createtime#roleId#roleName#gender#level#campId#uionId
	 */
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo, boolean schedulerFlag, String timeStr) {
		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId()).append(CAT);
		sb.append(role.getLevel()).append(CAT);
		sb.append(role.getExp()).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		sb.append(getRoleBaseInfo(role));
		doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
	}
	/*
	 * #keyId#level#exp#createtime#roleId#roleName#gender#level#campId#uionId#unionName
	 * 1#17000001#18#93128#1402998071000#17000001#阿奎尼斯·星月#1#18#2##
	 * 说明：
	 0 名次 1
	 1 #keyId  17000001
	 2 #level  18
	 3 #exp 93128
	 4 #createtime 1402998071000
	 5 #roleId 17000001
	 6 #roleName 阿奎尼斯·星月
	 7 #gender 1
	 8 #level 18
	 9 #campId 2
	 10 #uionId NONE
	 11 #unionName NONE
	 * 1#17000001#18#93128#1402998071000#17000001#阿奎尼斯·星月#1#18#2##
	 * 2#1988000007#9#2#1401194291000#1988000007#尤格尔·逐日#0#9#0##
	 * 3#497000068#2#588#1402997687000#497000068#兰斯利德·巫火#1#2#-1##
	 * 4#17000002#2#255#1403173897000#17000002#麦什洛克·追求#1#2#-1##
	 */
	@Override
	public RankDetailItem parseLog(String row) {
		//排名 角色名 门派 阵营 等级
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();

		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols, 6));
		//公会名
		item.setData2(this.get(cols, 10));//11
		//阵营名
//		item.setData3(CampType.get(Byte.parseByte(this.get(cols, 9))).getName());
		//等级
		item.setData3(this.get(cols, 2));
		//roleId
		item.setKey(this.get(cols, 1)) ;
		return item ;
	}
	//从数据库初始化
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RoleInstance> roleList = GameContext.getRankDAO().selectLevelRole("campId", 
				rankInfo.getSubType(), 
				"limit", getRecordLimit(rankInfo));
		if(Util.isEmpty(roleList)){
			return ;
		}
		for(RoleInstance role : roleList){
			if(null == role){
				continue;
			}
			printLog4init(role, rankInfo);
		}
	}
	
	@Override
	public RankType getRankType() {
		return RankType.ROLE_LEVEL ;
	}

}
